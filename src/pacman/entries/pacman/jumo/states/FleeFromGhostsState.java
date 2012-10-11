package pacman.entries.pacman.jumo.states;

import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Game;
import pacman.game.Constants.MOVE;

public class FleeFromGhostsState extends AFleeFromGhostsState {

	public FleeFromGhostsState(int[] arguments) {
		super(arguments);
	}
	
	@Override
	public MOVE getNextMove(Game game, long timeDue) {
		int pacman_index = game.getPacmanCurrentNodeIndex();
		int highest_distance = Integer.MIN_VALUE;
		int highest_distance_index = -1;
		
		for (int n : game.getNeighbouringNodes(pacman_index)) {
			int distance_to_node = 0;
			
			for (GHOST g : GHOST.values()) {
				if (game.isGhostEdible(g)) {
					continue;
				}
				
				int ghost_distance;
				
				if (game.getGhostLairTime(g) > 0) {
					ghost_distance = game.getShortestPathDistance(game.getGhostInitialNodeIndex(), n);
				}
				else {
					ghost_distance = game.getShortestPathDistance(game.getGhostCurrentNodeIndex(g), n);
				}
				
				if (ghost_distance >= 0 && ghost_distance < distance_to_node) {
					distance_to_node = ghost_distance;
				}
			}
			
			if (distance_to_node > highest_distance) {
				highest_distance_index = n;
				highest_distance = distance_to_node;
			}
		}
		
		if (highest_distance_index != -1) {
			return game.getNextMoveTowardsTarget(pacman_index, highest_distance_index, game.getPacmanLastMoveMade(), DM.PATH);
		}
		else {
			return MOVE.NEUTRAL;
		}
	}
}
