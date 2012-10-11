package pacman.entries.pacman.jumoNewFSM.states;

import pacman.entries.pacman.jumoNewFSM.states.Pill.PARAMS;
import pacman.entries.pacman.jumoutility.Paths;
import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class GoToSafestNeighbor extends AState {

	public GoToSafestNeighbor() {
		this(new int[0]);
	}
	
	public GoToSafestNeighbor(int[] arguments) {
		this._arguments = new int[PARAMS.values().length];
		
		for (int i = 0; i < arguments.length && i < _arguments.length; i++) {
			this._arguments[i] = arguments[i];
		}
	}
	
	public enum PARAMS {
		pill_score,
		power_pill_score,
		min_distance_to_ghost,
	}
	
	@Override
	public int[][] getArgumentRanges() {
		return new int[][] {
				{ 0, 100 },
				{ 0, 100 },	
				{ 0, AState.MAX_DISTANCE },
		};
	}

	@Override
	public int getNumberOfArguments() {
		return PARAMS.values().length;
	}

	@Override
	public boolean canHandle(Game game) {
		return Paths.ShortestDistanceFromClosestGhost(game, game.getPacmanCurrentNodeIndex()) > arg(PARAMS.min_distance_to_ghost);
	}

	@Override
	public MOVE move(Game game, long timeDue) {
		int pacman_pos = game.getPacmanCurrentNodeIndex();
		
		int highest_distance = Integer.MIN_VALUE;
		int neighbor_index = -1;

		for (int n : game.getNeighbouringNodes(pacman_pos)) {
			int distance = Paths.ShortestDistanceFromClosestGhost(game, n);
			
			if (distance == Integer.MAX_VALUE) {
				distance = 0;
			}
			
			if (distance > highest_distance) {
				highest_distance = distance;
				neighbor_index = n;
			}
		}
		
		if (neighbor_index != -1) {
			return game.getApproximateNextMoveTowardsTarget(pacman_pos, neighbor_index, game.getPacmanLastMoveMade(), DM.PATH);
		}
		else {
			return MOVE.NEUTRAL;
		}
		
	}
}
