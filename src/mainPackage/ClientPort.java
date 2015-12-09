package mainPackage;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.Socket;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import utilities.FontClass;

public class ClientPort extends GrayPanel{
	public static final long serialVersionUID = 1;
	private JLabel portLabel, hostnameLabel,errorLabel;
	private JTextField portTextField, hostnameTextField;
	private JButton connectButton;
	private boolean connected;
	private ClientSocket ct;
	private JFrame mainFrame;//get access to main so it can update as server send data
	
	ClientPort(MainGUI frame){
		mainFrame = frame;
		initializeComponents();
		createGUI();
		addEvents(frame);
	}
	
	public void initializeComponents(){
		portLabel = new JLabel("Port:");
		portLabel.setFont(FontClass.kenThinFont(20));
		portLabel.setHorizontalTextPosition(SwingConstants.CENTER);
		hostnameLabel = new JLabel("IP:");
		hostnameLabel.setFont(FontClass.kenThinFont(20));
		hostnameLabel.setHorizontalTextPosition(SwingConstants.CENTER);
		errorLabel = new JLabel("");
		errorLabel.setFont(FontClass.kenThinFont(15));
		errorLabel.setHorizontalTextPosition(SwingConstants.CENTER);
		errorLabel.setForeground(Color.RED);
		portTextField = new JTextField(15);
		portTextField.setFont(FontClass.kenThinFont(20));
		portTextField.setBackground(Color.GRAY);
		portTextField.setText("" + 6789);
		hostnameTextField = new JTextField(15);
		hostnameTextField.setFont(FontClass.kenThinFont(20));
		hostnameTextField.setBackground(Color.GRAY);
		hostnameTextField.setText("localhost");
		connectButton = new NiceButton("Start");
		connectButton.setFont(FontClass.kenThinFont(20));
		connectButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		connectButton.setPreferredSize(new Dimension(90,35));
	}
	
	public void createGUI(){
		this.setOpaque(false);
		setLayout(new GridLayout(4,1));
		JPanel IPFieldPanel = new JPanel();
		IPFieldPanel = new JPanel();
		IPFieldPanel.setOpaque(false);
		IPFieldPanel.add(hostnameLabel);
		IPFieldPanel.add(hostnameTextField);
		hostnameLabel.setAlignmentY(CENTER_ALIGNMENT);
		JPanel portFieldPanel = new JPanel();
		portFieldPanel.setOpaque(false);
		portFieldPanel.add(portLabel);
		portFieldPanel.add(portTextField);
		portLabel.setAlignmentY(CENTER_ALIGNMENT);
		JPanel connectPanel = new JPanel();
		connectPanel.setOpaque(false);
		connectPanel.add(connectButton);
		errorLabel.setAlignmentX(CENTER_ALIGNMENT);
		JPanel errorPanel = new JPanel();
		errorPanel.add(errorLabel);
		errorPanel.setOpaque(false);
		this.add(errorPanel);
		this.add(IPFieldPanel);
		this.add(portFieldPanel);
		this.add(connectPanel);
		
	}
	
	public void addEvents(MainGUI frame){
		connectButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				int portNum = Integer.parseInt(portTextField.getText());
				String hostname = hostnameTextField.getText();
				ct = new ClientSocket(hostname, portNum, frame);
				ct.start();
				connected = ct.isConnected();
				if(!connected){
					errorLabel.setText("Error: Cannot connect to Server");
				}
				else{
					System.out.println("Connected!");
					frame.chooseColor();
				}
			}
		});
	}
	public ClientSocket getClientThread(){
		return ct;
	}
	
	public boolean isConnected(){
		return connected;
	}
}
