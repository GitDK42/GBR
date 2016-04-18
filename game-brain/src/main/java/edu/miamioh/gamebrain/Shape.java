import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Polygon;
import java.util.ArrayList;

import javax.swing.JPanel;


public class Shape extends JPanel{
	
	private static final int PANEL_WIDTH = 800;
	private static final int PANEL_HEIGHT = 800;
	
	private int[] xPoly;
	private int[] yPoly;
	private Polygon poly;
	private ArrayList<CheckersDrawing> items = new ArrayList<CheckersDrawing>();
	private int boardSize = 500;
	private int boardLoc = 150;
	
	public Shape() {
		
		//this.setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
		
//		int[] xPoly = new int[x.length];
//		int[] yPoly = new int[y.length];
//
//		System.arraycopy( x, 0, xPoly, 0, x.length );
//		System.arraycopy( y, 0, yPoly, 0, y.length );
//		poly = new Polygon(xPoly, yPoly, xPoly.length);
	}

	public Shape(int boardSize, int boardLoc) {
		this.boardSize = boardSize;
		this.boardLoc = boardLoc;
	}
	
	public void setXY(int[] x, int[] y) {
		int[] xPoly = new int[x.length];
		int[] yPoly = new int[y.length];

		System.arraycopy( x, 0, xPoly, 0, x.length );
		System.arraycopy( y, 0, yPoly, 0, y.length );
		poly = new Polygon(xPoly, yPoly, xPoly.length);
		
		repaint();
	}
	
	public void addObject(CheckersDrawing item){
		//CheckersDrawing temp = new CheckersDrawing(item);
		items.add(new CheckersDrawing(item));
		//System.out.println("here: "+items.size());
		repaint();
	}

    public void clear(){
        items.clear();
        repaint();
    }
	
	@Override
	 public void paintComponent(Graphics g){
		super.paintComponent(g);
		setOpaque(false);
//        g.setColor(Color.BLACK);
//        g.drawPolygon(poly);
//        
//        int xLoc=1, yLoc=7;
//        
//        int[] doubX = {1, 3, 5, 7, 9, 9, 1};
//        int[] doubY = {1, 3, 1, 3, 1, 9, 9};
//        int[] x = new int[doubX.length];
//        int[] y = new int[doubY.length];
//        for(int i=0; i<doubX.length; i++){
//        	x[i] = (int) (doubX[i]/10.0*(boardSize/8.0)+xLoc*(boardSize/8.0) + boardLoc);
//        	y[i] = (int) (doubY[i]/10.0*(boardSize/8.0)+yLoc*(boardSize/8.0) + boardLoc);
//        }
//        Polygon temp = new Polygon(x, y, x.length);
//        g.setColor(new Color(238, 213, 33, 175));
//        g.fillPolygon(temp);
		//System.out.println("thisone"+items.size());
		for(int i=0; i<items.size(); i++) {
			g.setColor(items.get(i).c);
			g.fillPolygon(items.get(i).getPoly(boardSize, boardLoc));
		}
	 }
	
//	@Override
//    public Dimension getPreferredSize() {
//        return new Dimension(800, 800);
//    }
	
}
