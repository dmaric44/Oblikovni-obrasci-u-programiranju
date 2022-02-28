package ooup.lab4.state;

import java.awt.Point;

import ooup.lab4.graphicalObject.GraphicalObject;
import ooup.lab4.renderer.Renderer;

public interface State {
	// poziva se kad progam registrira da je pritisnuta lijeva tipka miša
	void mouseDown(Point mousePoint, boolean shiftDown, boolean ctrlDown);
	
	// poziva se kad progam registrira da je otpuštena lijeva tipka miša
	void mouseUp(Point mousePoint, boolean shiftDown, boolean ctrlDown);
	
	// poziva se kad progam registrira da korisnik pomièe miš dok je tipka pritisnuta
	void mouseDragged(Point mousePoint);
	
	// poziva se kad progam registrira da je korisnik pritisnuo tipku na tipkovnici
	void keyPressed(int keyCode);

	// Poziva se nakon što je platno nacrtalo grafièki objekt predan kao argument
	void afterDraw(Renderer r, GraphicalObject go);
	
	// Poziva se nakon što je platno nacrtalo èitav crtež
	void afterDraw(Renderer r);

	// Poziva se kada program napušta ovo stanje kako bi prešlo u neko drugo
	void onLeaving();
}
