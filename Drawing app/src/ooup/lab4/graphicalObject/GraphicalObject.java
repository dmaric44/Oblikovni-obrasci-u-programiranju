package ooup.lab4.graphicalObject;

import java.util.List;
import java.util.Stack;

import ooup.lab4.Point;
import ooup.lab4.Rectangle;
import ooup.lab4.renderer.Renderer;

public interface GraphicalObject {

	// Podrška za ureðivanje objekta
	boolean isSelected();
	void setSelected(boolean selected);
	int getNumberOfHotPoints();
	Point getHotPoint(int index);
	void setHotPoint(int index, Point point);
	boolean isHotPointSelected(int index);
	void setHotPointSelected(int index, boolean selected);
	double getHotPointDistance(int index, Point mousePoint);

	// Geometrijska operacija nad oblikom
	void translate(Point delta);
	Rectangle getBoundingBox();
	double selectionDistance(Point mousePoint);

	// Podrška za crtanje (dio mosta)
	void render(Renderer r);
	
	// Observer za dojavu promjena modelu
	public void addGraphicalObjectListener(GraphicalObjectListener l);
	public void removeGraphicalObjectListener(GraphicalObjectListener l);

	// Podrška za prototip (alatna traka, stvaranje objekata u crtežu, ...)
	String getShapeName();
	GraphicalObject duplicate();
	
	// Podrška za snimanje i uèitavanje
	public String getShapeID();
	public void load(Stack<GraphicalObject> stack, String data);
	public void save(List<String> rows);
}