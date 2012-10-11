package pacman.entries.pacman.jumoNewFSM.states;

import pacman.entries.pacman.jumoutility.Paths;
import pacman.game.Constants.DM;
import pacman.game.Constants.MOVE;
import pacman.game.Game;
import pacman.game.Constants.GHOST;

public class NearestJunction extends AState {

	public NearestJunction() {
		this(new int[0]);
	}
	
	public NearestJunction(int[] arguments) {
		this._arguments = new int[PARAMS.values().length];
		
		for (int i = 0; i < arguments.length && i < _arguments.length; i++) {
			this._arguments[i] = arguments[i];
		}
	}
	
	public enum PARAMS {
		min_distance_to_nearest_ghost,
		//min_average_distance_to_ghosts,
		//min_combined_distance_to_ghosts,
		max_distance_to_nearest_pill,
	}
	
	@Override
	public int[][] getArgumentRanges() {
		return new int[][] {
				{ 0, AState.MAX_DISTANCE },
				//{ 0, AState.MAX_DISTANCE },
				//{ 0, AState.MAX_DISTANCE * GHOST.values().length },	
				{ 0, AState.MAX_DISTANCE }, 
		};
	}

	@Override
	public boolean canHandle(Game game) {
		int pacman_pos = game.getPacmanCurrentNodeIndex();
		int nearest_pill = game.getClosestNodeIndexFromNodeIndex(pacman_pos, game.getActivePillsIndices(), DM.PATH);
		
		return  //arg(PARAMS.min_average_distance_to_ghosts)  < Paths.AverageDistanceFromGhosts(game, pacman_pos)        &&
				//arg(PARAMS.min_combined_distance_to_ghosts) < Paths.CombinedDistanceFromGhosts(game, pacman_pos)       &&
				arg(PARAMS.min_distance_to_nearest_ghost)   < Paths.ShortestDistanceFromClosestGhost(game, pacman_pos) &&
				nearest_pill >= 0 && arg(PARAMS.max_distance_to_nearest_pill)    > game.getShortestPathDistance(pacman_pos, nearest_pill);
	}

	@Override
	public int getNumberOfArguments() {
		return PARAMS.values().length;
	}

	@Override
	public MOVE move(Game game, long timeDue) {
		int closest_pill_index = game.getClosestNodeIndexFromNodeIndex(game.getPacmanCurrentNodeIndex(), game.getActivePillsIndices(), DM.PATH);
		
		return game.getNextMoveTowardsTarget(game.getPacmanCurrentNodeIndex(), closest_pill_index, DM.PATH);
	}
}
