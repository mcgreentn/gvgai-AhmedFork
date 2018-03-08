package tracks.tutorialGeneration.AgentBasedGraphRepresentationGenerator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;

import core.game.GameDescription;
import core.game.SLDescription;
import core.game.GameDescription.InteractionData;
import core.game.GameDescription.SpriteData;
import core.game.GameDescription.TerminationData;
import tools.GameAnalyzer;
import tools.LevelAnalyzer;
import tracks.tutorialGeneration.ITSetParserGenerator.Node;

public class GraphBuilder {
	/**
	 * Information parsed from the VGDL File
	 */
	private GameDescription gd;
	private SLDescription sl;
	private GameAnalyzer ga;
	private LevelAnalyzer la;
	
	// holds parent types, with their associated entities
	private HashMap<String, ArrayList<Entity>> parentTypes;
	
	/**
	 * represents the entity which the player controls in the game
	 */
	private ArrayList<Entity> avatarEntities;
	
	boolean verbose = true;
	/**
	 * contains all the entities in the graph
	 */
	public ArrayList<Entity> allEntities;
	
	public ArrayList<Entity> allObjects;
	
	public ArrayList<Entity> allActions;
	public ArrayList<Entity> allConditions;
	
	public ArrayList<Entity> victoryTerminations;
	public ArrayList<Entity> lossTerminations;
	
	public ArrayList<Mechanic> allRules;
	
	public ArrayList<String> spriteCountActions = new ArrayList<String>(Arrays.asList("KillSprite"));

	public ArrayList<Mechanic> winPath;
	/**
	 * Creates a graph builder object, intakes all information parsed in from the VDGL file
	 * @param gd the game description
	 * @param sl the SL description
	 * @param ga the game analyzer
	 * @param la the level analyzer
	 */
	public GraphBuilder(GameDescription gd, SLDescription sl, GameAnalyzer ga, LevelAnalyzer la) {
		// init
		avatarEntities = new ArrayList<Entity>();
		allEntities = new ArrayList<Entity>();
		allObjects = new ArrayList<Entity>();
		allActions = new ArrayList<Entity>();
		allConditions = new ArrayList<Entity>();
		victoryTerminations = new ArrayList<Entity>();
		lossTerminations = new ArrayList<Entity>();
		allRules = new ArrayList<Mechanic>();       
		
		parentTypes = new HashMap<String, ArrayList<Entity>>();
		this.gd = gd;
		this.sl = sl;
		this.ga = ga;
		this.la = la;
	}
	
	/**
	 * Builds the graph for this graph builder by reading in spirte, interaction, and termination sets
	 */
	public void buildGraph() {
		if(verbose) 
			System.out.println("Building Mechanic graph...");
	
		readSpriteSet();
		readInteractionSet();
		readTerminationSet();
		
		createMechanicIO();
		// read out all the entities and their input/outputs
		for(Entity e : allEntities) {
			System.out.print("\n\n**Entity**: " + e.getName());
			System.out.print("\nType: " + e.getType());
			System.out.print("\nAttributes:");
			for(Pair a : e.getAttributes()) {
				System.out.print(a.getAttribute() + "=" + a.getValue() + " :: ");
			}
			System.out.print("\nInputs:");
			for(Entity i : e.getInputs()) {
				System.out.print(i.getName() + " :: ");
			}
			System.out.print("\nOutputs:");
			for(Entity o : e.getOutputs()) {
				System.out.print(o.getName() + " :: ");
			}

		}
	
	}
	
	/**
	 * Uses parsed information in the game description object to read in Sprite Data
	 */
	public void readSpriteSet() {
		if(verbose)
			System.out.println("Reading Sprite Set...");
		// get all sprite data
		ArrayList<SpriteData> allSpriteData = gd.getAllSpriteData();
		
		for(SpriteData current : allSpriteData) {
			// Create the given sprite
			createSpriteEntity(current);
		}
		
		// after all sprites are read in, go through each one in detail and add mechanics
		for(Entity object : allObjects){
			classifySprite(object);
		}
	}
	
	/**
	 * Uses parsed information from the game description object to read in Interaction Set Data
	 */
	public void readInteractionSet() {
		if(verbose)
			System.out.println("Reading Interaction Set");
		// loop through every sprite paired with every other sprite
		// get all sprite data
		ArrayList<SpriteData> allSpriteData = gd.getAllSpriteData();
		for(SpriteData sprite1 : allSpriteData) {
			for(SpriteData sprite2 : allSpriteData) {
				// get all interactions between these two sprites
				ArrayList<InteractionData> intDataList = gd.getInteraction(sprite1.name, sprite2.name);
				for(InteractionData interaction : intDataList) {
					classifyInteractionData(interaction, sprite1.name, sprite2.name);
				}
			}
		}	
	}
	
	/**
	 * Uses parsed information from the game description object to read in the Termination Set Data
	 */
	public void readTerminationSet() {
		if(verbose)
			System.out.println("Reading Termination Set");
		// grab termination data from game description object
		ArrayList<TerminationData> terminations = gd.getTerminationConditions();
		for(TerminationData termination : terminations) {
			classifyTerminationData(termination);
		}
	}
	
	/**
	 * Classifies the sprite data, creates a new object entity for the sprite
	 * @param current the sprite data to be classified
	 */
	public void createSpriteEntity(SpriteData current) {
		if(verbose)
			System.out.println("Creating entity to represent : " + current.name);
		String parent = "";
		if(current.parents.size() > 0) {
			parent = current.parents.get(current.parents.size() - 1);
		}
		Entity objectSprite;
		if(!parent.equals("")) {
			objectSprite = new Entity(current.name, current.name + " (" + parent + ")", "Object", current.type);
		}
		else {
			objectSprite = new Entity(current.name, current.name + " (" + current.type + ")", "Object", current.type);
		}
		objectSprite.setParents(current.parents);
		// add this entity to the lists
		allEntities.add(objectSprite);
		allObjects.add(objectSprite);
		// check if this is an avatar
		avatarCheck(current, objectSprite); 

		if(!parent.equals("")) {
			if(parentTypes.containsKey(parent)) {
				parentTypes.get(parent).add(objectSprite);
			} else {
				parentTypes.put(parent, new ArrayList<Entity>());
				parentTypes.get(parent).add(objectSprite);
			}
		}
		// add behaviors to this new entity
		HashMap<String,String> currParams = current.parameters;
	    Iterator it = currParams.entrySet().iterator();
	    while(it.hasNext()) {
	        HashMap.Entry pair = (HashMap.Entry)it.next();	    
	        // key = the first String in Pair
	        // value = the second String in Pair
	        Pair newAttribute = new Pair(pair.getKey().toString(), pair.getValue().toString());
	        // add the attribute to the entity
	        objectSprite.addAttribute(newAttribute);
	        if(verbose)
	        	System.out.println("Attribute Found... " + newAttribute.getAttribute() + " = " + newAttribute.getValue());
	    }
	    if(verbose)
	    	System.out.println("********");
	}
	/***
	 * classifies the sprite and adds mechanic information for any sprite related to it depending on what type of sprite it is
	 * @param sprite
	 */
	public void classifySprite(Entity sprite) {
		if(sprite.getSubtype().equals("ShootAvatar") || sprite.getSubtype().equals("OngoingShootAvatar") || sprite.getSubtype().equals("FlakAvatar")) {
			classifyAvatarSprite(sprite);
		}
	}
	/***
	 * classifies specifically the avatar sprite
	 * @param sprite
	 */
	public void classifyAvatarSprite(Entity sprite) {
		Entity condition = new Entity("Press Space", "Condition", "Player Input");
		Entity action = new Entity("Shoot", "Action", "Interaction");
		
		Entity missile = searchObjects(sprite.getAttribute("stype").getValue());
		
		condition.addInput(sprite);
		condition.addOutput(action);
		action.addInput(condition);
		action.addOutput(missile);
		sprite.addOutput(condition);
		
		Mechanic mech = createMechanic(action, sprite, condition);
		sprite.addMechanic(mech);
	}
	/**
	 * Creates a single timeout object entity for the timeout conditions
	 */
	public void createTimeOutObject() {
		if(verbose)
			System.out.println("Creating entity to represent : Timeout");
		Entity objectSprite = new Entity("Timeout", "Object", "n/a");
		objectSprite.setParents(new ArrayList<String>());
		allEntities.add(objectSprite);
		allObjects.add(objectSprite);

	}
	/**
	 * Classifies the given interaction data into a family
	 * @param interaction the interaction data to be classified
	 * @param sprite1 the name of sprite 1 of this interaction
	 * @param sprite2 the name of sprite 2 of this interaction
	 */
	public void classifyInteractionData(InteractionData interaction, String sprite1, String sprite2) {
		Entity condition = new Entity("Collision", "Condition", "n/a");
		Entity action = new Entity(interaction.type, "Action", "Interaction");
		
		
		action.addAttribute(new Pair("ScoreChange", interaction.scoreChange));
		
		if(verbose)
			System.out.println("Creating action to represent: " + action.getName());
		allActions.add(action);
		allEntities.add(action);

		// search for the entities
		Entity one = searchObjects(sprite1);
		Entity two = searchObjects(sprite2);
		if(one != null && two != null) {
			// construct the family
			one.addOutput(condition);
			two.addOutput(condition);
			condition.addOutput(action);
			
			condition.addInput(two);
			condition.addInput(one);
			action.addInput(condition);
			

			// create attributes for the condition
//			createInteractionConditionAttributes(condition, one, two, interaction);
			//create a mechanic
			Mechanic mech = createMechanic(action, one, two, condition);
			
			// create the output for the action
			createInteractionActionOutput(action, one, two, interaction, mech);
			
			one.addMechanic(mech);
			two.addMechanic(mech);
			action.addMechanic(mech);
			allRules.add(mech);
//			for(Entity output : action.getOutputs()) {
//				mech.addOutput(output);
//			}
		}
	}
	
	/**
	 * Generates the output for the action of a family, given the action, the interaction, and the entities involved
	 * @param action the action entity for this family
	 * @param one the first object entity for this family
	 * @param two the second object entity for this family
	 * @param interaction the interaction data for the action
	 */
	public void createInteractionActionOutput(Entity action, Entity one, Entity two, InteractionData interaction, Mechanic m) {
		// tracking for what kind of rule this is
		String name = action.getName();
		
		if(name.equals("KillSprite") || name.equals("StepBack") || name.equals("KillIfHasMore") || name.equals("KillIfHasLess")
				|| name.equals("KillIfFromAbove") || name.equals("KillIfOtherHasMore") || name.equals("CloneSprite")
				|| name.equals("AddHealthPoints") || name.equals("AddHealthPointsToMax") || name.equals("SubtractHealthPoints")) {
			action.addOutput(one);
		} else if(name.equals("KillAll") || name.equals("SpawnBehind") || name.equals("TransformTo") || name.equals("IncreaseSpeedToAll")
				|| name.equals("DecreaseSpeedToAll") || name.equals("SetSpeedForAll")) {
			Entity stype = searchObjects(interaction.sprites.get(0));
			action.addOutput(stype);
			stype.addMechanic(m);
		} else if(name.equals("KillBoth")) {
			action.addOutput(one);
			action.addOutput(two);
		}
	}
	/**
	 * Creates a family for the given entities
	 * @param action the action entity for this mechanic
	 * @param one the first input object for this mechanic
	 * @param two the second input object for this mechanic
	 * @param condition the condition for this mechanic
	 * @param others the output objects for this mechanic (optional)
	 */
	public Mechanic createMechanic(Entity action, Entity one, Entity two, Entity condition) {
		if(verbose)
			System.out.println("Creating a mechanic from entities: " + one.getName() + " " + two.getName() + " > " + condition.getName());
		Mechanic mech = new Mechanic(one, two, condition, action);
		allRules.add(mech);
		return mech;
	}
	
	public Mechanic createMechanic(Entity action, Entity one, Entity condition) {
		if(verbose)
			System.out.println("Creating a mechanic from entities: " + one.getName() + " > " + condition.getName());
		Mechanic mech = new Mechanic(one, condition, action);
		allRules.add(mech);
		return mech;
	}
	/**
	 * Generates attribute data for the conditions, given the entities involved and interaction data
	 * TODO: actually make this function functionality
	 * @param condition the condition to be used
	 * @param one the first entity for this condition
	 * @param two the second entity for this condition
	 * @param interaction the interaction data
	 */
	public void createInteractionConditionAttributes(Entity condition, Entity one, Entity two, InteractionData interaction) {
		
	}
	
	/**
	 * Classifies the given termination data into a family
	 * @param termination
	 */
	public void classifyTerminationData(TerminationData termination) {
		ArrayList<String> spritesInvolved = termination.sprites;
		String type = termination.type;
		
		Entity condition = new Entity(type, "Condition", "n/a");		
		Entity action = new Entity((termination.win.equals("True") ? "Win" : "Lose"), "Action", "Termination");
		
		
		// add to the lists
		allEntities.add(condition);
		allEntities.add(action);
		allConditions.add(condition);
		allActions.add(action);
		if(action.getName().equals("Win")) {
			victoryTerminations.add(action);	
		} else {
			lossTerminations.add(action);
		}
		
		// add attributes and input/output
		condition.addAttribute(new Pair("limit", termination.limit + ""));
		condition.addOutput(action);
		action.addInput(condition);
		Mechanic mech = new Mechanic(condition, action);
		allRules.add(mech);
		// add the mechanic to respective mechanic lists in entities
		condition.addMechanic(mech);
		action.addMechanic(mech);
		
		// deal with each sprite in this mechanic
		for(String sprite : spritesInvolved) {
			// find entity object, add this mechanic to its list
			Entity object = searchObjects(sprite);
			object.addMechanic(mech);
			object.addOutput(condition);
			condition.addInput(object);
			object.addMechanic(mech);
			mech.setObject1(object);
		}
		if(condition.getName().equals("Timeout")) {
			Entity timeoutObject = searchObjects("Timeout");
			if(timeoutObject == null) {
				createTimeOutObject();
			}
			timeoutObject = searchObjects("Timeout");
			condition.addInput(timeoutObject);
			timeoutObject.addOutput(condition);
		}	
	}
	/**
	 * creates the IO for each mechanic in the ruleset, allowing easier graph traversal
	 */
	public void createMechanicIO() {
		for(Mechanic mech : allRules) {
			for(Entity sprite : mech.getAction().getOutputs()) {
				for(Mechanic spriteMech : sprite.getMechanics()) {
					if(!sprite.equals(mech.getObject1()) && !sprite.equals(mech.getObject2()) || (spriteMech.getType().equals("Win") || spriteMech.getType().equals("Lose"))) {
						mech.addOutput(spriteMech);
					}
				}
			}
		}
	}
	public ArrayList<String> traceCriticalPath(Entity terminationAction) {
		// get the rule with this terminal action
		Mechanic mech = searchMechanics(terminationAction);
		ArrayList<String> instructions = new ArrayList<String>();
		
		if(mech.getType().equals("Timeout")) {
			instructions.add("You will " + (mech.getAction().getName().equals("Win") ? "win" : "lose") + " after " + mech.getAction().getAttribute("limit") + " seconds");
		} else {
			// we need to trace this down a bit, since the only other options have to deal with sprite counters
			// do an A* search from each mechanic to the terminal mechanic, using distance from a specific terminal as heuristic
			// for each dependent sprite, lets figure out what mechanics 
//			for(Entity e : mech.getOutputs()) {
//				// get all conditional outputs for this object entity
//				for(Entity c : e.getOutputs()) {
//					// get all actions for this conditional entity
//					for(Entity a : c.getOutputs()) {
//						if(spriteCountActions.contains(a.getName())) {
//							// if this is in spriteCountActions, then this mechanic directly impacts the sprite count of the object, so add this to the critical path
//							Mechanic parentMech = searchMechanics(a);
//							instructions.add("If " + parentMech.getObject1().getName() + " and " + parentMech.getObject2().getName() + " " + parentMech.getCondition().getName() 
//									+ ", " + parentMech.getAction().getName() + " to " + e.getName());
//						}
//					}
//				}

			
		}
		
		
		
		return instructions;
	}
	
	/***
	 * Changes the node to reflect whether we can generalize this rule to a subtype, rather than a specific sprite
	 * @param node which contains the mechanic in question. We will look over all mechanics and see if all subtypes and action are the same
	 * @return
	 */
	public BFSNode generalize(BFSNode node, ArrayList<Mechanic> siblingMechs) {
		Mechanic m = node.getMech();
		siblingMechs.add(m);

		// the specific mechanic in question is a 2-object mechanic
		Mechanic temp = null;
		BFSNode newNode = new BFSNode(m);
		int limit = 0;
		while((temp == null || !m.equals(temp)) && limit < 10) {
			temp = m;
			Mechanic generalizedObj1 = generalizeObject1(m, siblingMechs);
			Mechanic generalizedObj2 = generalizeObject2(generalizedObj1, siblingMechs);
			Mechanic generalizedActionOutput = generalizedObj2;
			if(!generalizedObj1.equals(m) && generalizedActionOutput.getAction().getOutputs().size() > 0)
				generalizedActionOutput = generalizeActionOutput(generalizedObj2);
			newNode = new BFSNode(generalizedActionOutput);
			m = generalizedActionOutput;
			limit++;
		}
		return newNode;	
	}
	public Mechanic generalizeObject1(Mechanic m, ArrayList<Mechanic> siblingMechs) {
		ArrayList<Mechanic> subSiblingMechs = new ArrayList<Mechanic>();
		
		if(m.getObject1() != null && m.getObject2() != null) {
			String subtype1 = "";
			String subtype2 = "";

			if(m.getObject1().getParents().size() > 0) {
				subtype1 = m.getObject1().getParents().get(m.getObject1().getParents().size()-1);
			}
			if(m.getObject2().getParents().size() > 0) {
				subtype2 = m.getObject2().getParents().get(m.getObject2().getParents().size()-1);
			}
			ArrayList<Entity> spritesToSearch = new ArrayList<Entity>();
			// query all objects and figure out which ones share a subtype with the first object
			if(!subtype1.equals("")) {
				for(Entity object : allObjects) {
					String parent = "";
					if(!object.equals(m.getObject1())) {
						if(object.getParents().size() > 0) {
							parent = object.getParents().get(object.getParents().size() - 1);
//							parent = object.getParents().get(0);
						}
						if(parent.equals(subtype1)) {
							spritesToSearch.add(object);
						}
					}
				}
			}
			int generalizable = 0;
			for(Entity object : spritesToSearch) {
				// works tracks which object matched with the sprite in question, if its the first object, then works = 1, if second object, then works = 2
				int works = 0;
				// go thru each mechanic for this object
				for(Mechanic aMech : object.getMechanics()) {
					// see if this mechanic is similar enough to the mechanic we are querying for
					// don't even bother if it isnt a 2-object mechanic
					if(aMech.getObject1() != null && aMech.getObject2() != null) {
						String s1 = "";		
						String s2 = "";
						if(aMech.getObject1().getParents().size() > 0) {
							s1 = aMech.getObject1().getParents().get(aMech.getObject1().getParents().size()-1);
						}
						if(aMech.getObject2().getParents().size() > 0) {
							s2 = aMech.getObject2().getParents().get(aMech.getObject2().getParents().size()-1);
						}
						String a = aMech.getAction().getName();
						if((s1.equals(subtype1)) && (s2.equals(subtype2)) && a.equals(m.getAction().getName()) && !aMech.equals(m)) {
							subSiblingMechs.add(aMech);
							works = 1;
							break;
						} 
					}
				}
				if(works == 0) {
					subSiblingMechs = new ArrayList<Mechanic>();
					generalizable = 0;
					break;
				}
				else if(works == 1) {
					generalizable = 1;
				}
				else {
					generalizable = 2;
				}
			}
			if(generalizable == 1 || generalizable == 2) {
				// create a new node to replace this one, with the new generalized Mechanic for object1
				Entity newObject = new Entity(subtype1, subtype1, "Object", subtype1);
//				allObjects.add(newObject);
				ArrayList<String> falseParents = new ArrayList<String>();
				// get the real parents, insert everything but the last parent into false parents list
				ArrayList<String> realParents = m.getObject1().getParents();
				if(realParents.size() > 1) {
					for(int i = realParents.size() - 2; i >= 0; i--) {
						falseParents.add(realParents.get(i));
					}
				}
				if(falseParents.size() == 0) {
					falseParents.add(subtype1);
				}
				newObject.setParents(falseParents);
				newObject.setAttributes(m.getObject1().getAttributes());
				Mechanic newMech = null;
				if(generalizable == 1)
					newMech = new Mechanic(newObject, m.getObject2(), m.getCondition(), m.getAction());
				else
					newMech = new Mechanic(m.getObject2(), newObject, m.getCondition(), m.getAction());
				siblingMechs.addAll(subSiblingMechs);
				return newMech;	
			}
		}
		// just check for object 1 stuff then
		else if(m.getObject1() != null) {
			String subtype1 = "";
			if(m.getObject1().getParents().size() > 0) {
				subtype1 = m.getObject1().getParents().get(m.getObject1().getParents().size() - 1);
			}
			ArrayList<Entity> spritesToSearch = new ArrayList<Entity>();
			// query all objects and figure out which ones share a subtype with the first object
			if(!subtype1.equals("")) {
				for(Entity object : allObjects) {
					String parent = "";
					if(object.getParents().size() > 0) {
						parent = object.getParents().get(object.getParents().size() - 1);
					}
					if(parent.equals(subtype1) && !parent.equals("")) {
						spritesToSearch.add(object);
					}
				}
			}
			int generalizable = 0;
			for(Entity object : spritesToSearch) {
				// works tracks which object matched with the sprite in question, if its the first object, then works = 1, if second object, then works = 2
				int works = 0;
				// go thru each mechanic for this object
				for(Mechanic aMech : object.getMechanics()) {
					// see if this mechanic is similar enough to the mechanic we are querying for
					// don't even bother if it isnt a 1-object mechanic
					if(aMech.getObject1() != null) {
						String s1 = "";
						if(aMech.getObject1().getParents().size() > 0) {
							s1 = aMech.getObject1().getParents().get(aMech.getObject1().getParents().size() - 1);
						}
						String a = aMech.getAction().getName();
						if((s1.equals(subtype1)) && a.equals(m.getAction().getName())) {
							subSiblingMechs.add(aMech);
							works = 1;
							break;
						} 
					}
				}
				if(works == 0) {
					subSiblingMechs = new ArrayList<Mechanic>();
					generalizable = 0;
					break;
				}
				else {
					generalizable = 1;
				}
			}
			if(generalizable == 1) {
				// create a new node to replace this one, with the new generalized Mechanic for object1
				Entity newObject = new Entity(subtype1, subtype1, "Object", subtype1);
//				allObjects.add(newObject);

				ArrayList<String> falseParents = new ArrayList<String>();
				// get the real parents, insert everything but the last parent into false parents list
				ArrayList<String> realParents = m.getObject1().getParents();
				if(realParents.size() > 1) {
					for(int i = realParents.size() - 2; i >= 0; i--) {
						falseParents.add(realParents.get(i));
					}
				}
				if(falseParents.size() == 0) {
					falseParents.add(subtype1);
				}
				newObject.setParents(falseParents);
				newObject.setAttributes(m.getObject1().getAttributes());
				Mechanic newMech = null;
				if(generalizable == 1)
					newMech = new Mechanic(newObject, m.getObject2(), m.getCondition(), m.getAction());
				siblingMechs.addAll(subSiblingMechs);
				return newMech;	
			}
		}
		return m;
	}
	
	public Mechanic generalizeObject2(Mechanic m, ArrayList<Mechanic> siblingMechs) {
		ArrayList<Mechanic> subSiblingMechs = new ArrayList<Mechanic>();
		if(m.getObject1() != null && m.getObject2() != null) {
			String subtype1 = "";
			String subtype2 = "";
			
			if(m.getObject1().getParents().size() > 0) {
				subtype1 = m.getObject1().getParents().get(m.getObject1().getParents().size() - 1);
			}
			if(m.getObject2().getParents().size() > 0) {
				subtype2 = m.getObject2().getParents().get(m.getObject2().getParents().size() - 1);
			}
			ArrayList<Entity> spritesToSearch = new ArrayList<Entity>();
			// query all objects and figure out which ones share a subtype with the first object
			if(!subtype2.equals("")) {
				for(Entity object : allObjects) {
					String parent = "";
					if(!object.equals(m.getObject2())) {
						if(object.getParents().size() > 0) {
							parent = object.getParents().get(object.getParents().size() - 1);
						}
						if(parent.equals(subtype2) && !parent.equals("")) {
							spritesToSearch.add(object);
						}
					}
				}
			}
			int generalizable = 0;
			for(Entity object : spritesToSearch) {
				// works tracks which object matched with the sprite in question, if its the first object, then works = 1, if second object, then works = 2
				int works = 0;
				// go thru each mechanic for this object
				for(Mechanic aMech : object.getMechanics()) {
					// see if this mechanic is similar enough to the mechanic we are querying for
					// don't even bother if it isnt a 2-object mechanic
					if(aMech.getObject1() != null && aMech.getObject2() != null) {
						String s1 = "";		
						String s2 = "";
						if(aMech.getObject1().getParents().size() > 0) {
							s1 = aMech.getObject1().getParents().get(aMech.getObject1().getParents().size()-1);
						}
						if(aMech.getObject2().getParents().size() > 0) {
							s2 = aMech.getObject2().getParents().get(aMech.getObject2().getParents().size()-1);
						}
						String a = aMech.getAction().getName();
						// check to make sure the score attribute is the same
						String l1 = m.getAction().getAttribute("ScoreChange").getValue();
						String l2 = aMech.getAction().getAttribute("ScoreChange").getValue();
						if((s1.equals(subtype1) || s1.equals(m.getObject1().getName())) && (s2.equals(subtype2)) && a.equals(m.getAction().getName()) && !aMech.equals(m) && l1.equals(l2)) {
							subSiblingMechs.add(aMech);
							works = 1;
							break;
						}
					}
				}
				if(works == 0) {
					generalizable = 0;
					break;
				}
				else if(works == 1) {
					generalizable = 1;
				}
				else {
					generalizable = 2;
				}
			}
			if(generalizable == 1 || generalizable == 2) {
				// create a new node to replace this one, with the new generalized Mechanic for object1
				Entity newObject = new Entity(subtype2, subtype2, "Object", subtype2);
//				allObjects.add(newObject);

				ArrayList<String> falseParents = new ArrayList<String>();
				// get the real parents, insert everything but the last parent into false parents list
				ArrayList<String> realParents = m.getObject2().getParents();
				if(realParents.size() > 1) {
					for(int i = realParents.size() - 2; i >= 0; i--) {
						falseParents.add(realParents.get(i));
					}
				} 
				if(falseParents.size() == 0) {
					falseParents.add(subtype2);
				}
				newObject.setParents(falseParents);
				newObject.setAttributes(m.getObject2().getAttributes());
				Mechanic newMech = null;
				if(generalizable == 1) {
					siblingMechs.addAll(subSiblingMechs);
					newMech = new Mechanic(m.getObject1(), newObject, m.getCondition(), m.getAction());
				}
				else {
					siblingMechs.addAll(subSiblingMechs);
					newMech = new Mechanic(newObject, m.getObject2(), m.getCondition(), m.getAction());
				}
				return newMech;	
			}
		}
		return m;
	}

	public Mechanic generalizeActionOutput(Mechanic m) {
		if(m.getObject1() != null && m.getObject2() != null) {
			String subtype1 = "";
			String subtype2 = "";
			String subtype3 = "";
			if(m.getObject1().getParents().size() > 0) {
				subtype1 = m.getObject1().getParents().get(m.getObject1().getParents().size() - 1);
			} 
			if(m.getObject2().getParents().size() > 0) {
				subtype2 = m.getObject2().getParents().get(m.getObject2().getParents().size() - 1);
			}
			if(m.getAction().getOutputs().get(0).getParents().size() > 0) {
				subtype3 = m.getAction().getOutputs().get(0).getParents().get(m.getAction().getOutputs().get(0).getParents().size() - 1);
			}
			ArrayList<Entity> spritesToSearch = new ArrayList<Entity>();
			// query all objects and figure out which ones share a subtype with the action entity
			if(!subtype3.equals("")) {
				for(Entity object : allObjects) {
					String parent = "";
					if(object.getParents().size() > 0) {
						parent = object.getParents().get(object.getParents().size() - 1);
					}
					if(parent.equals(subtype3) && !parent.equals("")) {
						spritesToSearch.add(object);
					}
				}
			}
			int generalizable = 0;
			for(Entity object : spritesToSearch) {
				// works tracks which object matched with the sprite in question, if its the first object, then works = 1, if second object, then works = 2
				int works = 0;
				// go thru each mechanic for this object
				for(Mechanic aMech : object.getMechanics()) {
					// see if this mechanic is similar enough to the mechanic we are querying for
					// don't even bother if it isnt a 2-object mechanic
					if(aMech.getObject1() != null && aMech.getObject2() != null) {
						String s1 = "";
						String s2 = "";
						
						if(aMech.getObject1().getParents().size() > 0) {
							s1 = aMech.getObject1().getParents().get(aMech.getObject1().getParents().size() - 1);
						}
						if(aMech.getObject2().getParents().size() > 0) {
							s2 = aMech.getObject2().getParents().get(aMech.getObject2().getParents().size() - 1);
						}
						String a = aMech.getAction().getName();
						if((s1.equals(subtype1)) && (s2.equals(subtype2)) && a.equals(m.getAction().getName())) {
							works = 1;
							break;
						} 
					}
				}
				if(works == 0) {
					generalizable = 0;
					break;
				}
				else {
					generalizable = 1;
				}
			}
			if(generalizable == 1) {
				// create a new node to replace this one, with the new generalized Mechanic for action
				Entity newAction = new Entity(m.getAction().getName(), "Action", "Interaction");
//				allActions.add(newAction);
				newAction.setAttributes(m.getAction().getAttributes());
				if(m.getAction().getOutputs().size() > 0) {
					String parent = "";
					if(m.getAction().getOutputs().get(0).getParents().size() > 0) {
						parent = m.getAction().getOutputs().get(0).getParents().get(m.getAction().getOutputs().get(0).getParents().size() - 1);
					}
					else {
						parent = m.getAction().getOutputs().get(0).getName() + " (" + m.getAction().getOutputs().get(0).getSubtype() + ")";
					}
					Entity newOutput = new Entity(parent,
							parent,
							"Object", 
							parent);
					ArrayList<String> falseParents = new ArrayList<String>();
					// get the real parents, insert everything but the last parent into false parents list
					ArrayList<String> realParents = m.getAction().getOutputs().get(0).getParents();
					if(realParents.size() > 1) {
						for(int i = realParents.size() - 2; i >= 0; i--) {
							falseParents.add(realParents.get(i));
						}
					}
					if(falseParents.size() == 0) {
						falseParents.add(subtype1);
					}
					newOutput.setAttributes(m.getAction().getOutputs().get(0).getAttributes());

					newOutput.setParents(falseParents);
					newAction.addOutput(newOutput);
				}
				
				Mechanic newMech = new Mechanic(m.getObject1(), m.getObject2(), m.getCondition(), newAction);
				return newMech;	
			}
		}
		// just check for object 1 stuff then
		else if(m.getObject1() != null) {
			String subtype1 = m.getObject1().getSubtype();
			ArrayList<Entity> spritesToSearch = new ArrayList<Entity>();
			// query all objects and figure out which ones share a subtype with the first object
			for(Entity object : allObjects) {
				if(object.getSubtype().equals(subtype1)) {
					spritesToSearch.add(object);
				}
			}
			int generalizable = 0;
			for(Entity object : spritesToSearch) {
				// works tracks which object matched with the sprite in question, if its the first object, then works = 1, if second object, then works = 2
				int works = 0;
				// go thru each mechanic for this object
				for(Mechanic aMech : object.getMechanics()) {
					// see if this mechanic is similar enough to the mechanic we are querying for
					// don't even bother if it isnt a 1-object mechanic
					if(aMech.getObject1() != null) {
						String s1 = aMech.getObject1().getSubtype();
						String a = aMech.getAction().getName();
						if((s1.equals(subtype1)) && a.equals(m.getAction().getName())) {
							works = 1;
							break;
						} 
					}
				}
				if(works == 0) {
					generalizable = 0;
					break;
				}
				else {
					generalizable = 1;
				}
			}
			if(generalizable == 1) {
				// create a new node to replace this one, with the new generalized Mechanic for object1
				// create a new node to replace this one, with the new generalized Mechanic for action
				Entity newAction = new Entity(m.getAction().getName(), "Action", "Interaction");
				if(m.getAction().getOutputs().size() > 0) {
					Entity newOutput = new Entity(m.getAction().getOutputs().get(0).getName(), m.getAction().getOutputs().get(0).getSubtype(), "Object", m.getAction().getOutputs().get(0).getSubtype());
					newOutput.setAttributes(m.getAction().getOutputs().get(0).getAttributes());
					newAction.addOutput(newOutput);
				}
				
				Mechanic newMech = new Mechanic(m.getObject1(), m.getObject2(), m.getCondition(), newAction);
				return newMech;	
			}
		}
		return m;
	}
	public ArrayList<String> traceUserInteractionChain(Entity avatar, Entity win) {
		if(winPath == null)
			winPath = new ArrayList<Mechanic>();

		ArrayList<String> instructions = new ArrayList<String>();
		BFSTree tree = new BFSTree(this);
		tree.buildTree(avatar, win);
		// trace all paths from avatar to win
		ArrayList<ArrayList<BFSNode>> validPaths = tree.getValidPaths();
		// find the longest path
		ArrayList<BFSNode> longestPath = null;
		int longestPathLength = -1;
		for(ArrayList<BFSNode> possiblePath : validPaths) {
			if(possiblePath.size() > longestPathLength) {
				longestPathLength = possiblePath.size();
				longestPath = possiblePath;
			}
		}
		
		// change nodes in the longest path to reflect generalized rules
//		ArrayList<BFSNode> extraInfo = createExtraInfo(validPaths, longestPath);
		if(longestPath != null) {
			for(int i = 0; i < longestPath.size(); i++) {
				ArrayList<Mechanic> siblings = new ArrayList<Mechanic>();
				if(!winPath.contains(longestPath.get(i).getMech())) {
					winPath.add(longestPath.get(i).getMech());
				}
				BFSNode node = longestPath.get(i);
				longestPath.set(i, generalize(node, siblings));
				for(Mechanic s : siblings) {
					if(!winPath.contains(s))
						winPath.add(s);	
				}
				
				System.out.println(longestPath.get(i).getMech().getObject1().getFullName());
			}
		}
//		for(int i = 0; i < validPaths.size(); i++) {
		if(longestPath != null) {
			for(BFSNode node : longestPath) {
				Mechanic m = node.getMech();
				
				instructions.add(getInteractionString(m, 1));
			}
		}
//		}
		// must be a timeout condition
		if(validPaths.size() == 0) {
			Mechanic winMech = win.getMechanics().get(0);
			Entity a = winMech.getAction();
			Entity c = winMech.getCondition();
			String instruct = "If time reaches " + winMech.getCondition().getAttribute("limit").getValue() + " then you " + a.getName();
			instructions.add(instruct);
			winPath.add(winMech);
		}
		return instructions;
	}
	
	
	public ArrayList<ArrayList<Mechanic>> visualPathGeneralization(ArrayList<Mechanic> wp) {
		ArrayList<ArrayList<Mechanic>> superWP = new ArrayList<ArrayList<Mechanic>>();
		
		// generalize each mechanic, but then put the og ones in the list, so we can search for any of these interactions
		for(int i = 0; i < wp.size(); i++) {
			superWP.add(new ArrayList<Mechanic>());
			ArrayList<Mechanic> siblingMechs = new ArrayList<Mechanic>();
//			generalize()
			superWP.get(i).add(generalize(new BFSNode(wp.get(i)), siblingMechs).getMech());
			superWP.get(i).addAll(siblingMechs);
		}
		
		// now go through each head mechanic and see if they are the same
		for(int i = 0; i < superWP.size(); i++) {
			for(int j = superWP.size() - 1; j > i; j--) {
				if(superWP.get(i).get(0).equals(superWP.get(j).get(0))) {
					superWP.remove(j);
				}
			}
		}
		return superWP;
	}
	public ArrayList<String> scoreChangers() {
		ArrayList<String> instructions = new ArrayList<String>();
		
		for(Entity action : allActions) {
			if(action.getAttribute("ScoreChange") != null && Integer.parseInt(action.getAttribute("ScoreChange").getValue()) > 0) {
				// then this action results in a positive score change. Tell the player about it
				Mechanic mech = action.getMechanics().get(0);
				BFSNode fakeNode = new BFSNode(mech);
				ArrayList<Mechanic> siblings = new ArrayList<Mechanic>();
				mech = generalize(fakeNode, siblings).getMech();
				String instruct = "If the " + mech.getObject1().getFullName() + " and the " + mech.getObject2().getFullName() + " collide, then you will gain " + action.getAttribute("ScoreChange").getValue() + " point" + ((action.getAttribute("ScoreChange").getValue().equals("1") || action.getAttribute("ScoreChange").getValue().equals("-1")) ? "" : "s") + ".";
				if(!instructions.contains(instruct)) {
					instructions.add(instruct);
				}
			}
		}
		
		return instructions;
	}
	
	public ArrayList<Mechanic> pointsPath() {
		ArrayList<Mechanic> instructions = new ArrayList<Mechanic>();
		
		for(Entity action : allActions) {
			if(action.getAttribute("ScoreChange") != null && Integer.parseInt(action.getAttribute("ScoreChange").getValue()) > 0) {
				// then this action results in a positive score change. Tell the player about it
				Mechanic mech = action.getMechanics().get(0);
				instructions.add(mech);
			}
		}
		
		return instructions;
	}
	
	public ArrayList<String> lossConditions() {
		ArrayList<String> instructions = new ArrayList<String>();
		
		for(Entity action : this.lossTerminations) {
			
			Mechanic m = action.getMechanics().get(0);

			instructions.add(getInteractionString(m, 0));
//			System.out.println(e1.getFullName() + " " + c.getName() + " " + action.getName());
		}
		
		return instructions;
	}
	
	public String getInteractionString(Mechanic m, int type) {
		String instruct = "";
		if(type == 0) {
			Entity e1 = m.getObject1();
			Entity c = m.getCondition();
			if(c.getName().equals("SpriteCounter") || c.getName().equals("MultiSpriteCounter")) {
				// this is a sprite counter, so make it sound nice about the count being a certain amount
				if(c.getAttribute("limit").getValue().equals("0")) {
					instruct += "If there are no more ";
					instruct += c.getInputs().get(0).getFullName() + " sprites ";
					for(int i = 1; i < c.getInputs().size(); i++) {
						instruct += "or " + c.getInputs().get(i).getFullName() + " sprites ";
					}
					 instruct += "then you will lose.";
				}
				else {
					instruct += "If the total amount of ";
					instruct += c.getInputs().get(0).getFullName() + " sprites ";
					for(int i = 1; i < c.getInputs().size(); i++) {
						instruct += "and " + c.getInputs().get(i).getFullName() + " sprites ";
					}
					instruct += "is " + c.getAttribute("limit").getValue() +", then you will lose.";
//					instruct += e1.getFullName() + " sprites reaches " + c.getAttribute("limit").getValue() + ", then you will lose.";
				}
			} else{
				// this is a timeout condition
//				"You will " + (mech.getAction().getName().equals("Win") ? "win" : "lose") + " after " + mech.getAction().getAttribute("limit") + " seconds"
				instruct += "If time reaches " + c.getAttribute("limit").getValue() + " seconds, then you will lose.";
			}
		}
		else if(type == 1) {
			if(!m.getAction().getName().equals("Win") && !m.getAction().getName().equals("Lose")) {
				Entity e1 = m.getObject1();
				Entity e2 = m.getObject2();
				Entity a = m.getAction();
				Entity c = m.getCondition();
				
				if(a.getName().equals("KillSprite")) {
					instruct = "If " + e1.getFullName()+ " and " + e2.getFullName() + " collide, then the "
							+ a.getOutputs().get(0).getFullName() + " sprite will be ";
					
					if(a.getOutputs().get(0).getSubtype().equals("Door")){
						instruct += "opened.";
					} 
					else if(a.getOutputs().get(0).getSubtype().equals("Resource")) {
						instruct += "collected.";
					}
					else {
						instruct += "destroyed.";
					}
				} else if(a.getName().equals("TransformTo")) {
					instruct = "If " + e1.getFullName() + " and " + e2.getFullName() + " collide, then the "
							+ e1.getFullName() + " sprite will be transformed into " + a.getOutputs().get(0).getFullName() + "."; 
				} else if(a.getName().equals("Shoot")) {
					instruct = "If you press space, then " + e1.getFullName() + " will ";
					Entity stypeObject = searchObjects(e1.getAttribute("stype").getValue());
					String missileType = stypeObject.getSubtype();
					if(missileType.equals("Missile")) {
						instruct += "shoot a " + stypeObject.getName() + " (Missile)."; 
					} else if(missileType.equals("OrientedFlicker")) {
						instruct += "swing a " + stypeObject.getName() + "(Weapon).";
					}
					else {
						instruct += "release a " + stypeObject.getName() + "(Item).";
					}
				}
				else if(a.getName().equals("KillBoth")) {
					instruct = "If " + e1.getFullName() + " and " + e2.getFullName() + " collide, then both sprites will be destroyed.";
				}
				else if(a.getName().equals("CollectResource")) {
					instruct = "If " + e1.getFullName() + " and " + e2.getFullName() + " collide, then " +e2.getFullName() + " will be collected.";
				}
				else if(a.getName().equals("KillIfOtherHasMore")) {
					instruct = "If " + e1.getFullName() + " and " + e2.getFullName() + " collide and " + e2.getFullName() + "has more than " 
							+ a.getAttribute("limit").getValue() + " " + a.getAttribute("resource") + " then " + e1.getFullName() + " will be destroyed.";
				}
//				+ a.getName();
					
			}
			// must be a SpriteCounter termination
			else if(m.getObject1() != null){
				Entity e1 = m.getObject1();
				Entity a = m.getAction();
				Entity c = m.getCondition();
				if(e1.getSubtype().equals("Door")) {
					instruct = "If the " + e1.getFullName() + " sprite is opened, then you " + a.getName() + ".";
				} else{
				instruct = "If the " + e1.getFullName() + " sprite reaches " + m.getCondition().getAttribute("limit").getValue() + " then you " + a.getName().toLowerCase() + ".";
			
				}
			}
		}
		else if(type == 2) {
//			BFSNode fakeNode = new BFSNode(m);
//			ArrayList<Mechanic> siblings = new ArrayList<Mechanic>();
//			m = generalize(fakeNode, siblings).getMech();
			if(!m.getAction().getName().equals("Win") && !m.getAction().getName().equals("Lose")) {
				instruct = "If the " + m.getObject1().getFullName() + " and the " + m.getObject2().getFullName() + " collide, then you will gain " + m.getAction().getAttribute("ScoreChange").getValue() + " point" + ((m.getAction().getAttribute("ScoreChange").getValue().equals("1") || m.getAction().getAttribute("ScoreChange").getValue().equals("-1")) ? "" : "s") + ".";
			}
			else {
				Entity c = m.getCondition();
				if(c.getName().equals("SpriteCounter") || c.getName().equals("MultiSpriteCounter")) {
					// this is a sprite counter, so make it sound nice about the count being a certain amount
					if(c.getAttribute("limit").getValue().equals("0")) {
						instruct += "If there are no more ";
						instruct += c.getInputs().get(0).getFullName() + " sprites ";
						for(int i = 1; i < c.getInputs().size(); i++) {
							instruct += "or " + c.getInputs().get(i).getFullName() + " sprites ";
						}
						 instruct += "then you will " + m.getAction().getName().toLowerCase() + ".";
					}
					else {
						instruct += "If the total amount of ";
						instruct += c.getInputs().get(0).getFullName() + " sprites ";
						for(int i = 1; i < c.getInputs().size(); i++) {
							instruct += "and " + c.getInputs().get(i).getFullName() + " sprites ";
						}
						instruct += "is " + c.getAttribute("limit").getValue() +", then you will " + m.getAction().getName().toLowerCase() + ".";
//						instruct += e1.getFullName() + " sprites reaches " + c.getAttribute("limit").getValue() + ", then you will lose.";
					}
				} else{
					// this is a timeout condition
//					"You will " + (mech.getAction().getName().equals("Win") ? "win" : "lose") + " after " + mech.getAction().getAttribute("limit") + " seconds"
					instruct += "If time reaches " + c.getAttribute("limit").getValue() + " seconds, then you will " + m.getAction().getName().toLowerCase() + ".";
				}
			}
		}
		return instruct;
	}
	
	public ArrayList<Mechanic> losePath() {
		ArrayList<Mechanic> instructions = new ArrayList<Mechanic>();
		for(Entity action : this.lossTerminations) {
			Mechanic m = action.getMechanics().get(0);
			Entity c = m.getCondition();
			for(int i = 0; i < c.getInputs().size(); i++) {
				Entity e = c.getInputs().get(i);
				ArrayList<Mechanic> possibleAffectants = e.getMechanics();
				for(Mechanic aff : possibleAffectants) {
					if(aff.getType().equals("KillSprite") && c.getAttribute("limit").getValue().equals("0") && aff.getAction().getOutputs().get(0).equals(e)) {
						instructions.add(aff);
					}
					else if((aff.getType().equals("TransformTo") || aff.getType().equals("Spawn")) && !c.getAttribute("limit").getValue().equals("0")) {
						instructions.add(aff);
					}
				}

			}
			instructions.add(m);
		}
		return instructions;
	}
	/** HELPER FUNCTIONS **/
	
	
	
	public ArrayList<BFSNode> createExtraInfo(ArrayList<ArrayList<BFSNode>> possiblePaths, ArrayList<BFSNode> longestPath) {
		ArrayList<BFSNode> extraInfo = new ArrayList<BFSNode>();
		for(ArrayList<BFSNode> path : possiblePaths) {
			// ignore longest path
			if(!path.equals(longestPath)) {
				for(BFSNode pathNode : path) {
					boolean contains = false;
					for(BFSNode lPathNode : longestPath) {
						if(lPathNode.getMech().equals(pathNode.getMech())) {
							contains = true;
							break;
						}
						
					}
					if(!contains && !extraInfo.contains(pathNode)) {
						extraInfo.add(pathNode);
					}
				}
			}
		}
		return extraInfo;
	}
	public float heuristic() {
		return 0.0f;
	}
	/**	 
	 * Searches the allObjects list for the given entity name
	 * @param name the name of the entity being sought
	 * @return the Entity, null if none is found
	 */
	public Entity searchObjects(String name) {
		for(Entity e : allObjects) {
			if(e.getName().equals(name)) {
				return e;
			}
		}
		return null;
	}
	
	public Mechanic searchMechanics(Entity action) {
		for(Mechanic r : allRules) {
			if(r.getAction().equals(action)) {
				return r;
			}
		}
		return null;
	}
	/**
	 * Adds the entity to the avatarEntities list if this object is an avatar sprite
	 * @param current the sprite data 
	 * @param objectSprite the entity
	 */
	public void avatarCheck(SpriteData current, Entity objectSprite) {
		if(current.isAvatar) {
			if(verbose)
				System.out.println("Avatar found, adding to avatarEntities...");
			avatarEntities.add(objectSprite);
		}
		
	}
	
	/**
	 * returns all the avatar entities
	 * @return
	 */
	public ArrayList<Entity> getAvatarEntites() {
		return avatarEntities;
	}
}
