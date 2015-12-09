package mainPackage;

import java.awt.Graphics;
import java.awt.Image;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JLabel;

public class SorryLabel extends JLabel{
	protected void paintComponent(Graphics g){
		super.paintComponent(g);
		Image background;
		try {
			background = ImageIO.read(new File("images/sorry.png"));
			int x = this.getWidth();
			int y = (int) (this.getHeight()*0.8);
			background = background.getScaledInstance(x,y,Image.SCALE_SMOOTH);
			g.drawImage(background,0,0,null);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
}
