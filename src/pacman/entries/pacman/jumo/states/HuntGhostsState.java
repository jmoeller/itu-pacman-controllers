package pacman.entries.pacman.jumo.states;

import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class HuntGhostsState extends AState {

	private static int MAX_DISTANCE_TO_EDIBLE_GHOST = 1;
	private static int MIN_EDIBLE_TIME = 2;
	private static int MIN_DISTANCE_TO_INEDIBLE_GHOST = 3;


	@Override
	public int[][] getParameterRanges() {
		return new int[][] {
				{0, AState.MAXIMUM_DISTANCE},
				{0, 1000},
				{0, AState.MAXIMUM_DISTANCE},
		};
	}
	
	public HuntGhostsState(int[] arguments) {
		super(arguments);
	}
	
	@Override
	public boolean willHandleGameState(Game game) {
		boolean edible_ghost_in_distance = false;
		
		int closest_inedible_ghost_distance = Integer.MAX_VALUE;
		for (GHOST g : GHOST.values()) {
			int shortest_path_distance = game.getShortestPathDistance(game.getPacmanCurrentNodeIndex(), game.getGhostCurrentNodeIndex(g));

			edible_ghost_in_distance = edible_ghost_in_distance ||
					(
							game.getGhostEdibleTime(g) > arguments[MIN_EDIBLE_TIME] &&
							arguments[MAX_DISTANCE_TO_EDIBLE_GHOST] > shortest_path_distance
					);
			
			
			if (game.getGhostEdibleTime(g) == 0) {
				closest_inedible_ghost_distance = Math.min(closest_inedible_ghost_distance, shortest_path_distance);
			}
		}
		
		return edible_ghost_in_distance && closest_inedible_ghost_distance >= arguments[MIN_DISTANCE_TO_INEDIBLE_GHOST];
	}

	@Override
	public MOVE getNextMove(Game game, long timeDue) {
		int minimum_distance = Integer.MAX_VALUE;
		int closest_ghost_index = -1;
		int pacman_position = game.getPacmanCurrentNodeIndex();
		
		for (GHOST g : GHOST.values()) {
			if (!game.isGhostEdible(g) || game.getGhostLairTime(g) > 0) { continue; }
			
			int ghost_position = game.getGhostCurrentNodeIndex(g);
			int distance = game.getShortestPathDistance(pacman_position, ghost_position);
			
			if (distance < minimum_distance) {
				minimum_distance = distance;
				closest_ghost_index = ghost_position;
			}
		}
		
		if (closest_ghost_index != -1) {
			return game.getNextMoveTowardsTarget(pacman_position, closest_ghost_index, DM.PATH);
		}
		else {
			return MOVE.NEUTRAL;
		}
	}
}
