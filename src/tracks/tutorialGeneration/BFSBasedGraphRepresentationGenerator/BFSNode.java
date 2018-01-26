package tracks.tutorialGeneration.BFSBasedGraphRepresentationGenerator;

import java.util.ArrayList;

public class BFSNode {
	private Mechanic mech;
	private BFSNode parent;
	private ArrayList<BFSNode> children;
	private ArrayList<Mechanic> ancestors;
	public BFSNode() {
		children = new ArrayList<BFSNode>();
		ancestors = new ArrayList<Mechanic>();
	}
	public BFSNode(Mechanic mech) {
		this.mech = mech;
		children = new ArrayList<BFSNode>();
		ancestors = new ArrayList<Mechanic>();
	}
	public Mechanic getMech() {
		return mech;
	}
	public void setMech(Mechanic mech) {
		this.mech = mech;
	}
	public BFSNode getParent() {
		return parent;
	}
	public void setParent(BFSNode parent) {
		this.parent = parent;
	}
	public ArrayList<BFSNode> getChildren() {
		return children;
	}
	public void setChildren(ArrayList<BFSNode> children) {
		this.children = children;
	}
	
	public void addChild(BFSNode child) {
		children.add(child);
	}
	public ArrayList<Mechanic> getAncestors() {
		return ancestors;
	}
	public void setAncestors(ArrayList<Mechanic> ancestors) {
		this.ancestors = ancestors;
	}
	
	public void addAncestor(Mechanic ancestor) {
		ancestors.add(ancestor);
	}
}
