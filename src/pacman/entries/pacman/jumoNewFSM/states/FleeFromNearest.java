package pacman.entries.pacman.jumoNewFSM.states;

import pacman.entries.pacman.jumoutility.Ghosts;
import pacman.entries.pacman.jumoutility.Paths;
import pacman.game.Constants.DM;
import pacman.game.Constants.MOVE;
import pacman.game.Game;
import pacman.game.Constants.GHOST;

public class FleeFromNearest extends AState {

	public FleeFromNearest() {
		this(new int[0]);
	}
	
	public FleeFromNearest(int[] arguments) {
		this._arguments = new int[PARAMS.values().length];
		
		for (int i = 0; i < arguments.length && i < _arguments.length; i++) {
			this._arguments[i] = arguments[i];
		}
	}
	
	public enum PARAMS {
		min_distance_to_nearest_ghost,
		max_distance_to_nearest_ghost,
	}
	
	@Override
	public int[][] getArgumentRanges() {
		return new int[][] {
				{ 0, AState.MAX_DISTANCE },
				{ 0, AState.MAX_DISTANCE },
		};
	}

	@Override
	public boolean canHandle(Game game) {
		if (Ghosts.NumberOfRegularGhosts(game) == 0) { return false; }
		
		int to_index = game.getPacmanCurrentNodeIndex();
		int shortest_distance = Paths.ShortestDistanceFromClosestGhost(game, to_index);
		
		if (shortest_distance == Integer.MAX_VALUE || shortest_distance < 0) { return false; }
		
		return shortest_distance < arg(PARAMS.max_distance_to_nearest_ghost) && shortest_distance >= arg(PARAMS.min_distance_to_nearest_ghost);
	}

	@Override
	public int getNumberOfArguments() {
		return PARAMS.values().length;
	}

	@Override
	public MOVE move(Game game, long timeDue) {
		GHOST closest_ghost = null;
		int shortest_distance = Integer.MAX_VALUE;
		int pacman_pos = game.getPacmanCurrentNodeIndex();
		
		for (GHOST g : GHOST.values()) {
			int distance = game.getShortestPathDistance(game.getGhostCurrentNodeIndex(g), pacman_pos);
			
			if (distance > 0 && distance < shortest_distance) {
				shortest_distance = distance;
				closest_ghost = g;
			}
		}
		
		if (closest_ghost != null) {
			return game.getNextMoveAwayFromTarget(pacman_pos, game.getGhostCurrentNodeIndex(closest_ghost), DM.PATH);
		}
		else {
			return MOVE.NEUTRAL;
		}
	}
}
