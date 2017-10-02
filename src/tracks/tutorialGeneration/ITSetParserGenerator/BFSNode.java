package tracks.tutorialGeneration.ITSetParserGenerator;

import java.util.ArrayList;

public class BFSNode {
	private Mechanic node;
	private ArrayList<BFSNode> children;
	private ArrayList<BFSNode> parents;
	private BFSNode parent;
	public BFSNode(Mechanic n) {
		node = n;
		children = new ArrayList<BFSNode>();
		parents = new ArrayList<BFSNode>();
	}
	
	public void setNode(Mechanic n) {
		node = n;
	}
	
	public Mechanic getNode() {
		return node;
	}
	
	public void addChild(BFSNode n) {
		children.add(n);
	}
	
	public ArrayList<BFSNode> getChildren() {
		return children;
	}
	
	public void addParent(BFSNode p) {
		parents.add(p);
	}
	
	public ArrayList<BFSNode> getParents() {
		return parents;
	}
	
	public BFSNode searchParents(Mechanic m) {
		for(BFSNode p : parents) {
			if(p.getNode() == m) {
				return p;
			}
		}
		return null;
	}
	public void setParent(BFSNode p) {
		parent = p;
		parents.addAll(parent.getParents());
	}
	
	public BFSNode getParent() {
		return parent;
	}
}
