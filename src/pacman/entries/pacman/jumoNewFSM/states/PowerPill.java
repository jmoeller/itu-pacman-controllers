package pacman.entries.pacman.jumoNewFSM.states;

import pacman.entries.pacman.jumoutility.Ghosts;
import pacman.entries.pacman.jumoutility.Paths;
import pacman.game.Constants.DM;
import pacman.game.Constants.MOVE;
import pacman.game.Game;
import pacman.game.Constants.GHOST;

public class PowerPill extends AState {

	public PowerPill() {
		this(new int[0]);
	}
	
	public PowerPill(int[] arguments) {
		this._arguments = new int[PARAMS.values().length];
		
		for (int i = 0; i < arguments.length && i < _arguments.length; i++) {
			this._arguments[i] = arguments[i];
		}
	}
	
	public enum PARAMS {
		min_distance_to_nearest_ghost,
		max_distance_to_nearest_ghost,
		
		//min_average_distance_to_ghosts,
		//max_average_distance_to_ghosts,
		
		//min_combined_distance_to_ghosts,
		//max_combined_distance_to_ghosts,
		
		max_distance_to_nearest_power_pill,
		
		min_number_of_ghosts_not_in_lair,
	}
	
	@Override
	public int[][] getArgumentRanges() {
		return new int[][] {
				{ 0, AState.MAX_DISTANCE },
				{ 0, AState.MAX_DISTANCE },
				
				//{ 0, AState.MAX_DISTANCE },
				//{ 0, AState.MAX_DISTANCE },
				
				//{ 0, AState.MAX_DISTANCE * GHOST.values().length },
				//{ 0, AState.MAX_DISTANCE * GHOST.values().length },
				
				{ 0, AState.MAX_DISTANCE },
				
				{ 0, GHOST.values().length },
		};
	}

	@Override
	public boolean canHandle(Game game) {
		int pacman_pos = game.getPacmanCurrentNodeIndex();
		int closest_power_pill_distance = game.getClosestNodeIndexFromNodeIndex(pacman_pos, game.getActivePowerPillsIndices(), DM.PATH);
		
		int   shortest_distance			   = Paths.ShortestDistanceFromClosestGhost(game, pacman_pos);
		float average_distance			   = Paths.AverageDistanceFromGhosts(game, pacman_pos);
		int   combined_distance			   = Paths.CombinedDistanceFromGhosts(game, pacman_pos);
		int   number_of_ghosts_not_in_lair = Ghosts.NumberOfRegularGhosts(game) - Ghosts.NumberOfGhostsInLair(game);
		
		boolean shortest_distance_valid = arg(PARAMS.min_distance_to_nearest_ghost)    <= shortest_distance && shortest_distance <= arg(PARAMS.max_distance_to_nearest_ghost);
		//boolean average_distance_valid  = arg(PARAMS.min_average_distance_to_ghosts)   <= average_distance  && average_distance  <= arg(PARAMS.max_average_distance_to_ghosts);
		//boolean combined_distance_valid = arg(PARAMS.min_combined_distance_to_ghosts)  <= combined_distance && combined_distance <= arg(PARAMS.max_combined_distance_to_ghosts);
		
		boolean pill_valid				= closest_power_pill_distance > 0 && closest_power_pill_distance <= arg(PARAMS.max_distance_to_nearest_power_pill);
		boolean number_of_ghosts_valid  = arg(PARAMS.min_number_of_ghosts_not_in_lair) <= number_of_ghosts_not_in_lair;
		
		return  shortest_distance_valid && /* average_distance_valid && combined_distance_valid && */ pill_valid && number_of_ghosts_valid;
	}

	@Override
	public int getNumberOfArguments() {
		return PARAMS.values().length;
	}

	@Override
	public MOVE move(Game game, long timeDue) {
		int closest_power_pill_index = game.getClosestNodeIndexFromNodeIndex(game.getPacmanCurrentNodeIndex(), game.getActivePowerPillsIndices(), DM.PATH);
		
		return game.getNextMoveTowardsTarget(game.getPacmanCurrentNodeIndex(), closest_power_pill_index, DM.PATH);
	}
}
