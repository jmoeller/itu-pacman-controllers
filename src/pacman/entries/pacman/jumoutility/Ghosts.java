package pacman.entries.pacman.jumoutility;

import pacman.game.Game;
import pacman.game.Constants.GHOST;;

public class Ghosts {
	public static int NumberOfEdibleGhosts(Game game) {
		int edible = 0;
		
		for (GHOST g : GHOST.values()) {
			if (game.isGhostEdible(g)) {
				edible += 1;
			}
		}
		
		return edible;
	}
	
	public static int NumberOfGhostsInLair(Game game) {
		int in_lair = 0;
		
		for (GHOST g : GHOST.values()) {
			if (game.getGhostLairTime(g) > 0) {
				in_lair += 1;
			}
		}
		
		return in_lair;
	}
	
	public static int NumberOfRegularGhosts(Game game) {
		return GHOST.values().length - NumberOfEdibleGhosts(game) - NumberOfGhostsInLair(game);
	}
	
	public static int MinimumEdibleTime(Game game) {
		int minimum_time = Integer.MAX_VALUE;
		
		for (GHOST g : GHOST.values()) {
			int edible_time = game.getGhostEdibleTime(g);
			
			if (edible_time > 0 && edible_time < minimum_time) {
				minimum_time += 1;
			}
		}
		
		if (minimum_time == Integer.MAX_VALUE) {
			minimum_time = 0;
		}
		
		return minimum_time;
	}
}
