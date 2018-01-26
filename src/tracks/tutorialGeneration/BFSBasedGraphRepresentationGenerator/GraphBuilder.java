package tracks.tutorialGeneration.BFSBasedGraphRepresentationGenerator;

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
			// Classify using the classifyfunction, add the mechanic necessary to the graph
			classifySpriteType(current);
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
	public void classifySpriteType(SpriteData current) {
		if(verbose)
			System.out.println("Creating entity to represent : " + current.name);
		Entity objectSprite = new Entity(current.name, "Object", current.type);
		// add this entity to the lists
		allEntities.add(objectSprite);
		allObjects.add(objectSprite);
		
		// check if this is an avatar
		avatarCheck(current, objectSprite); 

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
	
	/**
	 * Creates a single timeout object entity for the timeout conditions
	 */
	public void createTimeOutObject() {
		if(verbose)
			System.out.println("Creating entity to represent : Timeout");
		Entity objectSprite = new Entity("Timeout", "Object", "n/a");
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
			
			// create the output for the action
			createInteractionActionOutput(action, one, two, interaction);
			// create attributes for the condition
//			createInteractionConditionAttributes(condition, one, two, interaction);
			//create a mechanic
			Mechanic mech = createMechanic(action, one, two, condition);
			one.addMechanic(mech);
			two.addMechanic(mech);
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
	public void createInteractionActionOutput(Entity action, Entity one, Entity two, InteractionData interaction) {
		// tracking for what kind of rule this is
		String name = action.getName();
		
		if(name.equals("KillSprite") || name.equals("StepBack") || name.equals("KillIfHasMore") || name.equals("KillIfHasLess")
				|| name.equals("KillIfFromAbove") || name.equals("KillIfOtherHasMore") || name.equals("CloneSprite")
				|| name.equals("AddHealthPoints") || name.equals("AddHealthPointsToMax") || name.equals("SubtractHealthPoints")) {
			action.addOutput(one);
		} else if(name.equals("KillAll") || name.equals("SpawnBehind") || name.equals("TransformTo") || name.equals("IncreaseSpeedToAll")
				|| name.equals("DecreaseSpeedToAll") || name.equals("SetSpeedForAll")) {
			action.addOutput(searchObjects(interaction.sprites.get(0)));
		
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
					mech.addOutput(spriteMech);
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
	
	public ArrayList<String> traceUserInteractionChain(Entity avatar, Entity win) {
		ArrayList<String> instructions = new ArrayList<String>();
		BFSTree tree = new BFSTree(this);
		tree.buildTree(avatar, win);
		// trace all paths from avatar to win
		ArrayList<ArrayList<BFSNode>> validPaths = tree.getValidPaths();
		for(int i = 0; i < validPaths.size(); i++) {
			for(BFSNode node : validPaths.get(i)) {
				Mechanic m = node.getMech();
				String instruct = "";
				if(m.getAction().getName() != "Win" && m.getAction().getName() != "Lose") {
					Entity e1 = m.getObject1();
					Entity e2 = m.getObject2();
					Entity a = m.getAction();
					Entity c = m.getCondition();
					instruct = "If " + e1.getName() + " and " + e2.getName() + " collide " + a.getName();
				}
				// must be a SpriteCounter termination
				else if(m.getObject1() != null){
					Entity e1 = m.getObject1();
					Entity a = m.getAction();
					Entity c = m.getCondition();
					instruct = "If the " + e1.getName() + " sprite reaches " + m.getCondition().getAttribute("limit").getValue() + " then you " + a.getName();
				}
//				// must be a timeout condition
//				else {
//					Entity a = m.getAction();
//					Entity c = m.getCondition();
//					instruct = "If time reaches " + m.getCondition().getAttribute("limit").getValue() + " then you " + a.getName();
//				}
				instructions.add(instruct);
			}
		}
		// must be a timeout condition
		if(validPaths.size() == 0) {
			Mechanic winMech = win.getMechanics().get(0);
			Entity a = winMech.getAction();
			Entity c = winMech.getCondition();
			String instruct = "If time reaches " + winMech.getCondition().getAttribute("limit").getValue() + " then you " + a.getName();
			instructions.add(instruct);
		}
		return instructions;
	}
	/** HELPER FUNCTIONS **/
	
	
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
