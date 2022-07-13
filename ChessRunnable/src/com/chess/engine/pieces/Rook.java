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
 * The Rook class calculates the piece's legal moves through the vector coordinates which
 * is a tile up, down, left, and right. 
 * 
 */

public class Rook extends Piece {

	private final static int[] CANDIDATE_MOVE_VECTOR_COORDINATES = { -8, -1, 1, 8 };
	
	// CONSTRUCTORS
	public Rook(final Alliance pieceAlliance, final int piecePosition) {
		super(PieceType.ROOK, piecePosition, pieceAlliance, true);
	}
	
	public Rook(final Alliance pieceAlliance, final int piecePosition, final boolean isFirstMove) {
		super(PieceType.ROOK, piecePosition, pieceAlliance, isFirstMove);
	}
	
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
		return PieceType.ROOK.toString();
	}
		
	private static boolean isFirstColumnExclusion(final int currentPosition, final int candidateOffset) {
		return BoardUtils.FIRST_COLUMN[currentPosition] && (candidateOffset == -1);
	}
	
	private static boolean isEighthColumnExclusion(final int currentPosition, final int candidateOffset) {
		return BoardUtils.EIGHTH_COLUMN[currentPosition] && (candidateOffset == 1);
	}

	@Override
	public Rook movePiece(final Move move) {
		return new Rook(move.getMovedPiece().getPieceAlliance(), move.getDestinationCoordinate());
	}
	
}
