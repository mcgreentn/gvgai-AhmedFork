package tracks.tutorialGeneration;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import tracks.ArcadeMachine;
import tracks.singlePlayer.Test;
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
	
	public void queryVisualDemonstrator(String sprite1, String sprite2, String rule, String printText) throws FileNotFoundException, IOException, ParseException{
		// TODO use the above information to query the visualization system
		
		FrameInteractionAssociation frameInteractionAssociation = new FrameInteractionAssociation("interaction/interaction.json");
		JSONObject interactionObject = null;
		interactionObject = frameInteractionAssociation.retrieveInteraction(rule, sprite1, sprite2);
		String [] frames = null;
		try{
			 frames = frameInteractionAssociation.retrieveInteractionFrames(interactionObject);
			 VideoPlayer videoPlayer = new VideoPlayer(frames, 500);
		}
		catch (NullPointerException e)
		{
			System.out.println("The interaction requested does not exist or is not stored with the same argument values");
		}
		// TODO save the video file locally and name it after the rule
	}
	
	public static void main(String [] args) throws FileNotFoundException, IOException, ParseException
	{
		VisualDemonstrationInterfacer vdi = new VisualDemonstrationInterfacer();
		vdi.runGame("examples/gridphysics/zelda.txt", 
					"examples/gridphysics/zelda_lvl1.txt", 
					"tracks.singlePlayer.advanced.olets.Agent");
		vdi.queryVisualDemonstrator("monsterSlow", "sword", "KillSprite", "");
	}
}
