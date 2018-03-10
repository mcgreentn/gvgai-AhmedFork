package video.handlers;

import java.io.FileWriter;
import java.io.IOException;

import org.json.simple.JSONObject;

public class StoreGameSimulationResult 
{
	public JSONObject gameResult;

	public StoreGameSimulationResult()
	{
		gameResult = new JSONObject();
	}

	public void storeGameSimulationResult(String result)
	{
		this.gameResult.put("result", result);
	}

	public void writeResultToAJSONFile(String interactionFile) throws IOException
	{
		try (FileWriter file = new FileWriter(interactionFile)) {

			file.write(gameResult.toJSONString());
			file.flush();
			file.close();
		}
	}
}
