package ooup.lab4.state;

import java.awt.Point;

import ooup.lab4.DocumentModel;
import ooup.lab4.graphicalObject.GraphicalObject;

public class AddShapeState extends IdleState {
	
	private GraphicalObject prototype;
	private DocumentModel model;
	
	public AddShapeState(DocumentModel model, GraphicalObject prototype) {
		this.model = model;
		this.prototype = prototype;
	}

	@Override
	public void mouseDown(Point mousePoint, boolean shiftDown, boolean ctrlDown) {
		GraphicalObject addNew = prototype.duplicate();
		addNew.translate(new ooup.lab4.Point((int)mousePoint.getX(), (int)mousePoint.getY()));
		model.addGraphicalObject(addNew);
	}

	

}
