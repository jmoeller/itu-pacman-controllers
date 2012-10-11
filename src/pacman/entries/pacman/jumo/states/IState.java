package pacman.entries.pacman.jumo.states;

public interface IState {
	/** Will this state be able to/want to handle the current game state?
	 */
	public boolean willHandleGameState(pacman.game.Game game);
	
	/** Determine the next move this state would make.
	 * Assumes that this will only be called if `willHandleGameState` is true.
	 * The behavior if `willHandleGameState` is false is undefined.
	 */
	public pacman.game.Constants.MOVE getNextMove(pacman.game.Game game, long timeDue);
	
	public int getPriority();
	
	public int[][] getParameterRanges();
}