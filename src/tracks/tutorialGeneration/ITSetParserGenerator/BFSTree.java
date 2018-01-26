package tracks.tutorialGeneration.ITSetParserGenerator;

import java.util.ArrayList;
import java.util.Arrays;

public class BFSTree {

	private BFSNode root;
	private Graph mechanicGraph;
	
	private ArrayList<BFSNode> terminalLeaves;
	
	private ArrayList<String> spriteCountMechanics;

	public BFSTree(Graph g) {
		root = new BFSNode(null);
		mechanicGraph = g;
		terminalLeaves = new ArrayList<BFSNode>();
		spriteCountMechanics = new ArrayList<>(Arrays.asList("KillSprite", "KillAll", "KillIfHasMore", "KillIfHasLess", "KillIfOtherHasMore", "SpawnBehind", "SpawnIfHasMore",
			"SpawnIfHasLess", "CloneSprite"));
//		"TransformTo"));
	}
	
	public BFSNode buildTree(Node start, Mechanic search) {
		Mechanic empty = new Mechanic(null, null, "Start");
		root.setNode(empty);
		// build the tree man
		int count = 0;
		String tutorialString = "";
		ArrayList<BFSNode> queue = new ArrayList<BFSNode>();
		for(Mechanic mechie : start.getInteractionList()) {
			mechie.setVisited(true);
			BFSNode child = new BFSNode(mechie);
			root.addChild(child);
			queue.add(child);
			child.addParent(root);
			child.setParent(root);
			
			// special case for offshoot mechanics
			if(mechie.getMechanic().equals("Spawn") || mechie.getMechanic().equals("TransformTo")) {
//				System.out.println("OffshootFound");

				Mechanic offshoot = mechie.getOffshootMechanic();
				offshoot.setVisited(true);
				BFSNode child2 = new BFSNode(offshoot);
				root.addChild(child2);
				queue.add(child2);
				child2.addParent(root);
				child2.setParent(root);
			}
//			ArrayList<Mechanic> chain = new ArrayList<Mechanic>();
		}
		
		while(queue.size() > 0) {
			BFSNode next = queue.remove(0);
			
			for(Mechanic mechie : next.getNode().getObject().getInteractionList()) {
				if(next.searchParents(mechie) == null && next.getParents().size() < 7) {
					BFSNode child = new BFSNode(mechie);
					next.addChild(child);
					queue.add(child);
					child.addParent(next);
					child.setParent(next);
					// special case for offshoot mechanics
					if(mechie.getMechanic().equals("Spawn") || mechie.getMechanic().equals("TransformTo")) {
//						System.out.println("OffshootFound");
						Mechanic offshoot = mechie.getOffshootMechanic();
						offshoot.setVisited(true);
						BFSNode child2 = new BFSNode(offshoot);
						root.addChild(child2);
						queue.add(child2);
						child2.addParent(root);
						child2.setParent(root);
					}
				}
			}
			
			if(next.getNode().getObject().getIsTerminal()) {
				ArrayList<Mechanic> terminals = mechanicGraph.searchTerminalMechanics(next.getNode().getObject().getName());
				for(Mechanic term : terminals) {
					BFSNode leaf = new BFSNode(term);
					next.addChild(leaf);
					terminalLeaves.add(leaf);
					leaf.addParent(next);
					leaf.setParent(next);
					if(term.equals(search)) {
						return leaf;
					}
				}

			}
		}
		
		return null;
	}
	public BFSNode buildTreePriority(Node start, Mechanic search) {
		Mechanic empty = new Mechanic(null, null, "Start");
		root.setNode(empty);
		// build the tree man
		int count = 0;
		String tutorialString = "";
		ArrayList<BFSNode> queue = new ArrayList<BFSNode>();
		for(Mechanic mechie : start.getInteractionList()) {
			mechie.setVisited(true);
			BFSNode child = new BFSNode(mechie);
			root.addChild(child);
			queue.add(child);
			child.addParent(root);
			child.setParent(root);
			
			// special case for offshoot mechanics 
//			Redo this if it breaks
//			if(mechie.getMechanic().equals("Spawn") || mechie.getMechanic().equals("TransformTo")) {
			if(mechie.getMechanic().equals("OffshootMechanic")) {
//				System.out.println("OffshootFound");

				Mechanic offshoot = mechie.getOffshootMechanic();
				offshoot.setVisited(true);
				BFSNode child2 = new BFSNode(offshoot);
				root.addChild(child2);
				queue.add(child2);
				child2.addParent(root);
				child2.setParent(root);
			}
//			ArrayList<Mechanic> chain = new ArrayList<Mechanic>();
		}
		
		while(queue.size() > 0) {
			BFSNode next = queue.remove(0);
			
			for(Mechanic mechie : next.getNode().getObject().getInteractionList()) {
				if(next.searchParents(mechie) == null && next.getParents().size() < 7) {
					BFSNode child = new BFSNode(mechie);
					next.addChild(child);
					queue.add(child);
					child.addParent(next);
					child.setParent(next);
					// special case for offshoot mechanics
//					if(mechie.getMechanic().equals("Spawn") || mechie.getMechanic().equals("TransformTo")) {
					if(mechie.getMechanic().equals("OffshootMechanic")) {

//						System.out.println("OffshootFound");
						Mechanic offshoot = mechie.getOffshootMechanic();
						offshoot.setVisited(true);
						BFSNode child2 = new BFSNode(offshoot);
						root.addChild(child2);
						queue.add(child2);
						child2.addParent(root);
						child2.setParent(root);
					}
				}
			}
			
			if(next.getNode().getObject().getIsTerminal()) {
				ArrayList<Mechanic> terminals = mechanicGraph.searchTerminalMechanics(next.getNode().getObject().getName());
				for(Mechanic term : terminals) {
					BFSNode leaf = new BFSNode(term);
					next.addChild(leaf);
					terminalLeaves.add(leaf);
					leaf.addParent(next);
					leaf.setParent(next);
					if(term.equals(search)) {
						if(search.getMechanic().equals("SpriteCounter") || search.getMechanic().equals("MultiSpriteCounter")) {
							if(spriteCountMechanics.contains(next.getNode().getMechanic())) {
								return leaf;
							}
						} else {
						return leaf;
						}
					}
				}

			}
		}
		return null;
	}
	public ArrayList<BFSNode> getTerminalLeaves() {
		return terminalLeaves;
	}
	
	public ArrayList<BFSNode> traceLeaf(BFSNode terminal) {
		ArrayList<BFSNode> chain = new ArrayList<BFSNode>();
		chain.add(terminal);
		while(terminal.getParent() != null 
				&& !chain.contains((terminal.getParent()))) {
			terminal = terminal.getParent();
			chain.add(terminal);
		}
		
		return chain;
	}
	
}
