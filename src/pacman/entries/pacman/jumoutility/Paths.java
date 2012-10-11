package pacman.entries.pacman.jumoutility;

import pacman.game.Constants.*;
import pacman.game.Game;

public class Paths {
	public static int[] DistancesFromGhosts(Game game, int to_index) {
		int[] distances = new int[GHOST.values().length];
		
		int i = 0;
		for (GHOST g : GHOST.values()) {
			int distance_to_ghost = game.getShortestPathDistance(game.getGhostCurrentNodeIndex(g), to_index);
			
			distances[i] = distance_to_ghost;
			
			i += 1;
		}
		
		return distances;
	}
	
	public static int ShortestDistanceFromClosestGhost(Game game, int to_index) {
		int shortest_distance = Integer.MAX_VALUE;
		
		for (int d : DistancesFromGhosts(game, to_index)) {
			if (d > 0) {
				shortest_distance = Math.min(shortest_distance, d);
			}
		}
		
		return shortest_distance;
	}
	
	public static int CombinedDistanceFromGhosts(Game game, int to_index) {
		int combined_distance = 0;
		
		for (int d : DistancesFromGhosts(game, to_index)) {
			combined_distance += d;
		}
		
		return combined_distance;
	}
	
	public static float AverageDistanceFromGhosts(Game game, int to_index) {
		return CombinedDistanceFromGhosts(game, to_index) / (float)GHOST.values().length;
	}
	
	public static int ShortestDistanceFromClosestEdibleGhost(Game game, int to_index) {
		int shortest_distance = Integer.MAX_VALUE;
		
		for (GHOST g : GHOST.values()) {
			if (!game.isGhostEdible(g)) { continue; }
			
			int distance = game.getShortestPathDistance(game.getGhostCurrentNodeIndex(g), to_index);
			if (distance > 0 && distance < shortest_distance) {
				shortest_distance = distance;
			}
		}
		
		return shortest_distance;
	}
}
