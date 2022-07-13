
package pieces;

import java.util.Collection;

import ally.Alliance;
import board.Board;
import board.Move;

/*
 * 
 *  The Piece class contains methods to determine piece position,
 *  alliance, type, value, and if the piece is on its first move. 
 *  Also contains an enum PieceType which allows for each piece
 *  to be specifically accessed. 
 * 
 */

public abstract class Piece {

		protected final PieceType pieceType;
		protected final int piecePosition;
		protected final Alliance pieceAlliance;
		protected final boolean isFirstMove;
		private final int cacheHashCode;
		
		
		// CONSTRUCTOR
		Piece(final PieceType pieceType, final int piecePosition, final Alliance pieceAlliance, final boolean isFirstMove) {
			this.pieceType = pieceType;
			this.piecePosition = piecePosition;
			this.pieceAlliance = pieceAlliance;
			this.isFirstMove = isFirstMove;
			this.cacheHashCode = computeHashCode();
		}
		
		private int computeHashCode() {
			int result = pieceType.hashCode();
			result = 31 * result + pieceAlliance.hashCode();
			result = 31 * result + piecePosition;
			result = 31 * result + (isFirstMove ? 1 : 0);
			return result;
		}

		@Override
		public boolean equals(final Object other) {
			if(this == other) {
				return true;
			}
			if(!(other instanceof Piece)) {
				return false;
			}
			final Piece otherPiece = (Piece) other;
			return piecePosition == otherPiece.getPiecePosition() && pieceType == otherPiece.getPieceType() &&
				   pieceAlliance == otherPiece.getPieceAlliance() && isFirstMove == otherPiece.isFirstMove();
		}
		
		@Override
		public int hashCode() {
			return this.cacheHashCode;
		}
		
		public int getPiecePosition() {
			return this.piecePosition;
		}
		
		public Alliance getPieceAlliance() {
			return this.pieceAlliance;
		}
		
		public boolean isFirstMove() {
			return this.isFirstMove;
		}
		
		public PieceType getPieceType() {
			return this.pieceType;
		}
		
		public int getPieceValue() {
			return this.pieceType.getPieceValue();
		}
		
		public abstract Collection<Move> calculateLegalMoves(final Board board);

		public abstract Piece movePiece(Move move);
		
		// PieceType allows to access piece value, and to determine if a single
		// piece is a certain type. 
		public enum PieceType {
			
			PAWN("P", 100) {
				@Override
				public boolean isKing() {
					return false;
				}

				@Override
				public boolean isRook() {
					return false;
				}

				@Override
				public int getAssignedNum() {
					return 5;
				}

				@Override
				public boolean isPawn() {
					return true;
				}

				@Override
				public boolean isQueen() {
					return false;
				}

				@Override
				public boolean isBishop() {
					return false;
				}
			},
			KNIGHT("N", 300) {
				@Override
				public boolean isKing() {
					return false;
				}

				@Override
				public boolean isRook() {
					return false;
				}

				@Override
				public int getAssignedNum() {
					return 3;
				}

				@Override
				public boolean isPawn() {
					return false;
				}

				@Override
				public boolean isQueen() {
					return false;
				}

				@Override
				public boolean isBishop() {
					return false;
				}
			},
			BISHOP("B", 320) {
				@Override
				public boolean isKing() {
					return false;
				}

				@Override
				public boolean isRook() {
					return false;
				}

				@Override
				public int getAssignedNum() {
					return 2;
				}

				@Override
				public boolean isPawn() {
					return false;
				}

				@Override
				public boolean isQueen() {
					return false;
				}

				@Override
				public boolean isBishop() {
					return true;
				}
			},
			ROOK("R", 500) {
				@Override
				public boolean isKing() {
					return false;
				}

				@Override
				public boolean isRook() {
					return true;
				}

				@Override
				public int getAssignedNum() {
					return 4;
				}

				@Override
				public boolean isPawn() {
					return false;
				}

				@Override
				public boolean isQueen() {
					return false;
				}

				@Override
				public boolean isBishop() {
					return false;
				}
			},
			QUEEN("Q", 900) {
				@Override
				public boolean isKing() {
					return false;
				}

				@Override
				public boolean isRook() {
					return false;
				}

				@Override
				public int getAssignedNum() {
					return 1;
				}

				@Override
				public boolean isPawn() {
					return false;
				}

				@Override
				public boolean isQueen() {
					return true;
				}

				@Override
				public boolean isBishop() {
					return false;
				}
			},
			KING("K", 10000) {
				@Override
				public boolean isKing() {
					return true;
				}

				@Override
				public boolean isRook() {
					return false;
				}

				@Override
				public int getAssignedNum() {
					return 0;
				}

				@Override
				public boolean isPawn() {
					return false;
				}

				@Override
				public boolean isQueen() {
					return false;
				}

				@Override
				public boolean isBishop() {
					return false;
				}
			};
			
			private String pieceName;
			private int pieceValue;
			
			PieceType(final String pieceName, final int pieceValue) {
				this.pieceName = pieceName;
				this.pieceValue = pieceValue;
			}
			
			@Override
			public String toString() {
				return this.pieceName;
			}
			
			public int getPieceValue() {
				return this.pieceValue;
			}
			
			public abstract boolean isKing();
			
			public abstract boolean isQueen();
			
			public abstract boolean isPawn();
			
			public abstract boolean isBishop();

			public abstract boolean isRook();
			
			public abstract int getAssignedNum();
			
			
		}
}
