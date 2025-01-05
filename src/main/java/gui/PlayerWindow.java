package gui;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;
import exception.*;
import problemdomain.*;
import utilities.*;
import java.util.*;
import java.net.*;

/**
 * This class creates Player GUI
 * Player use this GUI to connect with other player. 
 * Player can chat, play game with opponent. 
 */
@SuppressWarnings("serial")
public class PlayerWindow extends JFrame implements Observer{
	
	// Basic GUI components
	private String playerName;
	private String enemyName;
	private JButton but_connect;
	private JButton but_disconnect;
	private JTextField txt_op; // Text field for opponent name
	private JTextField txt_player; // Text field for player name
	
	// Chat GUI components
	private JButton send;
	private JList<Message> chatHist;
	private DefaultListModel<Message> chatHistModel; // Message container to display incoming messages
	private JTextField msg; // Text field of player typed message
	
	// Game GUI components, used for gaming
	private JButton but_Aircraft;
	private JButton but_Battleship;
	private JButton but_Cruiser;
	private JButton but_Submarine;
	private JButton but_Destroyer;
	private JButton deployed;
	private Board playerBoard;
	private Board opBoard;

	// Player attributes 
	private int totalShipCells; // Amount of cells that are not hit
	private int deployedShipCells; // Amount of cells that have be taken by ships
	private boolean deployMod; // it is true if the player is delploying ships
	private Ship deployingShip; // The ship that is being deployed
	private boolean newSession; 
	private double myRand, opRand; // Random numbers that are used to decide which player start first
	private boolean myTurn; // True if it's the player starts first
	private boolean youWon; // True if player won a game
	private boolean ready; // True if player is ready to combat
	private boolean opReady; // True if opponent is ready
	
	// Network attributes
	private Socket socket;
	private InputListener listener;
	private ObjectOutputStream oos;
	
	public PlayerWindow() {
		setTitle("Player");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosing(WindowEvent e)
            {
            	try {
					oos.writeObject(new Message(playerName, "DISCONNECTED"));
				} catch (IOException e1) {			
					e1.printStackTrace();
				}
            	finally {
            		
            		disconnect();
            	} 

            }
            	
        });
		totalShipCells = 17; // all the ships occupy 17 cells on board in total;
		deployedShipCells = 0;
		setSize(1500,600);
		
		getContentPane().add(createNorth(), BorderLayout.NORTH);
		getContentPane().add(createWest(), BorderLayout.WEST);
		getContentPane().add(createCenter(), BorderLayout.CENTER);
		getContentPane().add(createEast(), BorderLayout.EAST);
		
		but_disconnect.setEnabled(false);
		deployed.setEnabled(false);
		send.setEnabled(false);
		
		setVisible(true);
	}
	
	// Below are GUI creation methods
	/**
	 * North panel contains player name, opponent name, connect, disconnect, ready to combat buttons
	 * @return JPanel north panel
	 */
	private JPanel createNorth() {
		JPanel panel = new JPanel(new BorderLayout());
		
		JPanel left = new JPanel(new GridLayout(1,2));
		JLabel lbl_pl = new JLabel ("Player: ");
		lbl_pl.setFont(new Font("Segoe UI", Font.PLAIN, 24));
		txt_player = new JTextField();
		txt_player.setEditable(false);
		txt_player.setFont(new Font("Segoe UI", Font.PLAIN, 24));
		txt_player.setPreferredSize(new Dimension(200,30));
		left.add(lbl_pl);
		left.add(txt_player);
		
		JPanel right = new JPanel(new GridLayout(1,2));
		JLabel lbl_ene = new JLabel("Opponent:");
		txt_op = new JTextField();
		txt_op.setEditable(false);
		txt_op.setFont(new Font("Segoe UI", Font.PLAIN, 24));
		txt_op.setPreferredSize(new Dimension(200,30));
		lbl_ene.setFont(new Font("Segoe UI", Font.PLAIN, 20));
		right.add(lbl_ene);
		right.add(txt_op);
		
		JPanel center = new JPanel(new GridLayout(1,3));
		but_connect = new JButton ("Connect");
		but_connect.setFont(new Font("Segoe UI Semibold", Font.BOLD, 20));
		but_connect.addActionListener(new ButtonListener());
		
		but_disconnect = new JButton ("Disconnect");
		but_disconnect.setFont(new Font("Segoe UI Semibold", Font.BOLD, 20));
		but_disconnect.addActionListener(new ButtonListener());
		
		deployed = new JButton("READY TO COMBAT");
		deployed.setFont(new Font("Segoe UI Semibold", Font.BOLD, 20));
		deployed.addActionListener(new ButtonListener());
		
		center.add(but_connect);
		center.add(but_disconnect);
		center.add(deployed);
		
		panel.add(center, BorderLayout.CENTER);
		panel.add(left, BorderLayout.WEST);
		panel.add(right, BorderLayout.EAST);
		return panel;
	}
	
	/**
	 * This panel contains all the ship buttons 
	 * Players can click those buttons to deploy ships
	 * @return JPanel the panel of ships
	 */
	private JPanel createWest() {
		JPanel panel = new JPanel(new GridLayout(5,1));
		but_Aircraft = new JButton("Aircraft 5 squares");
		but_Aircraft.setFont(new Font("Segoe UI Black", Font.BOLD, 18));
		but_Aircraft.addActionListener(new ShipListener());
		but_Aircraft.setEnabled(false);
		
		but_Battleship = new JButton("Battleship 4 sqaures");
		but_Battleship.setFont(new Font("Segoe UI Black", Font.BOLD, 18));
		but_Battleship.addActionListener(new ShipListener());
		but_Battleship.setEnabled(false);
		
		but_Cruiser = new JButton("Cruiser 3 squares");
		but_Cruiser.setFont(new Font("Segoe UI Black", Font.BOLD, 18));
		but_Cruiser.addActionListener(new ShipListener());
		but_Cruiser.setEnabled(false);
		
		but_Submarine = new JButton("Submaine 3 squares");
		but_Submarine.setFont(new Font("Segoe UI Black", Font.BOLD, 18));
		but_Submarine.addActionListener(new ShipListener());
		but_Submarine.setEnabled(false);
		
		but_Destroyer = new JButton("Destroyer 2 squares");
		but_Destroyer.setFont(new Font("Segoe UI Black", Font.BOLD, 18));
		but_Destroyer.addActionListener(new ShipListener());
		but_Destroyer.setEnabled(false);
		
		panel.add(but_Aircraft);
		panel.add(but_Battleship);
		panel.add(but_Cruiser);
		panel.add(but_Submarine);
		panel.add(but_Destroyer);
		return panel;
	}
	
	/**
	 * The panel contains two boards for player and opponent
	 * Player can deploy ships on board and hit grid of opponent board
	 * @return JPanel center panel 
	 */
	private JPanel createCenter() {
		JPanel panel = new JPanel(new GridLayout(1,2));
		JPanel playerPanel = new JPanel(new BorderLayout());
		JPanel opPanel = new JPanel(new BorderLayout());
		// player board
		JLabel lbl_player = new JLabel("Player");
		lbl_player.setHorizontalAlignment(SwingConstants.CENTER);
		lbl_player.setFont(new Font("Tahoma", Font.BOLD, 18));
		playerPanel.add(lbl_player, BorderLayout.NORTH);
		playerBoard = new Board(false);
		playerBoard.disable();
		// Load listener for each box inside
		playerBoard.loadListener(new BoardListener());
		playerPanel.add(playerBoard, BorderLayout.CENTER);
		
		// opponent board
		JLabel lbl_op = new JLabel("Opponent");
		lbl_op.setHorizontalAlignment(SwingConstants.CENTER);
		lbl_op.setFont(new Font("Tahoma", Font.BOLD, 16));
		opPanel.add(lbl_op, BorderLayout.NORTH);
		opBoard = new Board(true);
		opBoard.disable();
		opBoard.loadListener(new BoardListener());
		opPanel.add(opBoard, BorderLayout.CENTER);
		
		panel.add(playerPanel);
		panel.add(opPanel);
		return panel;
	}
	
	/**
	 * This panel contains chat components
	 * @return JPanel east panel
	 */
	private JPanel createEast() {
		JPanel panel = new JPanel(new BorderLayout());
		chatHistModel = new DefaultListModel<>();
		chatHist = new JList<>(chatHistModel);
		chatHist.setCellRenderer(new cellRender(190));
		chatHist.setFont(new Font("Tahoma", Font.PLAIN, 14));
		JScrollPane pane = new JScrollPane(chatHist);
		
		JPanel bottom = new JPanel(new BorderLayout());
		msg = new JTextField();
		msg.setFont(new Font("Segoe UI", Font.PLAIN, 16));
		msg.setPreferredSize(new Dimension(100,20));
		send = new JButton("Send");
		send.setFont(new Font("Segoe UI Black", Font.BOLD, 16));		
		send.addActionListener(new ButtonListener());
		bottom.add(msg, BorderLayout.CENTER);
		bottom.add(send, BorderLayout.EAST);
		
		panel.add(pane, BorderLayout.CENTER);
		panel.add(bottom, BorderLayout.SOUTH);
		return panel;
	}
	
	/**
	 * This private class is used to wrap words displaying in the panel
	 */
	private class cellRender extends DefaultListCellRenderer {
		public static final String HTML_1 = "<html><body style='width: ";
		public static final String HTML_2 = "px'>";
		public static final String HTML_3 = "</html>";
		private int width;

		public cellRender(int width) {
			this.width = width;
		}

		@Override
		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
				boolean cellHasFocus) {
			String text = HTML_1 + String.valueOf(width) + HTML_2 + value.toString() + HTML_3;
			return super.getListCellRendererComponent(list, text, index, isSelected, cellHasFocus);
		}
	}
	// GUI components creation methods end
	

	// Below are operational methods
	
	/**
	 * Connect to socket and also send message to indicate the player is online
	 */
	public void connect() {
		
		try {
			socket = new Socket("localhost", 2333);
			System.out.println(socket);
			// If it is a new session, player needs to enter name
			if (newSession) {
				playerName = JOptionPane.showInputDialog("Enter your name");
				txt_player.setText(playerName);
			}
			but_connect.setEnabled(false);			
			oos = new ObjectOutputStream(socket.getOutputStream());	
			chatHistModel.addElement(new Message("Me", "Connected! Waiting for another player"));
			listener = new InputListener(socket, PlayerWindow.this);
			Thread t1 = new Thread(listener);
			resetGame(); // Initialize the game components
			t1.start();			
		} 
		catch (HeadlessException e1) {
			e1.printStackTrace();
		}
		catch (UnknownHostException e1) {
			e1.printStackTrace();
		}
		catch (IOException e) {
			
			e.printStackTrace();
		}
	}
	
	/**
	 * Send message to indicate the player go offline and close everything
	 */
	public void disconnect() {
		chatHistModel.addElement(new Message("Disconnected"));
		but_disconnect.setEnabled(false);
		send.setEnabled(false);
		but_connect.setEnabled(true);
		// If disconnect, reset game board
		resetGame();
		myRand = opRand = 0; // reset random number to start the game randomly
		try {
			oos.flush();
			oos.close();
			socket.close();
			listener = null;
			System.out.println("player socket closed");
		} catch (SocketException e) {
			e.printStackTrace();
			System.out.println("Socket closed");
		} catch (IOException e) {			
			e.printStackTrace();
		}
	}
	
	/**
	 * This method resets the gaming area including ship buttons, player and opponent board
	 */
	public void resetGame() {
		playerBoard.reset();
		opBoard.reset();
		opBoard.disable();
		totalShipCells = 17;
		deployedShipCells = 0;
		ready = false;
		opReady = false;
		
		but_Aircraft.setEnabled(true);
		but_Battleship.setEnabled(true);
		but_Cruiser.setEnabled(true);
		but_Submarine.setEnabled(true);
		but_Destroyer.setEnabled(true);
	
		deployed.setEnabled(false);
	}

	/**
	 * Receive objects and update according to the objects
	 */
	@Override
	public void update(Observable o, Object arg) {
		// If received a message
		if (arg instanceof Message)  {
			Message m = (Message) arg;
			
			if (m.getMsg().equals("DEPLOYED")) {
				opBoard.enable();
				chatHistModel.addElement(new Message("Your opponent is ready"));
			}
			
			else if (m.getMsg().equals("You can start chatting")) {
				chatHistModel.addElement(m);
				send.setEnabled(true);
				but_disconnect.setEnabled(true);
				// After being notified chat session start, send message to the other player. This message also tells opponent player's name
				try {
					oos.writeObject(new Message(playerName, "CONNECTED"));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			// If receive opponent's random number
			else if (m.getMsg().startsWith("RAND:")) {
				String s = m.getMsg().substring(5);
				opRand = Double.parseDouble(s);
				opReady = true;
				System.out.println(playerName + " oprand: "+ opRand);
				// When the player is ready, the random number won't be 0
				// Only new seesion needs compare random numbers
				if (newSession && myRand != 0) {
					youWon = myRand >= opRand;
				}
				// If it is player start first, inform player when both are ready to play
				
				System.out.println("ready: " + ready);
				System.out.println("opReady: " + opReady);
				System.out.println("youWon: " + youWon);
				if (youWon && ready && opReady) {
					JOptionPane.showMessageDialog(null, playerName + " ,You start first");
					opBoard.enable();
				}	
				else {
					opBoard.disable();
				}
			}			

			// When get "CONNECTED" message, get the name of opponent
			else if (m.getMsg().equals("CONNECTED")) {
				enemyName = m.getName();
				txt_op.setText(enemyName);
				chatHistModel.addElement(new Message("You are paired with " + enemyName));
			}
			
			else if (m.getMsg().equals("DEPLOYED")) {
				opReady = true;
				opBoard.enable();
				chatHistModel.addElement(new Message("Your opponent is ready"));	
			}
			
			else if (m.getMsg().equals("DISCONNECTED")) {
				
				newSession = false;
				chatHistModel.addElement(m);
				disconnect();
				int option = JOptionPane.showConfirmDialog(null, "Your opponent left, do you want to continue?");
				if (option == 0) {
					connect();
				}
					
			}
			
			// If opponent lost the game
			else if (m.getMsg().equals("LOST THE GAME")) {
				chatHistModel.addElement(m);
				JOptionPane.showMessageDialog(null, playerName + " ,You win!");
				chatHistModel.addElement(new Message("You win the game!"));
				int continueOption = JOptionPane.showConfirmDialog(null, "Do you want to continue?");
				// Select No, drop current session
				if (continueOption == 1) {
					newSession = true;
					try {
						oos.writeObject(new Message(playerName, "DISCONNECTED"));
					} catch (IOException e1) {	
						e1.printStackTrace();
					}
					disconnect();
				}
				// Otherwise, continue the session
				else {
					youWon = true; // I won the game, so I'm first to hit. 
					resetGame();
				}			
			}
			
			// The rest type of messages are chat message
			else {
				chatHistModel.addElement(m);
			}
		}
		
		/**
		 * If receive the board from opponent, update boxes of current opponent board 
		 */
		if (arg instanceof Board) {		
			Board b = (Board)arg;
			Box[][] boxes = b.getBoard(); // Boxes from received board
			Box[][] opBoxes = opBoard.getBoard(); // Boxes from current opponent board
			for (int i = 0; i < 10; i++) {
				for (int j = 0; j < 10; j++) {
					if (boxes[i][j].isOccupied()) {
						opBoxes[i][j].occupy();
					}
				}			
			}
		}
		
		// Get location sent from other player
		if (arg instanceof Location) {
			Location l = (Location) arg;
			int x = l.getX();
			int y = l.getY();
			
			try {
				// The board get hit, judge if the player's ship get hit
				if(playerBoard.getHit(x, y)) {
					myTurn = true; 
					opBoard.enable();
					totalShipCells --;
					// when all the ship cells get hit, lose the game
					if (totalShipCells == 0) {	
						youWon = false;
						oos.writeObject(new Message(playerName, "LOST THE GAME"));
						chatHistModel.addElement(new Message("You lost the game"));
						JOptionPane.showMessageDialog(null, playerName + " ,You lose!");
						int continueOption = JOptionPane.showConfirmDialog(null, "Do you want to continue?");
						// If choose No, disconnect the session. 
						if (continueOption == 1) {
							newSession = true;
							try {
								oos.writeObject(new Message(playerName, "DISCONNECTED"));
							} catch (IOException e1) {							
								e1.printStackTrace();
							}
							disconnect();
						}
						// Choose to continue
						else {
							resetGame();
						}

					}
					
				}
				// If the ships are not hit, still update the myTurn 
				else {
					myTurn = true;
					opBoard.enable();
				}
			} catch (OverHitException e) {
				// If the cell has been hit, ignore the operation
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * This class take care of connect, disconnect, ready to combat, send button
	 *
	 */
	private class ButtonListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			// Connect button
			if (e.getSource() == but_connect) {
				newSession = true;
				connect();			
				playerBoard.enable();
			}
			
			// Disconnect button
			if (e.getSource() == but_disconnect) {
				newSession = true;
				try {
					oos.writeObject(new Message(playerName, "DISCONNECTED"));
				} catch (IOException e1) {			
					e1.printStackTrace();
				}
				disconnect();
			}
			
			// Send button
			if (e.getSource() == send) {
				Message m = new Message(playerName, msg.getText());				
				try {
					oos.writeObject(m);
					chatHistModel.addElement(new Message("Me", msg.getText()));
					msg.setText("");
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
			
			// Deployed (ready to combat) button
			else if( e.getSource() == deployed) {
				Message m = new Message(playerName, "DEPLOYED");
				try {
					oos.writeObject(m);
					chatHistModel.addElement(new Message("Me", "Ready to combat"));
					oos.writeObject(playerBoard);
					myRand = Math.random();
					oos.writeObject(new Message(playerName, "RAND:" + myRand));
					ready = true;
				} catch (IOException ex) {
					ex.printStackTrace();
				}
				
				// When opponent is ready, the random number won't be 0
				// Only new seesion needs compare random numbers
				System.out.println(playerName+" myrand: " + myRand);
				if (newSession && opRand != 0) {
					youWon = myRand >= opRand;
				}
				// If it is player's turn, inform player
				if (youWon && ready && opReady) {
					System.out.println(playerName);
					JOptionPane.showMessageDialog(null, playerName + " ,You start first");
					opBoard.enable();
				}
				else {
					// If it's not player's turn, he can't click on oponent's board
					opBoard.disable();
				}
			}
		}		
	}
	
	/**
	 * The class is responsible for dealing with deploying ships and hitting board 
	 *
	 */
	private class BoardListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			Box b = (Box) e.getSource();
			// Deploying ships
			if (!b.isEnemy() && deployMod) {
				int x = b.retreiveX();
				int y = b.retreiveY();
				try {
					playerBoard.deploy(x, y, deployingShip);
					deployedShipCells += deployingShip.getSize();
					// once deployed, reset deployMod and clear the currently selected ship
					deployMod = false;
					deployingShip = null;
					
					// Once all the ships are deployed, enable "ready to combat"
					if (deployedShipCells == 17) {
						deployed.setEnabled(true);
					}
				} catch (ShipOutOfBoundException e1) {
					JOptionPane.showMessageDialog(null, "The ship is out of bound");
				} catch (BoardCellTakenException e2) {
					JOptionPane.showMessageDialog(null, "There are occupied cells");
				}
			}
			
			// Send location to fire bullet
			if (b.isEnemy()) {
				int x = b.retreiveX();
				int y = b.retreiveY();				
				try {
					b.getHit();
					Location l = new Location(x, y);
					oos.writeObject(l);
					myTurn = false; // After firing, it's opponent's turn. 
					opBoard.disable();
				} catch (OverHitException e2) {
					// If the cell has been clicked before, inform player
					JOptionPane.showMessageDialog(null, "The cell has been clicked");
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}	
	}
	
	/**
	 * The class is for checking which ship is selected to be deployed
	 *
	 */
	private class ShipListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == but_Aircraft) {
				deployMod = true;
				// ask if rotate the ship
				boolean rotate = JOptionPane.showConfirmDialog(null,"Deploy ship vertically?") == 0;
				deployingShip = new Ship("Aircraft", rotate);
				but_Aircraft.setEnabled(false);				
			}
			
			else if (e.getSource() == but_Battleship) {
				deployMod = true;
				// ask if rotate the ship
				boolean rotate = JOptionPane.showConfirmDialog(null,"Deploy ship vertically?") == 0;
				deployingShip = new Ship("Battleship", rotate);
				but_Battleship.setEnabled(false);
			}
			
			else if (e.getSource() == but_Cruiser) {
				deployMod = true;
				// ask if rotate the ship
				boolean rotate = JOptionPane.showConfirmDialog(null,"Deploy ship vertically?") == 0;
				deployingShip = new Ship("Cruiser", rotate);
				but_Cruiser.setEnabled(false);
			}
			
			else if (e.getSource() == but_Submarine) {
				deployMod = true;
				// ask if rotate the ship
				boolean rotate = JOptionPane.showConfirmDialog(null,"Deploy ship vertically?") == 0;
				deployingShip = new Ship("Submarine", rotate);
				but_Submarine.setEnabled(false);
			}
			
			else {
				deployMod = true;
				// ask if rotate the ship
				boolean rotate = JOptionPane.showConfirmDialog(null,"Deploy ship vertically?") == 0;
				deployingShip = new Ship("Destroyer", rotate);
				but_Destroyer.setEnabled(false);
			}	
		}
	}
}
