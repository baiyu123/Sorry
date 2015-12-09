package mainPackage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

import javax.swing.JFrame;

import server.Communication;
import server.Timer;
import utilities.Html;

public class ClientSocket extends Thread{
	private String hostName;
	private int portNum;
	private boolean connected,inGame;
	private ObjectInputStream ois;
	private ObjectOutputStream oos;
	private String disabled, existed;
	private MainGUI mainFrame;
	private int color;
	private Socket s;
	private Game myGame;
	private boolean serverInGame;
	private int serverColor;
	private Game oldGame;
	private boolean enableSent;
	private String colorString;
	private Timer timer;
	private boolean confirmed = false;
	private boolean updating;
	
	public ClientSocket(String hostname, int port, MainGUI frame){
		updating = false;
		colorString = "";
		enableSent = false;
		oldGame = null;
		serverColor = -1;
		serverInGame = false;
		mainFrame = frame;
		disabled = "0000";
		existed = "0000";
		connected = false;
		hostName = hostname;
		portNum = port;
		color = -1;
		try {
			s = new Socket(hostName, portNum);
			ois = new ObjectInputStream(s.getInputStream());
			oos = new ObjectOutputStream(s.getOutputStream());			
			connected = true;
			if(connected){
				timer = new Timer((MainGUI) frame);
				timer.start();
			}
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public boolean isConnected(){
		return connected;
	}
	public Timer getTimer(){
		return timer;
	}
	
	public void run(){
			try {
				while(true){
					Communication commIn = (Communication) ois.readObject();
					if(!serverInGame)
					serverInGame = commIn.checkInGame();
					if(!serverInGame){
						existed = commIn.getDisableColors();
						if(!inGame){
							mainFrame.chooseColor();
						}
					
					}
					else{
						if(commIn.getStartGame()&&confirmed){
							mainFrame.setMyGame(commIn.getGame());
							mainFrame.timerThread.setInGame();
							mainFrame.timerThread.setTimeInsec(15);
							mainFrame.gameBoard();
							continue;
						}
						if(commIn.getMessage().length() != 0){
							if(commIn.getLeftGame()){
								mainFrame.addMessage(commIn.getMessage());
							}
							else{
								mainFrame.addMessage(commIn.getColor()+commIn.getMessage());
							}
							mainFrame.gameBoard();
						}
						//game object fetch
						else{
							if(commIn.getPlayerTurn()==true){
								myGame = commIn.getGame();
								myGame.setHuman(color);
								mainFrame.setMyGame(myGame);
								mainFrame.setMyTurn();
								mainFrame.timerThread.setMyRound(true);
								mainFrame.timerThread.setTimeInsec(15);
								enableSent = false;
								if(oldGame != commIn.getGame()){
									mainFrame.gameBoard();
								}
							}
							//just update the game
							else{
								//game end
								if(commIn.getGameEnd()){
									myGame.setHuman(color);
									String tempColor = commIn.getGameEndString();
									if(tempColor != colorString){
										mainFrame.setGameEnd(tempColor);
									}
								}
								//update score
								if(commIn.getUpdateScore()){
									 FileWriter fw = null;
									  PrintWriter pw = null;
									  try{
										  fw = new FileWriter("src/score/scores");
										  pw = new PrintWriter(fw);
										  pw.print(commIn.getFile());
										  pw.flush();
									  }catch (IOException ioe){
										  System.out.println("IOException: " + ioe.getMessage());
									  }finally{
										  try{
											  if(pw != null){
												  pw.close();
											  }
											  if(fw != null){
												  fw.close();
											  }
										  }catch (IOException ioe) {
											  System.out.println("IOException closing file: "+ ioe.getMessage());
										  }
									  }
								}
								if(commIn.getGame() != null&&confirmed&&!updating){
									if(oldGame != commIn.getGame()){
										myGame = commIn.getGame();
										oldGame = myGame;
										myGame.setHuman(color);
										mainFrame.setMyGame(myGame);
										mainFrame.gameBoard();
									}
								}
							}
						}
					}
				}
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally{
				mainFrame.rePaintStartMenu();
				try{
					if( s != null){
						s.close();
					}
					if(ois != null){
						ois.close();
					}
					if(oos != null){
						oos.close();
					}
				} catch (IOException ioe){
					System.out.println("ioe closing streams: " + ioe.getMessage());
				}
			}
	}
	public void dropGame(){
		try {
			s.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public String getDisabledColor(){
		if(color != -1){
			disabled = existed.substring(0,color)+"0"+existed.substring(color+1);
			return disabled;
		}
		else{
			disabled = existed;
			return disabled;
		}
	}
	
	private void setExist(int e){
		String newString = existed.substring(0,e)+'1'+existed.substring(e+1);
		existed = newString;
	}
	private void setNotExist(int e){
		String newString = existed.substring(0,e)+'0'+existed.substring(e+1);
		existed = newString;
	}
	
	public void setColorOfClient(int colr){
		if(color == -1){
			color = colr;
		}
		else{
			setNotExist(color);//clear previous color
			setExist(colr);
			color = colr;
		}
		//auto send the color
		Communication commOut = new Communication(color,null);
		sendObject(commOut);
		
	}
	private void sendObject(Communication commOut){
		try {
			oos.reset();
			oos.writeObject(commOut);
			oos.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void sendMessage(String msg) {
		Communication commOut = new Communication(color,null);
		commOut.setMessage(msg);
		sendObject(commOut);
	}
	public void setInGame(){
		inGame = true;
	}
	public void notifyConfirm(){
		Communication commOut = new Communication(color, null);
		commOut.setConfirm();
		sendObject(commOut);
		confirmed = true;
	}
	public void enableNextPlayer(){
		if(enableSent == false){
			enableSent = true;
			Communication commOut = new Communication(color,myGame);
			commOut.setMessage(null);
			commOut.setPlayerTurn();
			sendObject(commOut);
		}
	}
	public void updateGame(Game game){
		Communication commOut = new Communication(color,game);
		commOut.setMessage(null);
		sendObject(commOut);
		System.out.println("client p");
		int x = game.getCurrPawn().getLoc()[0];
		int y = game.getCurrPawn().getLoc()[1];
		System.out.println("x:"+x);
		System.out.println("y:"+y);
	}
	public void notifyServerGameEnd(String colorStr){
		colorString = colorStr;
		Communication commOut = new Communication(color,null);
		commOut.setMessage(null);
		commOut.setGameEnd();
		commOut.setGameEndString(colorStr);
		sendObject(commOut);
	}
	public void updateServerScore(String name, int score){
		Communication commOut = new Communication(color, null);
		commOut.setMessage(null);
		commOut.setInGame(true);
		commOut.updateScore(name,score);
		sendObject(commOut);
	}
	public void requestScore(){
		Communication commOut = new Communication(color, null);
		commOut.setMessage(null);
		commOut.setInGame(true);
		commOut.requestScore = true;
		sendObject(commOut);
	}
	public void setUpdating(boolean b) {
		updating = b;
	}
}
