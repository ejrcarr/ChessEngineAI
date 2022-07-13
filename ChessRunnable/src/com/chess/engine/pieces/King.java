package pieces;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import ally.Alliance;
import board.Board;
import board.BoardUtils;
import board.Move;
import board.Tile;
import board.Move.MajorAttackMove;
import board.Move.MajorMove;

/*
 * 
 *  The King class has a method that calculatesLegalMoves by using a vector to
 *  determine the surrounding tile coordinates. Constructors also contain if the king is 
 *  castled or not as well as if it is capable to king and queen side castle.
 *  Handles edge cases when the king is on first and last column.
 * 
 */

public class King extends Piece {
	private final static int[] CANDIDATE_MOVE_COORDINATE = { -9, -8, -7, -1, 1, 7, 8, 9 };
	private final boolean isCastled;
	private final boolean kingSideCastleCapable;
	private final boolean queenSideCastleCapable;
	
	// CONSTRUCTORS
	public King(final Alliance pieceAlliance, 
			    final int piecePosition,
			    final boolean kingSideCastleCapable,
			    final boolean queenSideCastleCapable) {
		super(PieceType.KING, piecePosition, pieceAlliance, true);
		this.isCastled = false;
		this.kingSideCastleCapable = kingSideCastleCapable;
		this.queenSideCastleCapable = queenSideCastleCapable;
	}
	
	public King(final Alliance pieceAlliance, 
			    final int piecePosition, 
			    final boolean isFirstMove,
			    final boolean isCastled,
			    final boolean kingSideCastleCapable,
			    final boolean queenSideCastleCapable) {
		super(PieceType.KING, piecePosition, pieceAlliance, isFirstMove);
		this.isCastled = isCastled;
		this.kingSideCastleCapable = kingSideCastleCapable;
		this.queenSideCastleCapable = queenSideCastleCapable;
	}
	
	
	
	public boolean isKingSideCastleCapable() {
		return this.kingSideCastleCapable;
	}
	
	public boolean isQueenSideCastleCapable() {
		return this.queenSideCastleCapable;
	}
 
	public Collection<Move> calculateLegalMoves(Board board) {
		final List<Move> legalMoves = new ArrayList<>();
		for(final int currentCandidateOffset : CANDIDATE_MOVE_COORDINATE) {
			final int candidateDestinationCoordinate = this.piecePosition + currentCandidateOffset;
			if (isFirstColumnExclusion(this.piecePosition, currentCandidateOffset) || 
				isEighthColumnExclusion(this.piecePosition, currentCandidateOffset)) {
				continue;
			}
			
			if(BoardUtils.isValidTileCoordinate(candidateDestinationCoordinate)) {
				final Tile candidateDestinationTile = board.getTile(candidateDestinationCoordinate);
				if (!candidateDestinationTile.isTileOccupied()) {
					legalMoves.add(new MajorMove(board, this, candidateDestinationCoordinate));
				}
				else {
					final Piece pieceAtDestination = candidateDestinationTile.getPiece();
					final Alliance pieceAlliance = pieceAtDestination.getPieceAlliance();
					if (this.pieceAlliance != pieceAlliance) {
						legalMoves.add(new MajorAttackMove(board, this, candidateDestinationCoordinate, pieceAtDestination));
					}
				}
			}
			try {
				for(final Move move : board.currentPlayer().calculateKingCastles(board.currentPlayer().getLegalMoves(), board.currentPlayer().getOpponent().getLegalMoves())) {
					if (!legalMoves.contains(move)) {
						legalMoves.add(move);
					}
				}
			} 
			catch(Exception e) {};
		}
		return Collections.unmodifiableList(legalMoves);
	}
	
	@Override
	public String toString() {
		return PieceType.KING.toString();
	}
	
	
	// Edge cases
	private static boolean isFirstColumnExclusion(final int currentPosition, final int candidateOffset) {
		return BoardUtils.FIRST_COLUMN[currentPosition] && ((candidateOffset == -9) || (candidateOffset == -1) ||
			(candidateOffset == 7));
	}
	
	private static boolean isEighthColumnExclusion(final int currentPosition, final int candidateOffset) {
		return BoardUtils.EIGHTH_COLUMN[currentPosition] && ((candidateOffset == 9) || (candidateOffset == 1) ||
			(candidateOffset == -7));
	}
	
	@Override
	public King movePiece(final Move move) {
		return new King(move.getMovedPiece().getPieceAlliance(), move.getDestinationCoordinate(), false, move.isCastlingMove(), false, false);
	}
	
}
