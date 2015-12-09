package mainPackage;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JLabel;

public class TileButton extends JButton{
	//color: 4 gray, 0 red, 1 blue, 2 green, 3 yellow, 5 redfill, 6 bluefill, 7 greenfill, 8 yellowfill, 9 carddeck
	int color;
	String text;
	boolean arrow;
	boolean highlight;
	boolean humanPawn;
	Font font;
	Graphics graph;
	Image bg;
	boolean pawn;
	int height;
	int width;
	int pawnColor;
	TileButton(){
		super();
		color = 4;
		arrow = false;
		text = "";
		font = new Font("San-Serif", Font.BOLD,10);
		highlight = false;
	}
	TileButton(String str){
		super(str);
		color = 4;
		arrow = false;
		text = str;
		font = new Font("San-Serif", Font.BOLD,10);
		highlight = false;
	}
	TileButton(int c){
		super();
		color = c;
		arrow = false;
		text = "";
		font = new Font("San-Serif", Font.BOLD,10);
		highlight = false;
	}
	TileButton(int c,boolean arr){
		super();
		color = c;
		arrow = arr;
		text = "";
		font = new Font("San-Serif", Font.BOLD,10);
		highlight = false;
	}
	TileButton(int c, int pawnColor){
		super();
		color = c;
		pawn = true;
		font = new Font("San-Serif", Font.BOLD,10);
		highlight = false;
	}
	TileButton(int c,String str){
		super(str);
		color = c;
		arrow = false;
		text = str;
		font = new Font("San-Serif", Font.BOLD,10);
		highlight = false;
	}
	TileButton(int c, String str, Font newFont){
		super();
		color = c;
		arrow = false;
		text = str;
		font = newFont;
		highlight = false;
	}
	public int getColor(){
		return color;
	}
	public void sethighlight(boolean HL){
		highlight = HL;
	}

	public void setFont(Font newFont){
		super.setFont(newFont);
		font = newFont;
	}
	public void setPawn(int pColor){
			pawn = true;
			pawnColor = pColor;
	}
	public void setHumanPawn(boolean isHuman){
		humanPawn = isHuman;
	}
	public boolean isHuman(){
		return humanPawn;
	}
	public boolean isPawn(){
		return pawn;
	}
	
	protected void paintComponent(Graphics g){
		super.paintComponent(g);
		Image background;
		Image foreground = null;
		graph = g;
		try {
			if(!highlight){
				switch(color){
					case 0:  background = ImageIO.read(new File("images/tiles/red_tile.png"));
							if(arrow)foreground = ImageIO.read(new File("images/sliders/red_slide.png"));
							break;
					case 1: background = ImageIO.read(new File("images/tiles/blue_tile.png"));
							if(arrow)foreground = ImageIO.read(new File("images/sliders/blue_slide.png"));
							break;
					case 2: background = ImageIO.read(new File("images/tiles/green_tile.png"));
							if(arrow)foreground = ImageIO.read(new File("images/sliders/green_slide.png"));
							break;
					case 3: background = ImageIO.read(new File("images/tiles/yellow_tile.png"));
							if(arrow)foreground = ImageIO.read(new File("images/sliders/yellow_slide.png"));
							break;
					case 4: background = ImageIO.read(new File("images/tiles/grey_tile.png"));
							break;
					case 5: background = ImageIO.read(new File("images/panels/red_panel.png"));
							break;
					case 6: background = ImageIO.read(new File("images/panels/blue_panel.png"));
							break;
					case 7: background = ImageIO.read(new File("images/panels/green_panel.png"));
							break;
					case 8: background = ImageIO.read(new File("images/panels/yellow_panel.png"));
							break;
					case 9: background = ImageIO.read(new File("images/cards/cardBack_red.png"));
							break;
					default: background = ImageIO.read(new File("images/tiles/grey_tile.png"));
					
				}
			}
			else{
				switch(pawnColor){
					case 0:	background = ImageIO.read(new File("images/panels/red_panel.png"));
							break;
					case 1: background = ImageIO.read(new File("images/panels/blue_panel.png"));
							break;
					case 2: background = ImageIO.read(new File("images/panels/green_panel.png"));
							break;
					case 3: background = ImageIO.read(new File("images/panels/yellow_panel.png"));
							break;
					default: background = ImageIO.read(new File("images/panels/red_panel.png"));
							break;
				}
				
			}
			
			if(pawn){
				switch(pawnColor){
					case 0: foreground = ImageIO.read(new File("images/pawns/red_pawn.png"));
						break;
					case 1: foreground = ImageIO.read(new File("images/pawns/blue_pawn.png"));
						break;
					case 2: foreground = ImageIO.read(new File("images/pawns/green_pawn.png"));
						break;
					case 3: foreground = ImageIO.read(new File("images/pawns/yellow_pawn.png"));
						break;
					default: foreground = ImageIO.read(new File("images/pawns/red_pawn.png"));
				}
			}
			background = background.getScaledInstance(this.getWidth(),this.getHeight(),Image.SCALE_SMOOTH);
			bg = background;
			g.drawImage(background,0,0,null);
			if(!pawn){
				FontMetrics metrics = g.getFontMetrics(font);
		    	int x1 = (this.getWidth() - metrics.stringWidth(text)) / 2;
		    	int y1 = ((this.getHeight() - metrics.getHeight()) / 2) + metrics.getAscent();
		    	g.setFont(font);
		    	g.drawString(text, x1, y1);
			}
			if(pawn && foreground != null){
				Double w = this.getWidth()*0.3;
				Double h = this.getHeight()*0.5;
				foreground = foreground.getScaledInstance( w.intValue(),h.intValue(),Image.SCALE_SMOOTH);
				Double x = this.getWidth()*0.5-w*0.5;
				Double y = this.getHeight()*0.5-h*0.5;
				g.drawImage(foreground, x.intValue(), y.intValue(), null);
			}
			if(foreground != null&&!pawn){
				Double w = this.getWidth()*0.3;
				Double h = this.getHeight()*0.5;
				foreground = foreground.getScaledInstance( w.intValue(),h.intValue(),Image.SCALE_SMOOTH);
				Double x = this.getWidth()*0.5-w*0.5;
				Double y = this.getHeight()*0.5-h*0.5;
				g.drawImage(foreground, x.intValue(), y.intValue(), null);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
}
