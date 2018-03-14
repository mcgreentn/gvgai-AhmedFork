package tracks.tutorialGeneration;

import java.io.File;
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
import tracks.tutorialGeneration.AgentBasedGraphRepresentationGenerator.Entity;
import tracks.tutorialGeneration.AgentBasedGraphRepresentationGenerator.Mechanic;
import video.basics.BunchOfGames;
import video.basics.GameSummary;
import video.basics.Interaction;
import video.basics.InteractionFrame;
import video.basics.InteractionQueryObject;
import video.gui.main.ShowFrames;
import video.gui.main.VideoPlayer;
import video.handlers.FrameInteractionAssociation;
import video.query.QueryGameResult;
import video.query.RuleCaptureQuery;

public class VisualDemonstrationInterfacer {

	/*** 
	 * The purpose of this interfacer is to provide a single point of contact between the graph-based representation tutorial generator and the system to query a completed game
	 * for a given interaction/termination rule, and receive in turn a sequence of images/video/gif of that mechanic being activated
	 */
	/***
	 * 
	 */

	private ShowFrames showFrames;
	
	public ArrayList<GameSummary> summaries;

	private long numberOfSimulations;

	public VisualDemonstrationInterfacer(boolean deleteFolders) throws FileNotFoundException, IOException, ParseException {
		numberOfSimulations = 0;
		if(deleteFolders)
			video.utils.Utils.deleteFolder(new File("simulation"));
		summaries = new ArrayList<>();
	}
	
	public long numberOfSimulationFoldersAreAvailable()
	{
		long count = 0;
		try {
			return count  = Files.find(
				    Paths.get("simulation/"), 
				    1, 
				    (path, attributes) -> attributes.isDirectory()
				).count() - 1;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			return count;
		} 
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
		String [] frames = new String[]{};
		ArrayList<String> interactionPaths = loadInteractionPaths(numberOfSimulationFoldersAreAvailable());
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
	
	public String[] mapFramePathsInTheCollectionByMechanic(Mechanic mechanic) throws FileNotFoundException, IOException, ParseException
	{
		Interaction interaction = new Interaction(mechanic.getAction().getName(), mechanic.getObject1().getName(), mechanic.getObject2().getName());
		String [] frames = new String[]{};
		ArrayList<String> interactionPaths = loadInteractionPaths(numberOfSimulationFoldersAreAvailable());
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
	
	public HashMap<String, String[]> mapFramePathsInTheCollectionByMechanics(ArrayList<Mechanic> mechanics) throws FileNotFoundException, IOException, ParseException
	{
		HashMap<String, String[]> mapMechanicsToFrames = new HashMap<>();
		for(int i = 0; i < mechanics.size(); i++)
		{
			Interaction interaction = new Interaction(mechanics.get(i).getAction().getName(), mechanics.get(i).getObject1().getName(), mechanics.get(i).getObject2().getName());
			String [] frames = new String[]{};
			ArrayList<String> interactionPaths = loadInteractionPaths(numberOfSimulationFoldersAreAvailable());
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
					for (int j = 0; j < frames.length; j++) 
					{
						frames[j] = path + frames[j];
					}
					mapMechanicsToFrames.put(mechanics.get(i).getAction().getName(), frames);
				}
			}
		}
		return mapMechanicsToFrames;
	}
	
	public HashMap<String, Integer> mapFrameNumbersInTheCollectionByMechanics(ArrayList<Mechanic> mechanics) throws FileNotFoundException, IOException, ParseException
	{
		HashMap<String, String[]> mapMechanicsToFrames = new HashMap<>();
		for(int i = 0; i < mechanics.size(); i++)
		{
			Interaction interaction = new Interaction(mechanics.get(i).getAction().getName(), mechanics.get(i).getObject1().getName(), mechanics.get(i).getObject2().getName());
			String [] frames = new String[]{};
			ArrayList<String> interactionPaths = loadInteractionPaths(numberOfSimulationFoldersAreAvailable());
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
					for (int j = 0; j < frames.length; j++) 
					{
						frames[j] = path + frames[j];
					}
					mapMechanicsToFrames.put(mechanics.get(i).getAction().getName(), frames);
				}
			}
		}
		
		return getMeTheFrameNumbers(mapMechanicsToFrames);
	}
	
	public HashMap<String, Integer> getMeTheFrameNumbers(HashMap<String, String[]> mapMechanicsToFrames)
	{
		HashMap<String, Integer> mapInteractionToFrameNumber = new HashMap<>();
		for(String key : mapMechanicsToFrames.keySet())
		{
			StringBuilder number = new StringBuilder(mapMechanicsToFrames.get(key)[2]).reverse();
			int frameNumber = getTheNumberOfTheFrame(number);
			mapInteractionToFrameNumber.put(key, frameNumber);
		}
		return mapInteractionToFrameNumber;
	}

	public ArrayList<String> loadInteractionPaths(long numberOfSimulations2)
	{
		ArrayList<String> interactionpaths = new ArrayList<>();
		for(int i = 0; i < numberOfSimulations2; i++)
		{
			interactionpaths.add("simulation/game" + i + "/interactions/interaction.json");
		}
		return interactionpaths;
	}

	public void runBunchOfGames(ArrayList<BunchOfGames> bunchOfGames) throws IOException
	{
		numberOfSimulations = bunchOfGames.size();
		this.createDirectories((int)numberOfSimulations);
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
			Files.createDirectories(Paths.get("simulation/game" + i + "/actions/"));
			Files.createDirectories(Paths.get("simulation/game" + i + "/result/"));
			Files.createDirectories(Paths.get("simulation/game" + i + "/capture/"));
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
	
	/**
	 * @param superP
	 * @param bunchOfGames
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws ParseException
	 */
	public String[][] retrieveFramesCollisionAndEndState(ArrayList<ArrayList<Mechanic>> superP, ArrayList<BunchOfGames> bunchOfGames)
			throws FileNotFoundException, IOException, ParseException {
		
		String allFrames[][] = null;
		String [] shootAndCollisionFrames;
		String [] lastFrames;
		
		int simulationNumber = (int) numberOfSimulationFoldersAreAvailable();
		for (int i = 0; i < simulationNumber; i++) {
			
			//1 - Load Files
			String fileInteraction = "simulation/game" 
					 + i + "/interactions/interaction.json";
			String fileCapture = "simulation/game" 
					 + i + "/capture/capture.json";
			String fileResult = "simulation/game" 
					+ i + "/result/result.json";

			//2 - Initialize Auxiliary classes
			QueryGameResult qgr = new QueryGameResult(fileResult);
			if(qgr.getResult() == 1)
			{
				RuleCaptureQuery rcq = new RuleCaptureQuery(fileInteraction, fileCapture, i);
				shootAndCollisionFrames = rcq.getShootFrameAndCollisionFrameActivateFromTheFirstTimeInThisMechanicList(superP.get(2));
				lastFrames = qgr.getLastFrames(i);
				allFrames = new String[][]{
						shootAndCollisionFrames,
						lastFrames
				};
				return allFrames;
			}
		}
		return null;
	}
	
	public HashMap<String, Integer> ma(ArrayList<Mechanic> mechanics, int simulationNumber) throws FileNotFoundException, IOException, ParseException
	{
		HashMap<String, String[]> mapMechanicsToFrames = new HashMap<>();
		
		for(int i = 0; i < mechanics.size(); i++)
		{
			Interaction interaction = new Interaction(mechanics.get(i).getAction().getName(), mechanics.get(i).getObject1().getName(), mechanics.get(i).getObject2().getName());
			String [] frames = new String[]{};
			String path = "simulation/game" 
					+ simulationNumber + "/interactions/interaction.json";
			FrameInteractionAssociation frameInteractionAssociation = new FrameInteractionAssociation(path);

			JSONObject interactionObject = null;
			interactionObject = frameInteractionAssociation.
					retrieveInteraction(interaction.rule, interaction.sprite1, interaction.sprite2);

			if(interactionObject != null)
			{
				frames = frameInteractionAssociation.retrieveInteractionFrames(interactionObject);
				path = path.replace("interactions/interaction.json", "");
				for (int j = 0; j < frames.length; j++) 
				{
					frames[j] = path + frames[j];
				}
				mapMechanicsToFrames.put(mechanics.get(i).getAction().getName(), frames);
			}

		}	
		return getMeTheFrameNumbers(mapMechanicsToFrames);
	}
	
	/**
	 * @param inputMechanics
	 * @param bunchOfGames
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws ParseException
	 */
	public void getAllRelevantFrames(ArrayList<Mechanic> inputMechanics,
			ArrayList<Mechanic> regularMechanics, ArrayList<BunchOfGames> bunchOfGames)
			throws FileNotFoundException, IOException, ParseException {
		
		HashMap<Integer, int[]> allRelevantFrames = null;
		HashMap<String, Integer> regularInteractionFrames = null;
		String [] shootAndCollisionFrames;
		String [] lastFrames;
		
		int simulationNumber = (int) numberOfSimulationFoldersAreAvailable();
		for (int i = 0; i < simulationNumber; i++) {
			
			//1 - Load Files
			String fileInteraction = "simulation/game" 
					 + i + "/interactions/interaction.json";
			String fileCapture = "simulation/game" 
					 + i + "/capture/capture.json";
			String fileResult = "simulation/game" 
					+ i + "/result/result.json";

			//2 - Initialize Auxiliary classes
			QueryGameResult qgr = new QueryGameResult(fileResult);
			int result = qgr.getResult();
			if(result == 1)
			{
				RuleCaptureQuery rcq = new RuleCaptureQuery(fileInteraction, fileCapture, i);
				shootAndCollisionFrames = rcq.getShootFrameAndCollisionFrameActivateFromTheFirstTimeInThisMechanicList(inputMechanics);
				lastFrames = qgr.getLastFrames(i);
				HashMap<Integer, int[]> shootCollision = retrieveFrameNumbersOfShootEvents(i, shootAndCollisionFrames);
				allRelevantFrames = resultAndAllFrames(i, result, shootCollision, lastFrames[lastFrames.length-1]);
				regularInteractionFrames = ma(regularMechanics, i);
				summaries.add(new GameSummary(i, result, regularInteractionFrames, allRelevantFrames));
			}
			else
			{
				RuleCaptureQuery rcq = new RuleCaptureQuery(fileInteraction, fileCapture, i);
				shootAndCollisionFrames = rcq.getShootFrameAndCollisionFrameActivateFromTheFirstTimeInThisMechanicList(inputMechanics);
				lastFrames = qgr.getLastFrames(i);
				HashMap<Integer, int[]> shootCollision = retrieveFrameNumbersOfShootEvents(i, shootAndCollisionFrames);
				allRelevantFrames = resultAndAllFrames(i, result, shootCollision, lastFrames[lastFrames.length-1]);
				regularInteractionFrames = ma(regularMechanics, i);
				summaries.add(new GameSummary(i, result, regularInteractionFrames, allRelevantFrames));
			}
		}
	}
	
	public HashMap<Integer, int[]> retrieveFrameNumbersOfShootEvents(Integer simulation, String [] shootAndCollisionFrames)
	{
		HashMap<Integer, int[]> simulationAndFrameNumbers = new HashMap<>();
		if(shootAndCollisionFrames != null)
		{
			int[] frames = new int[shootAndCollisionFrames.length];
			for (int i = 0; i < shootAndCollisionFrames.length; i++) 
			{
				StringBuilder framePath = new StringBuilder(shootAndCollisionFrames[i]).reverse();
				int frameNumber = getTheNumberOfTheFrame(framePath);
				frames[i] = frameNumber;
			}
			simulationAndFrameNumbers.put(simulation, frames);
		}
		return simulationAndFrameNumbers;
	}

	/**
	 * @param framePath
	 * @return
	 * @throws NumberFormatException
	 */
	public int getTheNumberOfTheFrame(StringBuilder framePath) throws NumberFormatException {
		String frame = framePath.substring(4, framePath.indexOf("e"));
		framePath = new StringBuilder(frame).reverse();
		int frameNumber = Integer.parseInt(framePath.toString());
		return frameNumber;
	}
	
	public HashMap<Integer, int[]> allFrames (int simulation, HashMap<Integer, int[]> simulationAndFrameNumbers, String lastFrames)
	{
		int [] allFrames = new int[]{simulationAndFrameNumbers.get(simulation)[0],
									 simulationAndFrameNumbers.get(simulation)[1],
									 getTheNumberOfTheFrame(new StringBuilder(lastFrames).reverse())};
		simulationAndFrameNumbers.put(simulation, allFrames);
		return simulationAndFrameNumbers;
	}
	
	public HashMap<Integer, int[]> resultAndAllFrames (int simulation, int result, HashMap<Integer, int[]> simulationAndFrameNumbers, String lastFrames)
	{
		int [] allFramesWithResult;
		int simFrameNumbers [] = simulationAndFrameNumbers.get(simulation);
		
		if(simFrameNumbers != null)
		{
			allFramesWithResult = new int[]{result, simFrameNumbers[0],
						simFrameNumbers[1],
							getTheNumberOfTheFrame(new StringBuilder(lastFrames).reverse())};
			simulationAndFrameNumbers.put(simulation, allFramesWithResult);
		}
		else
		{
			allFramesWithResult = new int[]{result,
						getTheNumberOfTheFrame(new StringBuilder(lastFrames).reverse())};
		simulationAndFrameNumbers.put(simulation, allFramesWithResult);
		}
			
		return simulationAndFrameNumbers;
	}
	
	public static void main(String [] args) throws FileNotFoundException, IOException, ParseException
	{
		//0 - Configure your mechanics
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
		
		//1 - regular mechanics (wo inputs involved)
		ArrayList<Mechanic> regularMechanics = new ArrayList<Mechanic>();
		regularMechanics.add(new Mechanic(
				new Entity("nokey", "Object", "Bomber"), 
				new Entity("key", "Object", "Missile"),
				new Entity("Collision", "Condition", "n/a"), 
				new Entity("TransformTo", "Action", "Interaction")));
		regularMechanics.add(new Mechanic(
				new Entity("goal", "Object", "Bomber"), 
				new Entity("withkey", "Object", "Missile"),
				new Entity("Collision", "Condition", "n/a"), 
				new Entity("KillSprite", "Action", "Interaction")));
		superP.add(regularMechanics);
		
		//2 - input mechanics
		ArrayList<Mechanic> inputMechanics = new ArrayList<Mechanic>();
		inputMechanics.add(new Mechanic(
				new Entity("monsterSlow", "Object", "Bomber"), 
				new Entity("sword", "Object", "Missile"), 
				new Entity("Collision", "Condition", "n/a"), 
				new Entity("KillSprite", "Action", "Interaction")));
		inputMechanics.add(new Mechanic(
				new Entity("monsterQuick", "Object", "Bomber"), 
				new Entity("sword", "Object", "Missile"), 
				new Entity("Collision", "Condition", "n/a"), 
				new Entity("KillSprite", "Action", "Interaction")));
		inputMechanics.add(new Mechanic(
				new Entity("monsterNormal", "Object", "Bomber"), 
				new Entity("sword", "Object", "Missile"), 
				new Entity("Collision", "Condition", "n/a"), 
				new Entity("KillSprite", "Action", "Interaction")));
		superP.add(inputMechanics);

		ArrayList<Mechanic> last = new ArrayList<Mechanic>();
		last.add(new Mechanic(
				new Entity("alien", "Object", "alien"), 
				new Entity("MultiSpriteCounter", "Condition", "n/a"), 
				new Entity("Win", "Action","Termination")));
		superP.add(last);

		//1 - Configure your games
		BunchOfGames bog1 = new BunchOfGames("examples/gridphysics/zelda.txt", 
				"examples/gridphysics/zelda_lvl0.txt", 
				"tracks.singlePlayer.tools.human.Agent");

		BunchOfGames bog2 = new BunchOfGames("examples/gridphysics/zelda.txt", 
				"examples/gridphysics/zelda_lvl0.txt", 
				"tracks.singlePlayer.tools.human.Agent");

		ArrayList<BunchOfGames> bunchOfGames = new ArrayList<>();
		bunchOfGames.add(bog1); bunchOfGames.add(bog2);
		//		
		//3 - Run the games
		VisualDemonstrationInterfacer vdi = new VisualDemonstrationInterfacer(false);
		vdi.runBunchOfGames(bunchOfGames);
		//			
		//4 - Query for everything
		vdi.getAllRelevantFrames(superP.get(2), superP.get(1), bunchOfGames);
		
		ArrayList<GameSummary> summaries = vdi.summaries;
		for (int i = 0; i < summaries.size(); i++) 
		{
			GameSummary gameSummary = summaries.get(i);
			if(gameSummary.result == 1)
			{
				HashMap<String, Integer> mtf = gameSummary.mapMechanicsToFrames;
				for (String key : mtf.keySet()) 
				{
					System.out.println(key + ";");
					int number = mtf.get(key);
					System.out.println(" " + number);
				}
				
				HashMap<Integer, int[]> rf = gameSummary.relevantFrames;
				for (Integer key : rf.keySet()) 
				{
					System.out.println("simulation : " + key);
					int resultAndFrames [] = rf.get(key);
					for (int j = 0; j < resultAndFrames.length; j++) 
					{
						System.out.println(" " + resultAndFrames[j]);
					}
				}
			}
			System.out.println();
		}
			
//		for (Integer i : relevantFrames.keySet()) 
//		{
//			System.out.println("number of the simulation: " + i);
//			int frameIntegers [] = relevantFrames.get(i);
//			for (int j = 0; j < frameIntegers.length; j++) {
//				System.out.println(frameIntegers[j]);
//			}
//		}
		
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