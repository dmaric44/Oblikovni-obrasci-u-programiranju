package ooup.lab4;

public class Point {

	private int x;
	private int y;
	
	public Point(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public void setX(int x) {
		this.x = x;
	}
	
	public void setY(int y) {
		this.y = y;
	}

	public Point translate(Point dp) {
		return new Point(this.x+dp.x, this.y+dp.y);
		// vra�a NOVU to�ku translatiranu za argument tj. THIS+DP...
	}
	
	public Point difference(Point p) {
		return new Point(this.x-p.x, this.y-p.y);
		// vra�a NOVU to�ku koja predstavlja razliku THIS-P...
	}
}
