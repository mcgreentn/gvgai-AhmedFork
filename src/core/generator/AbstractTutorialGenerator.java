package core.generator;

import java.util.ArrayList;
import java.util.HashMap;

import core.game.GameDescription;
import core.game.SLDescription;
import tools.ElapsedCpuTimer;

public abstract class AbstractTutorialGenerator {

	/**
	 * This function is called by the game engine to get a level description string.
	 * @param game			GameDescription object holding all game information
	 * @param elapsedTimer	count down until level generation is due
	 * @return				Level Description String that follows the game LevelMapping
	 */
	public abstract String[] generateTutorial(GameDescription game, SLDescription sl, ElapsedCpuTimer elapsedTimer);
	
	/**
	 * Optional function to get force game engine use different level mapping.
	 * @return	new level mapping if null is returned the engine use game default mapping.
	 */
	public HashMap<Character, ArrayList<String>> getLevelMapping()
	{
		return null;
	}
}
