package ooup.lab4;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.*;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JToolBar;
import javax.swing.WindowConstants;

import ooup.lab4.graphicalObject.CompositeShape;
import ooup.lab4.graphicalObject.GraphicalObject;
import ooup.lab4.renderer.G2DRendererImpl;
import ooup.lab4.renderer.Renderer;
import ooup.lab4.renderer.SVGRendererImpl;
import ooup.lab4.state.AddShapeState;
import ooup.lab4.state.EraserState;
import ooup.lab4.state.IdleState;
import ooup.lab4.state.SelectedState;
import ooup.lab4.state.State;



public class GUI extends JFrame {
	private static final long serialVersionUID = 1L;
	private DocumentModel documentModel;
	private State currentState;
	private Map<String, GraphicalObject> prototypes;
	

	public GUI(List<GraphicalObject> objects) {
		documentModel = new DocumentModel();
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setSize(500,500);
		prototypes = new HashMap<>();
		
		initGui(objects);
		currentState = new IdleState();
		
//		documentModel.addGraphicalObject(new LineSegment(new Point(50,50), new Point(300,300)));
//		documentModel.addGraphicalObject(new LineSegment(new Point(50,200), new Point(400,100)));
//		documentModel.addGraphicalObject(new Oval(new Point(100,100), new Point(50,200)));
//		documentModel.addGraphicalObject(new Oval(new Point(350,350), new Point(250,400)));
				
	}
	
	private void initGui(List<GraphicalObject> objects) {
		Container cp = getContentPane();
		cp.setLayout(null);
		cp.setLayout(new BorderLayout(5,5));
		
		Canvas canvas = new Canvas(documentModel);
		cp.add(canvas, BorderLayout.CENTER);
		canvas.requestFocusInWindow();
		
		for(GraphicalObject obj: objects) {
			prototypes.put(obj.getShapeID(), obj);
		}
		CompositeShape composite = new CompositeShape();
		prototypes.put(composite.getShapeID(), composite);

		JToolBar toolBar = new JToolBar();
		
		JButton loadBtn = new JButton("Load");
		loadBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser();
				if (fileChooser.showOpenDialog(GUI.this) != JFileChooser.APPROVE_OPTION) {
				  return;
				}
				String fileName = fileChooser.getSelectedFile().getPath();
				List<String> fileLines = new ArrayList<>();
				Stack<GraphicalObject> stack = new Stack<>();
				documentModel.clear();
				
				try {
					fileLines = Files.readAllLines(new File(fileName).toPath(), Charset.defaultCharset());
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				
				for(String line: fileLines) {
					String id = line.substring(0,line.indexOf(" "));
					String data = line.substring(line.indexOf(" ")+1);
					GraphicalObject obj = prototypes.get(id);
					obj.duplicate().load(stack, data);
				}
				
				if(!stack.isEmpty()) {
					for(GraphicalObject obj: stack) {
						documentModel.addGraphicalObject(obj);
					}
				}

			}
		});
		toolBar.add(loadBtn);
		
		
		JButton saveBtn = new JButton("Save");
		saveBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser();
				if (fileChooser.showSaveDialog(GUI.this) != JFileChooser.APPROVE_OPTION) {
				  return;
				}
				String fileName = fileChooser.getSelectedFile().getPath();
				
				List<String> writeList = new ArrayList<>();
				for(GraphicalObject obj: documentModel.list()) 
					obj.save(writeList);
				
				try {
					FileWriter fw = new FileWriter(new File(fileName));
					for(String line: writeList)
						fw.write(line + "\n");
					fw.flush();
					fw.close();
					JOptionPane.showMessageDialog(GUI.this, "Save successful", "INFO", JOptionPane.INFORMATION_MESSAGE);
				} catch (IOException e1) {
					JOptionPane.showMessageDialog(GUI.this, "Save failed" + e1, "ERROR", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		toolBar.add(saveBtn);
		
		JButton SVGExportBtn = new JButton("SVG Export");
		SVGExportBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser();
				if (fileChooser.showSaveDialog(GUI.this) != JFileChooser.APPROVE_OPTION) {
				  return;
				}
				String fileName = fileChooser.getSelectedFile().getPath();
				SVGRendererImpl r = new SVGRendererImpl(fileName);
				for(GraphicalObject obj: documentModel.list())
					obj.render(r);
				
				try {
					r.close();
					JOptionPane.showMessageDialog(GUI.this, "SVG export successful", "INFO", JOptionPane.INFORMATION_MESSAGE);

				} catch (IOException e1) {
					JOptionPane.showMessageDialog(GUI.this, "SVG export failed" + e1, "ERROR", JOptionPane.ERROR_MESSAGE);
				}
				
				
			}
		});
		toolBar.add(SVGExportBtn);
		
		
		for(GraphicalObject obj: objects) {
			JButton jb = new JButton(obj.getShapeName());
			jb.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					currentState.onLeaving();
					currentState = new AddShapeState(documentModel, obj);
				}
				
			});
			toolBar.add(jb);
		}
		JButton selectionBtn = new JButton("Select");
		selectionBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				currentState.onLeaving();
				currentState = new SelectedState(documentModel);
			}
			
		});
		toolBar.add(selectionBtn);
		
		JButton eraseBtn = new JButton("Erase");
		eraseBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				currentState.onLeaving();
				currentState = new EraserState(documentModel);
			}
			
		});
		toolBar.add(eraseBtn);
		
		cp.add(toolBar, BorderLayout.NORTH);
//		toolBar.setFocusable(false);
		
		
		
		documentModel.addDocumentModelListener(new DocumentModelListener() {
			@Override
			public void documentChange() {
				canvas.repaint();				
			}
		});
	}


	public class Canvas extends JComponent{
		private static final long serialVersionUID = 1L;
		private DocumentModel documentModel;
		
		public Canvas(DocumentModel documentModel) {
			setFocusable(true);
			this.documentModel = documentModel;
			requestFocusInWindow();
			registerKeyListeners();
			registerMouseListeners();
		}
		

		@Override
		protected void paintComponent(Graphics g) {
			Graphics2D g2d = (Graphics2D)g;
			g2d.setColor(Color.WHITE);
			g2d.fillRect(0, 0, getWidth(), getHeight());
			
			Renderer r = new G2DRendererImpl(g2d);
			for(GraphicalObject obj: documentModel.list()) {
				obj.render(r);
				currentState.afterDraw(r, obj);
			}
			currentState.afterDraw(r);
		}
		
		private void registerMouseListeners() {
			addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					currentState.mouseDown(e.getPoint(), e.isShiftDown(), e.isControlDown());
				}
				
				@Override
				public void mouseReleased(MouseEvent e) {
					currentState.mouseUp(e.getPoint(), e.isShiftDown(), e.isControlDown());
				}
			});
			
			addMouseMotionListener(new MouseAdapter() {
				@Override
				public void mouseDragged(MouseEvent e) {
					currentState.mouseDragged(e.getPoint());
				}
			});
			
		}
		
		private void registerKeyListeners(){
			addKeyListener(new KeyAdapter() {
				@Override
				public void keyPressed(KeyEvent e) {
					switch (e.getKeyCode()) {
					case KeyEvent.VK_ESCAPE:
						currentState.onLeaving();
						currentState = new IdleState();
						e.consume();
						break;
					default:
						currentState.keyPressed(e.getKeyCode());
					}
				}
			});
		}
		
		
	}
}
					
