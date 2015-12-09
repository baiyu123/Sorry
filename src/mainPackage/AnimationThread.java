package mainPackage;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JPanel;

public class AnimationThread extends Thread{
	MainGUI mainGUI;
	JPanel boardPanel;
	GridBagLayout gridBagLayout;
	Game myGame;
	
	AnimationThread(MainGUI mainGUI, GridBagLayout gridBagLayout){
		this.gridBagLayout = gridBagLayout;
		this.mainGUI = mainGUI;
		boardPanel = mainGUI.getBoardPanel();
		myGame = mainGUI.getGame();
	}
	public void run(){
		
		Vector<Point> path = mainGUI.getGame().getHightlightedPath();
		System.out.println("called");
		if(path != null){
			for(int i = path.size(); i >0; i--){
				myGame.animationMove();
				boardPanel.removeAll();
				mainGUI.gameBoard();
				mainGUI.setMyGame(myGame);
				try {
					Thread.sleep(250);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if(mainGUI.isServer){
					mainGUI.getServer().updateAllClientGame(mainGUI.getGame());
				}else{
					mainGUI.getClient().updateGame(mainGUI.getGame());
					mainGUI.getClient().setUpdating(true);
				}
				
			}
		}
		if(mainGUI.isServer){
			mainGUI.getServer().enableNextPlayer();
		}else{
			mainGUI.getClient().setUpdating(false);
			mainGUI.getClient().enableNextPlayer();
		}
		
	}
}
