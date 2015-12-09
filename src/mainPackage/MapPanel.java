package mainPackage;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.awt.Point;

import javax.swing.JPanel;

public class MapPanel extends JPanel{
	Map<Point,TileButton> compMap;
	MapPanel(){
		super();
		compMap = new HashMap<Point, TileButton>();
	}
	MapPanel(GridBagLayout gbl){
		super(gbl);
		compMap = new HashMap<Point, TileButton>();
	}
	public void add(TileButton comp,GridBagConstraints constrains){
		super.add(comp,constrains);
		Point newPoint = new Point(constrains.gridx,constrains.gridy);
		compMap.put(newPoint, comp);
	}
	public TileButton getComp(int x, int y){
		Point newPoint = new Point(x,y);
		return compMap.get(newPoint);
	}
}
