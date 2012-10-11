package pacman.entries.pacman.jumoNewFSM;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import pacman.controllers.Controller;
import pacman.entries.pacman.jumoNewFSM.states.FleeFromNearest;
import pacman.entries.pacman.jumoNewFSM.states.GoToSafestNeighbor;
import pacman.entries.pacman.jumoNewFSM.states.HuntGhost;
import pacman.entries.pacman.jumoNewFSM.states.IState;
import pacman.entries.pacman.jumoNewFSM.states.Junction;
import pacman.entries.pacman.jumoNewFSM.states.Pill;
import pacman.entries.pacman.jumoNewFSM.states.PowerPill;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

/*
 * This is the class you need to modify for your entry. In particular, you need to
 * fill in the getAction() method. Any additional classes you write should either
 * be placed in this package or sub-packages (e.g., game.entries.pacman.mypackage).
 */
public class JumoNewFSM extends Controller<MOVE>
{
	public class Range {
		public int min;
		public int max;
		
		public Range(int min, int max) {
			this.min = min;
			this.max = max;
		}
	}
	
	public ArrayList<IState> available_states;
	public ArrayList<Range> parameter_ranges;
	private IState.Comparer comparer;
	
	public JumoNewFSM() {
		this(
				new int[] {
						200, 200, 800,
						0, 200, 0, 200, 0, 800,
						10, 20,
						0, 200, 0, 200, 0, 800, 200, 0,
						200, 4, 0, 0
				},
				new int[] {
						10,
						20,
						15,
						5,
						12,
						1
				}
				);
	}

	@SuppressWarnings("rawtypes")
	public JumoNewFSM(int[] ps, int[] prioris) {
		comparer = new IState.Comparer();
		
		available_states = new ArrayList<IState>();
		parameter_ranges = new ArrayList<Range>();
		
		for (Class s : new Class[] { Pill.class, FleeFromNearest.class, GoToSafestNeighbor.class, PowerPill.class, HuntGhost.class, Junction.class }) {
			try {
				IState state = (IState) s.newInstance();
				parameter_ranges = AddToRange(parameter_ranges, state.getArgumentRanges());
				available_states.add(state);
				
			} catch (InstantiationException | IllegalAccessException e) {
				e.printStackTrace();
			}
			
		}
		/*
		IState pill = new Pill();
		parameter_ranges = AddToRange(parameter_ranges, pill.getArgumentRanges());
		available_states.add(pill);

		IState flee = new FleeFromNearest();
		parameter_ranges = AddToRange(parameter_ranges, flee.getArgumentRanges());
		available_states.add(flee);

		IState safestneighbor = new GoToSafestNeighbor();
		parameter_ranges = AddToRange(parameter_ranges, safestneighbor.getArgumentRanges());
		available_states.add(safestneighbor);

		IState powerpill = new PowerPill();
		parameter_ranges = AddToRange(parameter_ranges, powerpill.getArgumentRanges());
		available_states.add(powerpill);

		IState huntghosts = new HuntGhost();
		parameter_ranges = AddToRange(parameter_ranges, huntghosts.getArgumentRanges());
		available_states.add(huntghosts);
		
		IState junction = new Junction();
		parameter_ranges = AddToRange(parameter_ranges, junction.getArgumentRanges());
		available_states.add(junction);
		*/
		
		int param_index = 0;
		for (int i = 0; i < available_states.size(); i++) {
			available_states.get(i).setPriority(prioris[i]);
			
			int argument_index = 0;
			for (int j = param_index; j < param_index + available_states.get(i).getNumberOfArguments(); j++) {
				available_states.get(i).setArgument(argument_index, ps[j]);
				
				argument_index += 1;
			}
			
			param_index += available_states.get(i).getNumberOfArguments();
		}
	}
	
	private ArrayList<Range> AddToRange(ArrayList<Range> parameter_ranges, int[][] new_ranges) {
		for (int[] range : new_ranges) {
			if (range.length == 0) { continue; }
			parameter_ranges.add(new Range(range[0], range[1]));
		}
		
		return parameter_ranges;
	}
	
	public MOVE getMove(Game game, long timeDue) 
	{
		ArrayList<IState> applicable_states = new ArrayList<IState>();
		
		for (IState s : available_states) {
			if (s.canHandle(game)) {
				applicable_states.add(s);
			}
		}
		
		if (!applicable_states.isEmpty()) {
			Collections.sort(applicable_states, comparer);
			
			return applicable_states.get(0).move(game, timeDue);
		}
		else {	
			return MOVE.NEUTRAL;
		}
	}
}