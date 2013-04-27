/*
 * File: NameSurferGraph.java
 * ---------------------------
 * This class represents the canvas on which the graph of
 * names is drawn. This class is responsible for updating
 * (redrawing) the graphs whenever the list of entries changes or the window is resized.
 */

import acm.graphics.*;

import java.awt.event.*;
import java.util.*;
import java.awt.*;

public class NameSurferGraph extends GCanvas
	implements NameSurferConstants, ComponentListener {

	/**
	* Creates a new NameSurferGraph object that displays the data.
	*/
	public NameSurferGraph() {
		addComponentListener(this);
		addGrid();
	}
	
	/**
	* Clears the list of name surfer entries stored inside this class.
	*/
	public void clear() {
		while (!entry.isEmpty()) {
			entry.remove(0);
		}
	}
	
	/* Method: addEntry(entry) */
	/**
	* Adds a new NameSurferEntry to the list of entries on the display.
	* Note that this method does not actually draw the graph, but
	* simply stores the entry; the graph is drawn by calling update.
	*/
	public void addEntry(NameSurferEntry entry) {
		this.entry.add(entry);
		notValidNamePrompt = "";
	}
	
	public void addPrompt(String name) {
		 notValidNamePrompt = name + " is not in the database!";
	}
	
	/**
	* Updates the display image by deleting all the graphical objects
	* from the canvas and then reassembling the display according to
	* the list of entries. Your application must call update after
	* calling either clear or addEntry; update is also called whenever
	* the size of the canvas changes.
	*/
	public void update() {
		removeAll();
		addGrid();
		graphData();
		printPrompt();
	}
	
	private void printPrompt() {
		GLabel label = new GLabel(notValidNamePrompt);
		label.setLocation(0, GRAPH_MARGIN_SIZE / 2 + label.getAscent() / 2);
		label.setColor(Color.GREEN);
		add(label);
	}

	private void graphData() {
		for (int i = 0; i < entry.size(); i++) {
			for (int j = 0; j < NDECADES - 1; j++) {
				int rank = MAX_RANK;
				int nextRank = MAX_RANK;
				String rankLabel = "*";
				if (entry.get(i).getRank(j) != 0) {
					rank = entry.get(i).getRank(j);
					rankLabel = Integer.toString(rank);
				}
				if (entry.get(i).getRank(j + 1) != 0) {
					nextRank = entry.get(i).getRank(j + 1);
				}
				GLabel label = new GLabel(entry.get(i).getName() + " " + rankLabel, getWidth() / NDECADES * j, 
						(getHeight() - 2 * GRAPH_MARGIN_SIZE) * rank / MAX_RANK + GRAPH_MARGIN_SIZE);
				GLine line = new GLine(getWidth() / NDECADES * j, (getHeight() - 2 * GRAPH_MARGIN_SIZE) * rank / MAX_RANK + GRAPH_MARGIN_SIZE, 
						getWidth() / NDECADES * (j + 1), (getHeight() - 2 * GRAPH_MARGIN_SIZE) * nextRank / MAX_RANK + GRAPH_MARGIN_SIZE);
				setColor(i, label);
				setColor(i, line);
				add(label);
				add(line);
			}
			int rank = MAX_RANK;
			String rankLabel = "*";
			if (entry.get(i).getRank(NDECADES - 1) != 0) {
				rank = entry.get(i).getRank(NDECADES - 1);
				rankLabel = Integer.toString(rank);
			}
			GLabel label = new GLabel(entry.get(i).getName() + " " + rankLabel, getWidth() / NDECADES * (NDECADES - 1), 
					(getHeight() - 2 * GRAPH_MARGIN_SIZE) * rank / MAX_RANK + GRAPH_MARGIN_SIZE);
			setColor(i, label);
			add(label);
		}
		
	}

	private void setColor(int i, GObject obj){
		if (i % 4 == 0)
			obj.setColor(Color.BLACK);
		if (i % 4 == 1)
			obj.setColor(Color.RED);
		if (i % 4 == 2)
			obj.setColor(Color.BLUE);
		if (i % 4 == 3)
			obj.setColor(Color.MAGENTA);
	}
	
	private void addGrid() {
		addVerticalLines();
		addHorizontalLines();
		addDecadeLabels();
	}
	
	private void addVerticalLines() {
		for (int i = 0; i < NDECADES - 1; i++) {
			add(new GLine((getWidth() / NDECADES * (i + 1)), 0,
						  (getWidth() / NDECADES * (i + 1)), getHeight()));
		}
	}

	private void addHorizontalLines() {
		add(new GLine(0, GRAPH_MARGIN_SIZE, getWidth(), GRAPH_MARGIN_SIZE));
		add(new GLine(0, getHeight() - GRAPH_MARGIN_SIZE, getWidth(), getHeight() - GRAPH_MARGIN_SIZE));
	}

	private void addDecadeLabels() {
		for (int i = 0; i < NDECADES; i++) {
			add(new GLabel(Integer.toString(START_DECADE + i * YEARS_IN_DECADE), 
					(getWidth() / NDECADES * i), getHeight()));
		}
	}
	
	private ArrayList<NameSurferEntry> entry = new ArrayList<NameSurferEntry>();
	private String notValidNamePrompt = "";

	/* Implementation of the ComponentListener interface */
	public void componentHidden(ComponentEvent e) { }
	public void componentMoved(ComponentEvent e) { }
	public void componentResized(ComponentEvent e) { update(); }
	public void componentShown(ComponentEvent e) { }
}
