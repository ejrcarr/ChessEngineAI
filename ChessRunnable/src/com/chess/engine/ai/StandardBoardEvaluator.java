package ai;

import ally.Alliance;
import board.Board;
import board.BoardUtils;
import board.Move;
import pieces.Piece;
import player.Player;

/*
 * 
 *  This class analyzes current player's position based on king safety,
 *  pawn structure, mobility of pieces, possibility of check or checkmate, attacks
 *  on opponents pieces, the value of each piece, the ability to castle, and 
 *  if a piece vulnerability.
 * 
 */

public final class StandardBoardEvaluator implements BoardEvaluator {

	private static final int CHECK_BONUS = 45;
	private static final int CHECK_MATE_BONUS = 10000;
	private static final int DEPTH_BONUS = 100;
	private final static int MOBILITY_MULTIPLIER = 5;
	private static final int CASTLE_BONUS = 25;
	private final static int ATTACK_MULTIPLIER = 1;
	private final static int TWO_BISHOPS_BONUS = 25;
	private static final int CAN_CASTLE_BONUS = 50;
	private static final int EARLY_QUEEN_MOVE_PENALTY = -10;
	private static final int KING_SAFETY_BONUS = 35;
	
	@Override
	public int evaluate(final Board board, final int depth) {
		return scorePlayer(board, board.whitePlayer(), depth) -
			   scorePlayer(board, board.blackPlayer(), depth);
	}

	private int scorePlayer(final Board board, final Player player, final int depth) {
		return pieceValue(player)  + mobility(player) + check(player)
		      + checkmate(player, depth) + castled(player) + pawnStructure(player) + canCastle(board, player)
		      + queenOutEarly(board, player) + attacks(player) + kingSafety(board, player);
	}
	
	/*
	 * 
	 *  Checks for positions of king on either side of the board with pawns.
	 *  Also takes into account different pawn structures in front of the king.
	 *  
	 *  
	 *    K R        R K             K R        K R
	 *  P P P        P P P P		 P P        P P P
	 *  						   P				  P
	 *  
	 *  
	 *  						   P	   	     	  P
	 *  P P P        P P P P         P P        P P P
	 *    K R        R K             K R        R K
	 * 
	 */
	
	private int kingSafety(final Board board, final Player player) {
		boolean isCastled = player.isCastled();
		Alliance ally = player.getAlliance();
		if(ally.isBlack() && isCastled) {      // BLACKS KING PAWN STRUCTURE
			if(((board.getTile(13).getPiece().getPieceType().isPawn()
		         && board.getTile(14).getPiece().getPieceType().isPawn()
		         && board.getTile(15).getPiece().getPieceType().isPawn())||
			    (board.getTile(8).getPiece().getPieceType().isPawn()
			     && board.getTile(9).getPiece().getPieceType().isPawn()
			     && board.getTile(10).getPiece().getPieceType().isPawn()
			     && board.getTile(11).getPiece().getPieceType().isPawn()) ||
					
			   (board.getTile(13).getPiece().getPieceType().isPawn()
		         && board.getTile(14).getPiece().getPieceType().isPawn()
		         && board.getTile(23).getPiece().getPieceType().isPawn())||
			    (board.getTile(16).getPiece().getPieceType().isPawn()
			     && board.getTile(9).getPiece().getPieceType().isPawn()
			     && board.getTile(10).getPiece().getPieceType().isPawn()
			     && board.getTile(11).getPiece().getPieceType().isPawn()))
					) {
				
				return KING_SAFETY_BONUS;
			}
		}
		else if (ally.isWhite() && isCastled) {     // WHITE KING PAWN STRUCTURE
			if(((board.getTile(53).getPiece().getPieceType().isPawn()
		         && board.getTile(54).getPiece().getPieceType().isPawn()
		         && board.getTile(55).getPiece().getPieceType().isPawn())||
			    (board.getTile(48).getPiece().getPieceType().isPawn()
			     && board.getTile(49).getPiece().getPieceType().isPawn()
			     && board.getTile(50).getPiece().getPieceType().isPawn()
			     && board.getTile(51).getPiece().getPieceType().isPawn()) ||
					
			   (board.getTile(53).getPiece().getPieceType().isPawn()
		         && board.getTile(54).getPiece().getPieceType().isPawn()
		         && board.getTile(47).getPiece().getPieceType().isPawn())||
			    (board.getTile(40).getPiece().getPieceType().isPawn()
			     && board.getTile(49).getPiece().getPieceType().isPawn()
			     && board.getTile(50).getPiece().getPieceType().isPawn()
			     && board.getTile(51).getPiece().getPieceType().isPawn()))
					) {
					
					return KING_SAFETY_BONUS;
				}
		}
		return 0;
	}

	
	// Iterates over current player's pieces and checks if they 
	// move their queen out early while not having other pieces
	// developed.
	private int queenOutEarly(Board board, Player player) {
		int minorPiecesMoved = 0;
		boolean isQueenMoved = false;
		
		for(Piece piece : player.getActivePieces()) {
			if(piece.getPieceType().isQueen() && !piece.isFirstMove()) {
				isQueenMoved = false;
			}
			if(!piece.getPieceType().isQueen() && piece.isFirstMove()) {
				minorPiecesMoved++;
			}
		}
		
		if(isQueenMoved && minorPiecesMoved < 2) {
			return EARLY_QUEEN_MOVE_PENALTY;
		}
		else {
			return 0;
		}
	
	}
	
	
	// Checks ability to castle for king safety.
	private int canCastle(Board board, Player player) {
		
		if((player.isKingSideCastleCapable() || player.isQueenSideCastleCapable()) && !player.isCastled()) {
			return CAN_CASTLE_BONUS;
		}
		else {
			return 0;
		}
	}
	
	
	// Iterates over given players legal moves and checks for
	// exchanges that may benefit given player. Checks for important
	// piece threats.
	private static int attacks(final Player player) {
        int attackScore = 0;
        for(final Move move : player.getLegalMoves()) {
            if(move.isAttack()) {
                final Piece movedPiece = move.getMovedPiece();
                final Piece attackedPiece = move.getAttackedPiece();
                if(movedPiece.getPieceValue() <= attackedPiece.getPieceValue()) {
                    attackScore++;
                }
            }
        }
        return attackScore * ATTACK_MULTIPLIER;
    }
	
	
	// Calls pawn structure analyzer from the PawnStructure class.
	private static int pawnStructure(final Player player) {
        return PawnStructureAnalyzer.get().pawnStructureScore(player);
    }
	
	
	// Gives a castle bonus if given player is castled because of king safety.
	private static int castled(Player player) {
		return player.isCastled() ? CASTLE_BONUS : 0;
	}

	// Checks if checkmate is possibile within four plys.
	private int checkmate(Player player, int depth) {
		return player.getOpponent().isInCheckmate() ? CHECK_MATE_BONUS * depthBonus(depth): 0;
	}

	// Grants a depth bonus determinate on the amount of ply to checkmate function/
	private static int depthBonus(int depth) {
		return depth == 0 ? 1 : DEPTH_BONUS * depth;
	}

	// Grants a check bonus if the players opponent is in check.
	private int check(Player player) {
		return player.getOpponent().isInCheck() ? CHECK_BONUS : 0;
	}

	// These two functions determine how many potential legal moves each player
	// has based on developed pieces. The more potential legal moves a player has, the more
	// points are granted to player. 
	private int mobility(final Player player) {
		return MOBILITY_MULTIPLIER * mobilityRatio(player);
	}

	private static int mobilityRatio(final Player player) {
	    return (int)((player.getLegalMoves().size() * 10.0) / player.getOpponent().getLegalMoves().size());
	}
	
	
	// Simply calculates the individual values of each piece and returns the sum.
	// If a player has both dark and light squared bishops the a bonus is added. 
	private static int pieceValue(final Player player) {
        int pieceValuationScore = 0;
        int numBishops = 0;
        for (final Piece piece : player.getActivePieces()) {
            pieceValuationScore += piece.getPieceValue();
            if(piece.getPieceType().isBishop()) {
                numBishops++;
            }
        }
        return pieceValuationScore + (numBishops == 2 ? TWO_BISHOPS_BONUS : 0);
    }

	
	
}
