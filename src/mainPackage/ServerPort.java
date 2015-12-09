package mainPackage;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import utilities.FontClass;

public class ServerPort extends GrayPanel{
	private static final long serialVersionUID = 1;
	private JLabel portLabel,errorMessage;
	private JTextField portTextField;
	private JButton startButton;
	private int portNum;
	
	public ServerPort(ActionListener al){
		initializeComponents();
		createGUI();
		addEvents(al);
	}
	
	private void initializeComponents(){
		portLabel = new JLabel("Port:");
		portLabel.setFont(FontClass.kenThinFont(20));
		portLabel.setHorizontalTextPosition(SwingConstants.CENTER);
		portTextField = new JTextField(15);
		portTextField.setFont(FontClass.kenThinFont(20));
		portTextField.setBackground(Color.GRAY);
		portTextField.setText("" + 6789);
		startButton = new NiceButton("Start");
		startButton.setFont(FontClass.kenThinFont(20));
		startButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		startButton.setPreferredSize(new Dimension(90,35));
	}
	
	private void createGUI(){
		this.setOpaque(false);
		setLayout(new GridLayout(3,1));
		JPanel portFieldPanel = new JPanel();
		portFieldPanel.setOpaque(false);
		portFieldPanel.add(portLabel);
		portFieldPanel.add(portTextField);
		portLabel.setAlignmentY(CENTER_ALIGNMENT);
		JPanel startPanel = new JPanel();
		startPanel.setOpaque(false);
		startPanel.add(startButton);
		this.add(Box.createGlue());
		this.add(portFieldPanel);
		this.add(startPanel);
	}
	public int getPortNum(){
		return portNum;
	}
	
	private void addEvents(ActionListener al){
		startButton.addActionListener(al);
		startButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				portNum = Integer.parseInt(portTextField.getText());
				
			}
			
		});
	}
}
