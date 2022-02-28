package ooup.lab4.graphicalObject;

import java.util.List;
import java.util.Stack;

import ooup.lab4.GeometryUtil;
import ooup.lab4.Point;
import ooup.lab4.Rectangle;
import ooup.lab4.renderer.Renderer;

public class Oval extends AbstractGraphicalObject {
	private Point center;

	public Oval(Point p1, Point p2) {
		super(new Point[] {p1,p2});
		this.center = getCenter(p1,p2); 
	}
	

	public Oval() {
		super(new Point[] {new Point(10,0), new Point(0,10)});
		this.center = getCenter(new Point(10,0), new Point(0,10));
	}

	@Override
	public Rectangle getBoundingBox() {
		Point rightHP = getHotPoint(0);
		Point downHP = getHotPoint(1);
		
		int width = Math.abs(rightHP.getX() - downHP.getX())*2;
		int height = Math.abs(rightHP.getY() - downHP.getY())*2;
		
		return new Rectangle(rightHP.getX()-width, downHP.getY()-height, width, height);
	}

	@Override
	public double selectionDistance(Point mousePoint) {
		Point rightHP = getHotPoint(0);
		Point downHP = getHotPoint(1);
		
		int a = rightHP.getX()-downHP.getX();
		int b = downHP.getY()-rightHP.getY();
		
		
		
		if( Math.pow(mousePoint.getX()-center.getX(), 2)/Math.pow(a, 2) + Math.pow(mousePoint.getY()-center.getY(), 2)/Math.pow(b, 2) <= 1 ) {
			return 0;
		}
		
		Point[] points = getPoints(50);
		double min = GeometryUtil.distanceFromPoint(points[0], mousePoint);
		for(int i=1; i<50; i++) {
			double dist = GeometryUtil.distanceFromPoint(points[i], mousePoint);
			if(dist < min)
				min = dist;
		}
		return min;
		
		
	}

	private Point[] getPoints(int num) {
		Point rightHP = getHotPoint(0);
		Point downHP = getHotPoint(1);
		
		int a = rightHP.getX()-downHP.getX();
		int b = downHP.getY()-rightHP.getY();
		
		int x = rightHP.getX()-a;
		int y = downHP.getY()-b;
		
		Point[] points = new Point[num];
		for(int i=0; i<num; i++) {
			double k = (2*Math.PI/num) * i;
			int px = (int)(a*Math.cos(k)) + x;
			int py = (int)(b*Math.sin(k)) + y;
			points[i] = new Point(px,py);
		}		
		return points;
	}
	
	private Point getCenter(Point p1, Point p2) {
		Point rightHP = getHotPoint(0);
		Point downHP = getHotPoint(1);
		int a = rightHP.getX()-downHP.getX();
		int b = downHP.getY()-rightHP.getY();
		
		int x = rightHP.getX()-a;
		int y = downHP.getY()-b;
		return new Point(x,y);
	}
	

	@Override
	public String getShapeName() {
		return "Oval";
	}

	@Override
	public GraphicalObject duplicate() {
		return new Oval(new Point(getHotPoint(0).getX(),getHotPoint(0).getY()), new Point(getHotPoint(1).getX(), getHotPoint(1).getY()));
	}


	@Override
	public void render(Renderer r) {
		r.fillPolygon(getPoints(50));
	}


	@Override
	public String getShapeID() {
		return "@OVAL";
	}


	@Override
	public void save(List<String> rows) {
		Point rightHP = getHotPoint(0);
		Point downHP = getHotPoint(1);
		String line = getShapeID() +" "+ rightHP.getX() +" "+ rightHP.getY() +" "+ downHP.getX() +" "+ downHP.getY();
		rows.add(line);
	}


	@Override
	public void load(Stack<GraphicalObject> stack, String data) {
		data.replace("\n", "");
		String[] points = data.split(" ");
		Point rightHP = new Point(Integer.parseInt(points[0]), Integer.parseInt(points[1]));
		Point downHP = new Point(Integer.parseInt(points[2]), Integer.parseInt(points[3]));
		setHotPoint(0, rightHP);
		setHotPoint(1, downHP);
		stack.add(this);		
	}
}
