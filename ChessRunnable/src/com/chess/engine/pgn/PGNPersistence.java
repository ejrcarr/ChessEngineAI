package pgn;

import board.Board;
import board.Move;
import player.Player;

public interface PGNPersistence {

    void persistGame(Game game);

    Move getNextBestMove(Board board, Player player, String gameText);

}
