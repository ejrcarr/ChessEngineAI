package pieces;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import ally.Alliance;
import board.Board;
import board.BoardUtils;
import board.Move;
import board.Move.PawnAttackMove;
import board.Move.PawnEnPassantAttackMove;
import board.Move.PawnJump;
import board.Move.PawnMove;
import board.Move.PawnPromotion;

/*
 * 
 *  The Pawn class has a method that calculatesLegalMoves by using a vector to
 *  determine the tile coordinates. 
 * 
 */

public class Pawn extends Piece {

	private final static int[] CANDIDATE_MOVE_COORDINATE = { 8, 16, 7, 9 };
	
	public Pawn(final Alliance pieceAlliance, final int piecePosition) {
		super(PieceType.PAWN, piecePosition, pieceAlliance, true);
	}
	
	public Pawn(final Alliance pieceAlliance, final int piecePosition, final boolean isFirstMove) {
		super(PieceType.PAWN, piecePosition, pieceAlliance, isFirstMove);
	}

	@Override
	public Collection<Move> calculateLegalMoves(final Board board) {
	    final List<Move> legalMoves = new ArrayList<>();
		for(final int currentCandidateOffset : CANDIDATE_MOVE_COORDINATE) {
			final int candidateDestinationCoordinate = this.piecePosition + (this.pieceAlliance.getDirection() * currentCandidateOffset);
			if(!BoardUtils.isValidTileCoordinate(candidateDestinationCoordinate)) {
				continue;
			}
			
			// Pawn promotion cases
			if(currentCandidateOffset == 8 && !board.getTile(candidateDestinationCoordinate).isTileOccupied()) {
				if(this.pieceAlliance.isPawnPromotionSquare(candidateDestinationCoordinate)) {
					legalMoves.add(new PawnPromotion(new PawnMove(board, this, candidateDestinationCoordinate)));
				}
				else {
					legalMoves.add(new PawnMove(board, this, candidateDestinationCoordinate));
				}
			}
			
			// Pawn jumps
			else if (currentCandidateOffset == 16 && this.isFirstMove() && 
						((BoardUtils.SEVENTH_ROW[this.piecePosition] && this.getPieceAlliance().isBlack()) || 
						(BoardUtils.SECOND_ROW[this.piecePosition] && this.getPieceAlliance().isWhite()))) {
				final int behindCandidateDestinationCoordinate = this.piecePosition + (this.pieceAlliance.getDirection() * 8);
				if (!board.getTile(behindCandidateDestinationCoordinate).isTileOccupied() && 
					!board.getTile(candidateDestinationCoordinate).isTileOccupied()) {
					legalMoves.add(new PawnJump(board, this, candidateDestinationCoordinate));
				}
			}
	
			else if (currentCandidateOffset == 7 &&
					!((BoardUtils.EIGHTH_COLUMN[this.piecePosition] && this.pieceAlliance.isWhite() || 
					 (BoardUtils.FIRST_COLUMN[this.piecePosition] && this.pieceAlliance.isBlack())))) {
				
				// Pawn promotion attack edge case
				if (board.getTile(candidateDestinationCoordinate).isTileOccupied()) {
					final Piece pieceOnCandidate = board.getTile(candidateDestinationCoordinate).getPiece();
					if (this.pieceAlliance != pieceOnCandidate.getPieceAlliance()) {
						if(this.pieceAlliance.isPawnPromotionSquare(candidateDestinationCoordinate)) {
							legalMoves.add(new PawnPromotion(new PawnAttackMove(board, this, candidateDestinationCoordinate, pieceOnCandidate)));
						}
						else {
							legalMoves.add(new PawnAttackMove(board, this, candidateDestinationCoordinate, pieceOnCandidate));
						}


					}
				}
				
				// En Passant edge case
				else if (board.getEnPassantPawn() != null) {
					if(board.getEnPassantPawn().getPiecePosition() == (this.piecePosition + (this.pieceAlliance.getOppositeDirection()))) {
						final Piece pieceOnCandidate = board.getEnPassantPawn();
						if(this.pieceAlliance != pieceOnCandidate.getPieceAlliance()) {
							legalMoves.add(new PawnEnPassantAttackMove(board, this, candidateDestinationCoordinate, pieceOnCandidate));
						}
					}
				}
			}
			
			else if (currentCandidateOffset == 9 && 
					!((BoardUtils.FIRST_COLUMN[this.piecePosition] && this.pieceAlliance.isWhite() || 
					  (BoardUtils.EIGHTH_COLUMN[this.piecePosition] && this.pieceAlliance.isBlack())))) {
				
				// Pawn promotion attack edge case 
				if (board.getTile(candidateDestinationCoordinate).isTileOccupied()) {
					final Piece pieceOnCandidate = board.getTile(candidateDestinationCoordinate).getPiece();
					if (this.pieceAlliance != pieceOnCandidate.getPieceAlliance()) {
						if(this.pieceAlliance.isPawnPromotionSquare(candidateDestinationCoordinate)) {
							legalMoves.add(new PawnPromotion(new PawnAttackMove(board, this, candidateDestinationCoordinate, pieceOnCandidate)));
						}
						else {
							legalMoves.add(new PawnAttackMove(board, this, candidateDestinationCoordinate, pieceOnCandidate));
						}


					}
				}
				// En Passant edge case
				else if (board.getEnPassantPawn() != null) {
					if(board.getEnPassantPawn().getPiecePosition() == (this.piecePosition - (this.pieceAlliance.getOppositeDirection()))) {
						final Piece pieceOnCandidate = board.getEnPassantPawn();
						if(this.pieceAlliance != pieceOnCandidate.getPieceAlliance()) {
							legalMoves.add(new PawnEnPassantAttackMove(board, this, candidateDestinationCoordinate, pieceOnCandidate));
						}
					}
				}
			}
			
		}
		
		
		return Collections.unmodifiableList(legalMoves);
	}
	
	@Override
	public String toString() {
		return PieceType.PAWN.toString();
	}
	
	@Override
	public Pawn movePiece(final Move move) {
		return new Pawn(move.getMovedPiece().getPieceAlliance(), move.getDestinationCoordinate());
	}
	
	public Piece getPromotionPiece() {
		return new Queen(this.pieceAlliance, this.piecePosition, false);
	}
	
	

}
