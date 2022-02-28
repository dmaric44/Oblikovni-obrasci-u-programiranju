package ooup.lab4.state;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

//import java.awt.Point;

import ooup.lab4.DocumentModel;
import ooup.lab4.Rectangle;
import ooup.lab4.graphicalObject.CompositeShape;
import ooup.lab4.graphicalObject.GraphicalObject;
import ooup.lab4.renderer.Renderer;
import ooup.lab4.Point;

public class SelectedState extends IdleState {
	private DocumentModel documentModel;
	private GraphicalObject selectedObj = null;
	private int indexOfHP=-1;

	public SelectedState(DocumentModel model) {
		this.documentModel = model;
	}
	
	@Override
	public void mouseDown(java.awt.Point mousePoint, boolean shiftDown, boolean ctrlDown) {
		GraphicalObject currObj = documentModel.findSelectedGraphicalObject(new Point((int)mousePoint.getX(), (int)mousePoint.getY()));
		if(currObj != null) {
			if(!ctrlDown) {
				documentModel.deselectAll();
				selectedObj = currObj;
				currObj.setSelected(!currObj.isSelected());
			}
			else {
				selectedObj = null;
				currObj.setSelected(!currObj.isSelected());
			}
		}
		else {
			selectedObj = null;
			documentModel.deselectAll();
		}
	}
	
	@Override
	public void mouseDragged(java.awt.Point mousePoint) {
		Point mp = new Point((int)mousePoint.getX(), (int)mousePoint.getY());
		if(selectedObj != null & indexOfHP>=0) {
			if(selectedObj.isHotPointSelected(indexOfHP))
				selectedObj.setHotPoint(indexOfHP, mp);
		}
		else if(selectedObj != null) {
			int index = documentModel.findSelectedHotPoint(selectedObj, mp);
			if(index >= 0) {
				selectedObj.setHotPointSelected(index, true);
				selectedObj.setHotPoint(index, mp);
				indexOfHP = index;
			}
		}
		documentModel.notifyListeners();

	}
	
	@Override
	public void mouseUp(java.awt.Point mousePoint, boolean shiftDown, boolean ctrlDown) {
		if(selectedObj != null & indexOfHP >= 0) {
			selectedObj.setHotPointSelected(indexOfHP, false);
			indexOfHP = -1;
		}
	}
	
	@Override
	public void keyPressed(int keyCode) {
		switch(keyCode) {
		case KeyEvent.VK_UP:
			for(GraphicalObject obj: documentModel.getSelectedObjects()) {
				obj.translate(new Point(0,-1));
			}
			break;
		case KeyEvent.VK_DOWN:
			for(GraphicalObject obj: documentModel.getSelectedObjects()) {
				obj.translate(new Point(0,1));
			}
			break;
		case KeyEvent.VK_LEFT:
			for(GraphicalObject obj: documentModel.getSelectedObjects()) {
				obj.translate(new Point(-1,0));
			}
			break;
		case KeyEvent.VK_RIGHT:
			for(GraphicalObject obj: documentModel.getSelectedObjects()) {
				obj.translate(new Point(1,0));
			}
			break;
		case KeyEvent.VK_PLUS:
			if(selectedObj != null) {
				documentModel.increaseZ(selectedObj);
			}
			break;
		case KeyEvent.VK_MINUS:
			if(selectedObj != null) {
				documentModel.decreaseZ(selectedObj);
			}
			break;
		case KeyEvent.VK_G:
			List<GraphicalObject> currentSelected = documentModel.getSelectedObjects();
			if(!currentSelected.isEmpty() && currentSelected.size() > 1) {
				List<GraphicalObject> newCompObjects = new ArrayList<>();
				for(GraphicalObject obj: currentSelected) {
					newCompObjects.add(obj);
				}
				CompositeShape composite = new CompositeShape(newCompObjects, true);
				documentModel.deselectAll();
				for(GraphicalObject obj: newCompObjects) {
					documentModel.removeGraphicalObject(obj);
				}
				documentModel.addGraphicalObject(composite);
			}
			break;
		case KeyEvent.VK_U:
			List<GraphicalObject> currentSelectedUngroup = new ArrayList<>(documentModel.getSelectedObjects());
			if(!currentSelectedUngroup.isEmpty() && currentSelectedUngroup.size()==1 && currentSelectedUngroup.get(0).getShapeName()=="Composite") {
				CompositeShape composite = (CompositeShape)currentSelectedUngroup.get(0);
				List<GraphicalObject> compositeObjects = new ArrayList<>(composite.getObjects());
				
				documentModel.removeGraphicalObject(composite);
				for(GraphicalObject obj: compositeObjects) {
					obj.setSelected(true);
					documentModel.addGraphicalObject(obj);
				}
			}
			break;
		}
	}
	
	
	@Override
	public void afterDraw(Renderer r, GraphicalObject go) {
		if(go.isSelected()) {
			Rectangle rect = go.getBoundingBox();
			r.drawLine(new Point(rect.getX(),rect.getY()), new Point(rect.getX()+rect.getWidth(), rect.getY()));
			r.drawLine(new Point(rect.getX(),rect.getY()), new Point(rect.getX(), rect.getY()+rect.getHeight()));
			r.drawLine(new Point(rect.getX()+rect.getWidth(), rect.getY()), new Point(rect.getX()+rect.getWidth(), rect.getY()+rect.getHeight()));
			r.drawLine(new Point(rect.getX(), rect.getY()+rect.getHeight()), new Point(rect.getX()+rect.getWidth(), rect.getY()+rect.getHeight()));
		}
		
		if(documentModel.getSelectedObjects().size() == 1 && go == selectedObj) {
			int height = 10;
			int width = 10;
			for(int i=0; i<go.getNumberOfHotPoints(); i++) {
				Point p = go.getHotPoint(i);
				int x = p.getX()-5;
				int y = p.getY()-5;
				
				r.drawLine(new Point(x,y), new Point(x+width,y));
				r.drawLine(new Point(x,y), new Point(x,y+height));
				r.drawLine(new Point(x,y+height), new Point(x+width,y+width));
				r.drawLine(new Point(x+width,y), new Point(x+width,y+width));
				
			}			
		}
	}
	
	@Override
	public void onLeaving() {
		documentModel.deselectAll();
	}
}
