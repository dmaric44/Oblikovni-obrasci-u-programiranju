package ooup.lab4.graphicalObject;

import java.util.*;

import ooup.lab4.Point;
import ooup.lab4.Rectangle;
import ooup.lab4.renderer.Renderer;

public class CompositeShape implements GraphicalObject  {
	private boolean selected;
	private List<GraphicalObject> objects = new ArrayList<>();
	private List<GraphicalObjectListener> listeners = new ArrayList<>();
	
	public CompositeShape(List<GraphicalObject> objects, boolean selected) {
		this.objects = objects;
		this.selected = selected;
	}
	
	public CompositeShape() {
	}
	
	public List<GraphicalObject> getObjects(){
		return objects;
	}
	
	@Override
	public boolean isSelected() {
		return selected;
	}

	@Override
	public void setSelected(boolean selected) {
		this.selected = selected;
		notifySelectionListeners();
	}

	@Override
	public int getNumberOfHotPoints() {
		return 0;
	}

	@Override
	public Point getHotPoint(int index) {
		return null;
	}

	@Override
	public void setHotPoint(int index, Point point) {
	}

	@Override
	public boolean isHotPointSelected(int index) {
		return false;
	}

	@Override
	public void setHotPointSelected(int index, boolean selected) {
	}

	@Override
	public double getHotPointDistance(int index, Point mousePoint) {
		return 0;
	}

	@Override
	public void translate(Point delta) {
		for(GraphicalObject obj: objects) {
			obj.translate(delta);
		}
		notifyListeners();
	}

	private void notifyListeners() {
		listeners.forEach(l -> l.graphicalObjectChanged(this));
	}
	private void notifySelectionListeners() {
		listeners.forEach(l -> l.graphicalObjectSelectionChanged(this));
	}

	@Override
	public Rectangle getBoundingBox() {
		Rectangle current = objects.get(0).getBoundingBox();
		
		int minX = current.getX();
		int minY = current.getY();
		int maxX = current.getX() + current.getWidth();
		int maxY = current.getY() + current.getHeight();
		
		for(int i=1; i<objects.size(); i++) {
			current = objects.get(i).getBoundingBox();
			minX = current.getX() < minX ? minX=current.getX() : minX;
			minY = current.getY() < minY ? minY=current.getY() : minY;
			maxX = current.getX()+current.getWidth() > maxX ? maxX=current.getX()+current.getWidth() : maxX;
			maxY = current.getY()+current.getHeight() > maxY ? maxY=current.getY()+current.getHeight() : maxY;

		}

		return new Rectangle(minX, minY, maxX-minX, maxY-minY);
	}

	@Override
	public double selectionDistance(Point mousePoint) {
		double min = objects.get(0).selectionDistance(mousePoint);
		for(int i=1; i<objects.size(); i++) {
			double dist = objects.get(i).selectionDistance(mousePoint);
			if(dist < min)
				min = dist;
		}
		return min;
	}

	@Override
	public void render(Renderer r) {
		objects.forEach(obj -> obj.render(r));
	}

	@Override
	public void addGraphicalObjectListener(GraphicalObjectListener l) {
		listeners.add(l);
	}

	@Override
	public void removeGraphicalObjectListener(GraphicalObjectListener l) {
		listeners.add(l);
	}

	@Override
	public String getShapeName() {
		return "Composite";
	}

	@Override
	public GraphicalObject duplicate() {
		List<GraphicalObject> duplicates = new ArrayList<>();
		for(GraphicalObject obj: objects) {
			duplicates.add(obj.duplicate());
		}
		return new CompositeShape(duplicates, false);
	}

	@Override
	public String getShapeID() {
		return "@COMP";
	}

	@Override
	public void save(List<String> rows) {
		objects.forEach(obj -> obj.save(rows));
		String line = getShapeID() + " " + objects.size();
		rows.add(line);
	}

	@Override
	public void load(Stack<GraphicalObject> stack, String data) {
		int numOfObjects = Integer.parseInt(data.replace("\n", ""));
		List<GraphicalObject> newObjects = new ArrayList<>();
		for(int i=0; i<numOfObjects; i++) {
			newObjects.add(stack.pop());
		}
		this.objects = newObjects;
		stack.add(this);
	}

}
