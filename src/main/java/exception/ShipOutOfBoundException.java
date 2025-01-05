package exception;

/**
 * This exception is thrown if the deploying ship is out of the bound of board
 *
 */
public class ShipOutOfBoundException extends Exception{
	private String message = "The ship is out of bound";
	
	public ShipOutOfBoundException() {
		
	}
	
	public ShipOutOfBoundException(String s) {
		this.message = s;
	}
	
	public String toString() {
		return message;
	}
}
