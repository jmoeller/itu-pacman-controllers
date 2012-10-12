package pacman.entries.pacman.jumofsm;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import pacman.controllers.Controller;
import pacman.game.Constants.DM;
import pacman.game.Constants.MOVE;
import pacman.game.Game;
import pacman.game.GameView;

/*
 * This is the class you need to modify for your entry. In particular, you need to
 * fill in the getAction() method. Any additional classes you write should either
 * be placed in this package or sub-packages (e.g., game.entries.pacman.mypackage).
 */
public class JumoFSM extends Controller<MOVE>
{	
	public static int MAX_DISTANCE = 800;
	
	@SuppressWarnings("rawtypes")
	private List<Class> available_states = new ArrayList<Class>();
	private List<IState> states = new ArrayList<IState>();
	
	private int target;
	private LinkedList<Integer> path_list;
	
	private int MAX_ITERATIONS;
	private int iterations_on_path = 0;
	
	private static int[] DEFAULT_GENOME = new int[] {
		157, 27, 97, 172,
		82, 16, 172, 11,
		106, 15, 170,
		159, 179,
		
		56,
	};
	
	public static int GENOME_LENGTH = DEFAULT_GENOME.length;
	public static int MAX_GENOME_VALUE = 200;
	
	public JumoFSM() {
		this(DEFAULT_GENOME);
	}
	
	public JumoFSM(int[] genome) {
		available_states.add(PillState.class);
		available_states.add(GoToPowerPillState.class);
		available_states.add(HuntGhostState.class);
		available_states.add(SafestNodeState.class);
		
		MAX_ITERATIONS = genome[genome.length - 1];
		
		for (int i = 0; i < available_states.size(); i++) {
			try {
				IState state = (IState)available_states.get(i).newInstance();
				state.setPriority(genome[i * 4]);
				
				for (int j = 0; j < state.getNumberOfParams(); j++) {
					state.setParam(j, genome[i * 4 + j + 1]);
				}
				
				states.add(state);
			} catch (InstantiationException | IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		
		Collections.sort(states);
		
		path_list = new LinkedList<Integer>();
	}
	
	public MOVE getMove(Game game, long timeDue) 
	{
		if (game.wasPacManEaten()) {
			path_list = null;
		}
		
		int current = game.getPacmanCurrentNodeIndex();
		
		if (path_list != null && path_list.size() > 0 && iterations_on_path < MAX_ITERATIONS) {
			iterations_on_path += 1;
			
			//DrawPath(game, path_list);
			return game.getNextMoveTowardsTarget(current, path_list.pollFirst(), DM.PATH);
		}
		
		iterations_on_path = 0;
		
		String statename = "";
		
		boolean go_to_target = true;
		
		for (IState state : states) {
			
			if (state.canHandle(game)) {
				statename = state.getClass().getName();
				
				if (state.returnsTarget()) {
					target = state.getTarget(game);
				}
				else {
					int[] path = state.getPath(game);
					go_to_target = false;
					
					path_list = new LinkedList<Integer>();
					for (int i : path) { path_list.add(i); }
				}
				
				break;
			}
		}
		
		
		if (go_to_target) {
			//DrawPath(game, game.getShortestPath(current, target));
			try {
				return game.getNextMoveTowardsTarget(current, target, DM.PATH);
			} catch (ArrayIndexOutOfBoundsException e) {
				System.out.println(statename);
				e.printStackTrace();
				
				return MOVE.NEUTRAL;
			}
		}
		else {
			//DrawPath(game, path_list);
			return game.getNextMoveTowardsTarget(current, path_list.pollFirst(), DM.PATH);
		}
	}

	private static void DrawPath(Game game, int[] path) {
		int draw_from = game.getPacmanCurrentNodeIndex();
		
		for (int draw_to : path) {
			GameView.addLines(game, Color.CYAN, draw_from, draw_to);
			draw_from = draw_to;
		}
	}
	
	private static void DrawPath(Game game, LinkedList<Integer> path) {
		int[] path_array = new int[path.size()];
		for (int i = 0; i < path_array.length; i++) { path_array[i] = path.get(i); }
		
		DrawPath(game, path_array);
	}
}