package exception;

/**
 * The exception is thrown if player clicks the cell that was hit before
 *
 */
public class OverHitException extends Exception{
	private String msg;
	public OverHitException() {
		msg = "Over hit on the cell";
	}
	
	public OverHitException(String s) {
		msg = s;
	}
	
	public String toString() {
		return msg;
	}
}
