package mainPackage;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JButton;

public class NiceButton extends JButton {
	String text;
	Font font;
	Font titleFont;
	int size;
	int titleSize;
	boolean enable;
	Graphics graph;
	File file;
	String title;
	boolean card;
	NiceButton(File f,String tite, String str){
		super();
		file = f;
		text = str;
		title = tite;
		card = true;
	}
	NiceButton(String str){
		super(str);
		text = str;
		setEnabled(true);
		file = null;
		card = false;
	}
	NiceButton(String str,Font newFont,int s){
		super();
		text = str;
		font = newFont;
		size = s;
		setEnabled(true);
		file = null;
		card = false;
	}
	public void setFont(Font newFont){
		super.setFont(newFont);
		font = newFont;
	}
	public void setTitleFont(Font newFont){
		titleFont = newFont;
	}
	public void setSize(int s){
		size = s;
		titleSize = s+10;
	}
	public void setEnabled(boolean e){
		super.setEnabled(e);
		enable = e;
		}
	protected void paintComponent(Graphics g){
		super.paintComponent(g);
		graph = g;
		Image background;
		try{
			if(file == null){
				background = ImageIO.read(new File("images/buttons/grey_button00.png"));
			}
			else{
				background = ImageIO.read(file);
			}
			background = background.getScaledInstance(this.getWidth(), this.getHeight(), Image.SCALE_SMOOTH);
			g.drawImage(background, 0, 0, null);
		}catch(IOException e){
			e.printStackTrace();
		}
		FontMetrics metrics = g.getFontMetrics(font);
		g.setFont(font);
		if(!enable)g.setColor(Color.GRAY);
	    else g.setColor(Color.BLACK);
		if(card){
			
			int x = (this.getWidth() - metrics.stringWidth(title)) / 2;
		    int y = ((this.getHeight() - metrics.getHeight()) / 2) -4*metrics.getAscent();
		    g.setFont(titleFont);
		    g.drawString(title, x, y);
		    g.setFont(font);
		    y += 2*metrics.getAscent();
		    for (String line : text.split("\n")){
		    	x = (this.getWidth() - metrics.stringWidth(line)) / 2;
		        y += metrics.getAscent();
		        g.drawString(line ,x, y);
		    }
		}
		else{
		    int x = (this.getWidth() - metrics.stringWidth(text)) / 2;
		    int y = ((this.getHeight() - metrics.getHeight()) / 2) + metrics.getAscent();
		    //int y = this.getHeight()/2 + metrics.getAscent()/2;
		    g.drawString(text, x, y);
		}
	}
}
