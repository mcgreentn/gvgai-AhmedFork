package tracks.tutorialGeneration;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import tools.com.google.gson.JsonObject;
import tracks.ArcadeMachine;
import tracks.singlePlayer.Test;
import video.basics.InteractionQueryObject;
import video.gui.main.ShowFrames;
import video.gui.main.VideoPlayer;
import video.handlers.FrameInteractionAssociation;

public class VisualDemonstrationInterfacer {

	/*** 
	 * The purpose of this interfacer is to provide a single point of contact between the graph-based representation tutorial generator and the system to query a completed game
	 * for a given interaction/termination rule, and receive in turn a sequence of images/video/gif of that mechanic being activated
	*/
	/***
	 * 
	 */
	
	private ShowFrames showFrames;
	
	public VisualDemonstrationInterfacer() throws FileNotFoundException, IOException, ParseException {
		
	}
	
	public void runGame(String game, String level1, String agentName)
	{
		ArcadeMachine.runOneGame(game, level1, true, agentName, "", 0, 0);
	}
	
	public HashMap<Integer, TupleRuleFrames> queryVisualDemonstrations(InteractionQueryObject [] iqos) throws FileNotFoundException, IOException, ParseException
	{
		FrameInteractionAssociation frameInteractionAssociation = new FrameInteractionAssociation("interaction/interaction.json");
		JSONObject[] interactionObjects = new JSONObject[iqos.length];
		HashMap<Integer, TupleRuleFrames> frames = new HashMap<Integer, TupleRuleFrames>();
		for (int i = 0; i < iqos.length; i++) 
		{
			JSONObject tempObj = frameInteractionAssociation.retrieveInteraction(iqos[i].rule, iqos[i].sprite1, iqos[i].sprite2);
			interactionObjects[i] = tempObj;
			TupleRuleFrames tuple = new TupleRuleFrames(iqos[i].rule, 
					frameInteractionAssociation.retrieveInteractionFrames(interactionObjects[i]));
			frames.put(i, tuple);
		}
		
		return frames;
	}
	
	public JSONArray writeQueriedFramesInJSONArray(HashMap<Integer, TupleRuleFrames> frames)
	{
		JSONArray queriedFrames = new JSONArray();
		for (Integer key : frames.keySet()) 
		{
			TupleRuleFrames sequence = frames.get(key);
			JSONObject obj = new JSONObject();
			obj.put("interactionNumber", String.valueOf(key));
			obj.put("interactionType", sequence.rule);
			obj.put("frame1", sequence.frames[0]);
			obj.put("frame2", sequence.frames[1]);
			obj.put("frame3", sequence.frames[2]);
			queriedFrames.add(obj);
		}
		
		return queriedFrames;
	}
	
	public void queryVisualDemonstrator(String sprite1, String sprite2, String rule, String subtitleText) throws FileNotFoundException, IOException, ParseException{
		// TODO use the above information to query the visualization system
		
		FrameInteractionAssociation frameInteractionAssociation = new FrameInteractionAssociation("interaction/interaction.json");
		JSONObject interactionObject = null;
		interactionObject = frameInteractionAssociation.retrieveInteraction(rule, sprite1, sprite2);
		String [] frames = null;
		try{
			 frames = frameInteractionAssociation.retrieveInteractionFrames(interactionObject);
			 VideoPlayer videoPlayer = new VideoPlayer(frames, 500, subtitleText);
		}
		catch (NullPointerException e)
		{
			System.out.println("The interaction requested does not exist or is not stored with the same argument values");
		}
		// TODO save the video file locally and name it after the rule
	}
	
	public void writeQueryFramesInJSONFile(JSONArray mediaArray)
	{
		try (FileWriter file = new FileWriter("queriedFrames/qFrames.json")) {

			file.write(mediaArray.toJSONString());
			file.flush();
			file.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String [] args) throws FileNotFoundException, IOException, ParseException
	{
/*1     Video Example - Killing the lobster
		VisualDemonstrationInterfacer vdi = new VisualDemonstrationInterfacer();
		vdi.runGame("examples/gridphysics/zelda.txt", 
					"examples/gridphysics/zelda_lvl1.txt", 
					"tracks.singlePlayer.advanced.olets.Agent");
		vdi.queryVisualDemonstrator("monsterSlow", "sword", "KillSprite", "Go and kill that horrendous lobster!");*/
		
/*2     Video Example - Avatar being killed
		VisualDemonstrationInterfacer vdi = new VisualDemonstrationInterfacer();
		vdi.runGame("examples/gridphysics/zelda.txt", 
					"examples/gridphysics/zelda_lvl1.txt", 
					"tracks.singlePlayer.tools.human.Agent");
		vdi.queryVisualDemonstrator("nokey", "monsterSlow", "KillSprite", "Got killed by a lobster! - (Allergy?)");*/
		
		
/*3      Query for Frames Example - It will look for the interaction frames and saved them inside the folder
 * queriedFrames as a JSON file called qFrames.json*/
		VisualDemonstrationInterfacer vdi = new VisualDemonstrationInterfacer();
		vdi.runGame("examples/gridphysics/zelda.txt", 
					"examples/gridphysics/zelda_lvl1.txt", 
					"tracks.singlePlayer.advanced.olets.Agent");
		InteractionQueryObject iqo1 = new InteractionQueryObject
				("nokey", "key", "TransformTo", "Getting the key");
		InteractionQueryObject iqo2 = new InteractionQueryObject
				("monsterSlow", "sword", "KillSprite", "Go and kill that horrendous lobster!");
		InteractionQueryObject [] iqos = new InteractionQueryObject[2];
		iqos[0] = iqo2;
		iqos[1] = iqo1;
		HashMap<Integer, TupleRuleFrames> frames = vdi.queryVisualDemonstrations(iqos);
		JSONArray frameArray = vdi.writeQueriedFramesInJSONArray(frames);
		vdi.writeQueryFramesInJSONFile(frameArray);
	}
}

class TupleRuleFrames{
	
	public String rule;
	public String [] frames;
	public TupleRuleFrames(String rule, String [] frames)
	{
		this.rule = rule;
		this.frames = frames;
	}
	
}