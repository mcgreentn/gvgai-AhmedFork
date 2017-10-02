package tracks.tutorialGeneration.ITSetParserGenerator;

public class Mechanic {
	private Node subject;
	private Node object;
	private String scoreChange;
	private String mechanic;
	private boolean win;
	
	
	private Mechanic offshootMechanic;
	
	private boolean visited;
	
	private int limit = -1;
	// This class handles the mechanics of gameplay. They can be understood in the form of "Subject acts [interaction] upon Object"
	// For Example, in the case of a Spawn interaction, The Avatar Spawns Bombs (Bomberman). Therefore the Mechanic will read "Avatar [subject] Spawn Bomb [object]"
	// In the case of KillSprite, the Alien kills the player when they collide. Therefore the Mechanic will read "Alien KillSprite Avatar"
	public Mechanic(Node sub, Node ob, String inter){
		subject = sub;
		object = ob;
		mechanic = inter;
		visited = false;
	}
	
	public Mechanic(Node sub, Node ob, String inter, String scoCh) {
		subject = sub;
		object = ob;
		mechanic = inter;
		scoreChange = scoCh;
		visited = false;

	}
	public Mechanic(Node sub, Node ob, String inter, int lim, boolean win) {
		subject = sub;
		object = ob;
		mechanic = inter;
		limit = lim;
		this.win = win;
		visited = false;

	}
	
	public Node getSubject(){
		return subject;
	}
	public Node getObject() {
		return object;
	}
	public String getMechanic() {
		return mechanic;
	}
	
	public String getScoreChange() {
		return scoreChange;
	}
	
	public boolean getWin() {
		return win;
	}
	
	public int getLimit() {
		return limit;
	}
	
	public void setVisited(boolean flag) {
		visited = flag;
	}
	
	public boolean getVisited() {
		return visited;
	}
	
	public void setOffshootMechanic(Mechanic m) {
		offshootMechanic = m;
	}
	
	public Mechanic getOffshootMechanic() {
		return offshootMechanic;
	}
}
