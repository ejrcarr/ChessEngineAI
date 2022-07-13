package ai;

import board.Board;

public interface BoardEvaluator {

	int evaluate(Board board, int depth);
	
}
