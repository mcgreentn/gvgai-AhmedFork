package tracks.tutorialGeneration.BFSBasedGraphRepresentationGenerator;

import java.util.ArrayList;

import core.game.GameDescription;
import core.game.SLDescription;
import core.generator.AbstractTutorialGenerator;
import core.game.GameDescription.SpriteData;
import tools.ElapsedCpuTimer;
import tools.GameAnalyzer;
import tools.LevelAnalyzer;
import tracks.tutorialGeneration.ITSetParserGenerator.Graph;
import core.logging.Logger;

public class TutorialGenerator extends AbstractTutorialGenerator{
	private LevelAnalyzer la;
	private GameAnalyzer ga;
	private GameDescription game;
	public TutorialGenerator(SLDescription sl, GameDescription game, ElapsedCpuTimer time) {
		la = new LevelAnalyzer(sl);
		ga = new GameAnalyzer(game);
		this.game = game;
	}
	
	/**
	 * Searches GameDescription.getAllSpriteData() for the given spritename
	 * @param spriteName the sprite you are looking for
	 * @return the sprite, if it exists
	 */
	private SpriteData searchForSprite(String spriteName) {
		// grab all sprites in the game
		ArrayList<SpriteData> allSpriteData = game.getAllSpriteData();
		// iterate over this list, return the sprite with the same name
		for(SpriteData me : allSpriteData) {
			if(me.name.equals(spriteName)) {
				return me;
			}
		}
		return null;
	}

	@Override
	public String[] generateTutorial(GameDescription game, SLDescription sl, ElapsedCpuTimer elapsedTimer) {
		String[] generatedTutorial = new String[0];
		ArrayList<String> generatedTutorialList = new ArrayList<String>();
		
		// Build Graph of interactions
		GraphBuilder graph = new GraphBuilder(game, sl, ga, la);
		graph.buildGraph();
		generatedTutorialList.addAll(traceCriticalPaths(graph));
		
		getControlsInformation(graph);
		return generatedTutorialList.toArray(generatedTutorial);
	}
	
	public ArrayList<String> traceCriticalPaths(GraphBuilder graph) {
		ArrayList<String> pathText = new ArrayList<String>();
		// have agent play game
		// read through log and see what mechanics are triggered the most, do search, prioritizing the most visited mechanics
		
		
//		for(Entity terminal : graph.victoryTerminations) {
//			pathText.add("Stop");
//			pathText.addAll(graph.traceUserInteractionChain(graph.getAvatarEntites().get(0), terminal));
//		}
//		
//		System.out.println("\n\nStart Tutorial File");
//		for(String instruction : pathText) {
//			System.out.println(instruction);
//		}
		return pathText;
	}
	
	public void getControlsInformation(GraphBuilder graph) {
		ArrayList<Entity> avatars = graph.getAvatarEntites();
		String movementTutorial = "";
		String avatarType = "";
		ArrayList<String> avatarTypes = new ArrayList<String>();
		for(Entity avatar : avatars) {
			avatarType = avatar.getSubtype();
			if(!avatarTypes.contains(avatarType)) {
				avatarTypes.add(avatarType);
			}
			
		}
		for(String at : avatarTypes) {
			avatarType = at;
			movementTutorial += "As the " + avatarType + ",";
			if(avatarType.equals("MovingAvatar")){
				movementTutorial += " use the four arrow keys to move.";
			} else if(avatarType.equals("HorizontalAvatar") || avatarType.equals("FlakAvatar")) {
				movementTutorial += " use the left and right arrow keys to move.";
				// extra information needed for OngoingShoot, Shoot, and Flak avatars
				movementTutorial += extraMovementInformation(avatarType, avatars, graph);
			} else if(avatarType.equals("VerticalAvatar")) {
				movementTutorial += " use the up and down arrow keys to move.";
			} else if(avatarType.equals("OngoingAvatar") || avatarType.equals("OngoingShootAvatar")) {
				// extra information needed for OngoingShoot, Shoot, and Flak avatars
				movementTutorial += " use the arrow keys to change direction.";
				movementTutorial += extraMovementInformation(avatarType, avatars, graph);
			} else if(avatarType.equals("OngoingTurningAvatar")) {
				// TODO : reword this better?
				movementTutorial += " use the arrow keys to change direction. You cannot do 180 degree turns!";
			} else if(avatarType.equals("MissileAvatar")) {
				// TODO : Figure out what this means
				movementTutorial += " unsure what this means...";
			} else if(avatarType.equals("OrientedAvatar") || avatarType.equals("ShootAvatar")) {
				movementTutorial += " use the arrow keys to turn and move.";
				// extra information needed for OngoingShoot, Shoot, and Flak avatars
				movementTutorial += extraMovementInformation(avatarType, avatars, graph);

			} else {
				movementTutorial += " the system is unsure of what this means.";
			}
		}
		System.out.println(movementTutorial);
		
	}
		
	public String extraMovementInformation(String avatarType, ArrayList<Entity> avatars, GraphBuilder graph) {
		String extraMovementTutorial = "";
		Entity avatar = null;
		for(Entity e : avatars) {
			if(e.getSubtype().equals(avatarType)) {
				avatar = e;
				break;
			}
		}
		// find the thing the avatar is shooting	
		Pair missilePair = avatar.getAttribute("stype");
		String missileName = missilePair.getValue();
		
		Entity missileObject = graph.searchObjects(missileName);
		String missileType = missileObject.getSubtype();
		if(missileType.equals("Missile")) {
			extraMovementTutorial += "\nPress space to shoot a " + missileName + ".";
		} else if(missileType.equals("OrientedFlicker")) {
			extraMovementTutorial += "\nPress space to use the " + missileName + ".";
		} else if(missileType.equals("Immovable")) {
			extraMovementTutorial += "\nPress space to release a " + missileName + ".";
		}
		return extraMovementTutorial;
	}
	
}
