import board.Board;
import gui.Table;

public class Chess {

	public static void main(String[] args) {
		
		Board board = Board.createStandardBoard();
		
		System.out.println(board);
		
		Table.get().show();

	}

}
