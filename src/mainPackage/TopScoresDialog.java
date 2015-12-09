package mainPackage;

import java.util.List;
import java.util.ArrayList;

import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

public class TopScoresDialog extends JDialog{
	JTable jt;
	DefaultTableModel model;
	TopScoresDialog(){
		Object [][] data = new Object[][]{
			{"Ali", 34},
			{"Heather", 32},
			{"Patrick", 26},
			{"Aaron", 25},
			{"Elise", 26},
			{"Susie", 22},
			{"James", 22},
			{"Mario", 20},
			{"Johnny", 19},
			{"Ava",16},
			{"Milenne",24}
		};
		Object [] names = new Object[] {"Name", "Score"};
		model = new DefaultTableModel(data,names);
		jt = new JTable(model);
		TableRowSorter<TableModel> sorter = new TableRowSorter<>(jt.getModel());
		jt.setRowSorter(sorter);
		List<RowSorter.SortKey> sortKeys = new ArrayList<>();
		int colToSort = 1;
		sortKeys.add(new RowSorter.SortKey(colToSort, SortOrder.DESCENDING));
		sorter.setSortKeys(sortKeys);
		sorter.sort();
		
		JScrollPane scrollPanel = new JScrollPane(jt);
		add(scrollPanel);
		setVisible(true);
	}
	public void add(String name, int score){
		Object []data = new Object[]{name,score};
		model.addRow(data);
	}
}
