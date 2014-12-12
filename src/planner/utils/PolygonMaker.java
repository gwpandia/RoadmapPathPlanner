package planner.utils;

import java.awt.Point;
import java.awt.Polygon;
import java.awt.geom.Point2D;
import planner.data.MyPolygon;

public class PolygonMaker {
	
	public static MyPolygon toMyPolygon(Point2D.Double [] p){
		MyPolygon poly;
		double [] x = new double[p.length];
		double [] y = new double[p.length];
		for(int i = 0; i < p.length; i++){
			x[i] = p[i].x;
			y[i] = p[i].y;
		}
		poly = new MyPolygon(x, y, x.length);
		return poly;
	}
	
	public static Polygon toPolygon(Point [] p){
		Polygon poly;
		int [] x = new int[p.length];
		int [] y = new int[p.length];
		for(int i = 0; i < p.length; i++){
			x[i] = p[i].x;
			y[i] = p[i].y;
		}
		poly = new Polygon(x, y, x.length);
		return poly;
	}
}
