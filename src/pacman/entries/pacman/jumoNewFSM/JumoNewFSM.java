package pacman.entries.pacman.jumoNewFSM;

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
public class JumoNewFSM extends Controller<MOVE>
{
	
	
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