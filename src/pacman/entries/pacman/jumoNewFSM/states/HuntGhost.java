package pacman.entries.pacman.jumoNewFSM.states;

import java.util.ArrayList;

import pacman.entries.pacman.jumoutility.Ghosts;
import pacman.entries.pacman.jumoutility.Paths;
import pacman.game.Constants.DM;
import pacman.game.Constants.MOVE;
import pacman.game.Game;
import pacman.game.Constants.GHOST;

public class HuntGhost extends AState {

	public HuntGhost() {
		this(new int[0]);
	}
	
	public HuntGhost(int[] arguments) {
		this._arguments = new int[PARAMS.values().length];
		
		for (int i = 0; i < arguments.length && i < _arguments.length; i++) {
			this._arguments[i] = arguments[i];
		}
	}
	
	public enum PARAMS {
		maximum_distance_to_nearest_edible_ghost,
		maximum_number_of_regular_ghosts,
		minimum_remaining_edible_time,
		minimum_distance_to_nearest_regular_ghost,
	}
	
	@Override
	public int[][] getArgumentRanges() {
		return new int[][] {
				{ 0, AState.MAX_DISTANCE },
				{ 0, GHOST.values().length },
				{ 0, 200 },
				{ 0, AState.MAX_DISTANCE },
		};
	}

	@Override
	public boolean canHandle(Game game) {
		int pacman_pos = game.getPacmanCurrentNodeIndex();
		
		return  arg(PARAMS.maximum_distance_to_nearest_edible_ghost) >= Paths.ShortestDistanceFromClosestEdibleGhost(game, pacman_pos) &&
				arg(PARAMS.maximum_number_of_regular_ghosts) >= Ghosts.NumberOfRegularGhosts(game) &&
				arg(PARAMS.minimum_remaining_edible_time) <= Ghosts.MinimumEdibleTime(game) &&
				arg(PARAMS.minimum_distance_to_nearest_regular_ghost) <= Paths.ShortestDistanceFromClosestGhost(game, pacman_pos);
	}

	@Override
	public int getNumberOfArguments() {
		return PARAMS.values().length;
	}

	@Override
	public MOVE move(Game game, long timeDue) {
		ArrayList<Integer> edible_ghost_indices = new ArrayList<Integer>();
		
		for (GHOST g : GHOST.values()) {
			if (game.getGhostEdibleTime(g) >= arg(PARAMS.minimum_remaining_edible_time)) {
				edible_ghost_indices.add(game.getGhostCurrentNodeIndex(g));
			}
		}
		
		int[] edible_ghost_indices_array = new int[edible_ghost_indices.size()];
		
		for (int i = 0; i < edible_ghost_indices_array.length; i++) {
			edible_ghost_indices_array[i] = edible_ghost_indices.get(i);
		}
		
		int closest_ghost_index = game.getClosestNodeIndexFromNodeIndex(game.getPacmanCurrentNodeIndex(), edible_ghost_indices_array, DM.PATH);
		
		return game.getNextMoveTowardsTarget(game.getPacmanCurrentNodeIndex(), closest_ghost_index, DM.PATH);
	}
}
