package pacman.entries.pacman.jumo.states;

import pacman.game.Constants.*;
import pacman.game.Game;

public class GoToPowerPillState extends AState {
	private static int MAXIMUM_DISTANCE_TO_PP = 1;
	private static int MINIMUM_DISTANCE_TO_GHOST = 2;
	private static int MAXIMUM_DISTANCE_TO_GHOST = 3;
	private static int MINIMUM_NUMBER_OF_GHOSTS_ALIVE = 4;
	
	public int[][] getParameterRanges() {
		return new int[][] {
				{0, AState.MAXIMUM_DISTANCE},
				{0, AState.MAXIMUM_DISTANCE},
				{0, AState.MAXIMUM_DISTANCE},
				{0, 4},
		};
	}
	
	public GoToPowerPillState(int[] arguments) {
		super(arguments);
	}

	@Override
	public boolean willHandleGameState(Game game) {
		int[] pp_indices = game.getActivePillsIndices();
		
		if (pp_indices.length == 0) { return false; }
		
		int pacman_pos = game.getPacmanCurrentNodeIndex();
		
		boolean pp_valid = false;
		boolean a_ghost_valid = false;
		
		for (int ppi : pp_indices) {
			int distance = game.getShortestPathDistance(pacman_pos, ppi);
			pp_valid = pp_valid || distance < arguments[MAXIMUM_DISTANCE_TO_PP] && distance > 0;
		}
		
		int ghosts_alive = 0;
		for (GHOST g : GHOST.values()) {
			if (game.getGhostLairTime(g) > 0) { continue; }
			
			ghosts_alive += 1;
			
			int distance = game.getShortestPathDistance(game.getGhostCurrentNodeIndex(g), pacman_pos);
			
			a_ghost_valid = a_ghost_valid || (distance > arguments[MINIMUM_DISTANCE_TO_GHOST] && distance < arguments[MAXIMUM_DISTANCE_TO_GHOST]);
		}
		
		return pp_valid && a_ghost_valid && ghosts_alive >= arguments[MINIMUM_NUMBER_OF_GHOSTS_ALIVE];
	}

	// Assumes that there is a power pill on the board!
	@Override
	public MOVE getNextMove(Game game, long timeDue) {
		int pacman_pos = game.getPacmanCurrentNodeIndex();
		int closest_distance = Integer.MAX_VALUE;
		int closest_index = -1;
		
		for (int ppi : game.getActivePowerPillsIndices()) {
			int distance = game.getShortestPathDistance(pacman_pos, ppi, game.getPacmanLastMoveMade());
			
			if (distance < closest_distance) {
				closest_distance = distance;
				closest_index = ppi;
			}
		}
		
		if (closest_index == -1) { return MOVE.NEUTRAL; }
		
		return game.getNextMoveTowardsTarget(pacman_pos, closest_index, DM.PATH);
	}

}
