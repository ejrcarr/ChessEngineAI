package player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import ally.Alliance;
import board.Board;
import board.Move;
import board.Move.KingSideCastleMove;
import board.Move.QueenSideCastleMove;
import board.Tile;
import pieces.Piece;
import pieces.Rook;

/*
 * 
 * The WhitePlayer class inherits from Player and calculates the white king castles
 * while having getters of active pieces, aliiance and opponent. 
 * 
 */

public class WhitePlayer extends Player {

	public WhitePlayer(final Board board, 
			final Collection<Move> whiteStandardLegalMoves,
			final Collection<Move> blackStandardLegalMoves) {
		
		super(board, whiteStandardLegalMoves, blackStandardLegalMoves);
	}

	@Override
	public Collection<Piece> getActivePieces() {
		return this.board.getWhitePieces();
	}

	@Override
	public Alliance getAlliance() {
		return Alliance.WHITE;
	}

	@Override
	public Player getOpponent() {
		return this.board.blackPlayer();
	}
	
	@Override
	public String toString() {
		return "White Player";
	}

	@Override
	public Collection<Move> calculateKingCastles(final Collection<Move> playerLegals, final Collection<Move> opponentsLegals) {
		final List<Move> kingCastles = new ArrayList<>();
		if(this.playerKing.isFirstMove() && !this.isInCheck()) {    // If meets castling requirements
																	// of King first move and isn't in check
			if(!this.board.getTile(61).isTileOccupied() && 
			   !this.board.getTile(62).isTileOccupied()) {
				final Tile rookTile = this.board.getTile(63);
				if(rookTile.isTileOccupied() && rookTile.getPiece().isFirstMove()) { // Rook also needs to be the first move
					if(Player.calculateAttacksOnTile(61, opponentsLegals).isEmpty() &&
					   Player.calculateAttacksOnTile(62, opponentsLegals).isEmpty() &&
					   rookTile.getPiece().getPieceType().isRook()) {
						kingCastles.add(new KingSideCastleMove(this.board, this.playerKing, 62, (Rook)rookTile.getPiece(), rookTile.getTileCoordinate(), 61));
					}
				}
			}	
			if(!this.board.getTile(59).isTileOccupied() && 
			   !this.board.getTile(58).isTileOccupied() && 
			   !this.board.getTile(57).isTileOccupied()) {
				final Tile rookTile = this.board.getTile(56);
				if(rookTile.isTileOccupied() && rookTile.getPiece().isFirstMove() &&
						Player.calculateAttacksOnTile(58, opponentsLegals).isEmpty() &&
						Player.calculateAttacksOnTile(59, opponentsLegals).isEmpty() &&
						rookTile.getPiece().getPieceType().isRook()) {
					kingCastles.add(new QueenSideCastleMove(this.board, this.playerKing, 58, (Rook)rookTile.getPiece(), rookTile.getTileCoordinate(), 59));
				}
			}
		}
		return Collections.unmodifiableList(kingCastles);
	}
}
