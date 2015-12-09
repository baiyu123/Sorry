package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.JFrame;

import mainPackage.Game;
import mainPackage.MainGUI;
import webserver.WebServer;

public class ServerThread extends Thread{
	private Vector<ServerSocketThread> players;
	private int portNum, numOfPlayers;
	private int color;
	private Vector<ServerSocketThread> serverSocketThreads;
	private ArrayList<Integer> colorsExist;
	private String encodeExist;
	private MainGUI mainFrame;
	private boolean inGame; // when GUI is in gameBoard
	private Game mainGame;
	private int playerConfirm;
	private int currPlayer;
	private Vector<Integer> bots;
	private WebServer wServer;

	//private Communication commIn, commOut;

	
	public ServerThread(MainGUI frame,int pn, int playersNum){
		wServer = new WebServer();
		playerConfirm = 0;
		inGame = false;
		mainFrame = frame;
		encodeExist = "0000";
		portNum = pn;
		numOfPlayers = playersNum;
		color = -1;
		serverSocketThreads = new Vector<ServerSocketThread>();
		colorsExist = new ArrayList<Integer>();
	}
	
	public void run(){
		ServerSocket ss = null;
		bots = new Vector<Integer>();
		try{
			ss = new ServerSocket(portNum);
			while(true){
				Socket s = ss.accept();
				if(serverSocketThreads.size() < numOfPlayers-1){
					ServerSocketThread sst = new ServerSocketThread(s,this);
					serverSocketThreads.add(sst);
					sst.start();
					greeting(sst);
				}
			}
		}catch(IOException ioe){
			System.out.println("ioe in server constructor: " + ioe.getMessage());
		}finally{
			try{
				if(ss != null){
					ss.close();
				}
			}catch(IOException ioe){
				System.out.println("ioe closing ss: " + ioe.getMessage());
			}
		}
	}
	//only server send message to every one, msg is not encoded
	public void sendMessage(String msg){
		for(int i = 0; i < serverSocketThreads.size(); i++){
			serverSocketThreads.get(i).sendMessage(msg,color);
		}
	}
	//get a message from a client and then send it to every one, msg is encoded
	public void clientSendMessage(String msg){
		//we don want left game msg here
		if(msg.length()>1){
			int tempColor = msg.charAt(0)-48;
			String decodedStr = msg.substring(1);
			for(int i = 0; i < serverSocketThreads.size(); i++){
				serverSocketThreads.get(i).sendMessage(decodedStr,tempColor);
			}
		}
		else{
			int tempColor = Integer.parseInt(msg);
			for(int i = 0; i < serverSocketThreads.size(); i++){
				serverSocketThreads.get(i).sendMessage(msg,tempColor);
			}
		}
	}
	public void setInGame(){
		inGame = true;
	}
	public boolean checkInGame(){
		return inGame;
	}
	private void greeting(ServerSocketThread sst){
		Communication commOut = new Communication(-1,null);
		commOut.setDisableColors(encodeExist);
		commOut.setMessage("Welcome to Sorry server");
		sst.sendObject(commOut);
	}
	private void setExist(int e){
		if(e < 0 || e > 3) return;
		String newString = encodeExist.substring(0,e)+'1'+encodeExist.substring(e+1);
		encodeExist = newString;
	}
	private void setNotExist(int e){
		String newString = encodeExist.substring(0,e)+'0'+encodeExist.substring(e+1);
		encodeExist = newString;
	}
	private void clearExist(){
		encodeExist = "0000";
	}
	public void updateColor(){
		clearExist();
		for(int i = 0; i < serverSocketThreads.size();i++){
			setExist(serverSocketThreads.get(i).getColor());
		}
		setExist(color);
		//update every client
		for(int i = 0; i < serverSocketThreads.size();i++){
			Communication commOut = new Communication(-1,null);
			commOut.setDisableColors(encodeExist);
			serverSocketThreads.get(i).sendObject(commOut);
		}
		if(!inGame)
		mainFrame.chooseColor();
	}
	public String getServerDisabledColor(){
		if(color != -1){
			String newString = encodeExist.substring(0,color)+"0"+encodeExist.substring(color+1);
			return newString;
		}
		else{
			return "0000";
		}
	}
	public void setColorOfServer(int colr){
		if(color == -1){
			color = colr;
			colorsExist.add(color);
			setExist(color);
		}
		else{
			for(int i = 0; i < colorsExist.size();i++){
				if(colorsExist.get(i) == color){
					colorsExist.remove(i);
					setNotExist(color);
				}
			}
			colorsExist.add(colr);
			color = colr;
			currPlayer = color;
			setExist(colr);
		}
		for(int i =0; i < serverSocketThreads.size(); i++){
			Communication commOut = new Communication(-1,null);
			commOut.setDisableColors(encodeExist);
			commOut.setMessage("s:"+color);
			
			serverSocketThreads.get(i).sendObject(commOut);
		}
		
	}
	public void updateMessage(String msg){
		mainFrame.addMessage(msg);
		mainFrame.gameBoard();
		clientSendMessage(msg);
	}
	public void someoneLeaves(String str){
		updateMessage(str);
	}
	public int getColor(){
		return color;
	}
	public void deleteSocketThread(ServerSocketThread thisThread){
		for(int i = 0; i < serverSocketThreads.size(); i++ ){
			if(serverSocketThreads.get(i) == thisThread){
				serverSocketThreads.remove(i);
			}
		}
		if(!inGame){
			updateColor();
		}
	}
	public void setMainGame(Game theGame){
		mainGame = theGame;
	}
	public void setConfirm(){
		playerConfirm++;
	}
	public boolean checkAllReady(){
		return playerConfirm == numOfPlayers-1;
	}
	public void notifyAllContinue(Game theGame){
		for(int i = 0; i < serverSocketThreads.size(); i++ ){
			serverSocketThreads.get(i).notifyStartGame(theGame);
		}
	}
	public void enableNextPlayer(){
		mainGame = mainFrame.getGame();
		boolean foundEnable = false;
		for(int j = 0; j < 4; j++){
			currPlayer++;
			if(currPlayer == 4) currPlayer = 0;
			//check if it is server
			if(currPlayer == color){
				mainFrame.setMyTurn();
				mainFrame.timerThread.setMyRound(true);
				mainFrame.timerThread.setTimeInsec(15);
				break;
			}
			//check bot
			for(int i = 0; i < bots.size(); i++){
				if(currPlayer == bots.get(i)){
					mainGame.botDrawCard();
					mainFrame.gameBoard();
					break;
				}
			}
			//check human
			for(int i = 0; i < serverSocketThreads.size(); i++){
				if(serverSocketThreads.get(i).getColor() == currPlayer){
					//mainGame.setHuman(currPlayer);
					serverSocketThreads.get(i).sendYourTurn(mainGame);
					foundEnable = true;
					break;
				}
			}
			if(foundEnable) break;
		}
	}
	public void updateGame(Game game){
		System.out.println("server p");
		int x = game.getCurrPawn().getLoc()[0];
		int y = game.getCurrPawn().getLoc()[1];
		System.out.println("x:"+x);
		System.out.println("y:"+y);
		mainGame = game;
		mainFrame.setMyGame(game);
		mainFrame.gameBoard();
		//enableNextPlayer();
		updateAllClientGame(game);
	}
	public void setGame(Game game){
		mainGame = game;
	}
	public void updateAllClientGame(Game game){
		for(int i = 0; i < serverSocketThreads.size(); i++){
			serverSocketThreads.get(i).updateGame(game);
		}
	}
	public Vector<Integer> getClientColor(){
		Vector<Integer> clientColor = new Vector<Integer>();
		for(int i = 0; i < serverSocketThreads.size(); i++){
			int temp = serverSocketThreads.get(i).getColor();
			clientColor.add(temp);
		}
		return clientColor;
	}
	public void setBot(int botColor){
		bots.add(botColor);
		mainGame.setBot(botColor);
		if(botColor == currPlayer){
			mainGame.botDrawCard();
			enableNextPlayer();
		}
	}
	public void notifyAllClientGameEnd(String color){
		for(int i = 0; i < serverSocketThreads.size(); i++){
			serverSocketThreads.get(i).sendGameEnd(color);
		}
	}
	public void clientGameEnd(String colorStr){
		notifyAllClientGameEnd(colorStr);
		mainFrame.setGameEnd(colorStr);
	}
	public void updateAllClientScore(String name, int score){
		for(int i = 0; i < serverSocketThreads.size(); i++){
			serverSocketThreads.get(i).updateScore(name,score);
		}
	}
	public void updateServerScore(String name, int score){
		mainFrame.updateScore(name, score);
		updateAllClientScore(name,score);
	}
	
}
