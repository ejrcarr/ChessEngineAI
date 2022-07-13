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
 *  The Queen class essentially combines the Bishop and Rook's move coordinates to 
 *  make the queen piece. The class contains the same edge cases as the bishop since
 *  it can attack diagonally. 
 * 
 */

public class Queen extends Piece {
	
	private final static int[] CANDIDATE_MOVE_VECTOR_COORDINATES = { -9, -8, -7, -1, 1, 7, 8, 9 };
	
	// CONSTRUCTORS 
	public Queen(Alliance pieceAlliance, int piecePosition) {
		super(PieceType.QUEEN, piecePosition, pieceAlliance, true);
	}
	
	public Queen(Alliance pieceAlliance, int piecePosition, final boolean isFirstMove) {
		super(PieceType.QUEEN, piecePosition, pieceAlliance, isFirstMove);
	}

	@Override
	public Collection<Move> calculateLegalMoves(final Board board) {
		final List<Move> legalMoves = new ArrayList<>();
		for (final int candidateCoordinateOffset: CANDIDATE_MOVE_VECTOR_COORDINATES) {
			int candidateDestinationCoordinate = this.piecePosition;
			while (BoardUtils.isValidTileCoordinate(candidateDestinationCoordinate)) {
				if (isFirstColumnExclusion(candidateDestinationCoordinate, candidateCoordinateOffset) ||
					isEighthColumnExclusion(candidateDestinationCoordinate, candidateCoordinateOffset)) {
					break;
				}
				candidateDestinationCoordinate += candidateCoordinateOffset;
				if (BoardUtils.isValidTileCoordinate(candidateDestinationCoordinate)) {
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
						break;
					}
				}
			}
		}
		return Collections.unmodifiableList(legalMoves);
	}
	
	@Override
	public String toString() {
		return PieceType.QUEEN.toString();
	}
	
	private static boolean isFirstColumnExclusion(final int currentPosition, final int candidateOffset) {
		return BoardUtils.FIRST_COLUMN[currentPosition] && ((candidateOffset == -9) || (candidateOffset == 7) || (candidateOffset == -1));
	}
	
	private static boolean isEighthColumnExclusion(final int currentPosition, final int candidateOffset) {
		return BoardUtils.EIGHTH_COLUMN[currentPosition] && ((candidateOffset == 9) || (candidateOffset == -7) || (candidateOffset == 1));
	}
	
	@Override
	public Queen movePiece(final Move move) {
		return new Queen(move.getMovedPiece().getPieceAlliance(), move.getDestinationCoordinate());
	}
	

}
