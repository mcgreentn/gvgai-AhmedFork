package tracks.tutorialGeneration.ITSetParserGenerator;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;

import core.game.GameDescription;
import core.game.GameDescription.SpriteData;
import core.game.SLDescription;
import core.generator.AbstractTutorialGenerator;
import tools.ElapsedCpuTimer;
import tools.GameAnalyzer;
import tools.LevelAnalyzer;

public class TutorialGenerator extends AbstractTutorialGenerator{

	private LevelAnalyzer la;
	private GameAnalyzer ga;
	private GameDescription game;
	public TutorialGenerator(SLDescription sl, GameDescription game, ElapsedCpuTimer time) {
		la = new LevelAnalyzer(sl);
		ga = new GameAnalyzer(game);
		this.game = game;
	}
	
	private String figureOutMovement(GameDescription game) {
		// get avatar sprite data
		ArrayList<SpriteData> avatar = game.getAvatar();
		// figure out avatar type for movement data
		String avatarType = avatar.get(0).type;
		
		// make the movementTutorial string
		String movementTutorial = "You are the " + avatarType + ". ";
		if(avatarType.equals("MovementAvatar")){
			movementTutorial += "Use the four arrow keys to move.";
		} else if(avatarType.equals("HorizontalAvatar")) {
			movementTutorial += "Use the left and right arrow keys to move.";
		} else if(avatarType.equals("VerticalAvatar")) {
			movementTutorial += "Use the up and down arrow keys to move.";
		} else if(avatarType.equals("OngoingAvatar")) {
			movementTutorial += "Use the arrow keys to change direction.";
		} else if(avatarType.equals("OngoingTurningAvatar")) {
			// TODO : reword this better?
			movementTutorial += "Use the arrow keys to change direction. You cannot do 180 degree turns!";
		} else if(avatarType.equals("OngoingShootAvatar")) {
			movementTutorial += "Use the arrow keys to change direction. Use Space to shoot.";
		} else if(avatarType.equals("MissileAvatar")) {
			// TODO : Figure out what this means
			movementTutorial += "Unsure what this means...";
		} else if(avatarType.equals("OrientedAvatar")) {
			movementTutorial += "Use the arrow keys to turn and move.";
		} else if(avatarType.equals("ShootAvatar")) {
			movementTutorial += "Use the arrow keys to turn and move. Use Space to shoot in the direction you are facing.";
		} else if(avatarType.equals("FlakAvatar")) {
			movementTutorial += "Use the left and right keys to move. Use Space to shoot.";
		} else {
			movementTutorial += "The system is unsure of what this means.";
		}
		return movementTutorial;
	}
	
	/**
	 * Searches GameDescription.getAllSpriteData() for the given spritename
	 * @param spriteName the sprite you are looking for
	 * @return the sprite, if it exists
	 */
	private SpriteData searchForSprite(String spriteName) {
		// grab all sprites in the game
		ArrayList<SpriteData> allSpriteData = game.getAllSpriteData();
		// iterate over this list, return the sprite with the same name
		for(SpriteData me : allSpriteData) {
			if(me.name.equals(spriteName)) {
				return me;
			}
		}
		return null;
	}
	
	private String testChain(Graph graph) {
		String tutorialString = "The goal of the game is to...";
		Node terminalNode = graph.searchNodeList("Terminator");
		
		for(Mechanic mechie : terminalNode.getInteractionList()) {
			// if not a timeout or stop counter, then the games work pretty similar to each other
			if(!mechie.getMechanic().equals("TimeOut") && !mechie.getMechanic().equals("StopCounter")) {
				// build a chain of mechanics working backward from this one until...something..?
				tutorialString += (mechie.getWin()) ? "\nGet " : "\nNot Get ";
				tutorialString += mechie.getMechanic() + " " + mechie.getObject().getName() + " to " + mechie.getLimit();
				// start chaining
				// give info about the current node
				// find interactions with surrounding nodes
			}
		}
		
		// start at avatar
		Node avatarNode = graph.searchNodeList(game.getAvatar().get(0));

		BFSTree tree = new BFSTree(graph);
		ArrayList<Mechanic> termMechanics = graph.getTerminalMechanics();
		for(Mechanic terminalMechanic : termMechanics) {
			BFSNode leaf = tree.buildTreePriority(avatarNode, terminalMechanic);
			ArrayList<BFSNode> chain = tree.traceLeaf(leaf);
			tutorialString += "\nStart";
			for(BFSNode n : chain) {
				Mechanic m = n.getNode();
				if(n.getParent() != null)
					tutorialString += "\nMechanic: " + m.getSubject().getName() + " " + m.getMechanic() + " " + m.getObject().getName();
			}

		}
//		ArrayList<ArrayList<BFSNode>> chains = new ArrayList<ArrayList<BFSNode>>();
//		for(BFSNode term : tree.getTerminalLeaves()) {
//			ArrayList<BFSNode> chain = tree.traceLeaf(term);
//			chains.add(chain);			
//		}
		// one by one, for every terminal leaf, check for which terminal condition it is
		// and find the shortest path for each one back to the avatar
//		ArrayList<Mechanic> termMechanics = graph.getTerminalMechanics();
//		HashMap dict = new HashMap<Mechanic, ArrayList<BFSNode>>();
//		
//		for(Mechanic terminalMechanic : termMechanics) {
//			// make a minimum length path chain
//			ArrayList<BFSNode> chain = new ArrayList<BFSNode>();
//			int minSize = Integer.MAX_VALUE;
//			// compare mechanics to the terminalNode mechanics
//			for(BFSNode term : tree.getTerminalLeaves()) {
//				if(term.getNode().equals(terminalMechanic)) {
//					ArrayList<BFSNode> temp = tree.traceLeaf(term);
//					if(temp.size() < minSize) {
//						minSize = temp.size();
//						chain = temp;
//					}
//				}
// 			}
//			dict.put(terminalMechanic, chain);	
//		}
		
//		for(Mechanic terminalMechanic : termMechanics) {
//			ArrayList<BFSNode> chain = (ArrayList<BFSNode>) dict.get(terminalMechanic);
//			tutorialString += "\nStart";
//			for(BFSNode n : chain) {
//				Mechanic m = n.getNode();
//				if(n.getParent() != null)
//					tutorialString += "\nMechanic: " + m.getSubject().getName() + " " + m.getMechanic() + " " + m.getObject().getName();
//			}
//		}
		
//		for(BFSNode term : tree.getTerminalLeaves()) {
//			ArrayList<BFSNode> temp = tree.traceLeaf(term);
//			tutorialString += "\n**Possible Chain";
//			for(BFSNode n : temp) {
//				Mechanic m = n.getNode();
//				if(n.getParent() != null)
//					tutorialString += "\nMechanic: " + m.getSubject().getName() + " " + m.getMechanic() + " " + m.getObject().getName();
//			}
//			
//		}	
		return tutorialString;
	}
	
	@Override
	public String[] generateTutorial(GameDescription game, SLDescription sl, ElapsedCpuTimer elapsedTimer) {
		String[] generatedTutorial = new String[0];
		ArrayList<String> generatedTutorialList = new ArrayList<String>();
		
		// figure out movement
		generatedTutorialList.add(figureOutMovement(game));
		
		// Build Graph of interactions
		Graph interactionGraph = new Graph(game, sl, ga, la);
		interactionGraph.buildGraph();
		
		// build a test chain
		generatedTutorialList.add(testChain(interactionGraph));
		// Play game, figure out victory 
//		for(String s : generatedTutorialList) {
//			System.out.println(s);
//		}
		return generatedTutorialList.toArray(generatedTutorial);
	}
}
