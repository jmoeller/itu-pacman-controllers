package pacman.entries.pacman.jumoNewFSM.states;

public abstract class AState implements IState {
	protected int _priority;
	protected int[] _arguments;
	
	public static int MAX_DISTANCE = 300; // Farthest distance between two pills in first maze is 196.
	
	@Override
	public int getPriority() {
		return _priority;
	}

	@Override
	public void setPriority(int value) {
		this._priority = value;
	}

	public void setArgument(int argument_index, int value) {
		_arguments[argument_index] = value;
	}
	
	public int getArgument(int argument_index) {
		return _arguments[argument_index];
	}
	
	protected int arg(Enum<?> index) {
		return getArgument(index.ordinal());
	}
}
