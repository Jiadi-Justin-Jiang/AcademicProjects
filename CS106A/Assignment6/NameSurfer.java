/*
 * File: NameSurfer.java
 * ---------------------
 * When it is finished, this program will implements the viewer for
 * the baby-name database described in the assignment handout.
 */

import acm.program.*;
import java.awt.event.*;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.StringTokenizer;

import javax.swing.*;

public class NameSurfer extends Program implements NameSurferConstants {
	
	private JTextField nameField;
	private JButton graphButtton;
	private JButton clearButton;

	public void init() {
		nameField = new JTextField(MAX_TEXTFIELD);
		graphButtton = new JButton("Graph");
		clearButton = new JButton("Clear");
		add(new JLabel("Name"), SOUTH);
		add(nameField, SOUTH);
		add(graphButtton, SOUTH);
		add(clearButton, SOUTH);
		nameField.addActionListener(this);
		addActionListeners();
	}
	
	/**
	 * This method has the responsibility for reading in the database
	 * and initializing the interactors at the bottom of the window.
	 */
	public void run() {
		add(graph);
		validate();
		try {
			db = new NameSurferDataBase(NAMES_DATA_FILE);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	/** this method gets called when your interactors fire events. */
	public void actionPerformed(ActionEvent e) {
		
		if (e.getSource() == nameField || e.getActionCommand().equals("Graph")) {
			entry = db.findEntry(nameField.getText());
			if (entry != null) {
				graph.addEntry(entry);
				graph.update();
			} else {
				graph.addPrompt(nameField.getText());
				graph.update();
			}
		}
		
		if(e.getActionCommand().equals("Clear")) {
			graph.clear();
			graph.update();
		}
	}
	
	private NameSurferEntry entry;
	private NameSurferDataBase db = null;
	private NameSurferGraph graph = new NameSurferGraph();
}

