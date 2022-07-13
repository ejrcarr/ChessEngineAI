package ai;

import board.Board;
import board.Move;
import gui.Table.TilePanel;
import player.MoveTransition;


/*
 * 
 *  This class contains the algorithm that evalutes the board and
 *  chooses the best worst case senario which acts as the engine.
 *  The MiniMax class also uses the BoardEvaluator and the 
 *  StandardBoardEvaluator classes to evaluate the current player's
 *  position.  
 * 
 */

public class MiniMax implements MoveStrategy {

	private final BoardEvaluator boardEvaluator;
	private final int searchDepth;
	
	// CONSTRUCTOR 
	public MiniMax(final int searchDepth) {
		this.boardEvaluator = new StandardBoardEvaluator();
		this.searchDepth = searchDepth;
	}
	
	@Override
	public String toString() {
		return "MiniMax";
	}
	
	/*
	 * 
	 *  This logic uses a co-recursive algorithm called minimax.  
	 * 	This algorithm minimizes the possible loss for the worst
	 *  case senario. It is coded to search four ply (single move by one player)
	 *  deep. The execute function calls both min and max functions 
	 *  which call each other and evaluate the board by calling
	 *  the evaluate function in the BoardEvaluator class which then
	 *  calls the evulate function in the StandardBoardEvaluator class. 
	 * 
	 *  https://en.wikipedia.org/wiki/Minimax
	 * 
	 */
	
	@Override
	public Move execute(Board board) {
		
		final long startTime = System.currentTimeMillis();
		Move bestMove = null;
		
		int highestSeenValue = Integer.MIN_VALUE;
		int lowestSeenValue = Integer.MAX_VALUE;
		int currentValue;
		
		System.out.println(board.currentPlayer() + " analyzing with depth " + searchDepth);
		
		int numMoves = board.currentPlayer().getLegalMoves().size();
		
		for(final Move move : board.currentPlayer().getLegalMoves()) {
			
			final MoveTransition moveTransition = board.currentPlayer().makeMove(move);
			if(moveTransition.getMoveStatus().isDone()) {
				
				// If the current player is white, then get the lowest
				// seen value, else get the largest seen value.
				currentValue = board.currentPlayer().getAlliance().isWhite() ?
						min(moveTransition.getToBoard(), searchDepth -1) :
						max(moveTransition.getToBoard(), searchDepth -1);
				
				if(board.currentPlayer().getAlliance().isWhite() && currentValue >= highestSeenValue) {
					highestSeenValue = currentValue;
					bestMove = move;
					
				}
				else if (board.currentPlayer().getAlliance().isBlack() && currentValue <= lowestSeenValue) {
					lowestSeenValue = currentValue;
					bestMove = move;
				}
			
			}
		}
		
		final long executionTime = System.currentTimeMillis() - startTime;
		
		
		// SOUNDS 
		if (bestMove.isAttack()) {
			TilePanel.playSound("CapturedMove.wav");

		}
		else if(bestMove.isCastlingMove()) {
			TilePanel.playSound("CastledMove.wav");
		}
		else if(board.currentPlayer().isInCheck()) {
			TilePanel.playSound("Check.wav");
		}
		else {
			TilePanel.playSound("MovedPieceSound.wav");
		}
		return bestMove;
		
		
	}
	
	/* 
	 * 
	 *  This function is apart of the evaluate function of this
	 *  class. The min function interates over the current player's
	 *  legal moves and finds the lowest seen value by calling the maximum 
	 *  function which co-recursively calls the min function.
	 * 
	 */
	
	public int min(final Board board, final int depth) {
		
		if(depth == 0 || isEndGameScenario(board)) {
			return this.boardEvaluator.evaluate(board, depth);
		}
		
		int lowestSeenValue = Integer.MAX_VALUE;
		for(final Move move : board.currentPlayer().getLegalMoves()) {
			final MoveTransition moveTransition = board.currentPlayer().makeMove(move);
			if(moveTransition.getMoveStatus().isDone()) {
				final int currentValue = max(moveTransition.getToBoard(),depth-1);
				if(currentValue <= lowestSeenValue) {
					lowestSeenValue = currentValue;
				}
			}
		}
		return lowestSeenValue;
	}
	
	private boolean isEndGameScenario(Board board) {
		
		return board.currentPlayer().isInCheckmate() || 
				board.currentPlayer().isInStalemate();
	}

	public int max(final Board board, final int depth) {
		if(depth == 0 || isEndGameScenario(board)) {
			return this.boardEvaluator.evaluate(board, depth);
		}
		
		int highestSeenValue = Integer.MIN_VALUE;
		for(final Move move : board.currentPlayer().getLegalMoves()) {
			final MoveTransition moveTransition = board.currentPlayer().makeMove(move);
			if(moveTransition.getMoveStatus().isDone()) {
				final int currentValue = min(moveTransition.getToBoard(),depth-1);
				if(currentValue >= highestSeenValue) {
					highestSeenValue = currentValue;
				}
			}
		}
		return highestSeenValue;
	}

	
	
}
