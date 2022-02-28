package ooup.lab4.renderer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ooup.lab4.Point;

public class SVGRendererImpl implements Renderer {
	private List<String> lines = new ArrayList<>();
	private String fileName;
	
	
	public SVGRendererImpl(String fileName) {
		this.fileName = fileName;
		String header = "<svg xmlns=\"http://www.w3.org/2000/svg\">";
		lines.add(header);
	}

	public void close() throws IOException {
		lines.add("</svg>");
		
		FileWriter fw = new FileWriter(new File(fileName));
		for(String line: lines) {
			fw.write(line);
		}
		
		fw.flush();
		fw.close();
	}
	
	@Override
	public void drawLine(Point s, Point e) {
		String line = String.format("<line x1=\"%d\" y1=\"%d\" x2=\"%d\" y2=\"%d\" style=\"stroke:rgb(0,0,255); \" />  ",
				s.getX(), s.getY(), e.getX(), e.getY());
		lines.add(line);
	}

	@Override
	public void fillPolygon(Point[] points) {
		StringBuilder sb = new StringBuilder();
		sb.append("<polygon points=\"");
		for(Point p: points) {
			sb.append(p.getX()).append(",").append(p.getY()).append(" ");
		}
		sb.deleteCharAt(sb.length()-1);
		sb.append("\" style=\"stroke:rgb(255,0,0); fill:rgb(0,0,255);\" />");
		lines.add(sb.toString());
	}

}
