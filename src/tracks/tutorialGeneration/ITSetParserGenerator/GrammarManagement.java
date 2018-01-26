package tracks.tutorialGeneration.ITSetParserGenerator;

public class GrammarManagement {

	private Graph mechanicGraph;
	
	private String[] destroyActionVerbs = {"destroy", "kill", "obliterate"};
	private String[] avoidActionVerbs = {"dodge", "evade", "avoid"};
	private String[] helpingAdjectives = {"all", "every"};
	private String[] helpingVerbs = {"can", "will", "is"};
	private String[] negatives = {"no", "not"};
	private String[] controlUseVerb = {"press", "use"};
	
	
	public String start() {
		String tutorial = "#Tutorial#";
		
		
		String returnMe = parse(tutorial);

		return returnMe;
	}
	/**
	 * Recursively parse a tutorial together
	 * @param parseMe
	 * @return
	 */
	public String parse(String parseMe) {
		String returnMe = "";
		
		if(parseMe.equals("#Tutorial#")) {
			returnMe += parse("#Controls#");
			returnMe += parse("#Win#");
			returnMe += parse("#Lose#");
			returnMe += parse("#Mechanics#");
		} else if(parseMe.equals("#Controls#")) {
			returnMe += parse("#MovementControls#");
			returnMe += parse("#OtherControls#");
		} else if(parseMe.equals("#Win#")) {
			returnMe += "\nTo Win:\n";
			for(Mechanic mech : mechanicGraph.getWinConditions())
			{
				returnMe += parseWin(mech);
			}
		} else if(parseMe.equals("#Lose#")) {
			
		} else if(parseMe.equals("#Mechanics#")) {
			
		} 
		return returnMe;
	}
	
	public String parseWin(Mechanic winCondition) {
		String returnMe = "";
		if(winCondition.getMechanic().equals("SpriteCounter")) {
//			returnMe += parse("#ActionVerb#", winCondition);
//			returnMe += parse("#HelpingAdj#", winCondition);
//			returnMe += winCondition.getObject().getName();
			returnMe += "There must be " + winCondition.getLimit() + " " + winCondition.getObject().getName() + "s";
		} else if(winCondition.getMechanic().equals("Timeout")) {
			returnMe += "The game must go on for " + winCondition.getLimit();
		}
		
		return returnMe;
	}
	
	public String parse(String parseMe, Mechanic mech) {
		String returnMe = "";
		
		if(parseMe.equals("#HelpingAdj#")) {
			
		} else if(parseMe.equals("#HelpingVerb#")) {
			returnMe += helpingVerbs[0];
		} else if(parseMe.equals("#ActionVerb#")) {
			
		} else if(parseMe.equals("#ControlVerb#")) {
			
		} else if(parseMe.equals("#Button#")) {
			
		}
		
		return returnMe;
	}
	
}
