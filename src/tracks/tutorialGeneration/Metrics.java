package tracks.tutorialGeneration;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

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
	}
	
	public static void saveMetricsCSV(String fileName) throws FileNotFoundException{
        PrintWriter pw = new PrintWriter(new File(fileName));
        StringBuilder sb = new StringBuilder();
        
        sb.append(Metrics.maxHierarchy + ",");
        sb.append(Metrics.spriteCount + ",");
        sb.append(Metrics.interactionCount + ",");
        sb.append(Metrics.shownInteractionCount + ",");
        sb.append(Metrics.numberOfMergedInteractions + ",");
        sb.append(Metrics.criticalPathVictoryCount + ",");
        sb.append(Metrics.criticalPathFailureCount + ",");
        sb.append(Metrics.pointsCount);
        
        pw.write(sb.toString());
        pw.close();
	}
	
	

}
