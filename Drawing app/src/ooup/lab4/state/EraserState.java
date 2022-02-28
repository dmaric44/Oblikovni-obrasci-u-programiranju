package ooup.lab4.state;

import ooup.lab4.Point;
import java.util.*;

import ooup.lab4.DocumentModel;
import ooup.lab4.graphicalObject.GraphicalObject;
import ooup.lab4.renderer.Renderer;

public class EraserState extends IdleState {
	DocumentModel documentModel;
	List<Point> mousePoints;
	
	public EraserState(DocumentModel documentModel) {
		this.documentModel = documentModel;
		this.mousePoints = new ArrayList<>();
	}
	

	@Override
	public void mouseUp(java.awt.Point mousePoint, boolean shiftDown, boolean ctrlDown) {
		Set<GraphicalObject> selectedObjects = new HashSet<>();
		for(Point p: mousePoints) {
			GraphicalObject current = documentModel.findSelectedGraphicalObject(p);
			if(current != null)
				selectedObjects.add(current);
		}
		
		for(GraphicalObject obj: selectedObjects) {
			documentModel.removeGraphicalObject(obj);
		}
			
		mousePoints.clear();
		documentModel.notifyListeners();
	}

	@Override
	public void mouseDragged(java.awt.Point mousePoint) {
		mousePoints.add(new Point((int)mousePoint.getX(), (int)mousePoint.getY()));
		documentModel.notifyListeners();
	}
	
	@Override
	public void afterDraw(Renderer r) {
		if(mousePoints.size()>1) {
			for(int i=0; i<mousePoints.size()-2; i++) {
				r.drawLine(mousePoints.get(i), mousePoints.get(i+1));
			}
		}
	}
}
