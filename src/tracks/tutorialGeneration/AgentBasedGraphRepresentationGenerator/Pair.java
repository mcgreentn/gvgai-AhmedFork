package tracks.tutorialGeneration.AgentBasedGraphRepresentationGenerator;

public class Pair {
	
	/**
	 * The attribute for this pair
	 */
	private String attribute;
	
	/**
	 * The value for the attribute
	 */
	private String value;
	/**
	 * Creates a Pair
	 * @param attribute the string attribute
	 * @param value the string value attached to this attribute
	 */
	public Pair(String attribute, String value) {
		this.attribute = attribute;
		this.value = value;
	}
	/**
	 * just a generic pair constructor
	 */
	public Pair() {
	}
	/**
	 * Sets this pair's attribute
	 * @param attribute the attribute for this pair
	 */
	public void setAttribute(String attribute) {
		this.attribute = attribute;
	}
	/**
	 * Sets this pair's value
	 * @param value the value for this pair
	 */
	public void setValue(String value) {
		this.value = value;
	}
	/**
	 * Gets this pair's attribute
	 * @return the attribute
	 */
	public String getAttribute() {
		return this.attribute;
	}
	/**
	 * Get this pair's value
	 * @return the value
	 */
	public String getValue() {
		return this.value;
	}
}
