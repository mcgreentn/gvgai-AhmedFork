package tracks.tutorialGeneration.BFSBasedGraphRepresentationGenerator;

import java.util.ArrayList;

public class Mechanic {

	private Entity object1;
	private Entity object2;
	
	private Entity condition;
	private Entity action;
	
	private ArrayList<Mechanic> outputs;

	private String type;
	
	public Mechanic(Entity object1, Entity object2, Entity condition, Entity action) {
		this.object1 = object1;
		this.object2 = object2;
		this.condition = condition;
		this.action = action;
		type = action.getName();
		outputs = new ArrayList<Mechanic>();
	}
	
	public Mechanic(Entity condition, Entity action) {
		this.condition = condition;
		this.action = action;
		type = action.getName();
		outputs = new ArrayList<Mechanic>();
	}
	public ArrayList<Mechanic> getOutputs() {
		return outputs;
	}

	public void setOutputs(ArrayList<Mechanic> outputs) {
		this.outputs = outputs;
	}
	public void addOutput(Mechanic output) {
		outputs.add(output);
	}

	public Entity getAction() {
		return action;
	}

	public void setAction(Entity action) {
		this.action = action;
	}

	public Entity getCondition() {
		return condition;
	}

	public void setCondition(Entity condition) {
		this.condition = condition;
	}

	public Entity getObject2() {
		return object2;
	}

	public void setObject2(Entity object2) {
		this.object2 = object2;
	}

	public Entity getObject1() {
		return object1;
	}

	public void setObject1(Entity object1) {
		this.object1 = object1;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
}
