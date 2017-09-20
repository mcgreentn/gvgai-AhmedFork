package tracks.tutorialGeneration.ITSetParserGenerator;

import java.util.ArrayList;

public class Node {

	private String name;
	private ArrayList<Mechanic> interactionList;
	private boolean isTerminal;
	
	private ArrayList<Mechanic> terminalsList;
	
	public Node(String n) {
		name = n;
		interactionList = new ArrayList<Mechanic>();
		isTerminal = false;
	}
	
	public String getName() {
		return name;
	}
	
	public ArrayList<Mechanic> getInteractionList() {
		return interactionList;
	}
	
	public void addMechanic(Node object, String interactString) {
		Mechanic i = new Mechanic(this, object, interactString);
		interactionList.add(i);
	}
	public void addMechanic(Node object, String interactString, String scoreChange) {
		Mechanic i = new Mechanic(this, object, interactString, scoreChange);
		interactionList.add(i);
	}
	
	public void addMechanic(Node object, String interactString, int limit, boolean win) {
		Mechanic i = new Mechanic (this, object, interactString, limit, win);
		interactionList.add(i);
	}
	
	public boolean getIsTerminal() {
		return isTerminal;
	}
	public void setIsTerminal(boolean flag) {
		isTerminal = flag;
	}
	
}
