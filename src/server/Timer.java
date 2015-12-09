package server;

import mainPackage.MainGUI;

public class Timer extends Thread{
	private MainGUI mainGUI;
	private long startTime;
	private int timeInSec;
	private int timeElapse;
	private String timerString;
	private boolean inGame = false;
	private boolean confirmed = false;
	private boolean myRound;
	
	public Timer(MainGUI mainGUI){
		if(mainGUI.isServer) myRound = true;
		else myRound = false;
		this.mainGUI = mainGUI;
		startTime = System.currentTimeMillis();
		timeInSec = 30;
	}
	public void setTimeInsec(int time){
		timeInSec = time;
	}
	public void resetTimer(){
		startTime = System.currentTimeMillis();
	}
	
	public void run(){
		while(true){
			timeElapse = (int) (System.currentTimeMillis()-startTime);
			int timeElapseSec = timeElapse/1000;
			if(timeElapseSec == 1){
				resetTimer();
				timeInSec -= 1;
				if(timeInSec < 10){
					timerString = "0:0" + timeInSec;
				}else{
					timerString = "0:"+timeInSec;
				}
				//in choosing color screen
				if(!inGame){
					if(!confirmed){
						refreshChooseColor();
					}
					else{
						mainGUI.deleteTimer();
					}
					if(timeInSec == 0){
						if(!inGame&&!confirmed){
							mainGUI.kickPlayer();
							break;
						}
					}
				}
				else{
					if(myRound){
						refreshMainGUI();
						if(timeInSec <= 0){
							mainGUI.skipRound();
							mainGUI.setTimerTo15();
							myRound = false;
						}
					}else{
						
					}
				}
			}
			try {
				sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	public void setMyRound(boolean myRound){
		this.myRound = myRound;
	}
	
	public void setConfirmed(){
		confirmed = true;
	}
	public void setInGame(){
		inGame = true;
	}
	public void refreshChooseColor(){
		mainGUI.setTimerStr(timerString);
	}
	public void refreshMainGUI(){
		mainGUI.setTimerStrInGame(timerString);
	}
}
