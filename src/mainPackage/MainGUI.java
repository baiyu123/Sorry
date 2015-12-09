package mainPackage;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.TextArea;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.font.TextAttribute;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.text.AttributeSet;
import javax.swing.text.DefaultCaret;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;

import utilities.FontClass;
import utilities.MusicPlayer;
import mainPackage.Player.Pawn;
import score.Score;
import server.ServerThread;
import server.Timer;


public class MainGUI extends JFrame {
	public static final long serialVersionUID = 1;
	private JLabel sorryLabel, selectLabel,colorLabel,timerLabel,cardLabel,sorryLabelSmall;
	private JButton joinButton, hostButton, confirmButton, colorConfirm,cardButton,yellowStart,greenStart,redStart,blueStart
	,yellowHome,greenHome,redHome,blueHome,sendButton;
	private ColorButton redButton, blueButton, greenButton,yellowButton;
	private JPanel startPanel, firstPanel, secondPanel,selectPanel,colorPanel, doublePanel;
	private MapPanel boardPanel;
	private JFrame frame;
	private JRadioButton R2, R3, R4;
	private ButtonGroup radioGroup;
	private int numOfPlayers;
	private int myColor; //0=red, 1=blue, 2=green, 3=yellow
	private Game myGame;
	private Player mySelf;
	private GridBagLayout gridBagLayout;
	boolean waitForPawn,pawnHighlighted, validDes;//waiting player to choose a pawn to move
	boolean newRound;//waiting player to draw a card
	Map<Integer,String> IntToColor;
	boolean sorry;
	private JButton[] buttonArr;
	boolean swap;
	private boolean myPawnChosen;
	private int swapOriginX;
	private int swapOriginY;
	private int step1, step2;
	private boolean isSeven;
	private Font kenThin, ken, kenThin30, kenThin25;
	private Pawn focusPawn;
	private TileButton currHighlight;
	private JMenuBar jmb;
	private JMenu helpMenu, topScoresMenu;
	private JMenuItem helpMenuItem,topScores;
	private JScrollPane chatBox;
	private TopScoresDialog TSdialog;
	private ServerPort serverPort;
	private ClientPort clientPort;
	public boolean isServer;
	private ServerThread serverThread;
	private ClientSocket clientSocket;
	private JTextField sendBox;
	private ArrayList<String> msgVector;
	private JTextPane messageBoxText;
	private boolean confirmed; //the chose is confirmed
	private boolean myTurn; //turn for client
	private Score Score;
	private String timerStr = "             Time" + "0:30";
	public Timer timerThread;
	private Vector<Point> animationPath;
	private AnimationTileButton replaceTileRed = new AnimationTileButton(0,0);
	private AnimationTileButton replaceTileBlue = new AnimationTileButton(1,1);
	private AnimationTileButton replaceTileGreen = new AnimationTileButton(2,2);
	private AnimationTileButton replaceTileYellow = new AnimationTileButton(3,3);
	private long lastSend = 0;
	
	
	public MainGUI(){
		super("Sorry!");
		frame = this;
		initializeComponents();
		createGUI();
		createMenu();
		addEvents();
	}
	private void createMenu(){
		this.setJMenuBar(jmb);
		helpMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_H, ActionEvent.CTRL_MASK));
		topScores.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
		helpMenu.add(helpMenuItem);
		topScoresMenu.add(topScores);
		jmb.add(helpMenu);
		jmb.add(topScoresMenu);
	}
	
	private void initializeComponents(){
		//message
		messageBoxText = new JTextPane();
		msgVector = new ArrayList<String>();
		sendButton = new NiceButton("Send");
		sendButton.setFont(FontClass.kenThinFont(15));
		sendBox = new JTextField();
		chatBox = new JScrollPane(messageBoxText);
		isServer = false;
		//score
		TSdialog = new TopScoresDialog();
		TSdialog.setVisible(false);
		//menu
		jmb = new JMenuBar();
		helpMenu = new JMenu("Help");
		helpMenuItem = new JMenuItem("About");
		topScoresMenu = new JMenu("Top Scores");
		topScores = new JMenuItem("Top Scores");
		
		sorryLabel = new JLabel();
		//sorryLabel.setFont(new Font("San-Serif", Font.BOLD, 100));
		sorryLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		hostButton = new NiceButton("Host");
		joinButton = new NiceButton("Join");
		selectLabel = new JLabel("Select the number of players");
		selectLabel.setFont(new Font("San-Serif", Font.BOLD,30));
		selectLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		sorryLabelSmall = new SorryLabel();
		
		ImageIcon imageIcon = new ImageIcon("images/checkboxes/grey_box.png");
		Image image = imageIcon.getImage();
		Image newImg = image.getScaledInstance(30, 30, Image.SCALE_SMOOTH);
		imageIcon = new ImageIcon(newImg);
		R2 = new JRadioButton("2",imageIcon);
		R3 = new JRadioButton("3",imageIcon);
		R4 = new JRadioButton("4",imageIcon);
		
		confirmButton = new NiceButton("Confirm");
		
		//color select
		redButton = new ColorButton("Red");
		redButton.setColor(0);
		redButton.setFont(FontClass.kenThinFont(25));
		greenButton = new ColorButton("Green");
		greenButton.setColor(2);
		greenButton.setFont(FontClass.kenThinFont(25));
		yellowButton = new ColorButton("Yellow");
		yellowButton.setColor(3);
		yellowButton.setFont(FontClass.kenThinFont(25));
		blueButton = new ColorButton("Blue");
		blueButton.setColor(1);
		blueButton.setFont(FontClass.kenThinFont(25));
		colorLabel = new JLabel("Select your color");
		colorLabel.setFont(new Font("San-Serif", Font.BOLD,30));
		colorLabel.setHorizontalAlignment(SwingConstants.CENTER);
		timerLabel = new JLabel("0:00");
		timerLabel.setFont(FontClass.kenThinFont(30));
		timerLabel.setHorizontalAlignment(SwingConstants.LEFT);
		
		colorConfirm = new NiceButton("Confirm");
		colorConfirm.setEnabled(false);
		
		//game board buttons
		yellowHome = new TileButton(8,"Home",FontClass.kenThinFont(10));
		greenHome = new TileButton(7,"Home",FontClass.kenThinFont(10));
		redHome = new TileButton(5,"Home",FontClass.kenThinFont(10));
		blueHome = new TileButton(6,"Home",FontClass.kenThinFont(10));
		yellowHome.setPreferredSize(new Dimension(15,20));
		greenHome.setPreferredSize(new Dimension(15,20));
		redHome.setPreferredSize(new Dimension(15,20));
		blueHome.setPreferredSize(new Dimension(15,20));
		yellowHome.setBorder(BorderFactory.createLineBorder(Color.YELLOW,0));
		greenHome.setBorder(BorderFactory.createLineBorder(Color.GREEN,0));
		blueHome.setBorder(BorderFactory.createLineBorder(Color.BLUE,0));
		redHome.setBorder(BorderFactory.createLineBorder(Color.RED,0));
		yellowStart = new TileButton(8,"Start",FontClass.kenThinFont(10));
		greenStart = new TileButton(7,"Start",FontClass.kenThinFont(10));
		redStart = new TileButton(5,"Start",FontClass.kenThinFont(10));
		blueStart = new TileButton(6,"Start",FontClass.kenThinFont(10));
		yellowStart.setPreferredSize(new Dimension(30,15));
		greenStart.setPreferredSize(new Dimension(30,15));
		redStart.setPreferredSize(new Dimension(30,15));
		blueStart.setPreferredSize(new Dimension(30,15));
		redStart.setBorder(BorderFactory.createLineBorder(Color.RED,1));
		yellowStart.setBorder(BorderFactory.createLineBorder(Color.YELLOW,1));
		greenStart.setBorder(BorderFactory.createLineBorder(Color.GREEN,1));
		blueStart.setBorder(BorderFactory.createLineBorder(Color.BLUE,1));
		yellowStart.setHorizontalAlignment(SwingConstants.CENTER);
		greenStart.setHorizontalAlignment(SwingConstants.CENTER);
		blueStart.setHorizontalAlignment(SwingConstants.CENTER);
		redStart.setHorizontalAlignment(SwingConstants.CENTER);
		cardLabel = new JLabel("Cards:");
		cardLabel.setFont(FontClass.kenThinFont(9));
		cardButton = new TileButton(9);
		cardButton.setPreferredSize(new Dimension(20,10));
		newRound = true;
		IntToColor = new HashMap<Integer,String>();
		IntToColor.put(0, "Red");
		IntToColor.put(1, "Blue");
		IntToColor.put(2, "Green");
		IntToColor.put(3, "Yellow");
		buttonArr = new JButton[4];
		swap = false;
		myPawnChosen =false;
		isSeven = false;
		currHighlight = new TileButton();
	}
	
	private void createGUI(){
		setSize(650,622);
		setMinimumSize(new Dimension(650,622));
		setLocation(200,100);
		frame.setMaximumSize(new Dimension(960,640));
		startMenu();
		resetCursor();
		add(startPanel);
	}
	private void resetCursor(){
		setCursor(Toolkit.getDefaultToolkit().createCustomCursor(new ImageIcon("images/cursors/cursorHand_blue.png").getImage(),new Point(0,0),"custom cursor"));
	}
	
	public void rePaintStartMenu(){
		this.getContentPane().removeAll();
		this.getContentPane().add(startPanel);
		this.getContentPane().revalidate();
		this.repaint();
	}
	private void startMenu(){
		//set button
		try {
			kenThin = Font.createFont(Font.TRUETYPE_FONT, new FileInputStream("fonts/kenvector_future_thin.ttf"));
			kenThin = kenThin.deriveFont(Font.PLAIN,20);
			kenThin30 = kenThin.deriveFont(Font.PLAIN,30);
			ken = Font.createFont(Font.TRUETYPE_FONT, new FileInputStream("fonts/kenvector_future_thin.ttf"));
			ken = ken.deriveFont(Font.PLAIN,20);
		} catch (FontFormatException | IOException e) {
			e.printStackTrace();
		}
		hostButton.setFont(FontClass.kenThinFont(20));
		joinButton.setFont(FontClass.kenThinFont(20));
		hostButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		joinButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		hostButton.setPreferredSize(new Dimension(70,40));
		joinButton.setPreferredSize(new Dimension(70,40));
		
		startPanel = new GrayPanel();
		startPanel.setLayout(new GridLayout(2,1));
		firstPanel = new JPanel();
		firstPanel.setOpaque(false);
		firstPanel.setLayout(new BoxLayout(firstPanel,BoxLayout.Y_AXIS));
		firstPanel.add(Box.createGlue());
		sorryLabel.setIcon(new ImageIcon("images/sorry.png"));
		firstPanel.add(sorryLabel);
		secondPanel = new JPanel();
		secondPanel.setLayout(new BoxLayout(secondPanel,BoxLayout.Y_AXIS));
		secondPanel.setOpaque(false);
		secondPanel.add(Box.createGlue());
		doublePanel = new JPanel();
		doublePanel.setLayout(new FlowLayout(FlowLayout.LEADING, 150, 20));
		doublePanel.setOpaque(false);
		doublePanel.add(hostButton);
		doublePanel.add(joinButton);
		secondPanel.add(doublePanel);
		secondPanel.add(Box.createGlue());
		startPanel.add(firstPanel);
		startPanel.add(secondPanel);
	}
	
	private void chooseNumOfPlayers(){
		//label
		selectPanel = new GrayPanel(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		selectLabel.setFont(FontClass.kenThinFont(25));
		gbc.gridwidth = 3;
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weighty = 0.5;
		selectPanel.add(selectLabel,gbc);
		
		//radio button
		radioGroup = new ButtonGroup();
		R2.setFont(kenThin30);
		R3.setFont(kenThin30);
		R4.setFont(kenThin30);
		radioGroup.add(R2);
		radioGroup.add(R3);
		radioGroup.add(R4);
		
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.gridwidth = 1;
		gbc.weightx = 0.5;
		selectPanel.add(R2,gbc);
		gbc.gridx = 1;
		gbc.gridy = 1;
		gbc.weightx = 0.25;
		selectPanel.add(R3,gbc);
		gbc.gridx = 2;
		gbc.gridy = 1;
		gbc.weightx = 0.2;
		selectPanel.add(R4,gbc);
		//confirm
		gbc.gridx = 2;
		gbc.gridy = 2;
		confirmButton.setPreferredSize(new Dimension(150,45));
		confirmButton.setFont(kenThin);
		selectPanel.add(confirmButton,gbc);
		confirmButton.setEnabled(false);
		
		frame.getContentPane().removeAll();
		frame.getContentPane().add(selectPanel);
		frame.getContentPane().revalidate();
		frame.repaint();
	}
	public void setTimerStr(String str){
		if(!isServer){
			timerStr = str;
			timerStr = "             Time" + timerStr;
			timerLabel.setText(timerStr);
			timerLabel.revalidate();
			timerLabel.repaint();
		}
	}
	public void setTimerStrInGame(String str){
		timerLabel.setText(str);
		timerLabel.revalidate();
		timerLabel.repaint();
	}
	public void kickPlayer(){
		if(!isServer){
			clientSocket.dropGame();
		}
	}
	public void deleteTimer(){
		if(!isServer){
			timerLabel.setText("");
			timerLabel.revalidate();
			timerLabel.repaint();
		}
	}
	public void setTimerTo15(){
		timerLabel.setText("0:15");
		timerLabel.revalidate();
		timerLabel.repaint();
	}
	public void chooseColor(){
		if(!confirmed){
			String disable;
			if(!isServer){
				clientSocket = clientPort.getClientThread();
				timerThread = clientSocket.getTimer();
				disable = clientSocket.getDisabledColor();
			}
			else{
				disable = serverThread.getServerDisabledColor();
			}
			//enable all the buttons
			redButton.setEnabled(true);
			blueButton.setEnabled(true);
			greenButton.setEnabled(true);
			yellowButton.setEnabled(true);
			for(int i = 0; i < 4; i++){
				if(disable.charAt(i) == '1'){
					switch(i){
						case 0: redButton.setEnabled(false);
								break;
						case 1: blueButton.setEnabled(false);
								break;
						case 2: greenButton.setEnabled(false);
								break;
						case 3: yellowButton.setEnabled(false);
								break;
					}
				}
			}
		}
		colorPanel = new GrayPanel(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		//timer
		if(!isServer){
	
			timerLabel.setText(timerStr);
			
			gbc.gridx = 0;
			gbc.gridy = 0;
			gbc.gridwidth = 4;
			gbc.gridx = 1;
			colorPanel.add(timerLabel);
		}
		
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.gridwidth = 2;
		gbc.ipady = 0;
		gbc.insets = new Insets(0,0,100,0);
		colorLabel.setFont(FontClass.kenThinFont(30));
		colorPanel.add(colorLabel,gbc);
		gbc.insets = new Insets(0,10,10,10);
		gbc.gridwidth = 1;
		gbc.gridx = 0;
		gbc.gridy = 2;
		gbc.ipady = 10;
		gbc.ipadx = 139;
		buttonArr[0] = redButton;
		colorPanel.add(redButton,gbc);
		gbc.gridx = 1;
		gbc.gridy = 2;
		buttonArr[1] = blueButton;
		colorPanel.add(blueButton,gbc);
		gbc.ipadx = 100;
		gbc.gridx = 0;
		gbc.gridy = 3;
		buttonArr[2] = greenButton;
		colorPanel.add(greenButton,gbc);
		gbc.ipadx = 100;
		gbc.gridx = 1;
		gbc.gridy = 3;
		buttonArr[3] = yellowButton;
		colorPanel.add(yellowButton,gbc);
		gbc.ipadx = 10;
		gbc.ipady = 3;
		gbc.gridx = 1;
		gbc.gridy = 6;
		gbc.insets = new Insets(50,100,0,0);
		colorConfirm.setPreferredSize(new Dimension(150,45));
		colorConfirm.setFont(FontClass.kenThinFont(20));
		colorPanel.add(colorConfirm,gbc);
		for(int i = 0; i < 4; i++){
			buttonArr[i].setForeground(Color.BLACK);
		}
		
		
		frame.getContentPane().removeAll();
		frame.getContentPane().add(colorPanel);
		frame.getContentPane().revalidate();
		frame.repaint();
	}
	
	public void addMessage(String msg){
		msgVector.add(msg);
	}
	
	public void gameBoard(){
		//game manager =new game(numOfPlayers,myColor);
		gridBagLayout = new GridBagLayout();
		boardPanel = new MapPanel(gridBagLayout);
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridwidth = 1;
		gbc.ipadx = 30;
		gbc.ipady = 28;
		gbc.fill = GridBagConstraints.BOTH;
		//draw the outer layer
		for(int i = 1; i < 16; i++){
			gbc.gridx = i;
			gbc.gridy = 0;
			//yellow
			if((i>4 && i<9)||i==14||i==15){
				TileButton blackBox = new TileButton();
				blackBox.setPreferredSize(new Dimension(2,2));
				blackBox.setBorder(BorderFactory.createLineBorder(Color.WHITE,0));
				blackBox.setOpaque(false);
				//blackBox.setContentAreaFilled(false);
				blackBox.addMouseListener(new mouseAdapter());
				blackBox.addActionListener(new boxClickAdapter());
				boardPanel.add(blackBox,gbc);
			}
			else{
				TileButton yellowBox = new TileButton(3,true);
				yellowBox.setPreferredSize(new Dimension(5,5));
				yellowBox.setBorder(BorderFactory.createLineBorder(Color.WHITE,0));
				yellowBox.setOpaque(false);
				yellowBox.setContentAreaFilled(false);
				yellowBox.addMouseListener(new mouseAdapter());
				yellowBox.addActionListener(new boxClickAdapter());
				boardPanel.add(yellowBox,gbc);
				//if(i == 4) yellowBox.setPawn(1);
			}
			
			//green
			gbc.gridx = 15;
			gbc.gridy = i;
			gbc.ipady = 8;
			gbc.ipadx = 20;
			if((i>4 && i<9)||i==14||i==15){
				TileButton blackBox = new TileButton();
				blackBox.setPreferredSize(new Dimension(15,5));
				blackBox.setBorder(BorderFactory.createLineBorder(Color.WHITE,0));
				blackBox.addMouseListener(new mouseAdapter());
				blackBox.addActionListener(new boxClickAdapter());
				boardPanel.add(blackBox,gbc);
			}
			else{
				TileButton greenBox = new TileButton(2,true);
				greenBox.setPreferredSize(new Dimension(10,5));
				greenBox.setOpaque(false);
				greenBox.addMouseListener(new mouseAdapter());
				greenBox.addActionListener(new boxClickAdapter());
				boardPanel.add(greenBox,gbc);
			}
			gbc.ipadx = 30;
			//red
			gbc.gridx = 15-i;
			gbc.gridy = 15;
			gbc.ipady = 28;
			if((i>4 && i<9)||i==14||i==15){
				TileButton blackBox = new TileButton();
				blackBox.setPreferredSize(new Dimension(3,3));
				blackBox.setBorder(BorderFactory.createLineBorder(Color.WHITE,0));
				blackBox.setOpaque(false);
				blackBox.addMouseListener(new mouseAdapter());
				blackBox.addActionListener(new boxClickAdapter());
				boardPanel.add(blackBox,gbc);
			}
			else{
				TileButton redBox = new TileButton(0,true);
				redBox.setPreferredSize(new Dimension(5,2));
				redBox.setBorder(BorderFactory.createLineBorder(Color.WHITE,0));
				redBox.setOpaque(false);
				redBox.setContentAreaFilled(false);
				redBox.addMouseListener(new mouseAdapter());
				redBox.addActionListener(new boxClickAdapter());
				boardPanel.add(redBox,gbc);
			}
			
			//blue
			gbc.gridx = 0;
			gbc.gridy = 15-i;
			gbc.ipady = 8;
			if((i>4 && i<9)||i==14||i==15){
				JButton blackBox = new TileButton();
				blackBox.setBorder(BorderFactory.createLineBorder(Color.WHITE,0));
				blackBox.setPreferredSize(new Dimension(10,10));
				blackBox.setOpaque(false);
				blackBox.setContentAreaFilled(false);
				blackBox.addMouseListener(new mouseAdapter());
				blackBox.addActionListener(new boxClickAdapter());
				boardPanel.add(blackBox,gbc);
			}
			else{
				JButton blueBox = new TileButton(1,true);
				blueBox.setBorder(BorderFactory.createLineBorder(Color.BLUE,0));
				blueBox.setHorizontalAlignment(SwingConstants.CENTER);
				blueBox.setContentAreaFilled(false);
				blueBox.setOpaque(false);
				blueBox.addMouseListener(new mouseAdapter());
				blueBox.addActionListener(new boxClickAdapter());
				boardPanel.add(blueBox,gbc);
			}
		}
		//draw safe zone
		for(int i = 1; i < 6;i++){
			//yellow
			gbc.gridx = 2;
			gbc.gridy = i;
			JButton yellowBox = new TileButton(3);
			if(i == 3 || i == 5 )
			{
				yellowBox.setPreferredSize(new Dimension(10,20));
			}
			yellowBox.setBorder(BorderFactory.createLineBorder(Color.YELLOW,0));
			yellowBox.setOpaque(false);
			yellowBox.setContentAreaFilled(false);
			yellowBox.addMouseListener(new mouseAdapter());
			yellowBox.addActionListener(new boxClickAdapter());
			boardPanel.add(yellowBox,gbc);
			//green
			gbc.gridx = 15-i;
			gbc.gridy = 2;
			JButton greenBox = new TileButton(2);
			greenBox.setPreferredSize(new Dimension(10,20));
			greenBox.setBorder(BorderFactory.createLineBorder(Color.GREEN,0));
			greenBox.setOpaque(false);
			greenBox.setContentAreaFilled(false);
			greenBox.addMouseListener(new mouseAdapter());
			greenBox.addActionListener(new boxClickAdapter());
			boardPanel.add(greenBox,gbc);
			//red
			gbc.gridx = 13;
			gbc.gridy = 15-i;
			JButton redBox = new TileButton(0);
			if(i == 3 || i == 5 )
			{
				redBox.setPreferredSize(new Dimension(15,20));
			}
			redBox.setBorder(BorderFactory.createLineBorder(Color.RED,0));
			redBox.setOpaque(false);
			redBox.setContentAreaFilled(false);
			redBox.addMouseListener(new mouseAdapter());
			redBox.addActionListener(new boxClickAdapter());
			boardPanel.add(redBox,gbc);
			//blue
			gbc.gridx = i;
			gbc.gridy = 13;
			JButton blueBox = new TileButton(1);
			blueBox.setPreferredSize(new Dimension(10,20));
			blueBox.setBorder(BorderFactory.createLineBorder(Color.BLUE,0));
			blueBox.setOpaque(false);
			blueBox.setContentAreaFilled(false);
			blueBox.addMouseListener(new mouseAdapter());
			blueBox.addActionListener(new boxClickAdapter());
			boardPanel.add(blueBox,gbc);
			gbc.ipady = 8;
		}
		//home label
		gbc.ipadx = 0;
		gbc.ipady = 8;
		
		gbc.gridx = 2;
		gbc.gridy = 6;
		boardPanel.add(yellowHome,gbc); 
		
		gbc.gridx = 9;
		gbc.gridy = 2;
		gbc.ipadx = 20;
		boardPanel.add(greenHome,gbc); 
		
		gbc.gridx = 13;
		gbc.gridy = 9;
		gbc.ipadx = 0;
		boardPanel.add(redHome,gbc); 
		
		gbc.gridx = 6;
		gbc.gridy = 13;
		gbc.ipadx = 20;
		boardPanel.add(blueHome,gbc); 
		
		//start label
		gbc.ipadx = 0;
		gbc.ipady = 10;
		gbc.gridx = 4;
		gbc.gridy = 1;
		yellowStart.setPreferredSize(new Dimension(15,15));
		yellowStart.addActionListener(new boxClickAdapter());
		yellowStart.setContentAreaFilled(false);
		boardPanel.add(yellowStart,gbc);
		
		gbc.gridx = 14;
		gbc.gridy = 4;
		greenStart.setPreferredSize(new Dimension(15,15));
		greenStart.addActionListener(new boxClickAdapter());
		greenStart.setContentAreaFilled(false);
		boardPanel.add(greenStart,gbc);
		
		gbc.gridx = 11;
		gbc.gridy = 14;
		redStart.setPreferredSize(new Dimension(15,15));
		redStart.addActionListener(new boxClickAdapter());
		redStart.setContentAreaFilled(false);
		boardPanel.add(redStart,gbc);
		
		gbc.gridx = 1;
		gbc.gridy = 11;
		blueStart.setPreferredSize(new Dimension(15,15));
		blueStart.addActionListener(new boxClickAdapter());
		blueStart.setContentAreaFilled(false);
		boardPanel.add(blueStart,gbc);
		
		//Sorry label
		gbc.gridx = 5;
		gbc.gridy = 4;
		gbc.gridwidth = 6;
		gbc.gridheight = 3;
		boardPanel.add(sorryLabelSmall,gbc);
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		
		//draw pawn
		Map<String,Player> activePlayer = myGame.getActivePlayers();
		for(Player value : activePlayer.values()){
			for(int i = 0; i < 4; i++){
				Pawn tempPawn = value.getPawn(i);
				if(tempPawn.checkHome()) continue;
				if(!tempPawn.checkStart()&&!tempPawn.checkHome()){
					int x = tempPawn.getLoc()[0];
					int y = tempPawn.getLoc()[1];
					int color = tempPawn.getColor();
					TileButton match = null;
					GridBagConstraints gb = new GridBagConstraints();
					Component [] compArr = boardPanel.getComponents();
					for(int j = 0; j < compArr.length; j++){
						gb = gridBagLayout.getConstraints(compArr[j]);
						if(gb.gridx == x && gb.gridy == y){
							match = (TileButton) compArr[j];
							break;
						}
					}
					if(match != null){
						if(color == 0){
							match.setPawn(0);
						}
						else if(color == 1){
							match.setPawn(1);
						}
						else if(color == 2){
							match.setPawn(2);
						}
						else if(color == 3){
							match.setPawn(3);
						}
					}
					if(tempPawn.checkHuman()){
						match.addMouseListener(new mouseAdapter());
						match.setHumanPawn(true);
					}
				}
			}
		}
		
		//board data
		
		Map<String,Player> playersMap = myGame.getActivePlayers();
		mySelf = myGame.getHuman();
		if(playersMap.containsKey("Yellow")){
			Player tempPlayer = playersMap.get("Yellow");
			int num = tempPlayer.getNumOfStartPawn();
			gbc.gridx = 4;
			gbc.gridy = 2;
			JLabel startNum = new JLabel(""+num);
			startNum.setFont(FontClass.kenThinFont(15));
			startNum.setPreferredSize(new Dimension(15,15));
			startNum.setHorizontalAlignment(SwingConstants.CENTER);
			boardPanel.add(startNum,gbc);
			gbc.gridx = 2;
			gbc.gridy = 7;
			int homeNumInt = tempPlayer.getNumOfHomePawn();
			JLabel homeNum = new JLabel(""+homeNumInt);
			homeNum.setFont(FontClass.kenThinFont(15));
			homeNum.setPreferredSize(new Dimension(15,15));
			homeNum.setHorizontalAlignment(SwingConstants.CENTER);
			boardPanel.add(homeNum,gbc);
		}
		
		if(playersMap.containsKey("Green")){
			Player tempPlayer = playersMap.get("Green");
			int num = tempPlayer.getNumOfStartPawn();
			gbc.gridx = 13;
			gbc.gridy = 4;
			JLabel startNum = new JLabel(""+num);
			startNum.setFont(FontClass.kenThinFont(15));
			startNum.setPreferredSize(new Dimension(15,15));
			startNum.setHorizontalAlignment(SwingConstants.CENTER);
			boardPanel.add(startNum,gbc);
			gbc.gridx = 8;
			gbc.gridy = 2;
			int homeNumInt = tempPlayer.getNumOfHomePawn();
			JLabel homeNum = new JLabel(""+homeNumInt);
			homeNum.setFont(FontClass.kenThinFont(15));
			homeNum.setPreferredSize(new Dimension(15,15));
			homeNum.setHorizontalAlignment(SwingConstants.CENTER);
			boardPanel.add(homeNum,gbc);
		}
		
		if(playersMap.containsKey("Red")){
			Player tempPlayer = playersMap.get("Red");
			int num = tempPlayer.getNumOfStartPawn();
			gbc.gridx = 11;
			gbc.gridy = 13;
			JLabel startNum = new JLabel(""+num);
			startNum.setFont(FontClass.kenThinFont(15));
			startNum.setPreferredSize(new Dimension(15,15));
			startNum.setHorizontalAlignment(SwingConstants.CENTER);
			boardPanel.add(startNum,gbc);
			gbc.gridx = 13;
			gbc.gridy = 8;
			int homeNumInt = tempPlayer.getNumOfHomePawn();
			JLabel homeNum = new JLabel(""+homeNumInt);
			homeNum.setFont(FontClass.kenThinFont(15));
			homeNum.setPreferredSize(new Dimension(15,15));
			homeNum.setHorizontalAlignment(SwingConstants.CENTER);
			boardPanel.add(homeNum,gbc);
		}
		
		if(playersMap.containsKey("Blue")){
			Player tempPlayer = playersMap.get("Blue");
			int num = tempPlayer.getNumOfStartPawn();
			gbc.gridx = 2;
			gbc.gridy = 11;
			JLabel startNum = new JLabel(""+num);
			startNum.setFont(FontClass.kenThinFont(15));
			startNum.setPreferredSize(new Dimension(15,15));
			startNum.setHorizontalAlignment(SwingConstants.CENTER);
			boardPanel.add(startNum,gbc);
			gbc.gridx = 7;
			gbc.gridy = 13;
			int homeNumInt = tempPlayer.getNumOfHomePawn();
			JLabel homeNum = new JLabel(""+homeNumInt);
			homeNum.setFont(FontClass.kenThinFont(15));
			homeNum.setPreferredSize(new Dimension(15,15));
			homeNum.setHorizontalAlignment(SwingConstants.CENTER);
			boardPanel.add(homeNum,gbc);
		}
		//card button and label
		
		gbc.gridx = 8;
		gbc.gridy = 7;
		gbc.ipadx = 2;
		gbc.ipady = 10;
		boardPanel.add(cardButton,gbc);
		
		gbc.gridx = 7;
		gbc.gridy = 7;
		boardPanel.add(cardLabel,gbc);
		//adding chat box
		gbc.gridwidth = 16;
		gbc.gridx = 0;
		gbc.gridy = 16;
		gbc.gridheight = 1;
		chatBox.getViewport().setBackground(Color.BLACK);
		chatBox.setFont(FontClass.kenThinFont(15));
		chatBox.setOpaque(true);
		chatBox.setPreferredSize(new Dimension(400,80));
		String tempString = "";
		messageBoxText.setBackground(Color.BLACK);
		messageBoxText.setForeground(Color.WHITE);
		messageBoxText.setFont(FontClass.kenThinFont(15));
		messageBoxText.setText("");//clear it
		DefaultCaret caret = (DefaultCaret)messageBoxText.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		for(int i = 0; i < msgVector.size(); i++){
			tempString = msgVector.get(i);
			int tempColor = tempString.charAt(0)-48;
			String shortString = "";
			if(tempString.length()>1)
			shortString = tempString.substring(1);
			switch(tempColor){
				case 0:	appendToPane(messageBoxText,"Red:",Color.RED);
						break;
				case 1:	appendToPane(messageBoxText,"Blue:",Color.BLUE);
						break;
				case 2:	appendToPane(messageBoxText,"Green:",Color.GREEN);
						break;
				case 3:	appendToPane(messageBoxText,"Yellow:",Color.YELLOW);
						break;
				case 4: appendToPane(messageBoxText,"Red has left the game.",Color.RED);
						shortString = "";
						break;
				case 5:	appendToPane(messageBoxText,"Blue has left the game.",Color.BLUE);
						shortString = "";
						break;
				case 6:	appendToPane(messageBoxText,"Green has left the game.",Color.GREEN);
						shortString = "";
						break;
				case 7:	appendToPane(messageBoxText,"Yellow has left the game.",Color.YELLOW);
						shortString = "";
						break;
			}
			appendToPane(messageBoxText,shortString+"\n",Color.WHITE);
			System.out.println(tempColor);
		}
		boardPanel.add(chatBox,gbc);
		//add textField and Button
		gbc.gridwidth = 14;
		gbc.gridx = 0;
		gbc.gridy = 17;
		sendBox.setBackground(Color.BLACK);
		sendBox.setForeground(Color.WHITE);
		sendBox.setFont(FontClass.kenThinFont(15));
		boardPanel.add(sendBox,gbc);
		gbc.gridx = 14;
		gbc.gridwidth = 3;
		gbc.ipadx = 0;
		gbc.ipady = 0;
		boardPanel.add(sendButton,gbc);
		
		
		gbc.gridwidth = 4;
		gbc.gridx = 7;
		gbc.gridy = 11;
		timerStr = "0:15";
		timerLabel = new JLabel(timerStr);
		timerLabel.setFont(FontClass.kenThinFont(20));
		timerLabel.setOpaque(true);
		boardPanel.add(timerLabel,gbc);
		
		boardRepaint();
	}
	private void boardRepaint(){
		frame.getContentPane().removeAll();
		frame.getContentPane().add(boardPanel);
		frame.getContentPane().revalidate();
		frame.repaint();
	}
	public void skipRound(){
		newRound = false;
		waitForPawn = false;
		if(isServer){
			serverThread.enableNextPlayer();
		}
		else{
			clientSocket.enableNextPlayer();
		}
	}
	//////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////
	///////////////////EVENTS//////////////////////////////////
	///////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////
	private void addEvents(){
		//send message
		AbstractAction sendAction = new AbstractAction(){
			private static final long serialVersionUID = 1;

			@Override
			public void actionPerformed(ActionEvent e) {
				String msg = sendBox.getText();
				sendBox.setText("");
				if(isServer){
					serverThread.sendMessage(msg);
					msgVector.add(myColor+msg);
				}
				else{
					clientSocket.sendMessage(msg);
				}
				gameBoard();
			}
		};
		sendButton.addActionListener(sendAction);
		sendButton.getInputMap(javax.swing.JComponent.WHEN_IN_FOCUSED_WINDOW).
        put(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ENTER,0), "Enter_pressed");
		sendButton.getActionMap().put("Enter_pressed", sendAction);
		//home listener
		yellowHome.addMouseListener(new mouseAdapter());
		yellowHome.addActionListener(new boxClickAdapter());
		blueHome.addMouseListener(new mouseAdapter());
		blueHome.addActionListener(new boxClickAdapter());
		greenHome.addMouseListener(new mouseAdapter());
		greenHome.addActionListener(new boxClickAdapter());
		redHome.addMouseListener(new mouseAdapter());
		redHome.addActionListener(new boxClickAdapter());
		//menu
		helpMenuItem.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				String helpMenuStr = "sorry!\n starting the game:\n  "
						+ "press 'start'\n  select the number of players\n  "
						+ "select the number of players\n  select your color\n"
						+ "playing the game\n  draw a card\n  select a square that is valid\n"
						+ "  pawn will be highlighted\n  move the pawn with high lighted\n  path";
				int x = (int) (frame.getLocation().getX()+ frame.getWidth()/2 - 200);
				int y = (int) (frame.getLocation().getY()+ frame.getHeight()/2 - 200);
				HelpMenuDialog help = new HelpMenuDialog(helpMenuStr,FontClass.kenThinFont(15));
				help.setLocation(x, y);
				help.setVisible(true);
				help.setSize(360, 400);
				
			}
			
		});
		//host port
		hostButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				frame.getContentPane().removeAll();
				frame.getContentPane().add(serverPort);
				frame.getContentPane().revalidate();
				frame.repaint();
				isServer = true;
			}
		});
		//client port
		joinButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				frame.getContentPane().removeAll();
				frame.getContentPane().add(clientPort);
				frame.getContentPane().revalidate();
				frame.repaint();
			}
		});
		
		clientPort = new ClientPort(this);
		serverPort = new ServerPort(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent ae) {
				chooseNumOfPlayers();
			}
			
		});
		
		//top scores
		topScores.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				int x = (int) (frame.getLocation().getX()+ frame.getWidth()/2 - 200);
				int y = (int) (frame.getLocation().getY()+ frame.getHeight()/2 - 200);
				if(!isServer){
					if(clientSocket != null && clientSocket.isConnected()){
						clientSocket.requestScore();
					}
				}
				Score.display();
				//TSdialog.setLocation(x,y);
				//TSdialog.setVisible(true);
				//TSdialog.setSize(360,400);
			}
			
		});
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		R2.addActionListener(new playersAdapter());
		R3.addActionListener(new playersAdapter());
		R4.addActionListener(new playersAdapter());
		confirmButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				serverThread = new ServerThread((MainGUI) frame,serverPort.getPortNum(),numOfPlayers);
				serverThread.start();
				chooseColor();
			}
		});
		blueButton.addActionListener(new colorsAdapter());
		redButton.addActionListener(new colorsAdapter());
		yellowButton.addActionListener(new colorsAdapter());
		greenButton.addActionListener(new colorsAdapter());
		colorConfirm.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				if(isServer){
					Vector<Integer> clientsColor = serverThread.getClientColor();
					myGame = new Game(numOfPlayers,myColor,clientsColor);
					newRound = true;
					serverThread.setMainGame(myGame);
					//testing
					serverThread.updateAllClientGame(myGame);
					timerThread = new Timer((MainGUI) frame);
					timerThread.start();
					timerThread.setTimeInsec(15);
					timerThread.setConfirmed();
					timerThread.setInGame();
				}
				else{
					newRound = false;
					timerThread.setConfirmed(); 
				}
				//once confirm cannot change color
				redButton.setEnabled(false);
				blueButton.setEnabled(false);
				greenButton.setEnabled(false);
				yellowButton.setEnabled(false);
				switch(myColor){
					case 0: redButton.setEnabled(true);
							break;
					case 1: blueButton.setEnabled(true);
							break;
					case 2: greenButton.setEnabled(true);
							break;
					case 3: yellowButton.setEnabled(true);
							break;
				}
				if(isServer){
					if(serverThread.checkAllReady()){
						serverThread.notifyAllContinue(myGame);
						gameBoard();
						serverThread.setInGame();
					}
				}
				else{
					//clientSocket.setInGame();
					confirmed = true;
					chooseColor();
					clientSocket.notifyConfirm();
				}
				//gameBoard();
			}
		});
		//draw a card
		cardButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				if(newRound){
					myGame.humanDrawCard();
					int card = myGame.checkCard();
					String dialogStr = "";
					swap = false;
					String title = "";
					switch(card){
						case 1: dialogStr = "Move a pawn from\n Start or move a pawn\n one space forward.";
								title = "1";
								break;
						case 2: dialogStr = "Move a pawn from\n Start or move a \npawn two spaces \nforward.Drawing a \ntwo entitles the\n player to draw\n again at the end \nof their turn.";
								title = "2";
								break;
						case 3: dialogStr = "Move a pawn three\n spaces forward";
								title = "3";
								break;
						case 4: dialogStr = "Move a pawn four\n spaces backwards.";
								title = "4";
								break;
						case 5: dialogStr = "Move a pawn five\n spaces forward.";
								title = "5";
								break;
						case 7: dialogStr = "Move one pawn seven \nspaces forward or \nsplit the seven \nspaces between\n two pawns.";
								title = "7";
								break;
						case 8: dialogStr = "Move a pawn eight\n spaces forward.";
								title = "8";
								break;
						case 10: dialogStr = "Move a pawn ten \nspaces forward or\n one space backward";
								title = "10";
								break;
						case 11: dialogStr = "Move a pawn eleven\n spaces forward, or \nswitch spaces with\n an opposing pawn.";
								title = "11";
								break;
						case 12: dialogStr = "Move a pawn twelve\n spaces forward.";
								title = "12";
								break;
						case 13: dialogStr = "Move any one pawn \nfrom Start to a \nsquare occupied by\n any opponent, sending\n that pawn back \nto its own Start.";
								title = "Sorry";
								break;
					}
						int x = (int) (frame.getLocation().getX()+ frame.getWidth()/2 - 100);
						int y = (int) (frame.getLocation().getY()+ frame.getHeight()/2 - 150);
						cardPanel dialog = new cardPanel(title,dialogStr);
						dialog.setLocation(x,y);
						dialog.setSize(230, 300);
						dialog.setFont(FontClass.kenThinFont(15));
						dialog.setTitleFont(FontClass.kenThinFont(25));
						//JOptionPane.showMessageDialog(frame,dialogStr,"Sorry!", JOptionPane.INFORMATION_MESSAGE);
					if(card == 10&&myGame.existHumanPawnOnBoard()){
						Object[] options = {"Forward 10", "Backward 1"};
						int n = JOptionPane.showOptionDialog(null,
								"10-What would you like to do?",
								"???",
								JOptionPane.YES_NO_OPTION,
								JOptionPane.QUESTION_MESSAGE,
								null,
								options,
								options[1]);
						if(n==1){//Chose to swap
							myGame.updatePlayerCard(14);
						}
						waitForPawn = true;
					}
					if(card == 11&&myGame.existHumanPawnOnBoard()){
						Object[] options = {"Move Forward", "Swap"};
						int n = JOptionPane.showOptionDialog(null,
								"What would you like to do?",
								"???",
								JOptionPane.YES_NO_OPTION,
								JOptionPane.QUESTION_MESSAGE,
								null,
								options,
								options[1]);
						if(n==1){//Chose to swap
							myGame.updatePlayerCard(15);
						    swap = true;
						}
						waitForPawn = true;
					}
					if(card == 7&&myGame.existHumanPawnOnBoard()){
						Object[] options ={"0","1","2","3","4","5","6","7"};
						String step1Str = (String) JOptionPane.showInputDialog(frame,
								"How many steps do you want to move for the first pawn?",
								"Steps",
								JOptionPane.QUESTION_MESSAGE,
								null,
								options,
								options[0]);
						step1 = Integer.parseInt(step1Str);
						step2 = 7-step1;
						if(step2 == 4) step2 = 16;
						if(step1 == 4) step1 = 16;
						waitForPawn = true;
						isSeven = true;
						myGame.updatePlayerCard(step1);
						if(!myGame.existHumanPawnOnBoard()){
							isSeven = false;
							waitForPawn = false;
						}
					}
					if(!myGame.startingCondition()&& card != 13){
						waitForPawn = true;
					}
					else if(myGame.startingCondition()&&(card == 1||card == 2)){//sorry
						waitForPawn = true;
					}
					else if(card == 13){
						sorry = true;
						waitForPawn = true;
						if(!myGame.existEnemyPawnOnBoard()&&myGame.HumanPawnHomeEmpty()){
							sorry = false;
							waitForPawn = false;
						}
					}
					else{
						waitForPawn = false;
					}
				
					newRound = false;
					MusicPlayer player = new MusicPlayer("sounds/draw_card.mp3");
					player.start();
					/*
					//testing
					String winner = "Red";
					if(isServer){
						serverThread.notifyAllClientGameEnd(winner);
					}
					else{
						//clientSocket.notifyServerGameEnd(winner);
					}
					JOptionPane.showMessageDialog(frame,winner+" player won!","Sorry!", JOptionPane.INFORMATION_MESSAGE);
					getUserName();
					*/
					//notify client to play
					
					if(!waitForPawn){
						timerThread.setMyRound(false);
						newRound = false;
						setTimerTo15();
						if(isServer){
							serverThread.enableNextPlayer();
						}
						else{
							clientSocket.enableNextPlayer();
						}
						
					}
				}
			}
		});
		
	}
	
	private class mouseAdapter implements MouseListener{
		boolean isHL;
		private Vector<TileButton> deMatch = new Vector<TileButton>();
		public void mouseExited(MouseEvent e) {
			Object source = e.getSource();
			if(source instanceof TileButton){
				TileButton tbutton = (TileButton)source;
				if(!isHL){
					tbutton.sethighlight(false);
					
					
					//remove highlighted path
					Vector<Point> path = myGame.getHightlightedPath();
					for(int i = 0; i < path.size(); i++){
						TileButton match = null;
						int HLx = (int) path.get(i).getX();
						int HLy = (int) path.get(i).getY();
						GridBagConstraints gb = new GridBagConstraints();
						Component [] compArr = boardPanel.getComponents();
						for(int j = 0; j < compArr.length; j++){
							gb = gridBagLayout.getConstraints(compArr[j]);
							if(gb.gridx == HLx && gb.gridy == HLy){
								if(compArr[j] instanceof JButton){
									match = (TileButton) compArr[j];
									match.sethighlight(false);
								}
							}
						}
					}
				}
			}
			boardRepaint();
		}
		public void mousePressed(MouseEvent e){
			
		}
		public void mouseReleased(MouseEvent e){
			
		}
		public void mouseClicked(MouseEvent e){
			//if this is a right click
			if(SwingUtilities.isRightMouseButton(e)){
				pawnHighlighted = false;
				currHighlight.sethighlight(false);
				boardRepaint();
			}
			for(int i = 0; i < deMatch.size(); i++){
				deMatch.get(i).sethighlight(false);
			}
			
			Object source = e.getSource();
			TileButton tbutton = (TileButton)source;
			GridBagConstraints gbc = new GridBagConstraints();
			gbc = gridBagLayout.getConstraints(tbutton);
			int x = gbc.gridx;
			int y = gbc.gridy;
			
			//check if it is a human pawn
			if(myGame.validPawnLoc(x, y)){
				if(currHighlight != tbutton)currHighlight.sethighlight(false);
				tbutton.sethighlight(true);
				currHighlight = tbutton;
				pawnHighlighted = true;
				isHL = true;
				for(int i = 0 ; i < 4; i++){
					if(x == mySelf.getPawn(i).getLoc()[0]&&y == mySelf.getPawn(i).getLoc()[1]){
						focusPawn = mySelf.getPawn(i);
					}
				}
			}
			//move the pawn!
			if(pawnHighlighted && myGame.checkValidMove(x,y)&&!swap){
				pawnHighlighted = false;
				validDes = true;
			}
			boardRepaint();
		}
		public void mouseEntered(MouseEvent e) {
			Object source = e.getSource();
			if(!pawnHighlighted){
				isHL = false;
			}
			if(source instanceof TileButton){
				//hover a pawn
				TileButton tbutton = (TileButton)source;
				if(tbutton.isHuman()){
					tbutton.sethighlight(true);
				}
				//hover a destination
				if(pawnHighlighted){
					GridBagConstraints gbc = new GridBagConstraints();
					gbc = gridBagLayout.getConstraints(tbutton);
					int x = gbc.gridx;
					int y = gbc.gridy;
					
					boolean tempValid = myGame.checkValidMove(x,y);
					if(tempValid){
						tbutton.sethighlight(true);
						//highlight all the way back
						Vector<Point> path = myGame.getHightlightedPath();
						animationPath = path;
						for(int i = 0; i < path.size(); i++){
							TileButton match = null;
							int HLx = (int) path.get(i).getX();
							int HLy = (int) path.get(i).getY();
							GridBagConstraints gb = new GridBagConstraints();
							Component [] compArr = boardPanel.getComponents();
							for(int j = 0; j < compArr.length; j++){
								gb = gridBagLayout.getConstraints(compArr[j]);
								if(gb.gridx == HLx && gb.gridy == HLy){
									if(compArr[j] instanceof JButton){
										match = (TileButton) compArr[j];
										match.sethighlight(true);
										deMatch.add(match);
									}
								}
							}
						}
						
						
					}
					boardRepaint();
				}
			}
			
		}
		
	}
	public JPanel getBoardPanel(){
		return boardPanel;
	}
	
	private void animation(){
		AnimationThread at = new AnimationThread((MainGUI) frame, gridBagLayout);
		at.start();
		System.out.println("start");
		//try {
		//	at.join();
		//} catch (InterruptedException e) {
		//	// TODO Auto-generated catch block
		//	e.printStackTrace();
		//}
		//System.out.println("Done");
		/*
		System.out.println("called");
		if(animationPath != null){
			Vector<Point> path = myGame.getHightlightedPath();
			for(int i = path.size(); i >0; i--){
				myGame.animationMove();
				boardPanel.removeAll();
				gameBoard();
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}*/
		
		
		/*
		for(Player value : activePlayer.values()){
			for(int i = 0; i < 4; i++){
				Pawn tempPawn = value.getPawn(i);
				if(tempPawn.checkHome()) continue;
				if(!tempPawn.checkStart()&&!tempPawn.checkHome()){
					int x = tempPawn.getLoc()[0];
					int y = tempPawn.getLoc()[1];
					int color = tempPawn.getColor();
					TileButton match = null;
					GridBagConstraints gb = new GridBagConstraints();
					Component [] compArr = boardPanel.getComponents();
					for(int j = 0; j < compArr.length; j++){
						gb = gridBagLayout.getConstraints(compArr[j]);
						if(gb.gridx == x && gb.gridy == y){
							match = (TileButton) compArr[j];
							break;
						}
					}
					if(color == 0){
						match.setPawn(0);
					}
					else if(color == 1){
						match.setPawn(1);
					}
					else if(color == 2){
						match.setPawn(2);
					}
					else if(color == 3){
						match.setPawn(3);
					}
					if(tempPawn.checkHuman()){
						match.addMouseListener(new mouseAdapter());
						match.setHumanPawn(true);
					}
				}
			}
		}*/
	}
	
	//get the position of the button that was clicked
	class boxClickAdapter implements ActionListener{
		public void actionPerformed(ActionEvent ae){
			Object source = ae.getSource();
			if(source instanceof JButton){
				JButton btn = (JButton)source;
				GridBagConstraints gbc = new GridBagConstraints();
				gbc = gridBagLayout.getConstraints(btn);
				int x = gbc.gridx;
				int y = gbc.gridy;
				validDes = myGame.checkValidMove(x, y);
				if(waitForPawn&&validDes){
					pawnHighlighted = false;
					validDes = false;
					//right enemy pawn selected;
					if( isSeven){
						//first pawn choose and move first
						if(!myGame.existHumanPawnOnBoard()){
							waitForPawn = false;
						}
						boolean validMove = false;
						if(!myPawnChosen){
							myGame.updatePlayerCard(step1);
							validMove = myGame.movePawn();
							if(validMove) {
								animation();
								myPawnChosen = true;//ready for second move
								myGame.updatePlayerCard(step2);
								waitForPawn = true;
							}
						}
						else if(myPawnChosen){
							validMove = myGame.movePawn();
							if(validMove){
								animation();
								myPawnChosen = false;//complete
								waitForPawn = false;
								isSeven = false;
							}
						}
						//gameBoard();
						int temp = myGame.checkEnd();//check if the game is end or not
						if(temp != -1){
							String winner = IntToColor.get(temp);
							JOptionPane.showMessageDialog(frame,winner+" player won!","Sorry!", JOptionPane.INFORMATION_MESSAGE);
							getUserName();
						}
						
						
					}
					else{
						boolean validMove = myGame.movePawn();
						animation();
						//gameBoard();
						int temp = myGame.checkEnd();//check if the game is end or not
						if(temp != -1){
							String winner = IntToColor.get(temp);
							JOptionPane.showMessageDialog(frame,winner+" player won!","Sorry!", JOptionPane.INFORMATION_MESSAGE);
							getUserName();
						}
						if(validMove) waitForPawn = false;
						if(myGame.checkCard() == 2) newRound = true;
					}
				}
				//make sure pawn can move out of start by one click
				else if(((x == 4 && y == 1)||(x == 14 && y == 4)||(x == 1 && y == 11)||(x == 11 && y == 14))&&waitForPawn){
					boolean validloc = myGame.validPawnLoc(gbc.gridx, gbc.gridy);
					boolean validMove = myGame.movePawn();
					gameBoard();
					int temp = myGame.checkEnd();//check if the game is end or not
					if(temp != -1){
						String winner = IntToColor.get(temp);
						JOptionPane.showMessageDialog(frame,winner+" player won!","Sorry!", JOptionPane.INFORMATION_MESSAGE);
						getUserName();
					}
					if(validMove) waitForPawn = false;
					if(myGame.checkCard() == 2) newRound = true;
				}
				//sorry can be used by one click
				else if(sorry && waitForPawn){
					if( myGame.validEnemyPawnLoc(gbc.gridx, gbc.gridy)){
						myGame.swapAndBump(gbc.gridx,gbc.gridy);
					}
					waitForPawn = false;
					sorry = false;
				}
				if(swap){
					if(myGame.validPawnLoc(gbc.gridx,gbc.gridy)){
						myPawnChosen = true;
						waitForPawn = true;
						swapOriginX = gbc.gridx;
						swapOriginY = gbc.gridy;
					}
					if(myPawnChosen == true &&myGame.validEnemyPawnLoc(gbc.gridx, gbc.gridy)){
						myGame.swap(gbc.gridx,gbc.gridy,swapOriginX,swapOriginY);
						waitForPawn = false;
						myPawnChosen = false;
						swap = false;
					}
				}
				
				
				//old !waitForPawn && !newRound
				if(!waitForPawn&&!newRound){ 
					timerThread.setMyRound(false);
					setTimerTo15();
					//clients turn
					//we have thread to deal with this
					System.out.println("currCard"+myGame.getCurrCard());
					System.out.println("start"+myGame.getCurrPawn().checkStart());
					if(myGame.getCurrCard()==1||myGame.getCurrCard()==13||myGame.getCurrCard()==15){
						long interval = (int) (System.currentTimeMillis()-lastSend);
						if(interval > 1000){
							if(isServer){
								
								serverThread.enableNextPlayer();
								serverThread.updateAllClientGame(myGame);
								gameBoard();
							}
							//server's turn
							else{
								clientSocket.enableNextPlayer();
							}
							lastSend = System.currentTimeMillis();
						}
					}
					int temp = myGame.checkEnd();//check if the game is end or not
					if(temp != -1){
						String winner = IntToColor.get(temp);
						if(isServer){
							serverThread.notifyAllClientGameEnd(winner);
						}
						else{
							clientSocket.notifyServerGameEnd(winner);
						}
						if(temp == myColor){
							MusicPlayer winGame = new MusicPlayer("sounds/victory.mp3");
							winGame.start();
						}
						JOptionPane.showMessageDialog(frame,winner+" player won!","Sorry!", JOptionPane.INFORMATION_MESSAGE);
						getUserName();
					}
					
				}
			}
		}
	}
	
	class colorsAdapter implements ActionListener{
		public void actionPerformed(ActionEvent ae){
			for(int i = 0; i < 4; i++){
				buttonArr[i].setForeground(Color.BLACK);
			}
			colorConfirm.setEnabled(true);
			Object source = ae.getSource();
			if(source instanceof ColorButton){
				resetColorButton();
				ColorButton btn = (ColorButton)source;
				String str = "";
				str = btn.getText();
				if(str == "Red" ){
					myColor = 0;
					btn.onClick();
					if(isServer){
						serverThread.setColorOfServer(0);
					}
					else{
						clientSocket.setColorOfClient(0);
					}
					
				}
				else if(str == "Blue"){
					myColor = 1;
					btn.onClick();
					if(isServer){
						serverThread.setColorOfServer(1);
					}
					else{
						clientSocket.setColorOfClient(1);
					}
				}
				else if(str == "Green"){
					myColor = 2;
					btn.onClick();
					if(isServer){
						serverThread.setColorOfServer(2);
					}
					else{
						clientSocket.setColorOfClient(2);
					}
				}
				else{
					myColor = 3;
					btn.onClick();
					if(isServer){
						serverThread.setColorOfServer(3);
					}
					else{
						clientSocket.setColorOfClient(3);
					}
				}
				chooseColor();
			}
		}
	}
	
	private void resetColorButton(){
		redButton.notOnClick();
		greenButton.notOnClick();
		blueButton.notOnClick();
		yellowButton.notOnClick();
	}
	
	class playersAdapter implements ActionListener{
		public void actionPerformed(ActionEvent ae){
			ImageIcon imageIcon = new ImageIcon("images/checkboxes/grey_boxCross.png");
			Image image = imageIcon.getImage();
			Image newImg = image.getScaledInstance(30, 30, Image.SCALE_SMOOTH);
			imageIcon = new ImageIcon(newImg);
			if(R2.isSelected()){
				numOfPlayers = 2;
				resetRadioButton();
				R2 = new JRadioButton("2",imageIcon,true);
			}
			else if(R3.isSelected()){
				numOfPlayers = 3;
				resetRadioButton();
				R3 = new JRadioButton("3",imageIcon,true);
			}
			else if(R4.isSelected()){
				numOfPlayers = 4;
				resetRadioButton();
				R4 = new JRadioButton("4",imageIcon,true);
			}
			chooseNumOfPlayers();
			confirmButton.setEnabled(true);
		}
		private void resetRadioButton(){
			ImageIcon imageIcon = new ImageIcon("images/checkboxes/grey_box.png");
			Image image = imageIcon.getImage();
			Image newImg = image.getScaledInstance(30, 30, Image.SCALE_SMOOTH);
			imageIcon = new ImageIcon(newImg);
			R2 = new JRadioButton("2",imageIcon);
			R3 = new JRadioButton("3",imageIcon);
			R4 = new JRadioButton("4",imageIcon);
			R2.addActionListener(new playersAdapter());
			R3.addActionListener(new playersAdapter());
			R4.addActionListener(new playersAdapter());
		}
	}
	private void getUserName(){
		int score = myGame.calculatePlayerScore();
		//int score = 20;
		String s = (String)JOptionPane.showInputDialog(
		                    frame,
		                    " You earn:"
		                    + score+" points! \nPlease Enter Your Name:",
		                    "Your Score",
		                    JOptionPane.PLAIN_MESSAGE,
		                    null,
		                    null,
		                    null);
		//TSdialog.add(s,score);
		if(isServer){
			Score.add(s,score);
			serverThread.updateAllClientScore(s,score);
		}
		else{
			clientSocket.updateServerScore(s,score);
		}
	}
	private void appendToPane(JTextPane tp, String msg, Color c)
    {
        StyleContext sc = StyleContext.getDefaultStyleContext();
        AttributeSet aset = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, c);
        aset = sc.addAttribute(aset, TextAttribute.FONT, FontClass.kenThinFont(15));
        aset = sc.addAttribute(aset, StyleConstants.Alignment, StyleConstants.ALIGN_JUSTIFIED);

        int len = tp.getDocument().getLength();
        tp.setCaretPosition(len);
        tp.setCharacterAttributes(aset, false);
        tp.replaceSelection(msg);
    }
	public void setMyGame(Game newGame){
		myGame = newGame;
	}
	public void setMyTurn(){
		newRound = true;
		timerThread.setTimeInsec(15);
		MusicPlayer player = new MusicPlayer("sounds/your_turn.mp3");
		player.start();
	}
	public Game getGame(){
		return myGame;
	}
	public void setGameEnd(String str){
		MusicPlayer gameEnd = new MusicPlayer("sounds/lose.mp3");
		gameEnd.start();
		JOptionPane.showMessageDialog(frame,str+" player won!","Sorry!", JOptionPane.INFORMATION_MESSAGE);
		getUserName();
	}
	public static void main(String [] args){
		MainGUI cgui = new MainGUI();
		cgui.setVisible(true);
	}
	@SuppressWarnings("static-access")
	public void updateScore(String name, int score){
		Score.add(name, score);
	}
	public ServerThread getServer(){
		return serverThread;
	}
	public ClientSocket getClient(){
		return clientSocket;
	}
}
