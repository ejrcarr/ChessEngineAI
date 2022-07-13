
package board;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import pieces.Piece;


/*
 * 
 *  The Tile class has child classes EmptyTile and OccupiedTile to 
 *  return whether the tile is occupied and have getters to return
 *  if so to return a piece if not empty.
 * 
 */

public abstract class Tile {

	protected final int tileCoordinate;
	private static final Map<Integer, EmptyTile> EMPTY_TILES_CACHE = createAllPossibleEmptyTiles();
	
	// CONSTRUCTOR
	private Tile(final int tileCoordinate) {
		this.tileCoordinate = tileCoordinate;
	}
	
	
	public static Tile createTile(final int tileCoordinate, final Piece piece) {
		if (piece != null) {
			return new OccupiedTile(tileCoordinate, piece);
		}
		return EMPTY_TILES_CACHE.get(tileCoordinate);
	}
	
	private static Map<Integer, EmptyTile> createAllPossibleEmptyTiles() {
		
		final Map<Integer, EmptyTile> emptyTileMap = new HashMap<>();
		
		for (int i = 0; i < BoardUtils.NUM_TILES; i++) {
			emptyTileMap.put(i,  new EmptyTile(i));
		}
		
		return Collections.unmodifiableMap(emptyTileMap);
		
	}

	public abstract boolean isTileOccupied();
	
	public abstract Piece getPiece();
	
	public int getTileCoordinate() {
		return this.tileCoordinate;
	}
	
	
	
	// The EmptyTile class allows the toString method to 
	// have black and white empty tiles. Adds getter
	// methods to return false for isTileOccupied and
	// null for getPiece since the tile is an instance of
	// EmptyTile.
	public static final class EmptyTile extends Tile {
		
		EmptyTile(final int coordinate) {
			super(coordinate); 
		}
		
		@Override
		public String toString() {
			if ((tileCoordinate / 8) % 2 == 1) {
				return tileCoordinate % 2==1 ? "■" : "☐";
			}
			else {
				return tileCoordinate % 2 == 1 ? "☐" : "■";
			}
		}
		
		@Override
		public boolean isTileOccupied() {
			return false;
		}
		
		@Override
		public Piece getPiece() {
			return null;
		}
		
	}
	
	// The OccupiedTile class allows toString to show lowercase 
	// when on black pieces and uppercase while on white pieces. 
	// Adds getter methods to return true when asked isTileOccupied
	// and returns piece on occupied tile.
	public static final class OccupiedTile extends Tile {
		
		private final Piece pieceOnTile;
		
		OccupiedTile(int tileCoordinate, Piece pieceOnTile) {
			super(tileCoordinate);
			this.pieceOnTile = pieceOnTile;
		}
		
		@Override
		public String toString() {
			if (getPiece().getPieceAlliance().isBlack()) {
				return getPiece().toString().toLowerCase();
			}
			else {
				return getPiece().toString();
			}
		}
		
		@Override
		public boolean isTileOccupied() {
			return true;
		}
		
		@Override
		public Piece getPiece() {
			return this.pieceOnTile;
		}
		
	}
	
}
