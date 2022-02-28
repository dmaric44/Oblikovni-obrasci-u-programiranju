package ooup.lab4.renderer;

import ooup.lab4.Point;

public interface Renderer {
	void drawLine(Point s, Point e);
	void fillPolygon(Point[] points);
}
