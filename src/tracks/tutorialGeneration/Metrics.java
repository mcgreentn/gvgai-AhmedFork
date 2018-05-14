package tracks.tutorialGeneration;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.simple.parser.ParseException;

import core.game.GameDescription;
import core.game.GameDescription.SpriteData;
import tracks.tutorialGeneration.AgentBasedGraphRepresentationGenerator.Mechanic;
import video.basics.BunchOfGames;
import video.basics.Interaction;
import video.query.ScalableQuery;

public class Metrics {

	// VGDL Stats
	public static int maxHierarchy = 0;
	public static int spriteCount = 0;
	public static int interactionCount = 0;
	
	// Tutorial Stats
	public static int shownInteractionCount = 0;
	public static int numberOfMergedInteractions = 0;
	public static int criticalPathVictoryCount = 0;
	public static int criticalPathFailureCount = 0;
	public static int pointsCount = 0;
	
	public static int[] numInts;
	public static int[] numIntSprites;
	public static int[] numIntAvatar;
	public static int[] firstFrame;

	public static int bogsSize;
	public static ArrayList<ArrayList<Mechanic>> superP;
	public static int[][][] winPath;
	public static int[][] relevantFrames;
	
	public static void printMetrics() {
		System.out.println("Metrics");
		System.out.println("**************");
		System.out.println("Max Hierarchy: " + Metrics.maxHierarchy);
		System.out.println("Sprite Count: " + Metrics.spriteCount);
		System.out.println("Interaction Count: " + Metrics.interactionCount);
		System.out.println("Interaction Show Count: " + Metrics.shownInteractionCount);
		System.out.println("numberOfMergedInteractions: " + Metrics.numberOfMergedInteractions);
		System.out.println("Crit Path Victory Count: " + Metrics.criticalPathVictoryCount);
		System.out.println("Crit Path Failure Count: " + Metrics.criticalPathFailureCount);
		System.out.println("Points Count: " + Metrics.pointsCount);
		
		String a = "", b = "", c = "", d = "";
		for(int i = 0; i < numInts.length; i++) {
			a += numInts[i] + ",";
			b += numIntSprites[i] + ",";
			c += numIntAvatar[i] + ",";
//			d += firstFrame[i] + " ";
		}
		System.out.println("Number of Unique Ints: " + a);
		System.out.println("Number of interact sprites: " + b);
		System.out.println("Number of avatar interacts: " + c);
		System.out.println("First Frame: " + d);
		
		System.out.println("First frame of winpath interactions: ");
		for(int i = 1; i < winPath.length-1; i++) {
			for(int j = 0; j < winPath[i].length-1; j++) {
				System.out.print(superP.get(i).get(j) + ",");
				for(int k = 0; k < winPath[i][j].length; k++) {
					System.out.print(winPath[i][j][k] + ",");
				}
//				System.out.print(",");
			}
//			System.out.print(">><<");
		}
		System.out.println("\n");
	}
	
	public static void saveMetricsCSV(String fileName) throws FileNotFoundException{
        PrintWriter pw = new PrintWriter(new File(fileName));
        StringBuilder sb = new StringBuilder();
        
        sb.append("maxHierarchy,spriteCount,interactionCount,shownInteractionCount,mergedInteractionCount,criticalPathVictoryCount,criticalPathFailureCount,pointsCount,");
        
        
        sb.append("\n");
        
        sb.append(Metrics.maxHierarchy + ",");
        sb.append(Metrics.spriteCount + ",");
        sb.append(Metrics.interactionCount + ",");
        sb.append(Metrics.shownInteractionCount + ",");
        sb.append(Metrics.numberOfMergedInteractions + ",");
        sb.append(Metrics.criticalPathVictoryCount + ",");
        sb.append(Metrics.criticalPathFailureCount + ",");
        sb.append(Metrics.pointsCount);
        
//        for(int i = 0; i < numInts.length; i++) {
//        	sb.append("," + numInts[i]);
//        }
        
//        for(int i = 0; i < numInts.length; i++) {
//        	sb.append("," + numIntSprites[i]);
//        }
//        for(int i = 0; i < numInts.length; i++) {
//        	sb.append("," + numIntAvatar[i]);
//        }
        
        sb.append("\n");
//		for(int i = 1; i < winPath.length-1; i++) {
//			for(int j = 0; j < winPath[i].length-1; j++) {
//				sb.append(superP.get(i).get(j) + ",");
//				for(int k = 0; k < winPath[i][j].length; k++) {
//					sb.append(winPath[i][j][k] + ",");
//				}
//			}
//		}
//		sb.append("Win Path Proof");
		for (int[] gamePath : relevantFrames) 
		{
//			if(gamePath[0] == 1) {
				for (int j = 0; j < gamePath.length; j++) {
					sb.append(gamePath[j] + ",");		
				}
				sb.append("\n");
//			}
		}
		
		
        pw.write(sb.toString());
        pw.close();
	}
	
	public static void getAgentMetrics(ArrayList<BunchOfGames> bogs, String game, GameDescription gd) {
		ScalableQuery scalableQuery = new ScalableQuery();
		bogsSize = bogs.size();
		try {
			numInts = scalableQuery.numberOfUniqueInteractions(bogs.size());
			numIntSprites = scalableQuery.numberOfInteractedSprites(bogs.size(), "examples/gridphysics/zelda.txt");
			numIntAvatar = new int[bogs.size()];
			int count = 0;
			for(SpriteData avatar : gd.getAvatar()) {
				
				int [] numberOfInteractedSpritesWithThisParticularSprite
					= scalableQuery.numberOfInteractedSpritesWithThisSprite(avatar.name, 3, "examples/gridphysics/zelda.txt");
				if(count == 0) {
					for(int i = 0; i < numberOfInteractedSpritesWithThisParticularSprite.length; i++) {
						numIntAvatar[i] = numberOfInteractedSpritesWithThisParticularSprite[i];
					}
				} else{
					for(int i = 0; i < numberOfInteractedSpritesWithThisParticularSprite.length; i++) {
						numIntAvatar[i] += numberOfInteractedSpritesWithThisParticularSprite[i];
					}
				}
			}
			
			firstFrame = scalableQuery.firstFrameOfInteraction(bogs.size());
		} catch (IOException | ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void winPathing(int[][] relevantFrames) {
		Metrics.relevantFrames = relevantFrames;
//		winPath = new int[superP.size()][][];
//		for(int i = 1; i < superP.size()-1; i++) {
//			winPath[i] = new int[superP.get(i).size()][];
//			for(int j = 1; j < superP.get(i).size(); j++) {
//				String sprite1 = superP.get(i).get(j).getObject1().getName();
//				String sprite2 = superP.get(i).get(j).getObject2().getName();
//				String action = superP.get(i).get(j).getAction().getName(); 
//				
//				try {
//					winPath[i][j-1] = scalableQuery.firstFrameOfSpecifiedInteraction(new Interaction(action, sprite1, sprite2), bogsSize);
//				} catch (IOException | ParseException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//			}
//		}
	}
}
