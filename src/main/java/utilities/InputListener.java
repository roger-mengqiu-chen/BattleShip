package utilities;
import java.io.*;
import java.util.*;

import problemdomain.Message;

import java.net.*;

public class InputListener extends Observable implements Runnable{
	
	private int listenerNum;
	private ObjectInputStream ois;
	private Socket socket;
	
	public InputListener (Socket socket, Observer o) {
		this.listenerNum = 0;
		this.socket = socket;
		addObserver(o);
	}
	
	public InputListener (int listenerNumber, Socket socket, Observer o) {
		this.listenerNum = listenerNumber;
		this.socket = socket;
		addObserver(o);
	}

	public int getListenerNum() {
		return listenerNum;
	}

	public void setListenerNum(int listenerNum) {
		this.listenerNum = listenerNum;
	}

	@Override
	public void run() {
		try {
			ois = new ObjectInputStream(socket.getInputStream());
			
			while(true) {
				Object o = ois.readObject();
				setChanged();
				notifyObservers(o);
				if (o instanceof Message) {
					Message m = (Message) o;
					if(m.getMsg().equals("DISCONNECTED")) {
						ois.close();						
						socket.close();
					}
				}
			}
		} catch(SocketException e) {
			e.printStackTrace();
			System.out.println("InputListener" + listenerNum + " : Socket closed");
		} catch (EOFException e) {
			System.out.println("No stream available");
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			
			e.printStackTrace();
		}
	}

}
