package pacman.entries.pacman.jumo.states;

import java.util.LinkedHashSet;
import java.util.TreeSet;

import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Game;
import pacman.game.Constants.MOVE;

public class FleeFromNearestGhostState extends AFleeFromGhostsState {
	public FleeFromNearestGhostState(int[] arguments) {
		super(arguments);
	}
	
	@Override
	public MOVE getNextMove(Game game, long timeDue) {
		int pacman_index = game.getPacmanCurrentNodeIndex();
		int highest_distance = Integer.MIN_VALUE;
		int highest_distance_index = -1;
		
		for (int n : game.getNeighbouringNodes(pacman_index)) {
			int distance = Integer.MAX_VALUE;
			
			for (GHOST g : GHOST.values()) {
				if (game.isGhostEdible(g)) {
					continue;
				}
				
				int ghost_distance = game.getShortestPathDistance(game.getGhostCurrentNodeIndex(g), n);
				
				if (ghost_distance < distance && ghost_distance > 0) {
					distance = ghost_distance;
				}
			}
			
			if (distance > highest_distance && distance != Integer.MAX_VALUE) {
				highest_distance_index = n;
				highest_distance = distance;
			}
		}
		
		if (highest_distance_index != -1) {
			return game.getApproximateNextMoveTowardsTarget(pacman_index, highest_distance_index, game.getPacmanLastMoveMade(), DM.PATH);
		}
		else {
			return MOVE.NEUTRAL;
		}
	}
}
