package planner.utils;

import java.awt.Point;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import planner.data.*;

public class SimplePlanner {
	private SimpleModel sm;
	private int [][] bitmap;
	private ArrayList<Point2D.Double> randompoint;
	private ArrayList<MyPolygon> allpoly;
	private ArrayList<Line2D.Double> allline;
	private int npoint;
	private int length;
	
	public SimplePlanner(SimpleModel sm, int npoint, int len){
		this.sm = sm;
		bitmap = new int[Constant.BITMAPSIZE][Constant.BITMAPSIZE];
		randompoint = new ArrayList<Point2D.Double>();
		allpoly = new ArrayList<MyPolygon>();
		allline = new ArrayList<Line2D.Double>();
		this.npoint = npoint;
		this.length = len;
	}
	
	public ArrayList<Line2D.Double> getAllline() {
		return allline;
	}

	public void generateRoadmap(){
		allline.clear();
		randompoint.clear();
		allpoly.clear();
		initialBitMap();
		drawObstacle();
		while(randompoint.size() < npoint){
			randomPoint();
			//randomPoint();
		}
		
		generateLine();
		System.out.println(allline.size());
	}
	
	private void generateLine(){
		for(int i = 0; i < randompoint.size(); i++){
			for(int j = i; j < randompoint.size(); j++){
				if(!randompoint.get(i).equals(randompoint.get(j)) && 
						randompoint.get(i).distance(randompoint.get(j)) <= length){
					Line2D.Double line = new Line2D.Double(randompoint.get(i), randompoint.get(j));
					int k = 0;
					for(MyPolygon poly : allpoly){
						if(isCollision(poly, line)==Constant.FREE && 
								!poly.contains(randompoint.get(i)) && 
								!poly.contains(randompoint.get(j))){
							++k;
						}
					}
					if(k==allpoly.size()){
						allline.add(line);
					}
				}
			}
		}
	}
	
	public int isCollision(MyPolygon curblock, Line2D.Double testline){
		for(int i = 0; i < curblock.npoints; i++){
			int s = ( i + 1 ) % curblock.npoints;
			Point2D.Double p1 = new Point2D.Double(curblock.xpoints[i], curblock.ypoints[i]);
			Point2D.Double p2 = new Point2D.Double(curblock.xpoints[s], curblock.ypoints[s]);
			Line2D.Double curline = new Line2D.Double(p1, p2);
			if(curline.intersectsLine(testline)){
				return Constant.COLLISION;
			}
		}
		return Constant.FREE;
	}
	
	private void randomPoint(){
		int x = adjustRange((int)Math.round(new Double(Math.random()*Constant.BITMAPSIZE)), 0, Constant.BITMAPSIZE);
		int y = adjustRange((int)Math.round(new Double(Math.random()*Constant.BITMAPSIZE)), 0, Constant.BITMAPSIZE);
		if(bitmap[x][y] == Constant.INIT){
			randompoint.add(new Point2D.Double(x,y));
			bitmap[x][y] = Constant.POINTED;
		}
	}
	
	private void initialBitMap(){
		for(int i = 0; i < Constant.BITMAPSIZE; i++){
			for(int j = 0; j < Constant.BITMAPSIZE; j++){
				bitmap[i][j] = Constant.INIT;
			}
		}
	}
	
	private void drawObstacle(){
		Obstacle [] obstacles = sm.getObstacles();
		for(int i = 0; i < obstacles.length; i++){
			MyPolygon [] poly = obstacles[i].getPolygons();
			for(int j = 0; j < poly.length; j++){
				MyPolygon p = PolygonMaker.toMyPolygon(CoordinateTrans.toPlannerPoint(obstacles[i].getInitc(), poly[j].getPoints(), Constant.SWP));
				allpoly.add(p);
				Rectangle2D.Double bound = p.getBounds2DDouble();
				int xMaxInt = new Double(Math.floor(bound.getMaxX()+0.5)).intValue();
				int xMinInt = new Double(Math.floor(bound.getMinX()+0.5)).intValue();
				xMaxInt = adjustRange(xMaxInt, 0, Constant.BITMAPSIZE);
				xMinInt = adjustRange(xMinInt, 0, Constant.BITMAPSIZE);
				int size = xMaxInt - xMinInt+1;
				if(size == 0){
					size = 1;
				}
				int [] YMIN = new int[size];
				int [] YMAX = new int[size];
				for(int a = 0; a < size;a++){
					YMAX[a] = -1;
					YMIN[a] = 1000;
				}
				for(int k = 0; k < p.npoints; k++){
					int s = ( k + 1 ) % p.npoints;
					double d = Math.max(Math.abs(p.xpoints[s]-p.xpoints[k]), Math.abs(p.ypoints[s]-p.ypoints[k]));
					double dx = ((p.xpoints[s]-p.xpoints[k])/d);
					double dy = ((p.ypoints[s]-p.ypoints[k])/d);
					for(int l = 0; l < d; l++){
						int xi = new Double(p.xpoints[k]+(l*dx)).intValue();
						int yi = new Double(p.ypoints[k]+(l*dy)).intValue();
						xi = adjustRange(xi, xMinInt, xMaxInt);
						yi = adjustRange(yi, 0, Constant.BITMAPSIZE);
						int xPos = xi - xMinInt;
						xPos = adjustRange(xPos, 0, size);
						if(yi >= YMAX[xPos]){
							YMAX[xPos] = yi;
						}
						if(yi <= YMIN[xPos]){
							YMIN[xPos] = yi;
						}
					}
				}
				for(int a = xMinInt; a < xMaxInt; a++){
					for(int b = YMIN[a-xMinInt]; b <= YMAX[a-xMinInt]; b++){
							bitmap[a][b] = Constant.OBST;
					}
				}
			}
		}
	}
	
	private int adjustRange(int test, int min, int max){
		if(test >= max){
			test = max - 1;
		}
		if(test < min){
			test = min;
		}
		return test;
	}
	
}
