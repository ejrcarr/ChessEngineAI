package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;

import board.Move;
import gui.Table.TilePanel;
import pieces.Piece;

/*
 * 
 *  This class displays the taken pieces on each side of the chess board. The player has
 *  the opponent pieces on their side to compare evaluations.
 * 
 */

public class TakenPiecesPanel extends JPanel {
	
	private final JPanel northPanel;
	private final JPanel southPanel;
	
	private static final Dimension TAKEN_PIECES_DIMENSION = new Dimension(60, 80);
	private static final Color PANEL_COLOR = Color.decode("#EEEED3"); //0x312E2B
;	private static final EtchedBorder PANEL_BORDER = new EtchedBorder(EtchedBorder.RAISED);
	
	
	
	public TakenPiecesPanel() {
		super(new BorderLayout());
		this.setBackground(PANEL_COLOR);
		this.setBorder(PANEL_BORDER);
		this.northPanel = new JPanel(new GridLayout(8, 2));
		this.southPanel = new JPanel(new GridLayout(8, 2));
		this.northPanel.setBackground(PANEL_COLOR);
		this.southPanel.setBackground(PANEL_COLOR);
		add(this.northPanel, BorderLayout.NORTH);
		add(this.southPanel, BorderLayout.SOUTH);
		setPreferredSize(TAKEN_PIECES_DIMENSION);
		
	}
	
	public static BufferedImage toBufferedImage(Image img)
	{
	    if (img instanceof BufferedImage)
	    {
	        return (BufferedImage) img;
	    }

	    BufferedImage bufferedImage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);

	    Graphics2D bGr = bufferedImage.createGraphics();
	    bGr.drawImage(img, 0, 0, null);
	    bGr.dispose();

	    return bufferedImage;
	}
	
	public void redo(final Table.MoveLog moveLog) {
		
		this.southPanel.removeAll();
		this.northPanel.removeAll();
		
		final ArrayList<Piece> whiteTakenPieces = new ArrayList<>();
		final ArrayList<Piece> blackTakenPieces = new ArrayList<>();
		
		for(final Move move : moveLog.getMoves()) {
			if(move.isAttack()) {
				final Piece takenPiece = move.getAttackedPiece();
				if(takenPiece.getPieceAlliance().isWhite()) {
					whiteTakenPieces.add(takenPiece);
				}
				else if (takenPiece.getPieceAlliance().isBlack()) {
					blackTakenPieces.add(takenPiece);
				}
				else {
					throw new RuntimeException("Taken Piece is neither black nor white");
				}
			}
		}
		
		Collections.sort(whiteTakenPieces, new Comparator<Piece>() {

			@Override
			public int compare(Piece o1, Piece o2) {
				return o1.getPieceValue() - o2.getPieceValue();
			}
			
		});
		
		Collections.sort(blackTakenPieces, new Comparator<Piece>() {

			@Override
			public int compare(Piece o1, Piece o2) {
				return o1.getPieceValue() - o2.getPieceValue();
			}
			
		});
		
		
		for (final Piece takenPiece : whiteTakenPieces) {
			Image piece = TilePanel.getPieceImages()[takenPiece.getPieceType().getAssignedNum()].getScaledInstance(30, 30, BufferedImage.SCALE_SMOOTH);
			BufferedImage image = toBufferedImage(piece);
			final ImageIcon icon = new ImageIcon(image);
			final JLabel imageLabel = new JLabel(icon);
			this.northPanel.add(imageLabel);
		}
		
		for (final Piece takenPiece : blackTakenPieces) {
			Image piece = TilePanel.getPieceImages()[takenPiece.getPieceType().getAssignedNum() + 6].getScaledInstance(30, 30, BufferedImage.SCALE_SMOOTH);
			final BufferedImage image = toBufferedImage(piece);
			final ImageIcon icon = new ImageIcon(image);
			final JLabel imageLabel = new JLabel(icon);
			this.southPanel.add(imageLabel);
		}
		
		validate();
		
	}

}
