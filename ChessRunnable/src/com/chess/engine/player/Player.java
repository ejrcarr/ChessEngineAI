package player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import ally.Alliance;
import board.Board;
import board.Move;
import pieces.King;
import pieces.Piece;

/*
 * 
 *  The Player class can be instantiated and acts as the parent function for 
 *  BlackPlayer and WhitePlayer. Controls making moves, has getters for legal moves,
 *  the player king, castle capablility, and move legality. 
 * 
 */

public abstract class Player {

	protected final Board board;
	protected final King playerKing;
	protected final Collection<Move> legalMoves;
	private final boolean isInCheck;
	
	Player(final Board board,
		   final Collection<Move> legalMoves,
		   final Collection<Move> opponentMoves) {
		
		this.board = board;
		this.playerKing = establishKing();
		legalMoves.addAll(calculateKingCastles(legalMoves, opponentMoves));
		this.legalMoves = Collections.unmodifiableCollection(legalMoves);
		this.isInCheck = !Player.calculateAttacksOnTile(this.playerKing.getPiecePosition(), opponentMoves).isEmpty();
		
	}
	
	public King getPlayerKing() {
		return this.playerKing;
	}
	
	public Collection<Move> getLegalMoves() {
		return this.legalMoves;
	}

	protected static Collection<Move> calculateAttacksOnTile(int piecePosition, Collection<Move> moves) {
		final List<Move> attackMoves = new ArrayList<>();
		for(final Move move : moves) {
			if(piecePosition == move.getDestinationCoordinate()) {
				attackMoves.add(move);
			}
		}
		return Collections.unmodifiableList(attackMoves);
	}

	private King establishKing() {
		for(final Piece piece : getActivePieces()) {
			if (piece.getPieceType().isKing()) {
				return (King) piece;
			}
		}
		throw new RuntimeException("Should not reach here. Not a valid board.");
	}
	
	public boolean isMoveLegal(final Move move) {
		return this.legalMoves.contains(move);
	}
	
	public boolean isInCheck() {
		return this.isInCheck;
	}
	
	public boolean isKingSideCastleCapable() {
		return this.playerKing.isKingSideCastleCapable();
	}
	
	public boolean isQueenSideCastleCapable() {
		return this.playerKing.isQueenSideCastleCapable();

	}

	public boolean isInCheckmate() {
		return this.isInCheck() && !hasEscapeMoves();
	}
	
	protected boolean hasEscapeMoves() {
		for(final Move move : this.legalMoves) {
			final MoveTransition transition = makeMove(move);
			if(transition.getMoveStatus().isDone()) {
				return true;
			}
		}
		return false;
	}

	public boolean isInStalemate() {
		return !this.isInCheck() && !hasEscapeMoves();
	}
	
	public boolean isCastled() {
		return false;
	}
	
	public MoveTransition makeMove(final Move move) {
		if(!isMoveLegal(move)) {
			return new MoveTransition(this.board, this.board, move, MoveStatus.ILLEGAL_MOVE);
		}
		final Board transitionBoard = move.execute();
		final Collection<Move> kingAttacks = Player.calculateAttacksOnTile(transitionBoard.currentPlayer().getOpponent().getPlayerKing().getPiecePosition(),
						transitionBoard.currentPlayer().getLegalMoves());
		if(!kingAttacks.isEmpty()) {  // If player is in check or in checkmate then the move status
			   						  // is not done, yeilding an illegal attempt at a move.
			return new MoveTransition(this.board, this.board, move, MoveStatus.LEAVES_PLAYER_IN_CHECK);
		}
		return new MoveTransition(this.board, transitionBoard, move, MoveStatus.DONE);
	}
	
	 public MoveTransition unMakeMove(final Move move) {
	        return new MoveTransition(this.board, move.undo(), move, MoveStatus.DONE);
	 }
	
	public abstract Collection<Piece> getActivePieces();
	public abstract Alliance getAlliance();
	public abstract Player getOpponent();
	public abstract Collection<Move> calculateKingCastles(Collection<Move> playerLegals, Collection<Move> opponentsLegals); 
	
}
