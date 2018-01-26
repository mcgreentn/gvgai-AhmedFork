package tracks.tutorialGeneration.ITSetParserGenerator;

import java.util.ArrayList;

import core.game.GameDescription;
import core.game.GameDescription.InteractionData;
import core.game.GameDescription.SpriteData;
import core.game.GameDescription.TerminationData;
import core.game.SLDescription;
import tools.GameAnalyzer;
import tools.LevelAnalyzer;

public class Graph {
	private Node root;
	private GameDescription gd;
	private SLDescription sl;
	private GameAnalyzer ga;
	private LevelAnalyzer la;
	
	public ArrayList<Node> nodeList;
	private boolean verbose = true;
	
	public Graph(GameDescription gd, SLDescription sl, GameAnalyzer ga, LevelAnalyzer la) {
		// init
		nodeList = new ArrayList<Node>();
		this.gd = gd;
		this.sl = sl;
		this.ga = ga;
		this.la = la;
	}
	public Node getRoot() {
		return root;
	}
	public void buildGraph() {
		if(verbose) 
			System.out.println("Building Mechanic graph...");
		
		parseInteractionSet();
		
		parseSpriteSet();
		
		parseTerminationSet();
	}
	
	/**
	 * Parses the termination set to create win loss nodes in the graph
	 */
	private void parseTerminationSet() {
		if(verbose)
			System.out.println("Parsing termination set...");
		ArrayList<TerminationData> terminationSet = gd.getTerminationConditions();
		for(TerminationData tData : terminationSet) {
			String type = tData.type;
			if(verbose)
				System.out.println("Found new termination condition: " + type);
			if(tData.sprites.size() > 0) {
				String stypeName = tData.sprites.get(0);
				ArrayList<String> stypeNames = new ArrayList<String>();
				for(String s : tData.sprites) {
					if(!s.equals(stypeName)) {
						stypeNames.add(s);
					}
				}
				boolean win = tData.win.equals("True") ? true : false;
				makeNewMechanic("Terminator", stypeName, type, tData.limit, win);
				Node terminalNode = searchNodeList(stypeName);
				terminalNode.setIsTerminal(true);
				for(String s : stypeNames) {
					makeNewMechanic("Terminator", s, type, tData.limit, win);
					terminalNode = searchNodeList(s);
					terminalNode.setIsTerminal(true);
				}
			} else {
				boolean win = tData.win.equals("True") ? true : false;

				if(verbose)
					System.out.println("TimeOut found. Creating Timeout node...");
				makeNewMechanic("Terminator", "avatar", "Timeout", tData.limit, win);
				Node terminalNode = searchNodeList("avatar");
				terminalNode.setIsTerminal(true);
			}
		}
	}
	
	/**
	 * Parses the sprite set for sprite data to create mechanics between nodes not contained in the interaction set
	 */
	private void parseSpriteSet() {
		if(verbose)
			System.out.println("Parsing sprite set...");
		// get all sprite data
		ArrayList<SpriteData> allSpriteData = gd.getAllSpriteData();
		
		for(SpriteData current : allSpriteData) {
			if(verbose)
				System.out.println(current.name + " check");
			// figure out what this sprite type is
			String type = current.type;
			// Classify using the function, add the mechanic necessary to the graph
			classifySpriteType(current, type);

		}
	}
	/**
	 * Parses the interaction set and creates nodes and mechanics between objects
	 */
	private void parseInteractionSet() {
		if(verbose)
			System.out.println("Parsing interaction set...");
		// Start with finding all the sprites and making nodes
		ArrayList<SpriteData> allSpriteData = gd.getAllSpriteData();
		for(SpriteData sprite : allSpriteData) {
			if(verbose)
				System.out.println("Analyzing: " + sprite.name);
			// check to see if this node exists already
			Node spriteNode = searchNodeList(sprite);
			// if it doesn't, make a new node
			if(spriteNode == null) {
				if(verbose)
					System.out.println(sprite.name + " Node DNE. Initializing new node.");
				spriteNode = makeNewNode(sprite);
			}
			// go through every other sprite
			for(SpriteData sprite2 : allSpriteData) {
				// make sure not the same sprite, that makes no sense..
				if(!sprite.equals(sprite2)) {
					if(verbose)
						System.out.println(sprite.name + " to " + sprite2.name);
					// get all interaction data between these two sprites
					ArrayList<InteractionData> intDataList = gd.getInteraction(sprite.name, sprite2.name);
					if(intDataList.size() != 0) {
						if(verbose)
							System.out.println("Interactions found! Searching for node..");
						// search for sprite2's node
						Node sprite2Node = searchNodeList(sprite2);
						if(sprite2Node == null) {
							if(verbose)
								System.out.println(sprite2.name + " Node DNE. Initializing new node.");
							// make a new node for sprite2
							sprite2Node = makeNewNode(sprite2);
						}
						for(InteractionData intData : intDataList) {
							if(verbose)
								System.out.println("Initializing new interaction: " + sprite2Node.getName() 
									+ " " + intData.type +  " "+ spriteNode.getName() + " :: "  + " ScoreChange=" + intData.scoreChange);
							// reverse mechanics for TransformTo and Spawn (they are weird
							Mechanic newMech;
							if(!intData.type.equals("TransformTo") && !intData.type.equals("Spawn")) {
								// if this breaks, redo this
//								newMech = sprite2Node.addMechanic(spriteNode, intData.type, intData.scoreChange);
								newMech = sprite2Node.addMechanic(spriteNode, intData.type, intData.scoreChange);
							} else {
//								newMech = spriteNode.addMechanic(sprite2Node, intData.type, intData.scoreChange);
								newMech = spriteNode.addMechanic(sprite2Node, "OffshootMechanic", intData.scoreChange);
							}
							
							// add an offshootMechanic for TransformTo or Spawn
							for(String stypeName : intData.sprites) {
								if(intData.type.equals("Spawn") || intData.type.equals("TransformTo")) {
									SpriteData stype = searchForSprite(stypeName);
									Node sprite3Node = searchNodeList(stype);
									if(sprite3Node == null) {
										if(verbose) 
											System.out.println(stype.name + " Node DNE. Initializing new node.");
										sprite3Node = makeNewNode(stype);
									}
									if(verbose)
										System.out.println("Spawn or TransformTo type detected. Creating OffshootMechanic: " + sprite3Node.getName()
												+ " TransformTo " + spriteNode.getName());
									Mechanic m = new Mechanic(spriteNode, sprite3Node, intData.type);
									m.setParentMechanic(newMech);
									newMech.setOffshootMechanic(m);
								}
							}
						}
					}

				}
			}
			// for every sprite, add any EOS information
			ArrayList<InteractionData> intDataList = gd.getInteraction(sprite.name, "EOS");
			if(intDataList.size() != 0) {
				if(verbose)
					System.out.println("EOS Interactions found! Searching for node..");
				Node sprite2Node = searchNodeList("EOS");
				if(sprite2Node == null) {
					if(verbose)
						System.out.println("EOS Node DNE. Initializing new node.");
					sprite2Node = makeNewNode("EOS");
				}
				for(InteractionData intData : intDataList) {
					if(verbose)
						System.out.println("Initializing new mechanic: " + sprite2Node.getName() 
							+  " TurnAround " + spriteNode.getName() + " ScoreChange=" + intData.scoreChange);
					sprite2Node.addMechanic(spriteNode, intData.type, intData.scoreChange);
				}
			}
		}
	}
	
	public Node searchNodeList(SpriteData sprite) {
		
		for(Node me : nodeList) {
			if(me.getName().equals(sprite.name)) {
				return me;
			}
		}
		return null;
	}
	
	public Node searchNodeList(String spriteName) {
		for(Node me : nodeList) {
			if(me.getName().equals(spriteName)) {
				return me;
			}
		}
		return null;
	}
	
	/**
	 * Searches GameDescription.getAllSpriteData() for the given spritename
	 * @param spriteName the sprite you are looking for
	 * @return the sprite, if it exists
	 */
	private SpriteData searchForSprite(String spriteName) {
		// grab all sprites in the game
		ArrayList<SpriteData> allSpriteData = gd.getAllSpriteData();
		// iterate over this list, return the sprite with the same name
		for(SpriteData me : allSpriteData) {
			if(me.name.equals(spriteName)) {
				return me;
			}
		}
		return null;
	}
	private Node makeNewNode(SpriteData sprite){
		Node spriteNode = new Node(sprite.name);
		nodeList.add(spriteNode);
		return spriteNode;
	}
	
	private Node makeNewNode(String spriteName) {
		Node spriteNode = new Node(spriteName);
		nodeList.add(spriteNode);
		return spriteNode;
	}
	
	/**
	 * Classifies the sprite type and inserts new mechanics in the graph if it is necessary
	 * @param current the current sprite subject
	 * @param type the type of the current sprite
	 */
	private void classifySpriteType(SpriteData current, String type) {
		if(verbose)
			System.out.println("Classifying " + current.name +  " as a \"" + type + "\". Looking for stypes...");
		if(current.sprites.size() > 0) {
			// make arraylist of stypes of all the same type
			ArrayList<String> stypes1 = new ArrayList<String>();
			String stypeName = current.sprites.get(0);
			SpriteData stype = searchForSprite(stypeName);

			for(String s : current.sprites) {
				SpriteData typ = searchForSprite(s);
				if(typ.type.equals(stype.type)) {
					stypes1.add(s);
				}
			}

			// corner case for AlternateChaser and RandomAltChaser
			String stypeName2 = "";
			SpriteData stype2 = null;
			ArrayList<String> stypes2 = new ArrayList<String>();
			if(type.equals("AlternateChaser") || type.equals("RandomPathAltChaser")) {
				for(String s : current.sprites) {
					SpriteData typ = searchForSprite(s);
					if(!typ.type.equals(stype.type)){
						stypes2.add(s);
					}
				}
				stypeName2 = current.sprites.get(1);
				stype2 = searchForSprite(stypeName2);
			}
			if(stype != null) {
				if(verbose)
					System.out.println(stypeName + " is an stype. Searching for nodes...");
				// create a mechanic between the current node and the stype
				String mechanic = "";
				if(type.equals("Bomber") || type.equals("RandomBomber") || type.equals("BomberRandomMissile")
						|| type.equals("ShootAvatar") || type.equals("FlakAvatar"))
					mechanic = "Shoot";
				else if(type.equals("Chaser") || type.equals("AlternateChaser") || type.equals("RandomPathAltChaser"))
					mechanic = "Chase";
				else if(type.equals("Fleeing")) 
					mechanic = "Flee";
				else if(type.equals("SpawnPoint") || type.equals("Spreader"))
					mechanic = "Spawn";
				else if(type.equals("Portal"))
					mechanic = "Portal";
				for(String s : stypes1) {
					makeNewMechanic(current.name, s, mechanic);
				}
			}
		
			if(stype2 != null) {
				if(verbose)
					System.out.println(stypeName2 + " is an stype. Searching for nodes...");
				String mechanic = "";
				if(type.equals("AlternateChaser") || type.equals("RandomPathAltChaser")) {
					mechanic = "Flee";
				}
				for(String s : stypes2) {
					makeNewMechanic(current.name, s, mechanic);
				}
			}
		}
	}
	
	/**
	 * create a new mechanic link of the mechanic type between the subject and the object
	 * @param subject the subject of the new mechanic
	 * @param object the object of the new mechanic
	 * @param mechanicType the type of the new mechanic
	 */
	private void makeNewMechanic(String subject, String object, String mechanicType) {
		Node currentNode = searchNodeList(subject);
		if(currentNode == null) {
			if(verbose)
				System.out.println(subject + " Node DNE. Initializing  new node...");
			currentNode = makeNewNode(subject);
		}
		Node dependentNode = searchNodeList(object);
		if(dependentNode == null) {
			if(verbose)
				System.out.println(object + " Node DNE. Initializing new node...");
			dependentNode = makeNewNode(object);
		}
		if(verbose) 
			System.out.println("Creating new mechanic: " + currentNode.getName() + " " + mechanicType + " " + dependentNode.getName());
		currentNode.addMechanic(dependentNode, mechanicType);
	}
	
	private void makeNewMechanic(String subject, String object, String mechanicType, int limit, boolean win) {
		Node currentNode = searchNodeList(subject);
		if(currentNode == null) {
			if(verbose)
				System.out.println(subject + " Node DNE. Initializing  new node...");
			currentNode = makeNewNode(subject);
		}
		Node dependentNode = searchNodeList(object);
		if(dependentNode == null) {
			if(verbose)
				System.out.println(object + " Node DNE. Initializing new node...");
			dependentNode = makeNewNode(object);
		}
		if(verbose) 
			System.out.println("Creating new mechanic: " + currentNode.getName() + " " + mechanicType + " " + dependentNode.getName() + ", limit=" + limit + ", win=" + win);
		currentNode.addMechanic(dependentNode, mechanicType, limit, win);
	}
	private void makeNewMechanic(String subject, String object, String mechanicType, String scoreChange) {
		Node currentNode = searchNodeList(subject);
		if(currentNode == null) {
			if(verbose)
				System.out.println(subject + " Node DNE. Initializing  new node...");
			currentNode = makeNewNode(subject);
		}
		Node dependentNode = searchNodeList(object);
		if(dependentNode == null) {
			if(verbose)
				System.out.println(object + " Node DNE. Initializing new node...");
			dependentNode = makeNewNode(object);
		}
		if(verbose) 
			System.out.println("Creating new mechanic: " + currentNode.getName() + " " + mechanicType + " " + dependentNode.getName() + ", ScoreChange=" + scoreChange);
		currentNode.addMechanic(dependentNode, mechanicType, scoreChange);
	}
	
	public ArrayList<Mechanic> searchTerminalMechanics(String object) {
		ArrayList<Mechanic> returnedMechanics = new ArrayList<Mechanic>();
		for(Mechanic terminal : searchNodeList("Terminator").getInteractionList()) {
			if(terminal.getObject().getName().equals(object)) {
				returnedMechanics.add(terminal);
			}
		}
		return returnedMechanics;
	}
	
	public ArrayList<Mechanic> getTerminalMechanics() {
		ArrayList<Mechanic> returnedMechanics = new ArrayList<Mechanic>();
		for(Mechanic terminal : searchNodeList("Terminator").getInteractionList()) {
			returnedMechanics.add(terminal);
			
		}
		return returnedMechanics;
	}
	
	public ArrayList<Mechanic> getWinConditions() {
		ArrayList<Mechanic> returnedMechanics = new ArrayList<Mechanic>();
		for(Mechanic terminal : searchNodeList("Terminator").getInteractionList()) {
			if(terminal.getWin()) {
				returnedMechanics.add(terminal);
			}
		}
		return returnedMechanics;
	}
	
	public ArrayList<Mechanic> getLoseConditions() {
		ArrayList<Mechanic> returnedMechanics = new ArrayList<Mechanic>();
		for(Mechanic terminal : searchNodeList("Terminator").getInteractionList()) {
			if(!terminal.getWin()) {
				returnedMechanics.add(terminal);
			}
		}
		return returnedMechanics;
	}
}
