package planner.data;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

public class RoadMapLine {
	private Line2D.Double line;
	private int lineID;
	private Point2D.Double vector;
	private double angle;
	
	public RoadMapLine(int id, Line2D.Double l){
		this.lineID = id;
		this.line = l;
		double dist = Math.sqrt(l.x1*l.x2 + l.y1*l.y2);
		this.vector = new Point2D.Double((l.getP2().getX()-l.getP1().getX())/dist, 
				(l.getP2().getY()-l.getP1().getY())/dist);
		this.angle = Math.toDegrees(Math.atan2(vector.y, vector.x));
		while(angle < 0){
			angle += 360;
		}
		angle %= 360;
	}
	
	public Line2D.Double getLine() {
		return line;
	}
	
	public Point2D.Double getVector() {
		return vector;
	}

	public int getLineID() {
		return lineID;
	}
	
	public double getAngle() {
		return angle;
	}
	
}
