package mainPackage;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JDialog;
import javax.swing.JPanel;

public class HelpMenuDialog extends JDialog{
	String text;
	Font font;
	HelpMenuDialog(String str, Font f){
		text = str;
		font = f;
		add(new JPanel(){
			protected void paintComponent(Graphics g){
				super.paintComponents(g);
				g.setFont(font);
				try {
					Image background = ImageIO.read(new File("images/cards/card_beige.png"));
					background = background.getScaledInstance(this.getWidth(), this.getHeight(), Image.SCALE_SMOOTH);
					g.drawImage(background, 0, 0,null);
					int x = (int) (0.1*this.getWidth());
					int y = 10;
					FontMetrics metrics = g.getFontMetrics(font);
					for (String line : text.split("\n")){
				    	x = 10;
				        y += 2*metrics.getAscent();
				        g.drawString(line ,x, y);
				    }
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
	}
	

}
