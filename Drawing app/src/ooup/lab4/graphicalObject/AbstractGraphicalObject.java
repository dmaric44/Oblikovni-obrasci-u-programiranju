package ooup.lab4.graphicalObject;

import java.util.*;

import ooup.lab4.GeometryUtil;
import ooup.lab4.Point;

public abstract class AbstractGraphicalObject implements GraphicalObject {
	private Point[] hotPoints;
	private boolean[] hotPointSelected;
	private boolean selected;
	protected List<GraphicalObjectListener> listeners = new ArrayList<>();


	public AbstractGraphicalObject(Point[] points) {
		this.hotPoints = points;
		this.hotPointSelected = new boolean[hotPoints.length];
	}
	
	public Point getHotPoint(int position) {
		return hotPoints[position];
	}
	public void setHotPoint(int position, Point hp) {
		hotPoints[position] = hp;
	}
	
	public int getNumberOfHotPoints() {
		return hotPoints.length;
	}
	
	public double getHotPointDistance(int position, Point p) {
		return GeometryUtil.distanceFromPoint(hotPoints[position], p);
	}
	
	public boolean isHotPointSelected(int position) {
		return hotPointSelected[position];
	}
	public void setHotPointSelected(int position, boolean selection) {
		hotPointSelected[position] = selected;
		notifyListeners();
	}
	
	public boolean isSelected() {
		return selected;
	}
	public void setSelected(boolean selection) {
		this.selected = selection;
		notifySelectionListeners();
	}
	
	public void translate(Point p) {
		for(int i=0; i<getNumberOfHotPoints(); i++) {
			setHotPoint(i, getHotPoint(i).translate(p));
		}
		notifyListeners();
	}
	
	public void addGraphicalObjectListener(GraphicalObjectListener obj) {
		listeners.add(obj);
	}
	
	public void removeGraphicalObjectListener(GraphicalObjectListener obj) {
		listeners.remove(obj);
	}
	
	public void notifyListeners() {
		listeners.forEach(l -> l.graphicalObjectChanged(this));
	}
	public void notifySelectionListeners() {
		listeners.forEach(l -> l.graphicalObjectSelectionChanged(this));
	}
}
