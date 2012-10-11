package pacman.entries.pacman.jumoNewFSM.states;

import java.util.Comparator;

import pacman.game.Constants.MOVE;
import pacman.game.Game;

public interface IState {
	public int[][] getArgumentRanges();
	public int getPriority();
	public void setPriority(int value);
	public boolean canHandle(Game game);
	public void setArgument(int argument_index, int value);
	public int getArgument(int argument_index);
	public MOVE move(Game game, long timeDue);
	
	public class Comparer implements Comparator<IState> {
		
		@Override
		public int compare(IState arg0, IState arg1) {
			return Integer.compare(arg0.getPriority(), arg1.getPriority());
		}
		
	}

	int getNumberOfArguments();
}
