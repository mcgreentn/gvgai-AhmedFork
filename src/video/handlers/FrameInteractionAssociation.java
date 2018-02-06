package video.handlers;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.sun.javafx.scene.paint.GradientUtils.Parser;

import video.basics.Interaction;

/**
 * Code written by Tiago Machado (tiago.machado@nyu.edu)
 * Date: 06/02/2018
 * @author Tiago Machado
 */

public class FrameInteractionAssociation 
{
	
	public JSONArray interactionArray;
	
	public FrameInteractionAssociation(String interactionFileName) throws FileNotFoundException, IOException, ParseException
	{
		interactionArray = retrieveInteractionFile(interactionFileName);
	}
	
	public JSONObject retrieveInteraction(String interaction)
	{
		for (int i = 0; i < interactionArray.size(); i++) 
		{
			JSONObject interactionObject = (JSONObject)interactionArray.get(i);
			String interactionName = (String) interactionObject.get("interaction");
			if(interactionName.contains(interaction))
			{
				return interactionObject;
			}
		}
		return null;
	}
	
	public JSONObject retrieveInteraction(String interaction, String tick)
	{	
		int index = interaction.indexOf("-");
		String stringTick = interaction.substring(0, index - 1);
		String stringInteraction = interaction.substring(index + 1, interaction.length()); 
		
		for (int i = 0; i < interactionArray.size(); i++) 
		{
			JSONObject interactionObject = (JSONObject)interactionArray.get(i);
			String interactionName = (String) interactionObject.get("interaction");
			if(stringInteraction.contains(interactionName) && 
					stringTick.equals(((String)interactionObject.get("tick"))))
			{
				return interactionObject;
			}
		}
		return null;
	}
	
	public String[] retriveInteractionFrames(JSONObject interactionObject)
	{
		int tick = Integer.parseInt(interactionObject.get("tick").toString());
		
		String frame0 = "frames/frame" + (tick - 1) + ".png";
		String frame1 = "frames/frame" + (tick) + ".png";
		String frame2 = "frames/frame" + (tick + 1) + ".png";
		
		return new String[]{frame0, frame1, frame2};
	}
	
	public JSONArray retrieveInteractionFile(String interactionFileName) throws FileNotFoundException, IOException, ParseException
	{
		JSONParser parser = new JSONParser();

		JSONArray interactionArray = (JSONArray) parser.parse(new FileReader(interactionFileName));
		
		return interactionArray;
	}
	
	public String [] retrieveAllInteractionNames()
	{
		String [] interactionNames = null;
		try
		{
			interactionNames = new String[interactionArray.size()];
			for (int i = 0; i < interactionArray.size(); i++) 
			{
				JSONObject interactionObject = (JSONObject)interactionArray.get(i);
				interactionNames[i] = (String) interactionObject.get("pairInteractionTick");
			}
		}catch(NullPointerException e){
			e.getMessage();
		}
		return interactionNames;
	}
	
	public Interaction [] retrieveInteractionsAsArray()
	{
		Interaction [] interactionNames = null;
		try
		{
			interactionNames = new Interaction[interactionArray.size()];
			for (int i = 0; i < interactionArray.size(); i++) 
			{
				JSONObject interactionObject = (JSONObject)interactionArray.get(i);
				Interaction interaction = 
						new Interaction((String) interactionObject.get("tick"),
								(String) interactionObject.get("sprite1"),
								(String) interactionObject.get("sprite2"),
								(String) interactionObject.get("interaction"));
				
				interactionNames[i] = interaction;
			}
		}catch(NullPointerException e){
			e.getMessage();
		}
		return interactionNames;
	}
	
}
