package gui;

import java.awt.*;
import java.io.*;
import java.net.*;

import javax.swing.*;

import problemdomain.*;
import utilities.ClientHandler;

import java.util.*;
import javax.swing.border.LineBorder;

/**
 * Class of server GUI
 * Launching this GUI also launches the Server
 */
public class ServerWindow extends JFrame{

    private DefaultListModel<Message> msgHistMod; // Listmodel containing messages

    public ServerWindow() {
		int serverWindowWidth = 400;
		int serverWindowHeight = 500;
		int serverPort = 9999;
		// Create GUI
		setTitle("Server");

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		add(createNorth(), BorderLayout.NORTH);
		add(createCenter(), BorderLayout.CENTER);

        setSize(serverWindowWidth, serverWindowHeight);
		
		setVisible(true);

        ArrayList<Socket> socketList = new ArrayList<>();
		try (ServerSocket server = new ServerSocket(serverPort)) {
			msgHistMod.addElement(new Message("SERVER IS RUNNING ..........."));
			while(true) {
				Socket c = server.accept();

				msgHistMod.addElement(new Message("A PLAYER CONNECTED"));
				socketList.add(c);

				if(socketList.size() == 2) {
					ClientHandler ch = new ClientHandler(socketList.get(0), socketList.get(1));
					ch.start();
					socketList.clear();
				}
			}
		} catch (SocketException e) {
			e.printStackTrace();
			System.out.print("Socket Exception");
			System.out.println(e);
		} catch (IOException e2) {
			e2.printStackTrace();
		}
	}
	
	/*
	 * Below are methods creating GUI
	 */
	private JPanel createNorth() {
		JPanel panel = new JPanel(new GridLayout(1,4));
		JLabel labl_host = new JLabel("Host");
		labl_host.setHorizontalAlignment(SwingConstants.CENTER);
		labl_host.setFont(new Font("Segoe UI Black", Font.BOLD, 18));
		
		JTextField text_host = new JTextField("localhost");
		text_host.setHorizontalAlignment(SwingConstants.CENTER);
		text_host.setFont(new Font("Consolas", Font.PLAIN, 16));
		text_host.setEditable(false);
		
		JLabel labl_port = new JLabel("Port");
		labl_port.setHorizontalAlignment(SwingConstants.CENTER);
		labl_port.setFont(new Font("Segoe UI Black", Font.BOLD, 18));
		JTextField text_port = new JTextField("2333");
		text_port.setHorizontalAlignment(SwingConstants.CENTER);
		text_port.setFont(new Font("Consolas", Font.PLAIN, 16));
		text_port.setEditable(false);
		
		panel.add(labl_host);
		panel.add(text_host);
		panel.add(labl_port);
		panel.add(text_port);
		
		return panel;
	}
	
	private JPanel createCenter() {
		JPanel panel = new JPanel();
		
		msgHistMod = new DefaultListModel<>();

        JList<Message> msgHist = new JList<>(msgHistMod);
		
		msgHist.setCellRenderer(new cellRender(280)); // wrap displaying string
		msgHist.setFont(new Font("Tahoma", Font.PLAIN, 14));
		
		JScrollPane pane = new JScrollPane(msgHist);
		
		pane.setPreferredSize(new Dimension(380,400));
		pane.setViewportBorder(new LineBorder(new Color(0, 0, 0)));
		
		panel.add(pane);
		return panel;
	}

	/**
	 * This private class is used to wrap words displaying in the panel
	 *
	 */
	private class cellRender extends DefaultListCellRenderer {
		public static final String HTML_1 = "<html><body style='width: ";
		public static final String HTML_2 = "px'>";
		public static final String HTML_3 = "</html>";
		private final int width;

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

}
