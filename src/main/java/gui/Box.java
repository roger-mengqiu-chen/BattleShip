package gui;

import java.awt.*;
import java.io.*;
import javax.swing.*;
import exception.*;

/**
 * This component is a cell of player grid
 * occupied --> true if the cell is occupied by a ship
 * c --> background color of this cell
 * x, y --> coordinate value of this cell
 * isOpponent --> true if the cell belongs to opponent
 * isHit --> true if the cell has been hit once
 *
 */
public class Box extends JButton implements Serializable{
	
	private int x, y;
	private boolean occupied;
	private boolean isOpponent;
	private boolean isHit;
	
	public Box() {
		setBackground(Color.gray);
	}
	
	public Box(int x, int y, boolean op) {
		this.x = x;
		this.y = y;
		occupied = false;
		setBackground(Color.gray);
		isOpponent = op;
	}
	
	/**
	 * @return x coordinate value
	 */
	public int retreiveX() {
		return this.x;
	}
	
	/**
	 * @return y coordinate value
	 */
	public int retreiveY() {
		return this.y;
	}
	
	/**
	 * When this cell is clicked:
	 * 		If the cell is occupied by a ship, set it red
	 * 		else set it white
	 * @return true if hit on target
	 * @throws OverHitException if the cell has been hit
	 */
	public boolean getHit() throws OverHitException {
		if (!isHit) {
			isHit = true;
			if (occupied) {
				this.setBackground(Color.red);
				return true;
			}
			else {
				this.setBackground(Color.white);
				return false;
			}
		}
		else {
			throw new OverHitException();
		}
	}
	
	/**
	 * Occupy a cell with ship
	 */
	public void occupy() {
		occupied = true;
		// If this box is shown as player's box, get green color. Otherwise, don't change the color
		if(!isOpponent) {
			this.setBackground(Color.green);
		}		
	}
	/**
	 * 
	 * @return true if the cell is occupied by a ship
	 */
	public boolean isOccupied() {
		return occupied;
	}
	
	/**
	 * 
	 * @return true if the cell belongs to opponent
	 */
	public boolean isEnemy() {
		return isOpponent;
	}
	
	/**
	 * Make the cell belongs to opponent
	 */
	public void setAsEnemy() {
		isOpponent = true;
	}
	
	/**
	 * Reset the status of this cell
	 */
	public void reset() {
		isHit = false;
		occupied = false;
		this.setBackground(Color.gray);
	}
	
}
