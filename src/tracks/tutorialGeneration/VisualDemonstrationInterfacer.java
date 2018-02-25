package tracks.tutorialGeneration;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import tracks.ArcadeMachine;
import video.basics.BunchOfGames;
import video.basics.Interaction;
import video.basics.InteractionFrame;
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

	private int numberOfSimulations;

	public VisualDemonstrationInterfacer() throws FileNotFoundException, IOException, ParseException {
		numberOfSimulations = 0;
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

	public String[] retrieveFramePaths(String rule, String sprite1, String sprite2) throws FileNotFoundException, IOException, ParseException
	{
		FrameInteractionAssociation frameInteractionAssociation = new FrameInteractionAssociation("interaction/interaction.json");
		JSONObject interactionObject = null;
		interactionObject = frameInteractionAssociation.retrieveInteraction(rule, sprite1, sprite2);
		String [] frames = null;
		frames = frameInteractionAssociation.retrieveInteractionFrames(interactionObject);
		return frames;
	}

	public String[] retrieveFramePathsInTheSpecificInteractionFile(String rule, String sprite1, String sprite2,
			String filePath) throws FileNotFoundException, IOException, ParseException
	{
		FrameInteractionAssociation frameInteractionAssociation = new FrameInteractionAssociation(filePath);
		JSONObject interactionObject = null;
		interactionObject = frameInteractionAssociation.retrieveInteraction(rule, sprite1, sprite2);
		String [] frames = null;
		frames = frameInteractionAssociation.retrieveInteractionFrames(interactionObject);
		return frames;
	}

	/*
	 * It searches for the interaction passed as parameter in all the games whose frames are stored. 
	 * It returns an array if the interaction was ever stored or an empty one otherwise. 
	 */
	public String[] retrieveFramePathsInTheCollection(String rule, String sprite1, String sprite2,
			ArrayList<String> interactionPaths) throws FileNotFoundException, IOException, ParseException
	{
		for(String path : interactionPaths)
		{
			FrameInteractionAssociation frameInteractionAssociation = new FrameInteractionAssociation(path);
			JSONObject interactionObject = null;
			interactionObject = frameInteractionAssociation.retrieveInteraction(rule, sprite1, sprite2);
			String [] frames = null;
			if(interactionObject != null)
			{
				frames = frameInteractionAssociation.retrieveInteractionFrames(interactionObject);
				path = path.replace("interactions/interaction.json", "");
				for (int i = 0; i < frames.length; i++) 
				{
					frames[i] = path + frames[i];
				}
				return frames;
			}
		}
		return new String[]{""};
	}

	/*
	 * It searches for the interaction passed as parameter in all the games whose frames are stored. 
	 * It returns an array if the interaction was ever stored or an empty one otherwise. 
	 */
	public ArrayList<InteractionFrame> retrieveFramePathsInTheCollection(ArrayList<Interaction> interactions,
			ArrayList<String> interactionPaths) throws FileNotFoundException, IOException, ParseException
	{
		ArrayList<InteractionFrame> frameCollection = new ArrayList<>();

		for(Interaction interaction :  interactions)
		{
			for(String path : interactionPaths)
			{
				FrameInteractionAssociation frameInteractionAssociation = new FrameInteractionAssociation(path);
				JSONObject interactionObject = null;
				interactionObject = frameInteractionAssociation.
						retrieveInteraction(interaction.rule, interaction.sprite1, interaction.sprite2);
				String [] frames = null;
				if(interactionObject != null)
				{
					frames = frameInteractionAssociation.retrieveInteractionFrames(interactionObject);
					path = path.replace("interactions/interaction.json", "");
					for (int i = 0; i < frames.length; i++) 
					{
						frames[i] = path + frames[i];
					}
					frameCollection.add(new InteractionFrame(interaction.rule,
							interaction.sprite1, interaction.sprite2, frames));
				}
			}
		}
		return frameCollection;
	}

	/*
	 * It searches for the interaction passed as parameter in all the games whose frames are stored. 
	 * It returns an array if the interaction was ever stored or an empty one otherwise. 
	 */
	public HashMap<Interaction, String[]> mapFramePathsInTheCollection(ArrayList<Interaction> interactions,
			ArrayList<String> interactionPaths) throws FileNotFoundException, IOException, ParseException
	{
		HashMap<Interaction, String[]> frameCollection = new HashMap<Interaction, String[]>();

		for(Interaction interaction :  interactions)
		{
			for(String path : interactionPaths)
			{
				FrameInteractionAssociation frameInteractionAssociation = new FrameInteractionAssociation(path);
				JSONObject interactionObject = null;
				interactionObject = frameInteractionAssociation.
						retrieveInteraction(interaction.rule, interaction.sprite1, interaction.sprite2);
				String [] frames = null;
				if(interactionObject != null)
				{
					frames = frameInteractionAssociation.retrieveInteractionFrames(interactionObject);
					path = path.replace("interactions/interaction.json", "");
					for (int i = 0; i < frames.length; i++) 
					{
						frames[i] = path + frames[i];
					}
					frameCollection.put(new Interaction(interaction.rule, interaction.sprite1, interaction.sprite2),
							frames);
				}
			}
		}
		return frameCollection;
	}

	/*
	 * It searches for the interaction passed as parameter in all the games whose frames are stored. 
	 * It returns an array if the interaction was ever stored or an empty one otherwise. 
	 */
	public String[] mapFramePathsInTheCollectionByInteraction(Interaction interaction) throws FileNotFoundException, IOException, ParseException
	{
		String [] frames = null;
		ArrayList<String> interactionPaths = loadInteractionPaths(numberOfSimulations);
		for(String path : interactionPaths)
		{
			FrameInteractionAssociation frameInteractionAssociation = new FrameInteractionAssociation(path);
			JSONObject interactionObject = null;
			interactionObject = frameInteractionAssociation.
					retrieveInteraction(interaction.rule, interaction.sprite1, interaction.sprite2);
			
			if(interactionObject != null)
			{
				frames = frameInteractionAssociation.retrieveInteractionFrames(interactionObject);
				path = path.replace("interactions/interaction.json", "");
				for (int i = 0; i < frames.length; i++) 
				{
					frames[i] = path + frames[i];
				}
			}
		}

		return frames;
	}

	public ArrayList<String> loadInteractionPaths(int numberOfSimulations)
	{
		ArrayList<String> interactionpaths = new ArrayList<>();
		for(int i = 0; i < numberOfSimulations; i++)
		{
			interactionpaths.add("simulation/game" + i + "/interactions/interaction.json");
		}
		return interactionpaths;
	}

	public void runBunchOfGames(ArrayList<BunchOfGames> bunchOfGames) throws IOException
	{
		numberOfSimulations = bunchOfGames.size();
		this.createDirectories(numberOfSimulations);
		for (BunchOfGames game : bunchOfGames) 
		{
			this.runGame(game.gamePath, game.gameLevelPath, game.playerPath);
		}
	}

	public void createDirectories(int numberOfSimulations) throws IOException
	{
		for(int i = 0; i < numberOfSimulations; i++)
		{
			Files.createDirectories(Paths.get("simulation/game" + i + "/frames/"));
			Files.createDirectories(Paths.get("simulation/game" + i + "/interactions/"));
		}
	}

	public ArrayList<InteractionFrame> runGameSimulations(ArrayList<BunchOfGames> bunchOfgames,
			ArrayList<Interaction> interactions) throws IOException
	{
		ArrayList<InteractionFrame> frameCollection = new ArrayList<>();
		try {
			this.createDirectories(bunchOfgames.size());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.runBunchOfGames(bunchOfgames);

		ArrayList<String> interactionFiles = new ArrayList<>();
		for (int i = 0; i < bunchOfgames.size(); i++) 
		{
			interactionFiles.add("simulation/game" + i + "/interactions/interaction.json");
		}

		try {
			frameCollection = retrieveFramePathsInTheCollection(interactions, interactionFiles);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return frameCollection;
	}

	public HashMap<Interaction, String[]> runMultipleGameSimulations(ArrayList<BunchOfGames> bunchOfgames,
			ArrayList<Interaction> interactions) throws IOException
	{
		HashMap<Interaction, String[]> frameCollection = new HashMap<Interaction, String[]>();
		try {
			this.createDirectories(bunchOfgames.size());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.runBunchOfGames(bunchOfgames);

		ArrayList<String> interactionFiles = new ArrayList<>();
		for (int i = 0; i < bunchOfgames.size(); i++) 
		{
			interactionFiles.add("simulation/game" + i + "/interactions/interaction.json");
		}

		try {
			frameCollection = mapFramePathsInTheCollection(interactions, interactionFiles);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return frameCollection;
	}

	public String[] queryFrameCollection(HashMap<Interaction, String[]> frameMap, 
			String rule, String sprite1, String sprite2)
	{
		Interaction interaction = new Interaction(rule,  sprite1, sprite2);
		String [] frames = null;
		try
		{
			frames = frameMap.get(interaction);
		}
		catch(NullPointerException e)
		{
			System.out.println("interaction was not registered during simulations or it does not exist.");
		}
		return frames;
	}

	public static void main(String [] args) throws FileNotFoundException, IOException, ParseException
	{
		/*1    Video Example - Killing the lobster
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
		 * queriedFrames as a JSON file called qFrames.json
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
		vdi.writeQueryFramesInJSONFile(frameArray);*/

		/*4     Run the scalable version*/
		//		VisualDemonstrationInterfacer vdi = new VisualDemonstrationInterfacer();
		//
		////		//1st - configure your games
		//		BunchOfGames bog1 = new BunchOfGames("examples/gridphysics/zelda.txt", 
		//				"examples/gridphysics/zelda_lvl1.txt", 
		//				"tracks.singlePlayer.advanced.olets.Agent");
		//		
		//		BunchOfGames bog2 = new BunchOfGames("examples/gridphysics/zelda.txt", 
		//				"examples/gridphysics/zelda_lvl1.txt", 
		//				"tracks.singlePlayer.advanced.olets.Agent");
		//		
		//		BunchOfGames bog3 = new BunchOfGames("examples/gridphysics/zelda.txt", 
		//				"examples/gridphysics/zelda_lvl1.txt", 
		//				"tracks.singlePlayer.advanced.olets.Agent");
		//		ArrayList<BunchOfGames> bogs = new ArrayList<>();
		//		bogs.add(bog1); bogs.add(bog2); bogs.add(bog3);
		//		
		//		//2nd - configure the interactions you want to search for
		//		ArrayList<Interaction> interactions = new ArrayList<>();
		//		interactions.add(new Interaction("KillSprite", "monsterSlow", "sword"));
		//		interactions.add(new Interaction("TransformTo", "nokey", "key"));
		//		interactions.add(new Interaction("KillSprite", "monsterQuick", "sword"));
		//		
		//		
		//		//3rd run the method runMultipleGameSimulations
		//		HashMap<Interaction, String[]> frameCollection = vdi.runMultipleGameSimulations(bogs, interactions);
		//		
		//		System.out.println("-------------------------------------------");
		//		for (Interaction key : frameCollection.keySet()) {
		//			
		//			System.out.println("Interaction: " + key.rule);
		//			System.out.println("Sprite1: " + key.sprite1);
		//			System.out.println("Sprite2: " + key.sprite2);
		//			System.out.println();
		//			System.out.println("Interaction Frame List");
		//			String [] frames = frameCollection.get(key);
		//			for (int j = 0; j < frames.length; j++) {
		//				System.out.println(j + " - " + frames[j]);
		//			}
		//			System.out.println("-------------------------------------------");
		//			System.out.println();
		//		}
		//	}

		/*5     Mapping Interactions*/
		VisualDemonstrationInterfacer vdi = new VisualDemonstrationInterfacer();

		//1st - configure your games
		BunchOfGames bog1 = new BunchOfGames("examples/gridphysics/zelda.txt", 
				"examples/gridphysics/zelda_lvl1.txt", 
				"tracks.singlePlayer.advanced.olets.Agent");

		BunchOfGames bog2 = new BunchOfGames("examples/gridphysics/zelda.txt", 
				"examples/gridphysics/zelda_lvl1.txt", 
				"tracks.singlePlayer.advanced.olets.Agent");

		BunchOfGames bog3 = new BunchOfGames("examples/gridphysics/zelda.txt", 
				"examples/gridphysics/zelda_lvl1.txt", 
				"tracks.singlePlayer.advanced.olets.Agent");
		ArrayList<BunchOfGames> bogs = new ArrayList<>();
		bogs.add(bog1); bogs.add(bog2); bogs.add(bog3);

		//2nd - run a bunch of games
		vdi.runBunchOfGames(bogs);

		//3rd run the method mapFramePathsInTheCollectionByInteraction
		String [] frames = vdi.mapFramePathsInTheCollectionByInteraction(new Interaction("KillSprite", "monsterSlow", "sword"));
		
		//4th it will return the frames of the interaction (if it exists) 
		if(frames != null)
		for (int i = 0; i < frames.length; i++) {
			System.out.println(frames[i]);
		}
		else
		{
			System.out.println("interaction not registered!");
		}

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