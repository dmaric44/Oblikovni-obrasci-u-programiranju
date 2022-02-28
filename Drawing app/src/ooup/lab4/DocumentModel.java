package ooup.lab4;

import java.util.*;

import ooup.lab4.graphicalObject.GraphicalObject;
import ooup.lab4.graphicalObject.GraphicalObjectListener;

public class DocumentModel {
	public final static double SELECTION_PROXIMITY = 10;

	// Kolekcija svih grafièkih objekata:
	private List<GraphicalObject> objects = new ArrayList<>();
	// Read-Only proxy oko kolekcije grafièkih objekata:
	private List<GraphicalObject> roObjects = Collections.unmodifiableList(objects);
	// Kolekcija prijavljenih promatraèa:
	private List<DocumentModelListener> listeners = new ArrayList<>();
	// Kolekcija selektiranih objekata:
	private List<GraphicalObject> selectedObjects = new ArrayList<>();
	// Read-Only proxy oko kolekcije selektiranih objekata:
	private List<GraphicalObject> roSelectedObjects = Collections.unmodifiableList(selectedObjects);

	// Promatraè koji æe biti registriran nad svim objektima crteža...
	private final GraphicalObjectListener goListener = new GraphicalObjectListener() {
		@Override
		public void graphicalObjectChanged(GraphicalObject go) {
			notifyListeners();
		}

		@Override
		public void graphicalObjectSelectionChanged(GraphicalObject go) {
			if(objects.contains(go) ) {
				if(go.isSelected() && !selectedObjects.contains(go)) {
					selectedObjects.add(go);
				}
				else {
					selectedObjects.remove(go);
				}
			}
			notifyListeners();
		}
	};
	
	public DocumentModel() {
		objects = new ArrayList<GraphicalObject>();
		roObjects = Collections.unmodifiableList(objects);
		listeners = new ArrayList<DocumentModelListener>();
		selectedObjects = new ArrayList<GraphicalObject>();
		roSelectedObjects = Collections.unmodifiableList(selectedObjects);
	}

	public void clear() {
		objects.forEach(obj -> obj.removeGraphicalObjectListener(goListener));
		objects.clear();
		selectedObjects.clear();
		notifyListeners();
	}

	// Dodavanje objekta u dokument (pazite je li veæ selektiran; registrirajte model kao promatraèa)
	public void addGraphicalObject(GraphicalObject obj) {
		if(obj.isSelected()) {
			selectedObjects.add(obj);
		}
		objects.add(obj);
		obj.addGraphicalObjectListener(goListener);
		notifyListeners();
	}
	
	// Uklanjanje objekta iz dokumenta (pazite je li veæ selektiran; odregistrirajte model kao promatraèa)
	public void removeGraphicalObject(GraphicalObject obj) {
		if(obj.isSelected())
			selectedObjects.remove(obj);
		objects.remove(obj);
		obj.removeGraphicalObjectListener(goListener);
		notifyListeners();
	}

	// Vrati nepromjenjivu listu postojeæih objekata (izmjene smiju iæi samo kroz metode modela)
	public List<GraphicalObject> list() {
		return roObjects;
	}
	
	
	public void deselectAll() {
		while(selectedObjects.size() > 0) {
			selectedObjects.get(0).setSelected(false);
		}
	}
	

	// Prijava...
	public void addDocumentModelListener(DocumentModelListener l) {
		listeners.add(l);
	}
	
	// Odjava...
	public void removeDocumentModelListener(DocumentModelListener l) {
		listeners.remove(l);
	}

	// Obavještavanje...
	public void notifyListeners() {
		listeners.forEach(dc -> dc.documentChange());
	}
	
	// Vrati nepromjenjivu listu selektiranih objekata
	public List<GraphicalObject> getSelectedObjects() {
		return roSelectedObjects;
	}

	// Pomakni predani objekt u listi objekata na jedno mjesto kasnije...
	// Time æe se on iscrtati kasnije (pa æe time možda veæi dio biti vidljiv)
	public void increaseZ(GraphicalObject go) {
		int index = -1;
		for(int i=0; i<objects.size(); i++) {
			if(objects.get(i).equals(go)) {
				index = i;
				break;
			}
		}
		if(index >= 0 & index < objects.size()-1) {
			GraphicalObject next = objects.get(index+1);
			objects.set(index, next);
			objects.set(index+1, go);
			notifyListeners();
		}
	}
	
	// Pomakni predani objekt u listi objekata na jedno mjesto ranije...
	public void decreaseZ(GraphicalObject go) {
		int index = -1;
		for(int i=0; i<objects.size(); i++) {
			if(objects.get(i).equals(go)) {
				index = i;
				break;
			}
		}
		if(index>0 & index < objects.size()) {
			GraphicalObject prev = objects.get(index-1);
			objects.set(index, prev);
			objects.set(index-1, go);
			notifyListeners();
		}
	}
	
	// Pronaði postoji li u modelu neki objekt koji klik na toèku koja je
	// predana kao argument selektira i vrati ga ili vrati null. Toèka selektira
	// objekt kojemu je najbliža uz uvjet da ta udaljenost nije veæa od
	// SELECTION_PROXIMITY. Status selektiranosti objekta ova metoda NE dira.
	public GraphicalObject findSelectedGraphicalObject(Point mousePoint) {
		if(!objects.isEmpty()) {
			GraphicalObject go = objects.get(0);
			double min = go.selectionDistance(mousePoint);
			for (GraphicalObject obj: objects) {
				double dist = obj.selectionDistance(mousePoint);
				if(dist < min) {
					min = dist;
					go = obj;
				}
			}
			if(min <= SELECTION_PROXIMITY)
				return go;
		}
		return null;
	}

	// Pronaði da li u predanom objektu predana toèka miša selektira neki hot-point.
	// Toèka miša selektira onaj hot-point objekta kojemu je najbliža uz uvjet da ta
	// udaljenost nije veæa od SELECTION_PROXIMITY. Vraæa se indeks hot-pointa 
	// kojeg bi predana toèka selektirala ili -1 ako takve nema. Status selekcije 
	// se pri tome NE dira.
	public int findSelectedHotPoint(GraphicalObject object, Point mousePoint) {
		for(int i=0; i<object.getNumberOfHotPoints(); i++) {
			if(object.getHotPointDistance(i, mousePoint) < SELECTION_PROXIMITY) {
				return i;
			}
		}
		return -1;
	}

}
