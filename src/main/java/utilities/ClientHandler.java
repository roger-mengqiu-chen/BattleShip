package utilities;

import java.util.*;
import java.io.*;
import java.net.*;
import problemdomain.*;

public class ClientHandler extends Thread implements Observer{
	private ObjectOutputStream oos1, oos2;
	private Socket socket1, socket2;
	private InputListener listener1, listener2;
	
	
	public ClientHandler(Socket s1, Socket s2) {
		socket1 = s1;
		socket2 = s2;
		
		try {
			oos1 = new ObjectOutputStream(socket1.getOutputStream());
			oos2 = new ObjectOutputStream(socket2.getOutputStream());
			
		} catch (IOException e) {
			
			e.printStackTrace();
		}
	}
	
	public void run(){
		listener1 = new InputListener(1, socket1, this);
		listener2 = new InputListener(2, socket2, this);
		
		Thread t1 = new Thread(listener1);
		t1.start();
		Thread t2 = new Thread(listener2);
		t2.start();
		
		Message m = new Message("You can start chatting");
		try {
			oos1.writeObject(m);
			oos2.writeObject(m);
						
			while(socket1.isConnected() && socket2.isConnected());
			
			socket1.close();
			socket2.close();
			oos1.close();
			oos2.close();
			
		} catch (SocketException e) {
			e.printStackTrace();
			
			System.out.println("Client handler: socket closed");
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void update(Observable o, Object obj) {
		InputListener listener = (InputListener) o;
		
		try {
			if (listener.getListenerNum() == 1 && socket2.isConnected()) {
				
				oos2.writeObject(obj);
			}		
			else if (listener.getListenerNum() == 2 && socket1.isConnected()){			
				oos1.writeObject(obj);
					
			}	
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
