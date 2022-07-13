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
 *  The Bishop class has a method that calculatesLegalMoves by using a vector to
 *  determine the diagonal tile coordinates. Handles edge cases when the bishop is 
 *  on first and last column.
 * 
 */

public class Bishop extends Piece {
	
	// If the board is in a coordinate array, subtracting and adding 9 and 7 to
	// a certain coordinate results in diagonal movement. 
	private final static int[] CANDIDATE_MOVE_VECTOR_COORDINATES = { -9, -7, 7, 9 };
	
	// CONSTRUCTORS 
	public Bishop(final Alliance pieceAlliance, final int piecePosition) {
		super(PieceType.BISHOP, piecePosition, pieceAlliance, true);
	}
	
	public Bishop(final Alliance pieceAlliance, final int piecePosition, final boolean isFirstMove) {
		super(PieceType.BISHOP, piecePosition, pieceAlliance, isFirstMove);
	}
	

	@Override
	public Collection<Move> calculateLegalMoves(final Board board) {
		final List<Move> legalMoves = new ArrayList<>();
		for (final int candidateCoordinateOffset: CANDIDATE_MOVE_VECTOR_COORDINATES) {
			int candidateDestinationCoordinate = this.piecePosition;	
			while (BoardUtils.isValidTileCoordinate(candidateDestinationCoordinate)) {
				
				// Adding and subtracting the vector coordinates does not work when the bishop is 
				// on the first and last column of the board so those cases are isolated and fixed. 
				// Since the bishop is on the end of the board the tile diagonal is no longer existing.
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
		return PieceType.BISHOP.toString();
	}
	
	// Edge cases take care of the algorithm when the bishop is on the edges of the board.
	private static boolean isFirstColumnExclusion(final int currentPosition, final int candidateOffset) {
		return BoardUtils.FIRST_COLUMN[currentPosition] && ((candidateOffset == -9) || (candidateOffset == 7));
	}
	
	private static boolean isEighthColumnExclusion(final int currentPosition, final int candidateOffset) {
		return BoardUtils.EIGHTH_COLUMN[currentPosition] && ((candidateOffset == 9) || (candidateOffset == -7));
	}

	@Override
	public Bishop movePiece(final Move move) {
		return new Bishop(move.getMovedPiece().getPieceAlliance(), move.getDestinationCoordinate());
	}
	
	

}
