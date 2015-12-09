package utilities;

import java.io.BufferedInputStream;
import java.io.FileInputStream;

import javazoom.jl.player.Player;

public class MusicPlayer extends Thread{
	Player player;
	boolean close;
	String fileName;
	public MusicPlayer(String fileName){
		this.fileName = fileName;
	}
	public void run(){
		try {
            BufferedInputStream buffer = new BufferedInputStream(new FileInputStream(fileName));
            player = new Player(buffer);
            player.play();
        } catch (Exception e) {
            System.out.println(e);
        }
	}
	public void stopMusic(){
		player.close();
		close = true;
	}
}
