package video.query;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import video.constants.SimulationCounter;
import video.handlers.FrameInteractionAssociation;


public class RuleActionQuery extends FrameInteractionAssociation{
	
	public String fileInteraction;//rules
	public String fileAction;
	public int simulationNumber;
	
	public RuleActionQuery(String fileInteraction,
	 String fileAction, int simulationNumber) throws FileNotFoundException, IOException, ParseException
	{
		super(fileInteraction);
		this.fileInteraction = fileInteraction;
		this.fileAction = fileAction;
		this.simulationNumber = simulationNumber;
	}
	
	public String getFirstRuleActionFrame(String spriteFilter) throws FileNotFoundException, IOException, ParseException
	{
		JSONParser parser = new JSONParser();
		JSONArray actionArray = 
				(JSONArray) parser.parse(new FileReader(this.fileAction));
		for (int i = 0; i < actionArray.size(); i++) 
		{
			JSONObject actionObj = (JSONObject) actionArray.get(i);
			String tick = (String)actionObj.get("tick");
			JSONObject interactionObj = this.getRuleBasedOnTick(tick, spriteFilter);
			if(interactionObj != null)
			{
				return tick;
			}
		}
		
		return "-1";
	}
	
	public String[] getFirstEventActionFrames(String spriteFilter) throws FileNotFoundException, IOException, ParseException
	{
		String [] frames = null;
		String frameNumber = getFirstRuleActionFrame(spriteFilter);
		if(!frameNumber.equals("-1"))
		{
			int integerFrame = Integer.parseInt(frameNumber);
			frames = new String[5];
			frames[0] = "simulation/" + "game" + simulationNumber + "/frames/" + "frame" + (integerFrame - 2) + ".png";
			frames[1] = "simulation/" + "game" + simulationNumber + "/frames/" + "frame" + (integerFrame - 1) + ".png";
			frames[2] = "simulation/" + "game" + simulationNumber + "/frames/" + "frame" + integerFrame + ".png";
			frames[3] = "simulation/" + "game" + simulationNumber + "/frames/" + "frame" + (integerFrame + 1) + ".png";
			frames[4] = "simulation/" + "game" + simulationNumber + "/frames/" + "frame" + (integerFrame + 2) + ".png";
		}
		
		if(frames != null)
		{
			super.checkLastTwoFramesAfterInteraction(frames, Integer.parseInt(frameNumber));
			super.checkNegativeFramesBeforeTheInteraction(frames, Integer.parseInt(frameNumber));
		}

		return frames;
	}
	
	public JSONObject getRuleBasedOnTick(String tick, String spriteFilter) throws FileNotFoundException, IOException, ParseException
	{
		JSONParser parser = new JSONParser();
		JSONArray interactionArray = 
				(JSONArray) parser.parse(new FileReader(this.fileInteraction));
		for (int i = 0; i < interactionArray.size(); i++) 
		{
			JSONObject obj = (JSONObject) interactionArray.get(i);
			String objInteractionTick = (String)obj.get("tick");
			String filter = (String)obj.get("sprite2");
			if(objInteractionTick.equals(tick) && filter.equals(spriteFilter))
			{
				return obj;
			}
		}
		return null;
	}
	
	public static void main(String[] args) throws FileNotFoundException, IOException, ParseException
	{
		//It associates the first time a player hit a (valid) sprite with its object by pressing
		//the space key. Then it retrieves the frame of the interaction and returns a sequence of frames
		//wrapping the event
		RuleActionQuery raq = new RuleActionQuery(
				"simulation/game0/interactions/interaction.json",
				"simulation/game0/actions/actions.json", 0);
		
		String [] frames = raq.getFirstEventActionFrames("sword");
		
		if(frames != null)
		{
			for (int i = 0; i < frames.length; i++) {
				System.out.println(frames[i]);
			}
		}
	}

}
