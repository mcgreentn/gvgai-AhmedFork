package tracks.tutorialGeneration.AgentBasedGraphRepresentationGenerator;

import java.util.ArrayList;

public class Entity {

	/**
	 * A list of inputs for this node
	 */
	private ArrayList<Entity> inputs;
	/**
	 * A list of outputs for this node
	 */
	private ArrayList<Entity> outputs;
	
	/**
	 * a list of object attributes
	 */
	private ArrayList<Pair> attributes;
	/**
	 * The type of this node: Entity, Rule, or Condition
	 */
	private String type;
	/**
	 * The subtype of this node: depends on what the type is
	 */
	private String subtype;
	/**
	 * The name of this node, as written in VGDL
	 */
	private String name;
	
	/**
	 * The full name for this node, 
	 */
	private String fullName;
	/**
	 * a list of all mechanics this entity is a part of
	 */
	private ArrayList<Mechanic> mechanics;
	
	private ArrayList<String> parents;
 /***
  * Constructs an entity 
  * @param name
  * @param fullname
  * @param type
  * @param subtype
  */
	public Entity(String name, String fullname, String type, String subtype) {
		this.type = type;
		this.name = name;
		this.setSubtype(subtype);
		this.fullName = fullname;
		attributes = new ArrayList<Pair>();
		inputs = new ArrayList<Entity>();
		outputs = new ArrayList<Entity>();
		mechanics = new ArrayList<Mechanic>();
	}
	
	 /***
	  * Constructs an entity 
	  * @param name
	  * @param type
	  * @param subtype
	  */
		public Entity(String name, String type, String subtype) {
			this.type = type;
			this.name = name;
			this.setSubtype(subtype);
			attributes = new ArrayList<Pair>();
			inputs = new ArrayList<Entity>();
			outputs = new ArrayList<Entity>();
			mechanics = new ArrayList<Mechanic>();
		}
	
	/**
	 * Constructs an entity with type, name, inputs, and outputs known beforehand
	 * @param type the type of entity
	 * @param name the name of this entity
	 * @param inputs the input list
	 * @param outputs the output list
	 */
	public Entity(String name, String fullname, String type, String subtype, ArrayList<Entity> inputs, ArrayList<Entity> outputs) {
		this.type = type;
		this.setSubtype(subtype);
		this.name = name;
		this.inputs = inputs;
		this.outputs = outputs;
		this.fullName = fullname;
		attributes = new ArrayList<Pair>();
		mechanics = new ArrayList<Mechanic>();
	}
	
	/**
	 * sets the node's type
	 * @param type the new type for this node
	 */
	public void setType(String type) {
		this.type = type;
	}
	/**
	 * gets the node's type
	 * @return the type of this node
	 */
	public String getType() {
		return this.type;
	}
	/**
	 * sets this node's name
	 * @param subtype the new name for this node
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * gets this node's name
	 * @return the name for this node
	 */
	public String getName() {
		return this.name;
	}
	
	/**
	 * sets this node's inputs
	 * @param inputs the new input
	 */
	public void setInputs(ArrayList<Entity> inputs) {
		this.inputs = inputs;
	}
	
	/**
	 * Gets the intputs list
	 * @return the inputs list
	 */
	public ArrayList<Entity> getInputs() {
		return inputs;
	}
	
	/**
	 * Gets the outputs list
	 * @return the outputs list
	 */
	public ArrayList<Entity> getOutputs() {
		return outputs;
	}
	
	/**
	 * gets this node's attributes
	 * @return the attributes list
	 */
	public ArrayList<Pair> getAttributes() {
		return this.attributes;
	}
	
	public Pair getAttribute(String search) {
		for(Pair p : attributes) {
			if(p.getAttribute().equals(search)) {
				return p;
			}
		}
		return null;
	}
	/**
	 * Sets the attribute list
	 * @param attributes
	 */
	public void setAttributes(ArrayList<Pair> attributes) {
		this.attributes = attributes;
	}
	
	/**
	 * Adds the attribute to the attributes list
	 * @param attribute the one to be added to the list
	 */
	public void addAttribute(Pair attribute) {
		this.attributes.add(attribute);
	}
	
	/**
	 * Adds the entity to the inputs list
	 * @param entity the one to be added to the list
	 */
	public void addInput(Entity entity) {
		inputs.add(entity);
	}
	
	/**
	 * Adds the entity to the outputs list
	 * @param entity the one to be added to the list
	 */
	public void addOutput(Entity entity) {
		outputs.add(entity);
	}

	public String getSubtype() {
		return subtype;
	}

	public void setSubtype(String subtype) {
		this.subtype = subtype;
	}

	public ArrayList<Mechanic> getMechanics() {
		return mechanics;
	}

	public void setMechanics(ArrayList<Mechanic> mechanics) {
		this.mechanics = mechanics;
	}
	
	public void addMechanic(Mechanic mech) {
		mechanics.add(mech);
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
	
	public void setParents(ArrayList<String> par) {
		parents = par;
	}
	
	public ArrayList<String> getParents() {
		return parents;
	}
}
