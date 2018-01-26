package tracks.tutorialGeneration.AgentBasedGraphRepresentationGenerator;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;

public class BFSTree {
	private BFSNode root;
	private GraphBuilder graph;
	
	private ArrayList<ArrayList<BFSNode>> validPaths;
	public BFSTree(GraphBuilder graph) {
		this.setGraph(graph);
		validPaths = new ArrayList<ArrayList<BFSNode>>(); 
	}

	public BFSNode getRoot() {
		return root;
	}

	public void setRoot(BFSNode root) {
		this.root = root;
	}

	public GraphBuilder getGraph() {
		return graph;
	}

	public void setGraph(GraphBuilder graph) {
		this.graph = graph;
	}
	
	public void buildTree(Entity avatar, Entity win) {
		BFSNode start = new BFSNode();
		ArrayList<BFSNode> queue = new ArrayList<BFSNode>();
		
		for(Mechanic mech : avatar.getMechanics()) {
			BFSNode child = new BFSNode(mech);
			start.addChild(child);
			queue.add(child);
			// add myself to avoid cycles
			child.addAncestor(mech);
			child.setParent(start);
		}
		try {
			PrintWriter out = new PrintWriter(new FileWriter("output.txt"));
			int count = 0;
			while(!queue.isEmpty()) {
				count++;
				BFSNode current = queue.get(0);
				queue.remove(0);
				// check to see if the current is a terminal state. if it is, don't look down this branch
				if(!current.getMech().getAction().getName().equals("Win") && !current.getMech().getAction().getName().equals("Lose")) {
					for(Mechanic mech : current.getMech().getOutputs()) {
						if(!current.getAncestors().contains(mech) && !current.equals(mech)) {
							System.out.println("current path");
							for(Mechanic anc : current.getAncestors()) {
								System.out.println(anc.toString());
							}
							System.out.println(mech.toString());
//							out.println(mech.toString());
//							&& current.getAncestors().size() < 6
							BFSNode child = new BFSNode(mech);
							current.addChild(child);
							for(Mechanic ancestor : current.getAncestors()) {
								child.addAncestor(ancestor);
							}
							// add myself to avoid cycles
							child.addAncestor(mech);
							child.setParent(current);
							queue.add(child);
							System.out.println(child.getAncestors().size());
						} 
					}
				}
				// if this goes, we found a path from avatar to win!
				else if(current.getMech().getAction().getName().equals("Win")) {
					ArrayList<BFSNode> reversedPath = new ArrayList<BFSNode>();

					BFSNode temp = current;
					BFSNode tempParent = current.getParent();
//					reversedPath.add(temp);
					while(!temp.equals(start)) {
						reversedPath.add(temp);
						temp = tempParent;
						tempParent = tempParent.getParent();
					}
					Collections.reverse(reversedPath);
					validPaths.add(reversedPath);
//					break;
				}
			}
			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 


		
	}

	public ArrayList<ArrayList<BFSNode>> getValidPaths() {
		return validPaths;
	}

	public void setValidPaths(ArrayList<ArrayList<BFSNode>> validPaths) {
		this.validPaths = validPaths;
	}
}
