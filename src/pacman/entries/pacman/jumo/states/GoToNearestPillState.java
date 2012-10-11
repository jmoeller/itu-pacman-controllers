package pacman.entries.pacman.jumo.states;

import pacman.game.Constants.*;
import pacman.game.Game;

public class GoToNearestPillState extends AState {
	// Placements in the arguments array
	private static int GHOST_DISTANCE = 1;
	private static int PILL_DISTANCE = 2;

	@Override
	public int[][] getParameterRanges() {
		return new int[][] {
				{0, AState.MAXIMUM_DISTANCE},
				{0, AState.MAXIMUM_DISTANCE},	
		};
	}

	public GoToNearestPillState(int[] arguments) {
		super(arguments);
	}
	
	// Can/want to handle the current game state if there is a certain minimum distance to the nearest ghost 
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
		int closest_pill_distance = Integer.MAX_VALUE;
		
		for (int n : game.getActivePillsIndices()) {
			int distance = game.getShortestPathDistance(pacman_pos, n);
			
			if (distance < closest_pill_distance) {
				closest_pill_distance = distance;
			}
		}

		return (arguments[GHOST_DISTANCE] < minimum_distance) && (arguments[PILL_DISTANCE] > closest_pill_distance);
	}

	@Override
	public MOVE getNextMove(Game game, long timeDue) {
		int minimum_distance = Integer.MAX_VALUE;
		int closest_pill_index = -1;
		int pacman_pos = game.getPacmanCurrentNodeIndex();
		
		for (int i : game.getActivePillsIndices()) {
			int distance = game.getShortestPathDistance(pacman_pos, i);
			
			if (distance < minimum_distance) {
				closest_pill_index = i;
				minimum_distance = distance;
			}
		}
		
		if (closest_pill_index != -1) {
			return game.getNextMoveTowardsTarget(pacman_pos, closest_pill_index, DM.PATH);
		}
		else {
			return MOVE.NEUTRAL;
		}
	}
}
