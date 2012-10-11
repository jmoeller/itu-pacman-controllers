package pacman.entries.pacman.jumo;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import pacman.controllers.Controller;
import pacman.entries.pacman.jumo.states.*;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

/*
 * This is the class you need to modify for your entry. In particular, you need to
 * fill in the getAction() method. Any additional classes you write should either
 * be placed in this package or sub-packages (e.g., game.entries.pacman.mypackage).
 */
public class MyPacMan extends Controller<MOVE>
{
	private IState states[];
	private StateComparer sc;
	
	private static String NO_STATE_FOUND_NAME = "No applicable state found.";
	private String last_state_name = MyPacMan.NO_STATE_FOUND_NAME;
	private String this_state_name = "";
	
	private IState current_state;
		
	public MyPacMan() {
		/*
		 
				new GoToNearestPillState(new int[] { 3000, 50, 100 }),
				new HuntGhostsState(new int[] { 4100, 200, 10 }),
				new FleeFromGhostsState(new int[] { 4200, 50 }),
				new FleeFromNearestGhostState(new int[] { 4300, 50 }),
				new GoToPowerPillState(new int[] { 4000, 50, 1, 45 }),
				new GoToSafestPillState(new int[] { 10000 }),
		 */
		//this(new int[] { 68, 175, 88, 201, 70, 23, 102, 61, 59, 36, 97, 192, 185, 89, 130 });
		this(new int[] {58, 12, 49, 91, 71, 34, 18, 49, 68, 20, 69, 84, 32, 56, 73});
	}
	
	public MyPacMan(int[] p) {
		
		states = new IState[] {
				new GoToNearestPillState(new int[] { p[0], p[1], p[2] }),
				new HuntGhostsState(new int[] { p[3], p[4], p[5] }),
				new FleeFromGhostsState(new int[] { p[6], p[7] }),
				new FleeFromNearestGhostState(new int[] { p[8], p[9] }),
				new GoToPowerPillState(new int[] { p[10], p[11], p[12], p[13] }),
				//new GoToSafestNeighborState(new int[] { p[14] }),
		};
		
		sc = new StateComparer();
	}
	
	private MOVE my_move;
	
	public MOVE getMove(Game game, long timeDue) 
	{
		//long current_time = System.currentTimeMillis();
		
		my_move = MOVE.NEUTRAL;
		this_state_name = MyPacMan.NO_STATE_FOUND_NAME;
		
		List<IState> applicable_states = new LinkedList<IState>();
		
		for (IState s : states) {
			if (s.willHandleGameState(game)) {
				applicable_states.add(s);
			}
		}
		
		if (applicable_states.size() > 0) {
			Collections.sort(applicable_states, sc);
			
			for (int i = 0; i < applicable_states.size(); i++) {
				current_state = applicable_states.get(i);
				
				this_state_name = current_state.getClass().getCanonicalName();
				
				my_move = current_state.getNextMove(game, timeDue);

				if (my_move != null) {
					break;
				}
			}
		}
		
		if (!this_state_name.equalsIgnoreCase(last_state_name)) {
			last_state_name = this_state_name;
			
			//System.out.println("Changed state to: " + this_state_name);
		}
		
		return my_move;
	}
}