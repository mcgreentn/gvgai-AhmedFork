package video.query;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import tracks.tutorialGeneration.AgentBasedGraphRepresentationGenerator.Entity;
import tracks.tutorialGeneration.AgentBasedGraphRepresentationGenerator.Mechanic;
import video.basics.BunchOfGames;
import video.basics.Interaction;
import video.utils.Utils;

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
	
	public String[] getShootFrameAndCollisionFrameActivateFromTheFirstTimeInThisMechanicList(ArrayList<ArrayList<Mechanic>> mechanics) throws FileNotFoundException, IOException, ParseException
	{
		String frames [] = null;
		//Navigate in the ArrayList<ArrayList<Mechanic>>
		ArrayList<Mechanic> ms = mechanics.get(1);
		for (int j = 0; j < ms.size(); j++) 
		{
			Mechanic m  =  ms.get(j);
			Entity obj1 =  m.getObject1();
			Entity obj2 =  m.getObject2();
			Entity act  =  m.getAction();
			
			frames = getFrameCollectionOfTheVeryFirstTimeThisEventHappened(act.getName(), obj1.getName(), obj2.getName());
			if(frames != null)
			{
				frames = new String[]{frames[0], frames[frames.length-1]};
				return frames;
			}
		}
		
		return null;
	}
	
	public String[] getFrameCollection(JSONObject objCaptured, String lastFrame, Interaction interaction) throws FileNotFoundException, IOException, ParseException
	{
		String frames[] = null;
		JSONParser parser = new JSONParser();
		JSONArray interactionArray = 
				(JSONArray) parser.parse(new FileReader(this.actionFile));
		
		if(!checkIfInteractionValuesAreValid(interaction, interactionArray))
			return null;
		
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
					frames = getFramePathsInThisFrameCollection(objCaptured);
					return frames;
				}	
				
			}
		}
		return frames;
	}

	/**
	 * @param interaction
	 * @param interactionArray
	 */
	public boolean checkIfInteractionValuesAreValid(Interaction interaction, JSONArray interactionArray) {
		if(Utils.isValueValid(interactionArray, interaction.sprite1) && Utils.isValueValid(interactionArray, interaction.sprite2))
		{
			return true;
		}
		return false;
	}

	public String[] getFramePathsInThisFrameCollection(JSONObject objCaptured) 
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
	
	public Integer[] getFrameNumberInThisFrameCollection(JSONObject objCaptured) 
	{
		ArrayList<Integer> frames = new ArrayList<Integer>();
		JSONArray tickArray = (JSONArray) objCaptured.get("tickCollection");
		int initialFrame = Integer.parseInt((String) tickArray.get(0)) - 1;
		for (int i = 0; i < tickArray.size(); i++) 
		{
			String frameNumber = (String) tickArray.get(i);
			int frame = Integer.parseInt(frameNumber);
			frames.add(frame);
		}
		int frame = (initialFrame);
		
		frames.add(0, frame);
		
		return frames.toArray(new Integer[frames.size()]);
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
//		
//		String frames [] = 
//				rcq.
//					getFrameCollectionOfTheVeryFirstTimeThisEventHappened
//						("KillSprite", "monsterSlow", "sword");
//		for (int i = 0; i < frames.length; i++) 
//		{
//			System.out.println(frames[i]);
//		}
		
		ArrayList<ArrayList<Mechanic>> superP = new ArrayList<ArrayList<Mechanic>>();		
		// example critical path
		// first mechanic
		ArrayList<Mechanic> first = new ArrayList<Mechanic>();
		Mechanic input = new Mechanic(
				new Entity("avatar", "Object", "FlakAvatar"), 
				new Entity("Press Space", "Condition", "Player Input"), 
				new Entity("Shoot", "Action", "Interaction"));
		
		input.getAction().getOutputs().add(new Entity("sam","Object","Missile"));
		first.add(input);
		first.add(input);
		superP.add(first);
		
		ArrayList<Mechanic> next1 = new ArrayList<Mechanic>();
		next1.add(new Mechanic(
				new Entity("alien", "Object", "alien"), 
				new Entity("sam", "Object", "Missile"), 
				new Entity("Collision", "Condition", "n/a"),
				new Entity("KillSprite", "Action", "Interaction")));
		next1.add(new Mechanic(
				new Entity("alienGreen", "Object", "Bomber"), 
				new Entity("sam", "Object", "Missile"), 
				new Entity("Collision", "Condition", "n/a"), 
				new Entity("KillSprite", "Action", "Interaction")));
		next1.add(new Mechanic(
				new Entity("alienBlue", "Object", "Bomber"), 
				new Entity("sam", "Object", "Missile"),
				new Entity("Collision", "Condition", "n/a"), 
				new Entity("KillSprite", "Action", "Interaction")));
		superP.add(next1);
		
		ArrayList<Mechanic> last = new ArrayList<Mechanic>();
		last.add(new Mechanic(
				new Entity("alien", "Object", "alien"), 
				new Entity("MultiSpriteCounter", "Condition", "n/a"), 
				new Entity("Win", "Action","Termination")));
		superP.add(last);
		
		String frames[] = rcq.getShootFrameAndCollisionFrameActivateFromTheFirstTimeInThisMechanicList(superP);
		
		System.out.println(frames[0]);
		System.out.println(frames[1]);		
		
	}
	
	
}