package board;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/*
 * 
 *  This class initializes columns and rows, the algebreic notation of the board and 
 *  position to coordinate. Also this class contains contants to be used throughout 
 *  the application: NUM_TILES, and NUM_TILES_PER_ROW for readability. 
 * 
 */

public class BoardUtils {
	


	public static final boolean[] FIRST_COLUMN = initColumn(0);
	public static final boolean[] SECOND_COLUMN = initColumn(1);
	public static final boolean[] SEVENTH_COLUMN = initColumn(6);
	public static final boolean[] EIGHTH_COLUMN = initColumn(7);
	
	public static final boolean[] EIGHTH_ROW = initRow(0);
	public static final boolean[] SEVENTH_ROW = initRow(8);
	public static final boolean[] SECOND_ROW = initRow(48);
	public static final boolean[] FIRST_ROW = initRow(56);
	
	public static final int NUM_TILES = 64;
	public static final int NUM_TILES_PER_ROW = 8;
	
	public static final String[] ALGEBREIC_NOTATION = initalizeAlgebreicNotation();
	public static final Map<String, Integer> POSITION_TO_COORDINATE = initializePositionToCoordinateMap();
	
	
	// CONSTRUCTOR
	// Throws exception if the class tries to be instantiated.
	private BoardUtils() {
		throw new RuntimeException("Tried to instantiate BoardUtils class.");
	}

	// Returns a String array of the algebreic notation fo the chess board
	// with tile names as a1, a2, b1, b2, etc. 
	private static String[] initalizeAlgebreicNotation() {
		String[] tileNotation = new String[64];
        int[] numbers = {8, 7, 6, 5, 4, 3, 2, 1};
        String[] letters = {"a", "b", "c", "d", "e", "f", "g", "h"};
        int count = 0;
        for(int x : numbers) {
            for (String y : letters) {
                tileNotation[count] = y + x;
                        count++;
            }
        }
        return tileNotation;
	}
	
	// Returns a hashmap of the of the algebreic notation as a key and the 
	// coordinates as a value for easy conversions. 
	private static Map<String, Integer> initializePositionToCoordinateMap() {

		final Map<String, Integer> positionToCoordinate = new HashMap<>();
		
		for(int i = 0; i < NUM_TILES; i++) {
			positionToCoordinate.put(ALGEBREIC_NOTATION[i], i);
		}
		
		return Collections.unmodifiableMap(positionToCoordinate);
	}
	
	public static boolean[] initColumn(int columnNumber) {
			
			final boolean[] column = new boolean[NUM_TILES];
			
			do {
				column[columnNumber] = true;
				columnNumber += NUM_TILES_PER_ROW;
				
			}
			while (columnNumber < NUM_TILES);
			
			return column;
			
		}
	
	private static boolean[] initRow(int rowNumber) {
		final boolean[] row = new boolean[NUM_TILES];
		do {
			row[rowNumber] = true;
			rowNumber++;
		}
		while (rowNumber % NUM_TILES_PER_ROW != 0);
		
		return row;
	}
	
	
	// Turns if a coordinate is valid given input. 
	public static boolean isValidTileCoordinate(final int coordinate) {
		return (coordinate >= 0 && coordinate < 64);
	}

	// GETTERS ///////
	public static int getCoordinateAtPosition(final String position) {
		return POSITION_TO_COORDINATE.get(position);
	}
	
	public static String getPositionAtCoordinate(final int coordinate) {
		return ALGEBREIC_NOTATION[coordinate];
	}
	//////////////////
	
}
