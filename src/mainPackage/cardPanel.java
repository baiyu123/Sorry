package mainPackage;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.JDialog;

public class cardPanel extends JDialog implements ActionListener{
	/**
	 * 
	 */
	private static final long serialVersionUID = 8441608833629608183L;
	String text;
	NiceButton button;
	public cardPanel(String title, String str){
		text = str;
		button = new NiceButton(new File("images/cards/card_brown.png"),title, str);
		button.addActionListener(this);
		add(button);
		button.setEnabled(true);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setLocation(300,200);
		pack();
		setVisible(true);
	}
	public void setFont(Font font){
		button.setFont(font);
	}
	public void setTitleFont(Font font){
		button.setTitleFont(font);
	}
	public void setSize(int size){
		button.setSize(size);
	}
	@Override
	public void actionPerformed(ActionEvent arg0) {
		dispose();
	}

}
