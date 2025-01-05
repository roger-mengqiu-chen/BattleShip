package problemdomain;

/**
 * Ship class.
 * The ship is aligned horizontally be default
 * x and y are line and column number of this ship
 */
public class Ship {
	private String name;
	private int size;
	private boolean rotate; 
	
	public Ship() {
		
	}
	
	public Ship (String _name, boolean _rotate) {
		this.name = _name;
		setSize(_name);
		
		this.rotate = _rotate; 
	}
	
	/**
	 * Set ship alignment
	 * @param _rotate true if aligned vertically
	 */
	public void setRotate(boolean _rotate) {
		this.rotate = _rotate;
	}
	
	/**
	 * @return true if it is aligned vertically
	 */
	public boolean isRotated() {
		return this.rotate;
	}
	
	/**
	 * Set size according to the name
	 * @param _name ship name
	 */
	private void setSize (String _name) {
		switch (_name.toLowerCase()) {
		case ("aircraft"): this.size = 5; break;
		case ("battleship"): this.size = 4; break;
		case ("cruiser"): this.size = 3; break;
		case ("submarine"): this.size = 3; break;
		case ("destroyer"): this.size = 2; break;
		}
	}
	
	/**
	 * 
	 * @return Size of this ship
	 */
	public int getSize() {
		return size;
	}
}
