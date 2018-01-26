package tracks.tutorialGeneration.AgentBasedGraphRepresentationGenerator;

import java.util.ArrayList;

public class Mechanic {

	private Entity object1;
	private Entity object2;
	
	private Entity condition;
	private Entity action;
	
	private ArrayList<Mechanic> outputs;

	private String type;
	
	private boolean generalized;
	public Mechanic(Entity object1, Entity object2, Entity condition, Entity action) {
		this.object1 = object1;
		this.object2 = object2;
		this.condition = condition;
		this.action = action;
		this.setGeneralized(false);
		type = action.getName();
		outputs = new ArrayList<Mechanic>();
	}
	
	public Mechanic(Entity object1, Entity condition, Entity action){
		this.object1 = object1;
		this.condition = condition;
		this.action = action;
		this.setGeneralized(false);
		type = action.getName();
		outputs = new ArrayList<Mechanic>();
	}
	
	public Mechanic(Entity condition, Entity action) {
		this.condition = condition;
		this.action = action;
		this.setGeneralized(false);
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
	
	public String toString() {
		String mechAsString = "";
		if(object1 != null && object2 != null) {
			mechAsString = object1.getName() + " + " + object2.getName() + " " + condition.getName() + " = " + action.getName();
			if(action.getOutputs().size() > 0) {
				mechAsString += " to " + action.getOutputs().get(0).getName();
			}
		} else if(type.equals("Win") || type.equals("Lose")) {
			if(object1 != null) {
				mechAsString = object1.getName() + " + " + condition.getName() + " = " + action.getName();
			}
			else {
				mechAsString = condition.getName() + " in " + condition.getAttribute("limit") + " seconds";
			}
		}
		return mechAsString;
	}

	public boolean isGeneralized() {
		return generalized;
	}

	public void setGeneralized(boolean generalized) {
		this.generalized = generalized;
	}
}
