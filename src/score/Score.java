package score;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Scanner;

import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class Score {
	
	private static final String filePath = "src/score/scores";
	
	private static JTable scoreTable;
	private static DefaultTableModel tableModel;
	private static final int NAME_COLUMN = 0;
	private static final int SCORE_COLUMN = 1;
	
	private final static JDialog scoreDisplay;
	
	static {
		Object[] tableHeaders = new Object[] {"Name", "Score"};
		tableModel = new DefaultTableModel(tableHeaders,0) {
			private static final long serialVersionUID = -2100979802046466684L;
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		scoreTable = new JTable(tableModel);
		try {
			Scanner sc = new Scanner(new File(filePath));
			while(sc.hasNext()) {
				Object[] row = {sc.next(),sc.nextInt()};
				tableModel.addRow(row);
			}
			sc.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		scoreTable = new JTable(tableModel);
		
		scoreDisplay = new JDialog();
		scoreDisplay.setTitle("Scores");
		scoreDisplay.setModal(true);
		scoreDisplay.setSize(200, 300);
		JScrollPane jsp = new JScrollPane(scoreTable);
		jsp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		jsp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scoreDisplay.add(jsp);
	}
	
	public static void add(String name, int score) {
		boolean placed = false;
		for(int i = 0; i < tableModel.getRowCount(); i++) {
			if ((int)tableModel.getValueAt(i, SCORE_COLUMN) < score) {
				Object[] row = {name,score};
				tableModel.insertRow(i, row);
				placed = true;
				break;
			}
		}
		if(!placed) {
			Object[] row = {name,score};
			tableModel.addRow(row);
		}
		save();
	}
	
	private static void save() {
		PrintWriter writer = null;
		try {
			writer = new PrintWriter(filePath, "UTF-8");
			for(int i = 0; i < tableModel.getRowCount(); i++) {
				writer.print(tableModel.getValueAt(i, NAME_COLUMN));
				writer.print(" ");
				writer.println(tableModel.getValueAt(i, SCORE_COLUMN));
			}
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			System.out.println("File not found: " + filePath);
			System.out.println("Could not save scores!");
		} finally {
			if(writer != null) writer.close();
		}
	}

	public static void display() {
		scoreDisplay.setLocationRelativeTo(null);
		scoreDisplay.setVisible(true);
	}
}
