package ooup.lab4;

import java.util.*;

import javax.swing.SwingUtilities;

import ooup.lab4.graphicalObject.GraphicalObject;
import ooup.lab4.graphicalObject.LineSegment;
import ooup.lab4.graphicalObject.Oval;

public class Main {

	public static void main(String[] args) {

		List<GraphicalObject> objects = new ArrayList<>();

		objects.add(new LineSegment());
		objects.add(new Oval());

		
		SwingUtilities.invokeLater(() -> {
			new GUI(objects).setVisible(true);
		});		
	}
}
