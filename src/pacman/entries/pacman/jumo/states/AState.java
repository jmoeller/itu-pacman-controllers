package pacman.entries.pacman.jumo.states;

public abstract class AState implements IState {

	protected static int PRIORITY = 0;
	public static final int MAXIMUM_DISTANCE = 200;
	
	protected int[] arguments;
	
	public AState(int[] arguments)
	{
		this.arguments = arguments;
	}
	
	public int getPriority() {
		return arguments[PRIORITY];
	}
}
