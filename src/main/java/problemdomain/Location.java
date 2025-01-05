package problemdomain;

import java.io.*;

/**
 * This class contain the coordinate of a cell
 *
 */
public class Location implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -7875352527653021646L;
	private int x;
	private int y;
	
	public Location(int _x, int _y) {
		x = _x;
		y = _y;
	}

	/**
	 * 
	 * @return x coordiante value
	 */
	public int getX() {
		return x;
	}

	/**
	 * 
	 * @return y coordinate value
	 */
	public int getY() {
		return y;
	}
	
	
}
