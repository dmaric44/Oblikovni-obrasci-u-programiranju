package ooup.lab4;

public class GeometryUtil {

	public static double distanceFromPoint(Point point1, Point point2) {
		int dx = point1.getX()-point2.getX();
		int dy = point1.getY()-point2.getY();
		return Math.sqrt(dx*dx + dy*dy);
	}
	
	public static double distanceFromLineSegment(Point s, Point e, Point p) {
		if(p.getX() <= s.getX()) {
			return Math.min(distanceFromPoint(p, s), distanceFromPoint(p, e));
		}
		else if(p.getX() >= e.getX()){
			return Math.min(distanceFromPoint(p, s), distanceFromPoint(p, e));
		}
		else {
			int A = s.getY() - e.getY();
			int B = s.getX() - e.getX();
			int C = s.getX()*e.getY() - e.getX()*s.getY();
			
			return Math.abs(A*p.getX() - B*p.getY() + C) / Math.sqrt(A*A + B*B);
		}
	}
}
