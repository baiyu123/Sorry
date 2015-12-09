package utilities;

import java.awt.Font;
import java.awt.FontFormatException;
import java.io.FileInputStream;
import java.io.IOException;

public class FontClass {
	public static Font kenThinFont(int size){
		Font result = null;
		try {
			result = Font.createFont(Font.TRUETYPE_FONT, new FileInputStream("fonts/kenvector_future_thin.ttf"));
			result = result.deriveFont(Font.PLAIN,size);
		} catch (FontFormatException | IOException e) {
			e.printStackTrace();
		}
		return result;
	}
}
