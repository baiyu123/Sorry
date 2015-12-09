package server;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

import mainPackage.Game;

public class ServerSocketThread extends Thread{
	private ObjectInputStream ois;
	private ObjectOutputStream oos;
	private Communication commIn;
	private ServerThread serverThread;
	private int color;
	private Socket s;
	private boolean confirmed;
	
	ServerSocketThread(Socket s, ServerThread st){
		serverThread = st;
		this.s = s;
		color = -1;
		try {
			oos = new ObjectOutputStream(s.getOutputStream());
			ois = new ObjectInputStream(s.getInputStream());
			
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void sendObject(Communication commOut){
		try {
			oos.reset();
			oos.writeObject(commOut);
			oos.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void run(){
		
			try {
				while(true){
					commIn =  (Communication) ois.readObject();
					color = commIn.getColor();
					if(!serverThread.checkInGame())
					serverThread.updateColor();
					//server in game
					if(serverThread.checkInGame()){
						String msg = commIn.getMessage();
						if(msg != "" && msg != null){
							serverThread.updateMessage(commIn.getColor()+commIn.getMessage());
						}
						//non msg object
						else{
							if(commIn.getGame() != null){
								Game tempGame = commIn.getGame();
								tempGame.setHuman(serverThread.getColor());
								serverThread.updateGame(tempGame);
								System.out.println("receive update");
							}
							if(commIn.getPlayerTurn()){
								serverThread.enableNextPlayer();
							}
							//game end
							if(commIn.getGameEnd()){
								serverThread.clientGameEnd(commIn.getGameEndString());
							}
							if(commIn.getUpdateScore()){
								int score = commIn.getScore();
								String name = commIn.getName();
								serverThread.updateServerScore(name, score);
								
							}
							if(commIn.requestScore){
								serverThread.updateAllClientScore("",1);
							}
						}
					}
					else{
						if(!confirmed && commIn.getConfirm()){
							confirmed = true;
							serverThread.setConfirm();
						}
					}
				}
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally{
				serverThread.deleteSocketThread(this);
				if(serverThread.checkInGame()){
					serverThread.someoneLeaves(color+4+"");
					serverThread.setBot(color);
				}
				try {
					if(ois != null){
						ois.close();
					}
					if(oos != null){
						oos.close();
					}
					if(s != null){
						s.close();
					}
				}catch(IOException ioe){
					System.out.println("ioe: "+ ioe.getMessage());
				} 
			}
	}
	public void sendMessage(String msg,int senderColor){
		Communication commOut = new Communication(senderColor,null);
		commOut.setMessage(msg);
		commOut.setInGame(true);
		sendObject(commOut);
	}
	public int getColor(){
		return color;
	}
	
	public Communication getCommIn(){
		return commIn;
	}
	
	public void notifyStartGame(Game theGame){
		System.out.println("Start Game");
		Communication commOut = new Communication(color,theGame);
		commOut.setStartGame();
		sendObject(commOut);
	}
	public void sendYourTurn(Game theGame){
		Communication commOut = new Communication(color,theGame);
		commOut.setInGame(true);
		commOut.setPlayerTurn();
		sendObject(commOut);
		
	}
	public void updateGame(Game theGame){
		Communication commOut = new Communication(color,theGame);
		commOut.setInGame(true);
		sendObject(commOut);
	}
	public void sendGameEnd(String str){
		Communication commOut = new Communication(color,null);
		commOut.setInGame(true);
		commOut.setGameEnd();
		commOut.setGameEndString(str);
		sendObject(commOut);
	}
	public void updateScore(String name, int score){
		 String tempStr = "";
		  try {
				Scanner sc = new Scanner(new File("src/score/scores"));
				while(sc.hasNext()) {
					tempStr = tempStr+sc.next()+" ";
					tempStr = tempStr+sc.nextInt()+" \n";	
				}
				sc.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		Communication commOut = new Communication(color, null);
		commOut.writeFile(tempStr);
		commOut.setInGame(true);
		commOut.updateScore(name,score);
		sendObject(commOut);
	}

}
