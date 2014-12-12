package planner.data;

import java.awt.Rectangle;
import java.awt.geom.*;
import java.util.ArrayList;
import java.util.Iterator;

import planner.utils.CoordinateTrans;

public class Agent {
	private Ellipse2D.Double body;
	private Arc2D.Double viewfield;
	private double velocity;
	//private double faceto;
	private Configuration currentConfig;
	private Configuration goalConfig;
	private Configuration initConfig;
	private double bodySize;
	private double viewDistance;
	private double viewAngle;
	private AgentGroup agentGroup;
	private RoadMapModel rmm;
	private ArrayList<Integer> path;
	private int curGoalNodeIndex;
	private int curNodeIndex;
	private static double IS_P1 = 1, IS_P2 = -1, NONE_P = 0;
	public boolean hasPath;
	private int agentID;
	private double desiredVelocity;
	//private boolean isGoal;
	
	public Agent(int id, double v, double b, double al, double vd, AgentGroup a, RoadMapModel rmm){
		agentID = id;
		bodySize = b;
		viewAngle = al;
		viewDistance = vd;
		body = new Ellipse2D.Double(0 - (bodySize / 2.0), 0 - (bodySize / 2.0), bodySize, bodySize);
		viewfield = new Arc2D.Double(0 - viewDistance, 0 - viewDistance, 
				viewDistance * 2, viewDistance * 2, -(viewAngle / 2.0), viewAngle, Arc2D.PIE);
		velocity = v;
		desiredVelocity = v;
		agentGroup = a;
		this.rmm = rmm;
		Point2D.Double point = this.rmm.getAllNode().get(agentGroup.getInitNode()).getPoint();
		initConfig = new Configuration(point.x, point.y, 0);
		currentConfig = new Configuration(point.x, point.y, 0);
		point = this.rmm.getAllNode().get(agentGroup.getGoalNode()).getPoint();
		goalConfig = new Configuration(point.x, point.y, 0);
		path = new ArrayList<Integer>();
		curGoalNodeIndex = 1;
		curNodeIndex = 0;
		//System.out.printf("%f %f\n", initConfig.getX(), initConfig.getY());
		//System.out.printf("%f %f\n\n\n", goalConfig.getX(), goalConfig.getY());
		hasPath = genPath(agentGroup.getInitNode(), agentGroup.getGoalNode());
		//isGoal = false;
	}
	/*
	public void run(){
		if(!genPath(agentGroup.getInitNode(), agentGroup.getGoalNode())){
			return;
		}
		System.out.printf("%f %f\n", rmm.getAllNode().get((path.get(0))).getPoint().x, rmm.getAllNode().get((path.get(0))).getPoint().y);
		System.out.printf("%f %f\n", rmm.getAllNode().get((path.get(path.size()-1))).getPoint().x, rmm.getAllNode().get((path.get(path.size()-1))).getPoint().y);
		while(curNodeIndex < path.size()){
			rmm.initOccurMap();
			onePhase();
		}
	}*/
	
	public double normalizeAngle(double a){
		while(a < 0){
			a += 360.0;
		}
		a %= 360.0;
		return a;
	}
	
	public void setVelocity(double velocity) {
		this.desiredVelocity = velocity;
	}

	public void setBodySize(double bodySize) {
		this.bodySize = bodySize;
	}

	public void setViewDistance(double viewDistance) {
		this.viewDistance = viewDistance;
	}

	public void setViewAngle(double viewAngle) {
		this.viewAngle = viewAngle;
	}

	public boolean isGoal(){
		return curNodeIndex >= path.size();
	}
	
	private int getCurNodeIndex(){
		return curNodeIndex;
	}
	
	public void onePhase(){
		if(curNodeIndex >= path.size()-1){
			++curNodeIndex;
			currentConfig.setX(goalConfig.getX());
			currentConfig.setY(goalConfig.getY());
			//currentConfig.setAngle(goalConfig.getAngle());
			rmm.occurmap[(int)Math.round(currentConfig.getX())][(int)Math.round(currentConfig.getY())] = Constant.OCCUPIED;
			//System.out.printf("E1ID: %d Config: %2.2f %2.2f %2.2f\n", agentID, currentConfig.getX(), currentConfig.getY(), currentConfig.getAngle());
			return;
		}
		checkCurNodeState();
		if(curGoalNodeIndex >= path.size() || curNodeIndex >= path.size()){
			rmm.occurmap[(int)currentConfig.getX()][(int)currentConfig.getY()] = Constant.OCCUPIED;
			//System.out.printf("E2ID: %d Config: %2.2f %2.2f %2.2f\n", agentID, currentConfig.getX(), currentConfig.getY(), currentConfig.getAngle());
			return;
		}
		int i = path.get(curNodeIndex);
		int j = path.get(curGoalNodeIndex);
		RoadMapLine curEdge = rmm.getAllLine().get(rmm.getNodeGraphEdge(i, j));
		double vec_dir = determineEdgeVector(curEdge);
		/*Point2D.Double curEdgeVector = new Point2D.Double(vec_dir*curEdge.getVector().x, vec_dir*curEdge.getVector().y);
		double curDirection = curEdge.getAngle();
		if(vec_dir == IS_P2){
			curDirection -= 180;
		}*/
		velocity = desiredVelocity;
		ArrayList<Agent> sawAgent = new ArrayList<Agent>();
		Iterator<Agent> it = rmm.agents.iterator();
		while(it.hasNext()){
			Agent a = it.next();
			Point2D.Double p = new Point2D.Double(a.currentConfig.getX(), a.currentConfig.getY());
			Arc2D.Double arc = CoordinateTrans.toPlannerArc(viewfield, currentConfig);
			//System.out.printf("%d %f %f %f %f\n", agentID, arc.getBounds2D().getMinX(), arc.getBounds2D().getMinY(), arc.getBounds2D().getMaxX(), arc.getBounds2D().getMaxY());
			if(a.agentID != this.agentID && !a.isGoal() && arc.contains(p)){
				sawAgent.add(a);
				//System.out.println("Saw Agent:" + a.agentID);
				
			}
		}
		/*
		ArrayList<Point2D.Double> sawObst = new ArrayList<Point2D.Double>();
		Obstacle [] obst = rmm.sm.getObstacles();
		for(int ii = 0; ii < obst.length; ii++){
			Arc2D.Double arc = CoordinateTrans.toPlannerArc(viewfield, currentConfig);
			for(int jj = 0; jj < obst[ii].getPolygons().length; jj++){
				if(arc.intersects(obst[ii].getPolygon(jj).getBounds2D())){
					double [] x = obst[ii].getPolygon(jj).xpoints;
					double [] y = obst[ii].getPolygon(jj).ypoints;
					for(int kk = 0; kk < x.length; kk++){
						sawObst.add(new Point2D.Double(x[kk], y[kk]));
					}
					System.out.println("Saw Obst: "+ii);
					//break;
				}
			}
		}
		*/
		double curDirection = currentConfig.getAngle();
		if(sawAgent.size() == 0 /*&& sawObst.size() == 0*/){
			Point2D.Double curVector = null;
			if(vec_dir == IS_P1){
				curVector = new Point2D.Double(curEdge.getLine().x2-currentConfig.getX(), curEdge.getLine().y2-currentConfig.getY());
			}
			else if(vec_dir == IS_P2){
				curVector = new Point2D.Double(curEdge.getLine().x1-currentConfig.getX(), curEdge.getLine().y1-currentConfig.getY());
			}
			//System.out.println("P12: "+vec_dir);
			curDirection = Math.atan2(curVector.y, curVector.x);
			curDirection = Math.toDegrees(curDirection);
			//curDirection = normalizeAngle(curDirection);
			//System.out.println("Nothing: "+curDirection);
		}
		else{
			double tempangle = Double.MAX_VALUE;
			double temp = Double.MAX_VALUE;
			Iterator<Agent> it2 = sawAgent.iterator();
			//System.out.println("Saw!"+agentID);
			velocity *= 0.9;
			if(velocity<=0){
				velocity = 0.1;
			}
			while(it2.hasNext()){
				Agent a = it2.next();
				Point2D.Double ap = new Point2D.Double(a.currentConfig.getX(), a.currentConfig.getY());
				Point2D.Double tp = new Point2D.Double(currentConfig.getX(), currentConfig.getY());
				Point2D.Double vec = new Point2D.Double(ap.x-tp.x, ap.y-tp.y);
				tempangle = Math.atan2(vec.y, vec.x);
				tempangle = Math.toDegrees(tempangle);
				tempangle = normalizeAngle(tempangle);
				double arcStartAngle = currentConfig.getAngle()+viewfield.extent/2.0;
				arcStartAngle = normalizeAngle(arcStartAngle);
				if(Math.abs(arcStartAngle-tempangle) < temp){
					temp = Math.abs(arcStartAngle-tempangle);
					curDirection = tempangle -10;
				}
			}
			/*
			Iterator<Point2D.Double> it3 = sawObst.iterator();
			while(it3.hasNext()){
				Point2D.Double ap = it3.next();
				Point2D.Double tp = new Point2D.Double(currentConfig.getX(), currentConfig.getY());
				Point2D.Double vec = new Point2D.Double(ap.x-tp.x, ap.y-tp.y);
				tempangle = Math.atan2(vec.y, vec.x);
				tempangle = Math.toDegrees(tempangle);
				tempangle = normalizeAngle(tempangle);
				double arcStartAngle = currentConfig.getAngle()+viewfield.extent/2.0;
				arcStartAngle = normalizeAngle(arcStartAngle);
				if(Math.abs(arcStartAngle-tempangle) < temp){
					temp = Math.abs(arcStartAngle-tempangle);
					curDirection = tempangle -10;
				}
			}*/
			curDirection = normalizeAngle(curDirection);
		}
		
		/*
		Point2D.Double curVector = null;
		if(vec_dir == IS_P1){
			curVector = new Point2D.Double(curEdge.getLine().x2-currentConfig.getX(), curEdge.getLine().y2-currentConfig.getY());
		}
		else if(vec_dir == IS_P2){
			curVector = new Point2D.Double(curEdge.getLine().x1-currentConfig.getX(), curEdge.getLine().y1-currentConfig.getY());
		}
		double curDirection = Math.atan2(curVector.y, curVector.x);
		curDirection = Math.toDegrees(curDirection);
		while(curDirection < 0){
			curDirection += 360;
		}
		curDirection %= 360;
		*/
		
		//System.out.printf("C1: %f %f\n", currentConfig.getX(), currentConfig.getY());
		//System.out.printf("C2: %f %f\n", Math.cos(Math.toRadians(curDirection)), Math.sin(Math.toRadians(curDirection)));
		//System.out.printf("ID: %d Config: %2.2f %2.2f %2.2f\nNew Direction: %2.2f\n", agentID, currentConfig.getX(), currentConfig.getY(), currentConfig.getAngle(), curDirection);
		double new_x = currentConfig.getX() + velocity*bodySize*Math.cos(Math.toRadians(curDirection));
		double new_y = currentConfig.getY() + velocity*bodySize*Math.sin(Math.toRadians(curDirection));
		new_x = RoadMapModel.adjustRange(new_x, 0, Constant.BITMAPSIZE);
		new_y = RoadMapModel.adjustRange(new_y, 0, Constant.BITMAPSIZE);
		int nx = (int)Math.round(new_x);
		int ny = (int)Math.round(new_y);
		nx = RoadMapModel.adjustRange(nx, 0, Constant.BITMAPSIZE);
		ny = RoadMapModel.adjustRange(ny, 0, Constant.BITMAPSIZE);
		if(rmm.occurmap[nx][ny] == Constant.INIT /*||	rmm.occurmap[nx][ny] == Constant.OBST*/){
			//new_x = RoadMapModel.adjustRange(new_x, 0, Constant.BITMAPSIZE);
			//new_y = RoadMapModel.adjustRange(new_y, 0, Constant.BITMAPSIZE);
			//System.out.println("AAA");
			/*Rectangle rec = CoordinateTrans.toPlannerCircle(body, currentConfig).getBounds();
			for(int ii = (int)Math.round(rec.getMinX()); ii <= rec.getMaxX(); ii++){
				for(int jj = (int)Math.round(rec.getMinY()); jj <= rec.getMaxY(); jj++){
					if(ii >= 0 && ii < Constant.BITMAPSIZE && jj >= 0 && jj < Constant.BITMAPSIZE){
						rmm.occurmap[ii][jj] = Constant.INIT;
					}
				}
			}*/
			rmm.occurmap[nx][ny] = Constant.OCCUPIED;
			rmm.occurmap[(int)currentConfig.getX()][(int)currentConfig.getY()] = Constant.INIT;
			currentConfig.setX(nx);
			currentConfig.setY(ny);
			currentConfig.setAngle(curDirection);
			/*rec = CoordinateTrans.toPlannerCircle(body, currentConfig).getBounds();
			for(int ii = (int)Math.round(rec.getMinX()); ii <= rec.getMaxX(); ii++){
				for(int jj = (int)Math.round(rec.getMinY()); jj <= rec.getMaxY(); jj++){
					if(ii >= 0 && ii < Constant.BITMAPSIZE && jj >= 0 && jj < Constant.BITMAPSIZE){
						rmm.occurmap[ii][jj] = Constant.OBST;
					}
				}
			}*/
			//System.out.println("Set1: "+new_x+" "+new_y+" "+curDirection);
		}
		/*
		else if(rmm.occurmap[nx][ny] == Constant.OBST){
			int nnx = nx;
			int nny = ny;
			velocity *= 0.9;
			curDirection -= 90;
			new_x = currentConfig.getX() + velocity*Math.cos(Math.toRadians(curDirection));
			new_y = currentConfig.getY() + velocity*Math.sin(Math.toRadians(curDirection));
			nnx = (int)Math.round(new_x);
			nny = (int)Math.round(new_y);
			nnx = RoadMapModel.adjustRange(nnx, 0, Constant.BITMAPSIZE);
			nny = RoadMapModel.adjustRange(nny, 0, Constant.BITMAPSIZE);
		}*/
		else if(rmm.occurmap[nx][ny] == Constant.OCCUPIED || rmm.occurmap[nx][ny] == Constant.OBST){
			int nnx = nx;
			int nny = ny;
			nnx = RoadMapModel.adjustRange(nnx, 0, Constant.BITMAPSIZE);
			nny = RoadMapModel.adjustRange(nny, 0, Constant.BITMAPSIZE);
			//System.out.println("BBB");
			while(rmm.occurmap[nnx][nny] != Constant.INIT){
				double q = Math.random();
				curDirection += ((q>=0.5)?1:-1)*10.0;
				curDirection = normalizeAngle(curDirection);
				new_x = currentConfig.getX() + velocity*Math.cos(Math.toRadians(curDirection));
				new_y = currentConfig.getY() + velocity*Math.sin(Math.toRadians(curDirection));
				nnx = (int)Math.round(new_x);
				nny = (int)Math.round(new_y);
				nnx = RoadMapModel.adjustRange(nnx, 0, Constant.BITMAPSIZE);
				nny = RoadMapModel.adjustRange(nny, 0, Constant.BITMAPSIZE);
				if(nnx==nx && nny==ny){
					int v = (int)Math.round(velocity);
					int temp = (int) (v*bodySize);
					if(temp < 1){
						temp = 1;
					}
					if(Math.abs(Math.cos(Math.toRadians(curDirection))) >= Math.abs(Math.sin(Math.toRadians(curDirection)))){
						nnx += v*bodySize*((Math.cos(Math.toRadians(curDirection)) >= 0)? 1 : -1);
						new_x = nnx;
					}
					else{
						nny += v*bodySize*((Math.sin(Math.toRadians(curDirection)) >= 0)? 1 : -1);
						new_y = nny;
					}
				}
				nnx = RoadMapModel.adjustRange(nnx, 0, Constant.BITMAPSIZE);
				nny = RoadMapModel.adjustRange(nny, 0, Constant.BITMAPSIZE);
				new_x = RoadMapModel.adjustRange(new_x, 0, Constant.BITMAPSIZE);
				new_y = RoadMapModel.adjustRange(new_y, 0, Constant.BITMAPSIZE);
			}
			//System.out.println("Turn!" + agentID);
			new_x = RoadMapModel.adjustRange(new_x, 0, Constant.BITMAPSIZE);
			new_y = RoadMapModel.adjustRange(new_y, 0, Constant.BITMAPSIZE);
			/*Rectangle rec = CoordinateTrans.toPlannerCircle(body, currentConfig).getBounds();
			for(int ii = (int)Math.round(rec.getMinX()); ii <= rec.getMaxX(); ii++){
				for(int jj = (int)Math.round(rec.getMinY()); jj <= rec.getMaxY(); jj++){
					if(ii >= 0 && ii < Constant.BITMAPSIZE && jj >= 0 && jj < Constant.BITMAPSIZE){
						rmm.occurmap[ii][jj] = Constant.INIT;
					}
				}
			}*/
			rmm.occurmap[nnx][nny] = Constant.OCCUPIED;
			rmm.occurmap[(int)currentConfig.getX()][(int)currentConfig.getY()] = Constant.INIT;
			currentConfig.setX(nnx);
			currentConfig.setY(nny);
			currentConfig.setAngle(curDirection);
			
			/*rec = CoordinateTrans.toPlannerCircle(body, currentConfig).getBounds();
			for(int ii = (int)Math.round(rec.getMinX()); ii <= rec.getMaxX(); ii++){
				for(int jj = (int)Math.round(rec.getMinY()); jj <= rec.getMaxY(); jj++){
					if(ii >= 0 && ii < Constant.BITMAPSIZE && jj >= 0 && jj < Constant.BITMAPSIZE){
						rmm.occurmap[ii][jj] = Constant.OBST;
					}
				}
			}*/
		}
		
	}
	
	private void checkCurNodeState(){
		Point2D.Double point = rmm.getAllNode().get(path.get(curGoalNodeIndex)).getPoint();
		if(Math.abs(currentConfig.getX()-point.x) <= 1 && 
				Math.abs(currentConfig.getY()-point.y) <= 1){
			curNodeIndex++;
			curGoalNodeIndex++;
		}
	}
	
	private double determineEdgeVector(RoadMapLine rml){
		Point2D.Double cur_p = rmm.getAllNode().get(path.get(curNodeIndex)).getPoint();
		if(cur_p.x == rml.getLine().x1 && cur_p.y == rml.getLine().y1){
			return IS_P1;
		}
		else if(cur_p.x == rml.getLine().x2 && cur_p.y == rml.getLine().y2){
			return IS_P2;
		}
		else{
			return NONE_P;
		}
	}
	
	private boolean genPath(int start, int goal){
		path.clear();
		path.add(start);
		path.add(goal);
		findPath(start, goal);
		//path.add(goal);
		/*for(int i = 0; i < path.size(); i++){
			System.out.print(path.get(i)+" ");
		}
		System.out.println();*/
		return (path.indexOf(-1) < 0);
	}
	
	private void findPath(int start, int goal){
		int mid = rmm.getPathPoint(start, goal);
		int index = path.indexOf(goal);
		if(mid != -1){
			path.add(index, mid);
			findPath(start, mid);
			findPath(mid, goal);
		}
		else{
			if(rmm.getPathDist(start, goal) == Double.MAX_VALUE){
				path.add(index, -1);
			}
		}
	}
	
	public Ellipse2D.Double getBody() {
		return body;
	}

	public Arc2D.Double getViewfield() {
		return viewfield;
	}

	public Configuration getCurrentConfig() {
		return currentConfig;
	}
}
