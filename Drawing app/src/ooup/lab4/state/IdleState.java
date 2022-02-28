package ooup.lab4.state;


import java.awt.Point;

import ooup.lab4.graphicalObject.GraphicalObject;
import ooup.lab4.renderer.Renderer;

public class IdleState implements State {

	@Override
	public void mouseDown(Point mousePoint, boolean shiftDown, boolean ctrlDown) {
	}

	@Override
	public void mouseUp(Point mousePoint, boolean shiftDown, boolean ctrlDown) {
	}

	@Override
	public void mouseDragged(Point mousePoint) {
	}

	@Override
	public void keyPressed(int keyCode) {
	}

	@Override
	public void afterDraw(Renderer r, GraphicalObject go) {
	}

	@Override
	public void afterDraw(Renderer r) {
	}

	@Override
	public void onLeaving() {
	}

}
