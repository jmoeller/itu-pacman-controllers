package pacman.entries.pacman.jumo.states;

import java.util.Comparator;

public class StateComparer implements Comparator<IState> {

	@Override
	public int compare(IState arg0, IState arg1) {
		return Integer.compare(-arg0.getPriority(), -arg1.getPriority());
	}

}
