package pieces;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import ally.Alliance;
import board.Board;
import board.BoardUtils;
import board.Move;
import board.Move.MajorAttackMove;
import board.Move.MajorMove;
import board.Tile;

public class Knight extends Piece {
	
	private final static int[] CANDIDATE_MOVE_COORDINATES = {-17, -15, -10, -6, 6, 10, 15, 17};
	
	public Knight(final Alliance pieceAlliance, final int piecePosition) {
		super(PieceType.KNIGHT, piecePosition, pieceAlliance, true);
	}
	
	public Knight(final Alliance pieceAlliance, final int piecePosition, final boolean isFirstMove) {
		super(PieceType.KNIGHT, piecePosition, pieceAlliance, isFirstMove);
	}
	
	@Override
	public Collection<Move> calculateLegalMoves(final Board board) {
		
		final List<Move> legalMoves = new ArrayList<>();
		
		for (int currOffset: CANDIDATE_MOVE_COORDINATES) {
			
			final int candidateDestinationCoordinate = this.piecePosition + currOffset;
		
			if (BoardUtils.isValidTileCoordinate(candidateDestinationCoordinate)) {
				
				if (isFirstColumnExclusion(this.piecePosition, currOffset) ||
						isSecondColumnExclusion(this.piecePosition, currOffset) ||
						isSeventhColumnExclusion(this.piecePosition, currOffset) ||
						isEighthColumnExclusion(this.piecePosition, currOffset)) {
					continue;
				}
				
				
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
		}
		
		return Collections.unmodifiableList(legalMoves);
	}
	
	@Override
	public String toString() {
		return PieceType.KNIGHT.toString();
	}
	
	private static boolean isFirstColumnExclusion(final int currentPosition, final int candidateOffset) {
		return BoardUtils.FIRST_COLUMN[currentPosition] && ((candidateOffset == -17) || (candidateOffset == -10) ||
			(candidateOffset == 6) || (candidateOffset == 15));
	}
	
	private static boolean isSecondColumnExclusion(final int currentPosition, final int candidateOffset) {
		return BoardUtils.SECOND_COLUMN[currentPosition] && ((candidateOffset == -10) || (candidateOffset == 6));
	}
	
	private static boolean isSeventhColumnExclusion(final int currentPosition, final int candidateOffset) {
		return BoardUtils.SEVENTH_COLUMN[currentPosition] && ((candidateOffset == -6) || (candidateOffset == 10));
	}
	
	private static boolean isEighthColumnExclusion(final int currentPosition, final int candidateOffset) {
		return BoardUtils.EIGHTH_COLUMN[currentPosition] && ((candidateOffset == -15) || (candidateOffset == -6) ||
			(candidateOffset == 10) || (candidateOffset == 17));
	}
	
	@Override
	public Knight movePiece(final Move move) {
		return new Knight(move.getMovedPiece().getPieceAlliance(), move.getDestinationCoordinate());
	}
}
