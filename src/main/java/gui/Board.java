package gui;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;
import exception.*;
import problemdomain.*;

/**
 * This class is used to create a board with 10 x 10 grid of Boxes
 *
 */
public class Board extends JPanel implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -3045448034753250498L;
	
	private Box[][] board;
	
	/**
	 * Constructor of Board
	 * @param o true if it is created for opponent. 
	 */
	public Board(boolean o) {
		super();
		setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		setLayout(new GridLayout(10,10));
		board = new Box[10][10];
		for (int x = 0; x < 10; x ++) {
			for (int y = 0; y < 10; y ++) {
				Box b = new Box(x,y, o);
				b.setPreferredSize(new Dimension(20,20));
				board[x][y] = b;
				add(b);
			}
		}
	}
	
	/**
	 * Disable the whole board
	 */
	public void disable() {
		for (int i = 0; i < 10; i ++) {
			for (int j =0; j < 10; j ++) {
				board[i][j].setEnabled(false);;
			}
		}
	}
	
	/**
	 * Enable the whole board
	 */
	public void enable() {
		for (int i = 0; i < 10; i ++) {
			for (int j =0; j < 10; j ++) {
				board[i][j].setEnabled(true);;
			}
		}
	}
	
	/**
	 * Deploy a ship at (x,y)
	 * @param x
	 * @param y
	 * @param ship
	 * @throws ShipOutOfBoundException
	 * @throws BoardCellTakenException
	 */
	public void deploy(int x, int y, Ship ship) throws ShipOutOfBoundException, BoardCellTakenException {
		int size = ship.getSize();
		if (!ship.isRotated()) {
			// Check if the ship will be out of bound
			if (y + size > 10) throw new ShipOutOfBoundException();
			// Check if there's a ship in the targe area
			for (int i = y; i < y + size; i++) {
				if (board[x][i].isOccupied()) {
					throw new BoardCellTakenException();
				}		
			}
			// Occupy with ship
			for (int i = y; i < y + size; i++) {
				board[x][i].occupy();
			}
		} 
		// Similar as not rotate
		else {
			if (x + size > 10) throw new ShipOutOfBoundException();
			
			for (int i = x; i < x + size; i++) {	
				if (board[i][y].isOccupied()) {
					throw new BoardCellTakenException();
				}
			}
			for (int i = x; i < x + size; i++) {
				board[i][y].occupy();
			}
		}
	}
	
	/**
	 * Cell get hit.
	 * @param x
	 * @param y
	 * @return true if the hit cell is occupied by a ship
	 * @throws OverHitException thrown if the cell is hit over 1 time. 
	 */
	public boolean getHit(int x, int y) throws OverHitException {
		board[x][y].getHit();
		return board[x][y].isOccupied(); // if the cell is occupied by ship, return true: ship get hit!
	}
	
	/**
	 * This method loads external listener for each box of this board
	 * @param listener
	 */
	public void loadListener(ActionListener listener) {
		for (int i = 0; i < 10; i ++) {
			for (int j =0; j < 10; j ++) {
				board[i][j].addActionListener(listener);
			}
		}
	}
	
	/**
	 * Get the Box[][] of this board
	 * @return board
	 */
	public Box[][] getBoard() {
		return board;
	}
	
	/**
	 * Reset the whole board to original status
	 */
	public void reset() {
		for (int i = 0; i < 10; i ++) {
			for (int j = 0; j < 10; j ++) {
				board[i][j].reset();
			}
		}
	}
	
	
}
