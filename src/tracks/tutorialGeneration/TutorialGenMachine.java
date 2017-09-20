package tracks.tutorialGeneration;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import core.competition.CompetitionParameters;
import core.game.Game;
import core.game.GameDescription;
import core.game.SLDescription;
import core.generator.AbstractTutorialGenerator;
import core.vgdl.VGDLFactory;
import core.vgdl.VGDLParser;
import core.vgdl.VGDLRegistry;
import tools.ElapsedCpuTimer;
import tools.IO;

public class TutorialGenMachine {
	
	public static boolean generateOneTutorial(String gameFile, String tutorialGenerator, String levelFile, String tutorialFile, int randSeed)
	{
        VGDLFactory.GetInstance().init(); // This always first thing to do.
        VGDLRegistry.GetInstance().init();
	
        System.out.println(
                " ** Generating a tutorial for " + gameFile + ", using tutorial generator " + tutorialGenerator + " **");
        
        // First, we create the game to be played..
        Game toPlay = new VGDLParser().parseGame(gameFile);
		String[] lines = new IO().readFile(levelFile);

        try{
        GameDescription description = new GameDescription(toPlay);
        SLDescription slDescription = new SLDescription(toPlay, lines, randSeed);
        
        AbstractTutorialGenerator generator = createTutorialGenerator(tutorialGenerator, slDescription, description);
        String[] tutorial = getGeneratedTutorial(description, slDescription, toPlay, generator);

        if(tutorial.equals(null)) {
        	System.out.println("Empty tutorial disqualified.");
        	toPlay.disqualify();
        	
            // Get the score for the result.
            toPlay.handleResult();
            toPlay.printResult();
            return false;
        }
        if(tutorialFile != null) {
        	saveTutorial(tutorial, tutorialFile);
        }
        return true;
        }
        catch (Exception e) {
			toPlay.disqualify();
			toPlay.handleResult();
			toPlay.printResult();
			System.out.println(e.getMessage());
			e.printStackTrace();
			return false;
		}

	}
	



	/// 	PRIVATE METHODS	:


	protected static AbstractTutorialGenerator createTutorialGenerator(String tutorialGenerator, SLDescription sl, GameDescription gd) 
			throws RuntimeException{
		AbstractTutorialGenerator generator = null;
		try {
			
            // Get the class and the constructor with arguments
            Class<? extends AbstractTutorialGenerator> controllerClass = Class.forName(tutorialGenerator)
                    .asSubclass(AbstractTutorialGenerator.class);
            Class[] gameArgClass = new Class[] { SLDescription.class, GameDescription.class, ElapsedCpuTimer.class };
            Constructor controllerArgsConstructor = controllerClass.getConstructor(gameArgClass);
            
            // Determine the time due for the controller creation.
            ElapsedCpuTimer ect = new ElapsedCpuTimer();
            ect.setMaxTimeMillis(CompetitionParameters.TUTORIAL_INITIALIZATION_TIME);
            
            // Call the constructor with the appropriate parameters.
            Object[] constructorArgs = new Object[] { sl, gd, ect.copy() };
            generator = (AbstractTutorialGenerator) controllerArgsConstructor.newInstance(constructorArgs);
            
            // Check if we returned on time, and act in consequence.
            long timeTaken = ect.elapsedMillis();
            if (ect.exceededMaxTime()) {
                long exceeded = -ect.remainingTimeMillis();
                System.out.println("Generator initialization time out (" + exceeded + ").");

                return null;
            } else {
                System.out.println("Generator initialization time: " + timeTaken + " ms.");
            }
            
         // This code can throw many exceptions (no time related):

        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            System.err.println(
                    "Constructor " + tutorialGenerator + "(StateObservation,long) not found in controller class:");
            System.exit(1);

        } catch (ClassNotFoundException e) {
            System.err.println("Class " + tutorialGenerator + " not found for the controller:");
            e.printStackTrace();
            System.exit(1);

        } catch (InstantiationException e) {
            System.err.println("Exception instantiating " + tutorialGenerator + ":");
            e.printStackTrace();
            System.exit(1);

        } catch (IllegalAccessException e) {
            System.err.println("Illegal access exception when instantiating " + tutorialGenerator + ":");
            e.printStackTrace();
            System.exit(1);
        } catch (InvocationTargetException e) {
            System.err.println("Exception calling the constructor " + tutorialGenerator + "(StateObservation,long):");
            e.printStackTrace();
            System.exit(1);
        }

        return generator;
	}
	
	protected static String[] getGeneratedTutorial(GameDescription description, SLDescription slDescription, Game toPlay,
			AbstractTutorialGenerator generator) {
        ElapsedCpuTimer ect = new ElapsedCpuTimer();
        ect.setMaxTimeMillis(CompetitionParameters.TUTORIAL_ACTION_TIME);
        String[] tutorial = generator.generateTutorial(description, slDescription, ect.copy());
        if (ect.exceededMaxTime()) {
            long exceeded = -ect.remainingTimeMillis();

            if (ect.elapsedMillis() > CompetitionParameters.TUTORIAL_ACTION_TIME_DISQ) {
                // The agent took too long to replay. The game is over and the
                // agent is disqualified
                System.out.println("Too long: " + "(exceeding " + (exceeded) + "ms): controller disqualified.");
                tutorial = null;
            } else {
                System.out.println("Overspent: " + "(exceeding " + (exceeded) + "ms): applying Empty Level.");
                tutorial = null;
            }
        }

        return tutorial;
	}
	
	private static void saveTutorial(String[] tutorial, String tutorialFile) {
		try{
			if(tutorialFile != null) {
				BufferedWriter writer = new BufferedWriter(new FileWriter(tutorialFile));
                writer.write("TutorialFile");
                writer.newLine();
                for(String tutorialLine : tutorial) {
                	writer.write(tutorialLine);
                	writer.newLine();
                }
                writer.close();
			}
		} catch (IOException e) {
            e.printStackTrace();
        }
		
	}
		
}

