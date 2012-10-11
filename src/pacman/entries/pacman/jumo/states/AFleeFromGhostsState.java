package pacman.entries.pacman.jumo.states;

import pacman.game.Constants.GHOST;
import pacman.game.Game;

public abstract class AFleeFromGhostsState extends AState {
	// Placements in the arguments array
	private static int MIN_DISTANCE = 1;

	public int[][] getParameterRanges() {
		return new int[][] {
				{0, AState.MAXIMUM_DISTANCE}	
		};
	}
	
	public AFleeFromGhostsState(int[] arguments) {
		super(arguments);
	}
	
	@Override
	public boolean willHandleGameState(Game game) {
		int minimum_distance = Integer.MAX_VALUE;
		int pacman_pos = game.getPacmanCurrentNodeIndex();
		
		for (GHOST g : GHOST.values()) {
			if (game.isGhostEdible(g) || game.getGhostLairTime(g) > 0) {
				continue;
			}
			
			int distance_to_ghost = game.getShortestPathDistance(game.getGhostCurrentNodeIndex(g), pacman_pos);
			
			minimum_distance = Math.min(minimum_distance, distance_to_ghost);
		}

		return minimum_distance != Integer.MAX_VALUE && minimum_distance < arguments[MIN_DISTANCE];
	}
}
