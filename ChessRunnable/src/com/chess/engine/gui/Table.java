package gui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.ExecutionException;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;
import static pgn.PGNUtilities.persistPGNFile;


import ai.MiniMax;
import ai.MoveStrategy;
import board.Board;
import board.BoardUtils;
import board.Move;
import board.Move.MoveFactory;
import board.Tile;
import pgn.MySqlGamePersistence;
import pieces.Piece;
import player.MoveTransition;

/*
 * 
 *  Table is the main file for the GUI. Contains AI watcher which recognizes
 *  when its the AI's turn to move, JFrame(window for application), JPanel (board),
 *  and child JPanels (tiles). The table has a mouse action listener that senses when 
 *  the human clicks a tile and the second click for the destination tile. Includes sounds
 *  for when pieces move, attack, check, checkmate, and caste. 
 * 
 */

public class Table extends Observable {

	private final JFrame gameFrame;
	JLayeredPane layeredPane;
	int xAdjustment;
	int yAdjustment;
	JLabel chessPiece;
	private final GameHistoryPanel gameHistoryPanel;
	private final TakenPiecesPanel takenPiecesPanel;
	private final BoardPanel boardPanel; 
	private final MoveLog moveLog;
	private final GameSetup gameSetup;
	private Board chessBoard;
	private Tile sourceTile;
	private Tile destinationTile;
	private Piece humanMovedPiece;
	private BoardDirection boardDirection;
	private boolean useBook;
	private Move computerMove;
	private boolean highlightLegalMoves;
																	   //900  725 LAYERED
	private final static Dimension OUTER_FRAME_DIMENSION = new Dimension(875, 725);
																	   //500  500 LAYERED
	private final static Dimension BOARD_PANEL_DIMENSION = new Dimension(500,500);
	private final static Dimension TILE_PANEL_DIMENSION = new Dimension(10,10);
	
	
	// Teal and white colors
	//private final Color darkTileColor = Color.decode("#00D4FF");
	//private final Color lightTileColor = Color.decode("#dbe1e8");
	
	private final Color lightTileColor = Color.decode("#EEEED3");
	private final Color lighterGreenActivePiece = Color.decode("#BACA41");
    private final Color darkTileColor = Color.decode("#769656");
    private final Color lighterWhiteActivePiece = Color.decode("#F6F687");
    private final Color illegalMoveColor = Color.decode("#FF0000");
    private static final Table INSTANCE = new Table();
    
	@SuppressWarnings("deprecation")
	private Table() {
		this.gameFrame = new JFrame("Chess");
		this.gameFrame.setLayout(new BorderLayout());
		final JMenuBar tableMenuBar = createTableMenuBar();
		this.gameFrame.setJMenuBar(tableMenuBar);
		this.gameFrame.setSize(OUTER_FRAME_DIMENSION);
		this.highlightLegalMoves = true;
		this.useBook = true;
		this.chessBoard = Board.createStandardBoard();
		this.gameHistoryPanel = new GameHistoryPanel();
		this.takenPiecesPanel = new TakenPiecesPanel();
		this.gameSetup = new GameSetup(this.gameFrame, true);
		this.boardPanel = new BoardPanel();
		this.boardPanel.setBackground(Color.decode("0x312E2B"));
		this.moveLog = new MoveLog();
		this.addObserver(new TableGameAIWatcher());
		this.boardDirection = BoardDirection.NORMAL;
		this.gameFrame.add(this.takenPiecesPanel, BorderLayout.WEST);
		
		/*
		 * Potential drag and drop layout TODO
		 * 
		 * layeredPane = new JLayeredPane();
		 * layeredPane.setPreferredSize(BOARD_PANEL_DIMENSION);
		 * layeredPane.add(boardPanel, JLayeredPane.DEFAULT_LAYER);
		 * gameFrame.add(layeredPane, BorderLayout.CENTER);
		 * 
 		*/
	
		this.gameFrame.add(this.boardPanel, BorderLayout.CENTER);
		this.gameFrame.add(this.gameHistoryPanel, BorderLayout.EAST);
		this.gameFrame.setResizable(false);
		this.gameFrame.setLocationRelativeTo(null);
		this.gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.gameFrame.setVisible(true);
	}
	
	// GETTERS /////////
	public static Table get() {
		return INSTANCE;
	}
	
	private JFrame getGameFrame() {
        return this.gameFrame;
    }
	
	private boolean getUseBook() {
        return this.useBook;
    }
	
	private GameSetup getGameSetup() {
		return this.gameSetup;
	}
	
	private Board getGameBoard() {
		return this.chessBoard;
	}
	////////////////////
	
	public void show() {
		Table.get().getMoveLog().clear();
		Table.get().getGameHistoryPanel().redo(chessBoard,  Table.get().getMoveLog());
		Table.get().getTakenPiecesPanel().redo(Table.get().getMoveLog());
		Table.get().getBoardPanel().drawBoard(Table.get().getGameBoard());
	}
	
	// Parent table menu bar to contain the file, preferences, and option menus. 
	private JMenuBar createTableMenuBar() {
		final JMenuBar tableMenuBar = new JMenuBar();
		tableMenuBar.add(createFileMenu());
		tableMenuBar.add(createPreferencesMenu());
		tableMenuBar.add(createOptionsMenu());
		return tableMenuBar;
	}

	
	
	private class BoardPanel extends JPanel {
		ArrayList<TilePanel> boardTiles;
		
		BoardPanel() {
			super(new GridLayout(8, 8)); // 8 x 8 is the dimensions of chess board
										 // so 64 tiles.
			this.boardTiles = new ArrayList<>();
			for(int i = 0; i < BoardUtils.NUM_TILES; i++) {
				final TilePanel tilePanel = new TilePanel(this, i);
				this.boardTiles.add(tilePanel);
				add(tilePanel);
			}
			
			// Layered
			//setOpaque(true);
			//setBounds(0, 0, 738, 674);
			
			setPreferredSize(BOARD_PANEL_DIMENSION);
			validate();
		}
		
		public void drawBoard(final Board board ) {
			removeAll();
			for(TilePanel tilePanel : boardDirection.traverse(boardTiles)) {
				tilePanel.drawTile(board);
				add(tilePanel);
			}
			validate();
			repaint();	
		}
	}
	
	
	public static class MoveLog {
		private final ArrayList<Move> moves;
		
		// CONSTRUCTOR
		MoveLog() {
			this.moves = new ArrayList<>();
		}

		public ArrayList<Move> getMoves() {
			return this.moves;
		}
		
		public void addMove(final Move move) {
			this.moves.add(move);
		}
		
		public int size() {
			return this.moves.size();
		}
		
		public void clear() {
			this.moves.clear();
		}
		
		public Move removeMove(int index) {
			return this.moves.remove(index);
		}
		
		public boolean removeMove(final Move move) {
			return this.moves.remove(move);
		}
		
	}
	
	
	enum PlayerType {
		HUMAN,
		COMPUTER
	}
	
	// Tile Panel class is reponsible for redrawing the board after every move,
	// recognizing mouse clicks, and drawing piece icons on each tile.
	public class TilePanel extends JPanel {
		private final int tileId;
		JLabel chessPiece;
		public final static Image imgs[] = new Image[12];
		public TilePanel(final BoardPanel boardPanel, final int tileId) {
			super(new GridBagLayout());
			this.tileId = tileId;
			
			// Makes subimages out of the chess.png which contains 
			// all piece images. Adds each individual piece to a Image array
			try {
				BufferedImage image = ImageIO.read(ResourceLoader.load("img/chess.png"));
				int ind = 0;
				for(int y = 0; y < 400; y += 200) {
					for (int x = 0; x < 1200; x+=200) {		     // Piece icon sizes    v    v	
						imgs[ind] = image.getSubimage(x, y, 200, 200).getScaledInstance(82, 82, BufferedImage.SCALE_SMOOTH);
						ind++;
					}
				}
			} catch (IOException e1) {	
				e1.printStackTrace();
			}
			
			setPreferredSize(TILE_PANEL_DIMENSION);
			assignTileColor();       // Makes the tile colors dark and light squares
			assignTilePieceIcon(chessBoard);  // Adds piece images to the JPanel tiles according to board
			addMouseListener(new MouseListener() {

				@Override
				public void mouseClicked(final MouseEvent e) {}

				@Override
				public void mousePressed(final MouseEvent e) {    
					// If right clicks, reset source tile to give a redo.
					if(SwingUtilities.isRightMouseButton(e)) {
						sourceTile = null;
						destinationTile = null;
						humanMovedPiece = null;
					}
					// If left click, track the first click as a source tile and second
					// as the desintation tile. 
					else if (SwingUtilities.isLeftMouseButton(e)) {
						if(sourceTile == null) {  // First Click
							sourceTile = chessBoard.getTile(tileId);
							humanMovedPiece = sourceTile.getPiece();
							if(humanMovedPiece == null) {
								sourceTile = null;
							}
						}
						else {                    // Second Click
							destinationTile = chessBoard.getTile(tileId);
							final Move move = MoveFactory.createMove(chessBoard, sourceTile.getTileCoordinate(), destinationTile.getTileCoordinate());
							final MoveTransition transition = chessBoard.currentPlayer().makeMove(move);
							if(transition.getMoveStatus().isDone()) {
								chessBoard = transition.getToBoard();
								moveLog.addMove(move);
								
							    // SOUNDS
								if (move.isAttack()) {
									playSound("CapturedMove.wav");
								}
								else if(move.isCastlingMove()) {
									playSound("CastledMove.wav");
								}
								else if(chessBoard.currentPlayer().isInCheck()) {
									playSound("Check.wav");
								}
								else {
									playSound("MovedPieceSound.wav");
								}
							}
							
							// If move status is not done, it is illegal so 
							// flash the background color red to signify illegality.
							else {
								setBackground(illegalMoveColor);
							}
							
							// now reset
							sourceTile = null;
							destinationTile = null;
							humanMovedPiece = null;	
						}
						
						// After clicks redraw the board to update the new moves on the board
						SwingUtilities.invokeLater(new Runnable() {
							@Override
							public void run() {
								gameHistoryPanel.redo(chessBoard, moveLog);
								takenPiecesPanel.redo(moveLog);
								if(gameSetup.isAIPlayer(chessBoard.currentPlayer())) {
									Table.get().moveMadeUpdate(PlayerType.HUMAN);
								}
								boardPanel.drawBoard(chessBoard);
							}
						});
					}
				}

				@Override
				public void mouseReleased(final MouseEvent e) {}
			
				// If tile has a piece on it, signify it is clickable by cursor change.
				@Override
				public void mouseEntered(final MouseEvent e) {
					if(chessBoard.getTile(tileId).isTileOccupied()) {
						gameFrame.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
					}
					else {
						gameFrame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
					}
				}
				
				// Ensure cursor changes back on empty tiles.
				@Override
				public void mouseExited(final MouseEvent e) {
					if(!chessBoard.getTile(tileId).isTileOccupied()) {
						gameFrame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
					}
				}
			});
			
			addMouseMotionListener(new MouseMotionListener() {	
				@Override
				public void mouseDragged(MouseEvent e) {}
				@Override
				public void mouseMoved(MouseEvent e) {}
			});
			validate();	
		}
		
		public static Image[] getPieceImages() {
			return imgs;
		}
		
		// Getting audio from sounds
		public static synchronized void playSound(final String url) {
			 new Thread(new Runnable() { 
			      public void run() {
			        try {
			          Clip clip = AudioSystem.getClip();												
			          AudioInputStream inputStream = AudioSystem.getAudioInputStream(ResourceLoader.load("sounds/" + url));    
			          clip.open(inputStream);
			          clip.start(); 
			        } 
			        catch (Exception e) {
			          System.err.println(e.getMessage());
			        }
			      }
			 }).start();
		}
		
		// Draw tile, including piece icon, tile colors, the highlight legal moves options.
		public void drawTile(final Board board) {
			assignTileColor();
			assignTilePieceIcon(board);
			highlightLegals(board);
			validate();		
		}
		
		public void assignTilePieceIcon(final Board board) {
			this.removeAll();
			if(board.getTile(this.tileId).isTileOccupied()) {
				int assignedNum = board.getTile(tileId).getPiece().getPieceType().getAssignedNum();
				if(board.getTile(this.tileId).getPiece().getPieceAlliance().isBlack()) {
					assignedNum += 6;
				}
				add(new JLabel(new ImageIcon(imgs[assignedNum])));
			}
		}
		
		private void highlightLegals(final Board board) {
			if (highlightLegalMoves) {
				for(final Move move : pieceLegalMoves(board)) {
					if(move.getDestinationCoordinate() == this.tileId) {
						try {
							if (!chessBoard.getTile(move.getDestinationCoordinate()).isTileOccupied()) {
								add(new JLabel(new ImageIcon(ImageIO.read(ResourceLoader.load("img/potentialsLighter.png")).getScaledInstance(40, 40, BufferedImage.SCALE_SMOOTH))));	
							}
							else {
								setBackground(((tileId + tileId / 8) % 2 == 0) ? lighterWhiteActivePiece : lighterGreenActivePiece);
							}
						} catch(Exception e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
		
		private Collection<Move> pieceLegalMoves(final Board board) {
			if(humanMovedPiece != null && humanMovedPiece.getPieceAlliance() == board.currentPlayer().getAlliance()) {
				return humanMovedPiece.calculateLegalMoves(board);
			}
			return Collections.emptyList();
		}

		private void assignTileColor() {
			boolean isLight = ((tileId + tileId / 8) % 2 == 0);
            setBackground(isLight ? lightTileColor : darkTileColor);
            if(chessBoard.getTile(tileId).equals(sourceTile)) {
            	 setBackground(isLight ? lighterWhiteActivePiece : lighterGreenActivePiece);
            }
		}
		
	}
	
	// File menu includes loading PGN files into the SQL database and an exit button.
	// Gets selected file from JFileChooser and the PGN files can contain more than one
	// game.
	private JMenu createFileMenu() {
		
		// File option 
		final JMenu fileMenu = new JMenu("File");
		final JMenuItem openPGN = new JMenuItem("Load PGN File", KeyEvent.VK_O);
        openPGN.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            int option = chooser.showOpenDialog(Table.get().getGameFrame());
            if (option == JFileChooser.APPROVE_OPTION) {
                loadPGNFile(chooser.getSelectedFile());    // user interface to select file
            }
        });
        fileMenu.add(openPGN);
        
        // Exit button
		final JMenuItem exitMenuItem = new JMenuItem("Exit");
		exitMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		fileMenu.add(exitMenuItem);
		return fileMenu;
	}
	
	// Preferences Menu includes flipping board option, toggle legal moves, and using book moves
	private JMenu createPreferencesMenu() {
		final JMenu preferencesMenu = new JMenu("Preferences");
		final JMenuItem flipBoardMenuItem = new JMenuItem("Flip Board");
		flipBoardMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				boardDirection = boardDirection.opposite();
				boardPanel.drawBoard(chessBoard);
			}
		});
		preferencesMenu.add(flipBoardMenuItem);
		preferencesMenu.addSeparator();
		final JCheckBoxMenuItem legalMoveHighlighterCheckbox = new JCheckBoxMenuItem("Toggle Legal Moves", false);
		legalMoveHighlighterCheckbox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				highlightLegalMoves = !highlightLegalMoves;
			}
		});
		
		preferencesMenu.add(legalMoveHighlighterCheckbox);
		final JCheckBoxMenuItem cbUseBookMoves = new JCheckBoxMenuItem(
                "Use Book Moves", false);
        cbUseBookMoves.addActionListener(e -> useBook = cbUseBookMoves.isSelected());
        preferencesMenu.add(cbUseBookMoves);
		return preferencesMenu;
	}
	
	private static void loadPGNFile(final File pgnFile) {
        try {
            persistPGNFile(pgnFile);
            
        }
        catch (final IOException e) {
            e.printStackTrace();
        }
    }
	
   private void undoAllMoves() {
        for(int i = Table.get().getMoveLog().size() - 1; i >= 0; i--) {
            final Move lastMove = Table.get().getMoveLog().removeMove(Table.get().getMoveLog().size() - 1);
            this.chessBoard = this.chessBoard.currentPlayer().unMakeMove(lastMove).getToBoard();
        }
        this.computerMove = null;
        Table.get().getMoveLog().clear();
        Table.get().getGameHistoryPanel().redo(chessBoard, Table.get().getMoveLog());
        Table.get().getTakenPiecesPanel().redo(Table.get().getMoveLog());
        Table.get().getBoardPanel().drawBoard(chessBoard);
    }
   
    private void undoLastMove() {
       final Move lastMove = Table.get().getMoveLog().removeMove(Table.get().getMoveLog().size() - 1);
       this.chessBoard = this.chessBoard.currentPlayer().unMakeMove(lastMove).getToBoard();
       this.computerMove = null;
       Table.get().getMoveLog().removeMove(lastMove);
       Table.get().getGameHistoryPanel().redo(chessBoard, Table.get().getMoveLog());
       Table.get().getTakenPiecesPanel().redo(Table.get().getMoveLog());
       Table.get().getBoardPanel().drawBoard(chessBoard);
    }
	
	private JMenu createOptionsMenu() {
		
		final JMenu optionsMenu = new JMenu("Options");
		
		final JMenuItem resetMenuItem = new JMenuItem("New Game");
		KeyStroke n = KeyStroke.getKeyStroke(KeyEvent.VK_N, 0);
	    resetMenuItem.setAccelerator(n);
        resetMenuItem.addActionListener(e -> undoAllMoves());
        optionsMenu.add(resetMenuItem);
        
        final JMenuItem undoMoveMenuItem = new JMenuItem("Undo");
        KeyStroke backspace = KeyStroke.getKeyStroke(KeyEvent.VK_Z, 0);
        undoMoveMenuItem.setAccelerator(backspace);
        undoMoveMenuItem.addActionListener(e -> {
            if(Table.get().getMoveLog().size() > 0) {
                undoLastMove();
            }
        });
        optionsMenu.add(undoMoveMenuItem);
		
		final JMenuItem setupGameMenuItem = new JMenuItem("Setup Game");
		setupGameMenuItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				Table.get().getGameSetup().promptUser();
				Table.get().setupUpdate(Table.get().getGameSetup());
				
			}
			
		});
		
		optionsMenu.add(setupGameMenuItem);
		return optionsMenu;
		
	}
	
	
	@SuppressWarnings("deprecation")
	private void setupUpdate(final GameSetup gameSetup) {
		
		setChanged();
		notifyObservers(gameSetup);
		
	}
	
	private static class TableGameAIWatcher implements Observer {

		@Override
		public void update(Observable o, Object arg) {
			
			if(Table.get().getGameSetup().isAIPlayer((Table.get().getGameBoard().currentPlayer())) && 
					!Table.get().getGameBoard().currentPlayer().isInCheckmate() &&
					!Table.get().getGameBoard().currentPlayer().isInStalemate()) {
				
				final AIThinkTank thinkTank = new AIThinkTank();
				thinkTank.execute();
			}
			
			if(Table.get().getGameBoard().currentPlayer().isInCheckmate()) {
				System.out.println("GAME OVER: " + Table.get().getGameBoard().currentPlayer() + " is in checkmate!");
			}
			if(Table.get().getGameBoard().currentPlayer().isInStalemate()) {
				System.out.println("GAME OVER: " + Table.get().getGameBoard().currentPlayer() + " is in stalemate!");
			}
			
		}
		
	}
	
	public void updateGameBoard(final Board board) {
		this.chessBoard = board;
	}
	
	public void updateComputerMove(final Move move) {
		this.computerMove = move;
	}
	
	private MoveLog getMoveLog() {
		return this.moveLog;
	}
	
	private GameHistoryPanel getGameHistoryPanel() {
		return this.gameHistoryPanel;
	}
	
	private TakenPiecesPanel getTakenPiecesPanel() {
		return this.takenPiecesPanel;
	}
	
	private BoardPanel getBoardPanel() {
		return this.boardPanel;
	}
	
	@SuppressWarnings("deprecation")
	private void moveMadeUpdate(final PlayerType playerType) {
		setChanged();
		notifyObservers(playerType);
	}
	
	private static class AIThinkTank extends SwingWorker<Move, String> {
		
		private AIThinkTank() {
			
		}

		@Override
		protected Move doInBackground() throws Exception {
			
			//final MoveStrategy miniMax = new MiniMax(4);
			//final Move bestMove = miniMax.execute(Table.get().getGameBoard());
			
			final Move bestMove;
            final Move bookMove = Table.get().getUseBook()
                    ? MySqlGamePersistence.get().getNextBestMove(Table.get().getGameBoard(),
                    Table.get().getGameBoard().currentPlayer(),
                    Table.get().getMoveLog().getMoves().toString().replaceAll("\\[", "").replaceAll("]", ""))
                    : MoveFactory.getNullMove();
            if (Table.get().getUseBook() && bookMove != MoveFactory.getNullMove()) {
                bestMove = bookMove;
                
            }
            else {
            	final MoveStrategy miniMax = new MiniMax(4);
    			bestMove = miniMax.execute(Table.get().getGameBoard());
            }
			
			return bestMove;
		}
		
		public void done() {
			
			try {
				final Move bestMove = get();
				
				Table.get().updateComputerMove(bestMove);
				Table.get().updateGameBoard(Table.get().getGameBoard().currentPlayer().makeMove(bestMove).getToBoard());
				Table.get().getMoveLog().addMove(bestMove);
				Table.get().getGameHistoryPanel().redo(Table.get().getGameBoard(), Table.get().getMoveLog());
				Table.get().getTakenPiecesPanel().redo(Table.get().getMoveLog());
				Table.get().getBoardPanel().drawBoard(Table.get().getGameBoard());
				Table.get().moveMadeUpdate(PlayerType.COMPUTER);
				
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
			}
			
		}
	}
	
	public enum BoardDirection {
		
		NORMAL {

			@Override
			ArrayList<TilePanel> traverse(ArrayList<TilePanel> boardTiles) {
				return boardTiles;
			}

			@Override
			BoardDirection opposite() {
				return FLIPPED;
			}
			
		},
		FLIPPED {

			@Override
			ArrayList<TilePanel> traverse(ArrayList<TilePanel> boardTiles) {
				ArrayList<TilePanel> clone = new ArrayList<TilePanel>(boardTiles.size());
				for(TilePanel tile : boardTiles) {
					clone.add(tile);
				}
				Collections.reverse(clone);
				return clone;
			}

			@Override
			BoardDirection opposite() {
				return NORMAL;
			}
			
		};
		
		abstract ArrayList<TilePanel> traverse(final ArrayList<TilePanel> boardTiles);
		abstract BoardDirection opposite();
	}


	
}
