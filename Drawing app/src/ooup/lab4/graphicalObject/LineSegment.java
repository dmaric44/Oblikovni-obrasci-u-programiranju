package ooup.lab4.graphicalObject;

import java.util.List;
import java.util.Stack;

import ooup.lab4.GeometryUtil;
import ooup.lab4.Point;
import ooup.lab4.Rectangle;
import ooup.lab4.renderer.Renderer;

public class LineSegment extends AbstractGraphicalObject {
	

	public LineSegment(Point p1, Point p2) {
		super(new Point[] {p1,p2});
	}
	
	public LineSegment() {
		super(new Point[] {new Point(0,0), new Point(10,0)});
	}

	
	
	@Override
	public Rectangle getBoundingBox() {
		Point p1 = getHotPoint(0);
		Point p2 = getHotPoint(1);
		int x = p1.getX() < p2.getX() ? p1.getX() : p2.getX();
		int y = p1.getY() < p2.getY() ? p1.getY() : p2.getY();
		
		int width = Math.abs(p2.getX() - p1.getX());
		int height = Math.abs(p2.getY() - p1.getY());
		return new Rectangle(x,y, width, height);
	}

	@Override
	public double selectionDistance(Point mousePoint) {
		Point p1 = getHotPoint(0);
		Point p2 = getHotPoint(1);
		return GeometryUtil.distanceFromLineSegment(p1, p2, mousePoint);
	}

	@Override
	public String getShapeName() {
		return "Line";
	}

	@Override
	public GraphicalObject duplicate() {
		return new LineSegment(new Point(getHotPoint(0).getX(),getHotPoint(0).getY()), new Point(getHotPoint(1).getX(), getHotPoint(1).getY()));
	}

	@Override
	public void render(Renderer r) {
		r.drawLine(getHotPoint(0), getHotPoint(1));
		
	}

	@Override
	public String getShapeID() {
		return "@LINE";
	}

	@Override
	public void save(List<String> rows) {
		Point p1 = getHotPoint(0);
		Point p2 = getHotPoint(1);
		String line = getShapeID() +" "+ p1.getX() +" "+ p1.getY() +" "+ p2.getX() +" " +p2.getY();
		rows.add(line);
	}

	@Override
	public void load(Stack<GraphicalObject> stack, String data) {
		data.replace("\n", "");
		String points[] = data.split(" ");
		Point p1 = new Point(Integer.parseInt(points[0]), Integer.parseInt(points[1]));
		Point p2 = new Point(Integer.parseInt(points[2]), Integer.parseInt(points[3]));
		
		setHotPoint(0,p1);
		setHotPoint(1,p2);		
		stack.push(this);
	}
}
