package video.query;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import video.basics.Interaction;

public class RuleCaptureQuery 
{
	public String actionFile;
	public String captureFile;
	public int simulationNumber;
	
	public RuleCaptureQuery(String interactionFile, String captureFile, int simulation)
	{
		this.actionFile = interactionFile;
		this.captureFile = captureFile;
		this.simulationNumber = simulation;
	}
	
	public String[] getFrameCollectionOfTheVeryFirstTimeThisEventHappened(String rule, String sprite1, String sprite2) throws FileNotFoundException, IOException, ParseException
	{
		String [] frames = null;
		JSONParser parser = new JSONParser();
		JSONArray captureArray = 
				(JSONArray) parser.parse(new FileReader(this.captureFile));
		for (int i = 0; i < captureArray.size(); i++) 
		{
			JSONObject obj = (JSONObject)captureArray.get(i);
			JSONArray ticks = (JSONArray) obj.get("tickCollection");
			for (int j = 0; j < ticks.size(); j++) 
			{
				String interactionFrame = (String) ticks.get(j);
				String temp [] = getFrameCollection(obj, interactionFrame, new Interaction(rule, sprite1, sprite2));
				if(temp != null && temp.length > 0)
				{
					frames = temp;
					return frames;
				}
			}
		}
		return frames;
	}
	
	public String[] getFrameCollection(JSONObject objCaptured, String lastFrame, Interaction interaction) throws FileNotFoundException, IOException, ParseException
	{
		String frames[] = null;
		JSONParser parser = new JSONParser();
		JSONArray interactionArray = 
				(JSONArray) parser.parse(new FileReader(this.actionFile));
		for (int i = 0; i < interactionArray.size(); i++) 
		{
			JSONObject obj = (JSONObject)interactionArray.get(i);
			String tick = (String) obj.get("tick");
			if(tick.equals(lastFrame))
			{
				String rule = (String) obj.get("interaction");
				String sprite1 = (String) obj.get("sprite1");
				String sprite2 = (String) obj.get("sprite2");
				
				if(rule.equals(interaction.rule)
						&& sprite1.equals(interaction.sprite1)
								&& sprite2.equals(interaction.sprite2))
				{
					frames = getFramesThisFrameCollection(objCaptured);
					return frames;
				}	
				
			}
		}
		return frames;
	}

	public String[] getFramesThisFrameCollection(JSONObject objCaptured) 
	{
		ArrayList<String> frames = new ArrayList<String>();
		JSONArray tickArray = (JSONArray) objCaptured.get("tickCollection");
		int initialFrame = Integer.parseInt((String) tickArray.get(0)) - 1;
		for (int i = 0; i < tickArray.size(); i++) 
		{
			
			String frameNumber = (String) tickArray.get(i);
			String frame = 
					"simulation/" + "game" + this.simulationNumber + 
					"/frames/" + "frame" + frameNumber + ".png";
			frames.add(frame);
		}
		String frame = 
				"simulation/" + "game" + this.simulationNumber + 
				"/frames/" + "frame" + initialFrame + ".png";
		
		frames.add(0, frame);
		
		return frames.toArray(new String[frames.size()]);
	}
	
	public static void main(String [] args) throws FileNotFoundException, IOException, ParseException
	{
		String fileInteraction = "simulation/game" 
				 + 0 + "/interactions/interaction.json";
		String fileCapture = "simulation/game" 
				 + 0 + "/capture/capture.json";
		int simulation = 0;
		RuleCaptureQuery rcq = new 
				RuleCaptureQuery(fileInteraction, fileCapture, simulation);
		
		String frames [] = 
				rcq.
					getFrameCollectionOfTheVeryFirstTimeThisEventHappened
						("KillSprite", "monsterSlow", "sword");
		for (int i = 0; i < frames.length; i++) 
		{
			System.out.println(frames[i]);
		}
		
	}
	
	
}