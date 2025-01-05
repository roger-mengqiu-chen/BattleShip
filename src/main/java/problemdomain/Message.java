package problemdomain;

import java.util.*;
import java.io.*;

/**
 * Message class to contain player name, message content and time
 *
 */
public class Message implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 4432465113761713641L;
	private String msg;
	private String name;
	private Date timeStamp;
	
	public Message(String m) {
		name = "";
		msg = m;
		timeStamp = new Date();
	}
	
	public Message(String n, String m) {
		name = n;
		msg = m;
		timeStamp = new Date();		
	}
	
	/**
	 * 
	 * @return msg message content
	 */
	public String getMsg() {
		return msg;
	}
	
	/**
	 * 
	 * @return name of player
	 */
	public String getName() {
		return name;
	}	
	
	/**
	 * @return full information of message
	 */
	public String toString() {
		return "[" + timeStamp + "]" + " " + name + ": " + msg;
	}
}
