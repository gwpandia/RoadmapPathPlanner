package planner.utils;

import java.awt.Point;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import planner.data.Configuration;
import planner.data.Constant;

public class CoordinateTrans{
	public static Point2D.Double [] toPlannerPoint(Configuration c, Point2D.Double [] p, double swp){
		Point2D.Double [] ret = new Point2D.Double[p.length];
		for(int i = 0; i < p.length; i++){
			double x = (((Math.cos(toRadian(c.getAngle()))* p[i].x - 
					Math.sin(toRadian(c.getAngle()))* p[i].y)*swp) + (c.getX()*swp));
			double y = ((((Math.sin(toRadian(c.getAngle()))* p[i].x +
					Math.cos(toRadian(c.getAngle()))*p[i].y)*swp)) + (c.getY()*swp));
			ret[i] = new Point2D.Double(x,y);
		}
		return ret;
	}
	
	public static Point [] toCanvasPoint(Configuration c, Point2D.Double [] p, double swp, double spc){
		Point [] ret = new Point[p.length];
		for(int i = 0; i < p.length; i++){
			int x = (int)((Math.cos(toRadian(c.getAngle()))* p[i].x - 
					Math.sin(toRadian(c.getAngle()))* p[i].y)*swp*spc) + ((int)(c.getX()*swp*spc));
			int y = (-1*(int)((Math.sin(toRadian(c.getAngle()))* p[i].x +
					Math.cos(toRadian(c.getAngle()))*p[i].y)*swp*spc)) + (Constant.HC-(int)(c.getY()*swp*spc));
			ret[i] = new Point(x,y);
		}
		return ret;
	}
	
	public static Ellipse2D.Double toPlannerCircle(Ellipse2D.Double circle, Configuration config){
		if(circle != null){
			double circleX, circleY, width , height;
			circleX = circle.x*Constant.SWP + config.getX() * Constant.SWP;;
			circleY = circle.y*Constant.SWP + config.getY() * Constant.SWP;
			width = circle.width * Constant.SWP;
			height = circle.height * Constant.SWP;
			return new Ellipse2D.Double(circleX,circleY,width, height);
		}else{
			return null;
		}
	}
	
	public static Ellipse2D.Double toCanvasCircle(Ellipse2D.Double circle, Configuration config){
		if(circle != null){
			double circleX, circleY, width , height;
			circleX = circle.x*Constant.SWP*Constant.SPC + config.getX() * Constant.SWP * Constant.SPC;
			circleY = circle.y*Constant.SWP*Constant.SPC + Constant.HC-config.getY() * Constant.SWP * Constant.SPC;
			width = circle.width * Constant.SWP * Constant.SPC;
			height = circle.height * Constant.SWP * Constant.SPC;
			return new Ellipse2D.Double(circleX,circleY,width, height);
		}else{
			return null;
		}
	}
	
	public static Arc2D.Double toPlannerArc(Arc2D.Double arc, Configuration config){
		if(arc != null){
			double x = arc.x * Constant.SWP + config.getX() * Constant.SWP;
			double y = arc.y * Constant.SWP + config.getY() * Constant.SWP;
			double width = arc.width * Constant.SWP;
			double height = arc.height * Constant.SWP;
			double angle = -config.getAngle() - (arc.extent/2.0);
			return new Arc2D.Double(x,y,width,height,angle,arc.extent,Arc2D.PIE);
		}else{
			return null;
		}
	}
	
	public static Arc2D.Double toCanvasArc(Arc2D.Double arc, Configuration config){
		if(arc != null){
			double x = arc.x * Constant.SWP * Constant.SPC + config.getX() * Constant.SWP * Constant.SPC;
			double y = arc.y * Constant.SWP * Constant.SPC + Constant.HC-config.getY() * Constant.SWP * Constant.SPC;
			double width = arc.width * Constant.SWP * Constant.SPC;
			double height = arc.height * Constant.SWP * Constant.SPC;
			double angle = config.getAngle() - (arc.extent/2.0);
			return new Arc2D.Double(x,y,width,height,angle,arc.extent,Arc2D.PIE);
		}else{
			return null;
		}
	}
	
	private static double toRadian(double a){
		return (a/360.0)*2*Math.PI;
	}
}