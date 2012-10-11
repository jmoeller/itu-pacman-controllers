package pacman.entries.pacman.jumoNewFSM.states;

import pacman.game.Constants.DM;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class Junction extends AState {

	public Junction() {
		this(new int[0]);
	}
	
	public Junction(int[] arguments) {
		this._arguments = new int[PARAMS.values().length];
		
		for (int i = 0; i < arguments.length && i < _arguments.length; i++) {
			this._arguments[i] = arguments[i];
		}
	}
	
	public enum PARAMS {
	}
	
	@Override
	public int[][] getArgumentRanges() {
		return new int[][] {
				{ }, 
		};
	}

	@Override
	public boolean canHandle(Game game) {
		return true;
	}

	@Override
	public int getNumberOfArguments() {
		return PARAMS.values().length;
	}

	@Override
	public MOVE move(Game game, long timeDue) {
		int closest_junction_index = game.getClosestNodeIndexFromNodeIndex(game.getPacmanCurrentNodeIndex(), game.getJunctionIndices(), DM.PATH);
		
		return game.getNextMoveTowardsTarget(game.getPacmanCurrentNodeIndex(), closest_junction_index, DM.PATH);
	}
}
