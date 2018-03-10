package video.query;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import video.handlers.FrameInteractionAssociation;

public class QueryGameResult extends FrameInteractionAssociation
{
	public String resultFile;
	private int gameResult;
	
	public QueryGameResult(String resultFile) throws FileNotFoundException, IOException, ParseException
	{
		super();
		this.resultFile = resultFile;
		this.gameResult = -1;
	}
	
	public int getResult() throws FileNotFoundException, IOException, ParseException
	{
		JSONObject resultObj = getGameResult();
		int result = Integer.parseInt((String)resultObj.get("result"));
		return result;
	}
	
	public JSONObject gameResultRetrievalSprites() throws FileNotFoundException, IOException, ParseException
	{
		JSONParser parser = new JSONParser();
		JSONObject resultObj = 
				(JSONObject) parser.parse(new FileReader(resultFile));
		return resultObj;
	}
	
	public JSONObject getGameResult() throws FileNotFoundException, IOException, ParseException
	{
		JSONObject resultObj = gameResultRetrievalSprites();
		return resultObj;
	}
	
	public String[] getLastFrames(int numberOfSimulation, JSONObject resultObj) throws FileNotFoundException, IOException, ParseException
	{
		String [] frames = new String[5];
		int tick = Integer.parseInt((String)resultObj.get("tick"));
		String prefix = "simulation/game" + numberOfSimulation + "/";
		String frame0 = prefix + "frames/frame" + (tick - 2) + ".png";
		String frame1 = prefix + "frames/frame" + (tick - 1) + ".png";
		String frame2 = prefix + "frames/frame" + (tick) + ".png";
		String frame3 = prefix + "frames/frame" + (tick + 1) + ".png";
		String frame4 = prefix + "frames/frame" + (tick + 2) + ".png";
		
		frames = new String[]{frame0, frame1, frame2, frame3, frame4};
		
		super.checkLastTwoFramesAfterInteraction(frames, tick);
		super.checkNegativeFramesBeforeTheInteraction(frames, tick);
		
		return frames;
	}
}
