package tracks.tutorialGeneration.ITSetParserGenerator;

import java.util.ArrayList;

public class Node {

	private String name;
	private ArrayList<Mechanic> interactionList;
	
	private ArrayList<Mechanic> interactsWithList;
	private boolean isTerminal;
	
	private ArrayList<Mechanic> terminalsList;
	
	public Node(String n) {
		name = n;
		interactionList = new ArrayList<Mechanic>();
		interactsWithList = new ArrayList<Mechanic>();
		isTerminal = false;
	}
	
	public String getName() {
		return name;
	}
	
	public ArrayList<Mechanic> getInteractionList() {
		return interactionList;
	}
	
	public Mechanic addMechanic(Node object, String interactString) {
		Mechanic i = new Mechanic(this, object, interactString);
		interactionList.add(i);
		object.interactsWithList.add(i);
		return i;
	}
	public Mechanic addMechanic(Node object, String interactString, String scoreChange) {
		Mechanic i = new Mechanic(this, object, interactString, scoreChange);
		interactionList.add(i);
		object.interactsWithList.add(i);
		return i;
	}
	
	public Mechanic addMechanic(Node object, String interactString, int limit, boolean win) {
		Mechanic i = new Mechanic (this, object, interactString, limit, win);
		interactionList.add(i);
		object.interactsWithList.add(i);
		return i;
	}
	
	public boolean getIsTerminal() {
		return isTerminal;
	}
	public void setIsTerminal(boolean flag) {
		isTerminal = flag;
	}
	
	public Mechanic addMechanic(Mechanic m) {
		interactionList.add(m);
		m.getObject().interactsWithList.add(m);
		return m;
	}
	
}
