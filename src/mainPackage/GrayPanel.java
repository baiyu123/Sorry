package mainPackage;

import java.awt.Graphics;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

public class GrayPanel extends JPanel{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -9084314634761255438L;
	GrayPanel(){
		super();
	}
	GrayPanel(GridBagLayout gb){
		super(gb);
	}
	@Override
	protected void paintComponent(Graphics g){
		super.paintComponent(g);
		Image background;
		try {
			background = ImageIO.read(new File("images/panels/grey_panel.png"));
			background = background.getScaledInstance(this.getWidth(),this.getHeight(),Image.SCALE_SMOOTH);
			g.drawImage(background,0,0,null);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
}
