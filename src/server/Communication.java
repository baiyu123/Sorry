package server;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Vector;

import mainPackage.Game;

public class Communication implements Serializable{
	public static final long serialVersionUID = 1;
	private String message;
	private int color;
	private String disableColors;//1 enable 0 disable
	private Game myGame;
	private boolean inGame; // indicate this is ingame message
	private boolean leftGame; // notify client that this is a left game message
	private boolean confirm;
	private boolean startGame;
	private boolean playerTurn;
	private boolean gameEnd;
	private String gameEndString;
	private boolean updateScore;
	private String userName;
	private int score;
	private String fileStr;
	public boolean requestScore;
	public Communication(int colr, Game gameIn){
		updateScore = false;
		gameEnd = false;
		confirm = false;
		inGame = false;
		leftGame = false;
		color = colr;
		myGame = gameIn;
		disableColors = "0000";
		message = "";
	}
	public void writeFile(String str){
		fileStr = str;
	}
	public String getFile(){
		return fileStr;
	}
	public void updateScore(String name, int score){
		this.score = score;
		userName = name;
		updateScore = true;
	}
	public boolean getUpdateScore(){
		return updateScore;
	}
	public String getName(){
		return userName;
	}
	public int getScore(){
		return score;
	}
	public void setGameEndString(String str){
		gameEndString = str;
	}
	public String getGameEndString(){
		return gameEndString;
	}
	public void setGameEnd(){
		gameEnd = true;
	}
	public boolean getGameEnd(){
		return gameEnd;
	}
	public void setPlayerTurn(){
		playerTurn = true;
	}
	public boolean getPlayerTurn(){
		return playerTurn;
	}
	public void setStartGame(){
		startGame = true;
	}
	public boolean getStartGame(){
		return startGame;
	}
	public void setConfirm(){
		confirm = true;
	}
	public boolean getConfirm(){
		return confirm;
	}
	public void setLeftGame(){
		leftGame = true;
	}
	public boolean getLeftGame(){
		return leftGame;
	}
	public void setInGame(boolean inG){
		inGame = inG;
	}
	public boolean checkInGame(){
		return inGame;
	}
	public void setMessage(String msg){
		message = msg;
	}
	
	public void setGame(Game newGame){
		myGame = newGame;
	}
	public Game getGame(){
		return myGame;
	}
	public String getMessage(){
		return message;
	}
	public void setDisableColors(String discol){
		disableColors = discol;
	}
	public void setEnableColors(int en){
		String newString = disableColors.substring(0,en)+'1'+disableColors.substring(en);
		disableColors = newString;
	}
	public String getDisableColors(){
		return disableColors;
	}
	public int getColor(){
		return color;
	}
}
