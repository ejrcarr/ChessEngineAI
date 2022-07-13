package ally;

import board.BoardUtils;
import player.BlackPlayer;
import player.Player;
import player.WhitePlayer;

/*
 * 
 *  This enum contains white and black alliances with functions 
 *  given to each.
 * 
 */

public enum Alliance {
	WHITE {
		@Override
		public int getDirection() {
			return -1;
		}
		
		@Override
		public boolean isWhite() {
			return true;
		}
		
		@Override
		public boolean isBlack() {
			return false;
		}

		@Override
		public Player choosePlayer(final WhitePlayer whitePlayer, final BlackPlayer blackPlayer) {
			return whitePlayer;
		}

		@Override
		public int getOppositeDirection() {
			return 1;
		}

		@Override
		public boolean isPawnPromotionSquare(int position) {
			return BoardUtils.EIGHTH_ROW[position];
		}
	},

	BLACK {
		@Override
		public int getDirection() {
			return 1;
		}
		
		@Override
		public boolean isWhite() {
			return false;
		}
		
		@Override
		public boolean isBlack() {
			return true;
		}

		@Override
		public Player choosePlayer(final WhitePlayer whitePlayer, final BlackPlayer blackPlayer) {
			return blackPlayer;
		}

		@Override
		public int getOppositeDirection() {
			return -1;
		}

		@Override
		public boolean isPawnPromotionSquare(int position) {
			return BoardUtils.FIRST_ROW[position];
		}
	};
	
	public abstract int getDirection();
	public abstract int getOppositeDirection();
	public abstract boolean isWhite();
	public abstract boolean isBlack();
	
	public abstract boolean isPawnPromotionSquare(int position);
	
	public abstract Player choosePlayer(WhitePlayer whitePlayer, BlackPlayer blackPlayer);
}
