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
public class ColorButton extends JButton{
	/**
	 * 
	 */
	private static final long serialVersionUID = -1268959459887603072L;
	String text;
	Font font;
	int size;
	boolean enable;
	Graphics graph;
	int color;
	boolean clicking;
	ColorButton(String str){
		super(str);
		text = str;
		setEnabled(true);
		clicking = false;
	}
	ColorButton(String str,Font newFont,int s){
		super();
		text = str;
		font = newFont;
		size = s;
		setEnabled(true);
		clicking = false;
	}
	public void setColor(int c){
		color = c;
	}
	public void setFont(Font newFont){
		super.setFont(newFont);
		font = newFont;
	}
	public void setSize(int s){
		size = s;
	}
	public void setEnabled(boolean e){
		super.setEnabled(e);
		enable = e;
		}
	public void onClick(){
		clicking = true;
	}
	public void notOnClick(){
		clicking = false;
	}
	protected void paintComponent(Graphics g){
		super.paintComponent(g);
		graph = g;
		Image background = null;
		try{
			if(color == 0){
				if(!clicking){
					background = ImageIO.read(new File("images/buttons/red_button00.png"));
				}
				else{
					background = ImageIO.read(new File("images/buttons/red_button01.png"));
				}
			}
			else if(color == 1){
				if(!clicking){
					background = ImageIO.read(new File("images/buttons/blue_button00.png"));
				}
				else{
					background = ImageIO.read(new File("images/buttons/blue_button01.png"));
				}
			}
			else if(color == 2){
				if(!clicking){
					background = ImageIO.read(new File("images/buttons/green_button00.png"));
				}
				else{
					background = ImageIO.read(new File("images/buttons/green_button01.png"));
				}
			}
			else if(color == 3){
				if(!clicking){
					background = ImageIO.read(new File("images/buttons/yellow_button00.png"));
				}
				else{
					background = ImageIO.read(new File("images/buttons/yellow_button01.png"));
				}
			}
			else{
				background = ImageIO.read(new File("images/buttons/grey_button00.png"));
			}
			background = background.getScaledInstance(this.getWidth(), this.getHeight(), Image.SCALE_SMOOTH);
			g.drawImage(background, 0, 0, null);
		}catch(IOException e){
			e.printStackTrace();
		}
		// Get the FontMetrics
	    FontMetrics metrics = g.getFontMetrics(font);
	    // Determine the X coordinate for the text
	    int x = (this.getWidth() - metrics.stringWidth(text)) / 2;
	    // Determine the Y coordinate for the text
	    int y = ((this.getHeight() - metrics.getHeight()) / 2) + metrics.getAscent();
	    //int y = this.getHeight()/2 + metrics.getAscent()/2;
	    // Set the font
	    g.setFont(font);
	    // Draw the String
	    
	    if(!enable)g.setColor(Color.GRAY);
	    else g.setColor(Color.BLACK);
	    g.drawString(text, x, y);
	    // Dispose the Graphics
	}
}
