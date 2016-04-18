import java.awt.Color;
import java.awt.Polygon;


public class CheckersDrawing {
	
	String type;
	String id;
	int xLoc;
	int yLoc;
	double[] x;
	double[] y;
	Color c;
	
	CheckersDrawing(String type, String id, Color c) {
		this.type = type;
		this.id = id;
		this.c = c;
		
		if(type == "Crown") {
			this.x = new double[]{1, 3, 5, 7, 9, 9, 1};
	        this.y = new double[]{1, 3, 1, 3, 1, 9, 9};
		} else if(type == "Remove") {
			this.x = new double[]{.293, .707, 1, 1, .707, .293, 0, 0};
			this.y = new double[]{0, 0, .293, .707, 1, 1, .707, .293};
        }
	}
	
	CheckersDrawing(CheckersDrawing cd) {
		this.type = cd.type;
		this.id = cd.id;
		this.c = cd.c;
		this.xLoc = cd.xLoc;
		this.yLoc = cd.yLoc;
		this.x = new double[cd.x.length];
		System.arraycopy(cd.x, 0, this.x, 0, this.x.length);
		this.y = new double[cd.y.length];
		System.arraycopy(cd.y, 0, this.y, 0, y.length);
	}
	
	public void setXLoc(int x) {
		xLoc = x;
	}
	
	public void setYLoc(int y) {
		yLoc = y;
	}
	
	public void setXYLoc(int x, int y) {
		xLoc = x;
		yLoc = y;
	}
	
	public void setXArray(double[] x){
		this.x = new double[x.length];
		System.arraycopy(x, 0, this.x, 0, x.length);
	}
	
	public void setYArray(double[] y){
		this.y = new double[y.length];
		System.arraycopy(y, 0, this.y, 0, y.length);
	}
	
	public void setXYArray(double[] x, double[] y){
		this.x = new double[x.length];
		System.arraycopy(x, 0, this.x, 0, x.length);
		
		this.y = new double[y.length];
		System.arraycopy(y, 0, this.y, 0, y.length);
	}
	
	public Polygon getPoly(int boardSize, int boardLocation){
		Polygon poly;
		if(type == "Crown"){
			int[] tempX = new int[x.length];
	        int[] tempY = new int[y.length];
	        
	        for(int i=0; i<x.length; i++){
	        	tempX[i] = (int) (x[i]/10.0*(boardSize/8.0)+xLoc*(boardSize/8.0) + boardLocation);
	        	tempY[i] = (int) (y[i]/10.0*(boardSize/8.0)+yLoc*(boardSize/8.0));
	        }
			poly = new Polygon(tempX, tempY, tempX.length);
        } else if(type == "Remove") {
			int[] tempX = new int[x.length];
	        int[] tempY = new int[y.length];
            
            for(int i=0; i<x.length; i++){
	        	tempX[i] = (int) (x[i]*(boardSize/8.0)+xLoc*(boardSize/8.0) + boardLocation);
	        	tempY[i] = (int) (y[i]*(boardSize/8.0)+yLoc*(boardSize/8.0));
	        }
			poly = new Polygon(tempX, tempY, tempX.length);
		} else if (type == "Path") {
			double[] octogonX = {.293, .707, 1, 1, .707, .293, 0, 0};
			double[] octogonY = {0, 0, .293, .707, 1, 1, .707, .293};
			int ind = 0;
			int[] tempX = new int[x.length*8];
	        int[] tempY = new int[y.length*8];
	        int counter = 0;
	        int switchPoint;
	        //System.out.println("xLength: " + x.length);
			for(int i=0; i<x.length; i++) {
				//System.out.println("i: " + i + " xLength: " + x.length);
				if(i==x.length-1){
					//Last point
					switchPoint = ind;
					tempX[counter] = (int) (octogonX[ind]*0.9*(boardSize/8.0)+x[i]*(boardSize/8.0)+boardLocation+0.05*(boardSize/8.0));
					tempY[counter] = (int) (octogonY[ind]*0.9*(boardSize/8.0)+y[i]*(boardSize/8.0)+0.05*(boardSize/8.0));
					ind = (ind+1)%8;
					counter++;
					while(ind!=switchPoint) {
						
						tempX[counter] = (int) (octogonX[ind]*0.9*(boardSize/8.0)+x[i]*(boardSize/8.0)+boardLocation+0.05*(boardSize/8.0));
						tempY[counter] = (int) (octogonY[ind]*0.9*(boardSize/8.0)+y[i]*(boardSize/8.0)+0.05*(boardSize/8.0));
						//System.out.println(counter + ": 1 : " + tempX[counter]);
						ind = (ind+1)%8;
						counter++;
					}
				} else {
					//Calculate next point direction
					
					if(x[i]>x[i+1]) {
						if(y[i]>y[i+1]) {
							// Bottom Left
							switchPoint = 7;
						} else {
							// Top Left
							switchPoint = 5;
						}
					} else {
						if(y[i]>y[i+1]) {
							// Bottom Right
							switchPoint = 1;
						} else {
							// Top Right
							switchPoint = 3;
						}
					}
					
					do{
						
						tempX[counter] = (int) (octogonX[ind]*0.9*(boardSize/8.0)+x[i]*(boardSize/8.0)+boardLocation+0.05*(boardSize/8.0));
						tempY[counter] = (int) (octogonY[ind]*0.9*(boardSize/8.0)+y[i]*(boardSize/8.0)+0.05*(boardSize/8.0));
						//System.out.println(counter + ": 2 : " + tempX[counter]);
						ind = (ind+1)%8;
						counter++;
					}while(ind!=(switchPoint+1)%8);
					ind = (switchPoint+5)%8;
				}
			}
			//Backwards loop
			for(int i=x.length-1; i>-1; i--) {
				if(i==0){
					//First point
					while(ind!=0) {
						
						tempX[counter] = (int) (octogonX[ind]*0.9*(boardSize/8.0)+x[i]*(boardSize/8.0)+boardLocation+0.05*(boardSize/8.0));
						tempY[counter] = (int) (octogonY[ind]*0.9*(boardSize/8.0)+y[i]*(boardSize/8.0)+0.05*(boardSize/8.0));
						//System.out.println(counter + ": 3 : " + tempX[counter]);
						ind = (ind+1)%8;
						counter++;
					}
				} else {
					//Calculate next point direction
					
					if(x[i]>x[i-1]) {
						if(y[i]>y[i-1]) {
							// Bottom Left
							switchPoint = 7;
						} else {
							// Top Left
							switchPoint = 5;
						}
					} else {
						if(y[i]>y[i-1]) {
							// Bottom Right
							switchPoint = 1;
						} else {
							// Top Right
							switchPoint = 3;
						}
					}
					while(ind!=(switchPoint+1)%8){
						tempX[counter] = (int) (octogonX[ind]*0.9*(boardSize/8.0)+x[i]*(boardSize/8.0)+boardLocation+0.05*(boardSize/8.0));
						tempY[counter] = (int) (octogonY[ind]*0.9*(boardSize/8.0)+y[i]*(boardSize/8.0)+0.05*(boardSize/8.0));
						//System.out.println(counter + ": 4 : " + tempX[counter]);
						ind = (ind+1)%8;
						counter++;
					}
					ind = (switchPoint+5)%8;
				}
			}
//			for(int i=0; i<tempX.length; i++) {
//				System.out.println(tempX[i]);
//			}
			poly = new Polygon(tempX, tempY, tempX.length);
		} else {
			int[] tempX = new int[x.length];
	        int[] tempY = new int[y.length];
	        for(int i=0; i<x.length; i++){
	        	tempX[i] = (int) (x[i]*boardSize + boardLocation);
	        	tempY[i] = (int) (y[i]*boardSize);
	        }
			poly = new Polygon(tempX, tempY, tempX.length);
		}
		
		return poly;
	}
}
