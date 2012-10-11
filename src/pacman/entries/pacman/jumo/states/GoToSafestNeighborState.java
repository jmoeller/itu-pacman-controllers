package pacman.entries.pacman.jumo.states;

import java.awt.Color;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import com.sun.xml.internal.bind.v2.runtime.unmarshaller.XsiNilLoader.Array;

import pacman.game.Game;
import pacman.game.GameView;
import pacman.game.Constants.*;

public class GoToSafestNeighborState extends AState {
	private static int MAXIMUM_DISTANCE_TO_INEDIBLE_GHOST = 1;
	private static int MINIMUM_LAIR_TIME = 2;
	
	public GoToSafestNeighborState(int[] arguments) {
		super(arguments);
	}
	
	@Override
	public boolean willHandleGameState(Game game) {
		int distance_to_closest_inedible_ghost = Integer.MAX_VALUE;
		
		for (GHOST g : GHOST.values()) {
			if (game.isGhostEdible(g)) { continue; }
			if (game.getGhostLairTime(g) > arguments[MINIMUM_LAIR_TIME]) { continue; }
			
			int distance;
			
			if (game.getGhostLairTime(g) > 0) {
				distance = game.getShortestPathDistance(game.getGhostInitialNodeIndex(), game.getPacmanCurrentNodeIndex());
			}
			else
			{
				distance = game.getShortestPathDistance(game.getGhostCurrentNodeIndex(g), game.getPacmanCurrentNodeIndex());
			}
			
			if (distance > 0 && distance < distance_to_closest_inedible_ghost) {
				distance_to_closest_inedible_ghost = distance;
			}
		}
		
		return distance_to_closest_inedible_ghost < MAXIMUM_DISTANCE_TO_INEDIBLE_GHOST;
	}

	@Override
	public int[][] getParameterRanges() {
		// TODO Auto-generated method stub
		return null;
	}
	
	private int[] findIndexes(Game game) {
		HashSet<Integer> is = new HashSet<Integer>();
		
		for (pacman.game.internal.Node n : game.getCurrentMaze().graph) {
			is.add(n.nodeIndex);
		}
		
		int indexes[] = new int[is.size()];
		int i = 0;
		for (int index : is) {
			indexes[i++] = index;
		}
		
		return indexes;
	}
	
	@Override
	public MOVE getNextMove(Game game, long timeDue) {
		int indexes[] = findIndexes(game);
	
		int pacman_pos = game.getPacmanCurrentNodeIndex();
		
		HashMap<Integer, Integer> pathscores = new HashMap<Integer, Integer>();
		
		for (int i = 0; i < indexes.length; i++) {
			int node_index = indexes[i];
			
			// The shortest distance from _a_ ghost to the point
			int ghost_path = 0;
			int ghost_count = 0;
			
			for (GHOST g : GHOST.values()) {
				if (game.isGhostEdible(g) || game.getGhostLairTime(g) > 0) { continue; }
				
				ghost_count += 1;
				
				int ghost_distance = game.getShortestPathDistance(game.getGhostCurrentNodeIndex(g), node_index);
				
				ghost_path += ghost_distance;
			}
			
			int average_ghost_path = Integer.MAX_VALUE;
			if (ghost_count > 0) {
				average_ghost_path = ghost_path / ghost_count;
			}
			
			// The shortest distance from Pacman to the point
			int shortest_path[];
			try {
				shortest_path = game.getShortestPath(pacman_pos, node_index);
			}
			catch (ArrayIndexOutOfBoundsException e) {
				shortest_path = new int[0];
			}
			
			int shortest_distance = shortest_path.length;
			
			int safe_score = average_ghost_path - shortest_distance;
			
			if (game.isPillStillAvailable(node_index) && safe_score > 0) {
				safe_score += 100;
			}
			
			// Update all points in the path with a new 'safe score' if it is lower or absent
			for (int path_part : shortest_path) {
				if (!pathscores.containsKey(path_part) || pathscores.get(path_part) > safe_score) {
					pathscores.put(path_part, safe_score);
				}
			}
		}
		
		int neighbor_nodes[] = game.getNeighbouringNodes(pacman_pos);
		
		int best_neighbor = -1;
		int best_score = Integer.MIN_VALUE;
		for (int n : neighbor_nodes) {
			if (n == pacman_pos) { continue; }
			
			Integer score = pathscores.get(n);
			if (score != null && score.intValue() > best_score) {
				best_neighbor = n;
				best_score = score.intValue();
			}
		}
		
		if (best_neighbor == -1) {
			return MOVE.NEUTRAL;
		}
		
		return game.getNextMoveTowardsTarget(pacman_pos, best_neighbor, DM.PATH);
	}
}
