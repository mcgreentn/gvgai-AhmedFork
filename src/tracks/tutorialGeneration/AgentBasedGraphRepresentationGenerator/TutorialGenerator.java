package tracks.tutorialGeneration.AgentBasedGraphRepresentationGenerator;

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
		
		generatedTutorialList.addAll(getControlsInformation(graph));
		generatedTutorialList.addAll(traceCriticalPaths(graph));
		generatedTutorialList.addAll(findPoints(graph));
		generatedTutorialList.addAll(findLosses(graph));
		
		System.out.println("\n\nStart Tutorial File");
		for(String instruction : generatedTutorialList) {
			System.out.println(instruction);
		}
		return generatedTutorialList.toArray(generatedTutorial);
	}
	
	public ArrayList<String> traceCriticalPaths(GraphBuilder graph) {
		ArrayList<String> pathText = new ArrayList<String>();
		for(Entity terminal : graph.victoryTerminations) {
			ArrayList<String> longestPath = new ArrayList<String>();
			for(Entity av : graph.getAvatarEntites()) {
				
				// find longest path for avatar to win
				ArrayList<String> temp = graph.traceUserInteractionChain(av, terminal);
				if(temp.size() > longestPath.size()) {
					longestPath = temp;
				}

			}
			String tip = "This is how you win:";
			pathText.add(tip);
			pathText.addAll(longestPath);
			pathText.add("");
		}
	
		return pathText;
	}
	
	public ArrayList<String> findPoints(GraphBuilder graph) {
		ArrayList<String> pathText = new ArrayList<String>();
		
		String tip = "This is how you get points:";
		pathText.add(tip);
		pathText.addAll(graph.scoreChangers());
		pathText.add("");
		
		return pathText;
	}
	
	public ArrayList<String> findLosses(GraphBuilder graph) {
		ArrayList<String> pathText = new ArrayList<String>();
		
		String tip = "This is how you lose:";
		pathText.add(tip);
		pathText.addAll(graph.lossConditions());
		pathText.add("");
		
		return pathText;
	}
	
	public ArrayList<String> getControlsInformation(GraphBuilder graph) {
		ArrayList<Entity> avatars = graph.getAvatarEntites();
		ArrayList<String> movementTutorialList = new ArrayList<String>();
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
			movementTutorial += "To move as the " + avatarType + ",";
			if(avatarType.equals("MovingAvatar")){
				movementTutorial += " use the four arrow keys to move.";
			} else if(avatarType.equals("HorizontalAvatar") || avatarType.equals("FlakAvatar")) {
				movementTutorial += " use the left and right arrow keys to move.";
				// extra information needed for OngoingShoot, Shoot, and Flak avatars
				if(avatarType.equals("FlakAvatar")) {
					movementTutorial += extraMovementInformation(avatarType, avatars, graph);
				}
			} else if(avatarType.equals("VerticalAvatar")) {
				movementTutorial += " use the up and down arrow keys to move.";
			} else if(avatarType.equals("OngoingAvatar") || avatarType.equals("OngoingShootAvatar")) {
				// extra information needed for OngoingShoot, Shoot, and Flak avatars
				movementTutorial += " use the arrow keys to change direction.";
				if(avatarType.equals("OngoingShootAvatar")) {
					movementTutorial += extraMovementInformation(avatarType, avatars, graph);
				}
			} else if(avatarType.equals("OngoingTurningAvatar")) {
				// TODO : reword this better?
				movementTutorial += " use the arrow keys to change direction. You cannot do 180 degree turns!";
			} else if(avatarType.equals("MissileAvatar")) {
				// TODO : Figure out what this means
				movementTutorial += " unsure what this means...";
			} else if(avatarType.equals("OrientedAvatar") || avatarType.equals("ShootAvatar")) {
				movementTutorial += " use the arrow keys to turn and move.";
				// extra information needed for OngoingShoot, Shoot, and Flak avatars
				if(avatarType.equals("ShootAvatar")) {
					movementTutorial += extraMovementInformation(avatarType, avatars, graph);
				}

			} else {
				movementTutorial += " the system is unsure of what this means.";
			}
		}
		movementTutorialList.add("This is how you control your avatar:");
		movementTutorialList.add(movementTutorial);
		movementTutorialList.add("");
		return movementTutorialList;
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
			extraMovementTutorial += "\nPress space to shoot a " + missileObject.getFullName() + ".";
		} else if(missileType.equals("OrientedFlicker")) {
			extraMovementTutorial += "\nPress space to use the " + missileObject.getFullName() + ".";
		} else if(missileType.equals("Immovable")) {
			extraMovementTutorial += "\nPress space to release a " + missileObject.getFullName() + ".";
		}
		return extraMovementTutorial;
	}
	
}
