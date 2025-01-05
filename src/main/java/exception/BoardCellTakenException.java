package exception;

/**
 * This exception is thrown when occupying the cell of the board is taken
 *
 */
public class BoardCellTakenException extends Exception{
	private String message;
	
	public BoardCellTakenException() {
	}
	
	public BoardCellTakenException(String s) {
		this.message = s;
	}
	
	public String toString() {
		return message;
	}
}
