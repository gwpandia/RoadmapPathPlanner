package planner.data;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;

import javax.swing.JTextField;
import javax.swing.Timer;
import planner.interfaces.AgentStateListener;
import planner.interfaces.RoadmapStateListener;
import planner.interfaces.RobotStateListener;
import planner.utils.CoordinateTrans;
import planner.utils.PolygonMaker;

public class RoadMapModel{
	public SimpleModel sm;
	private int [][] bitmap;
	public int [][] occurmap; 
	private ArrayList<RoadMapNode> randompoint;
	private ArrayList<MyPolygon> allpoly;
	private ArrayList<RoadMapLine> allline;
	private ArrayList<RoadmapStateListener> rmsl;
	private ArrayList<AgentStateListener> asl;
	private int npoint;
	private int length;
	private int [][] nodeGraph;
	private double [][] D;
	private int [][] Path;
	private Timer timer;
	private Agent agent;
	private int count = 0;
	public ArrayList<Agent> agents;
	public boolean enableCA;
	
	public RoadMapModel(SimpleModel sm){
		this.sm = sm;
		bitmap = new int[Constant.BITMAPSIZE][Constant.BITMAPSIZE];
		occurmap = new int[Constant.BITMAPSIZE][Constant.BITMAPSIZE];
		randompoint = new ArrayList<RoadMapNode>();
		allpoly = new ArrayList<MyPolygon>();
		allline = new ArrayList<RoadMapLine>();
		rmsl = new ArrayList<RoadmapStateListener>();
		asl = new ArrayList<AgentStateListener>();
		agents = new ArrayList<Agent>();
		this.npoint = -1;
		this.length = -1;
		this.enableCA = true;
	}
	
	public void setNPoint(int n){
		this.npoint = n;
	}
	
	public void setRoadMapLength(int l){
		this.length = l;	
	}
	
	public void generateAgent(double velo, double angle, double dist, double size, int num){
		int as = agents.size();
		for(int i = as; i < num+as;){
			int g1 = 0, g2 = 0;
			g1 = (int)Math.round(Math.random()*npoint);
			g2 = (int)Math.round(Math.random()*npoint);
			g1 = adjustRange(g1, 0, npoint);
			g2 = adjustRange(g2, 0, npoint);
			Agent a = new Agent(i, velo, size, angle, dist, new AgentGroup(g1, g2), this);
			if(g1 != g2 && a.hasPath){
				//a.setBodySize(size);
				//a.setVelocity(velo);
				//a.setViewAngle(angle);
				//a.setViewDistance(dist);
				agents.add(a);
				i++;
			}
		}
		notifyAgentStateListener();
	}
	
	public void cleanAgent(){
		agents.clear();
		notifyAgentStateListener();
	}
	
	public void simulate(){
		timer = new Timer(100, new TimerListener());
		timer.start();
	}
	
	public class TimerListener implements ActionListener{
		public void actionPerformed(ActionEvent e){
			/*synchronized(this){
				notifyAllListener();
			}*/
			//count = 0;
			//while(count < agents.size()){
				Iterator<Agent> it = agents.iterator();
				initOccurMap();
				count = 0;
				while(it.hasNext()){
					agent = it.next();
					if(agent.hasPath){
						if(!agent.isGoal()){
							agent.onePhase();
						}
						else{
							++count;
						}
					}
				}
				notifyAllListener();
				if(count == agents.size()){
					timer.stop();
				}
			//}
		}
	}
	
	public void initOccurMap(){
		for(int i = 0; i < occurmap.length; i++){
			for(int j = 0; j < occurmap[i].length; j++){
				if(occurmap[i][j] == Constant.OCCUPIED){
					occurmap[i][j] = Constant.INIT;
				}
			}
		}
	}
	
	public int getNodeGraphEdge(int i, int j){
		if(nodeGraph != null){
			return nodeGraph[i][j];
		}
		return -1;
	}
	
	public int getPathPoint(int i, int j){
		if(Path != null){
			return Path[i][j];
		}
		return -1;
	}
	
	public double getPathDist(int i, int j){
		if(D != null){
			return D[i][j];
		}
		return -1.0;
	}
	
	public void AllPairs(){
		D = new double[npoint][npoint];
		Path = new int[npoint][npoint];
		for(int i = 0; i < npoint; i++){
			for(int j = 0; j < npoint; j++){
				if(nodeGraph[i][j] != -1){
					Line2D.Double line = allline.get(nodeGraph[i][j]).getLine();
					if(i == j){
						D[i][j] = 0;
					}
					else{
						D[i][j] = Math.sqrt((line.x2-line.x1)*(line.x2-line.x1) + 
							(line.y2-line.y1)*(line.y2-line.y1));
					}
				}
				else{
					if(i == j){
						D[i][j] = 0;
					}
					else{
						D[i][j] = Double.MAX_VALUE;
					}
				}
				Path[i][j] = -1;
			}
		}
		
		for(int k = 0; k < npoint; k++){
			for(int i = 0; i < npoint; i++){
				for(int j = 0; j < npoint; j++){
					if(D[i][k] + D[k][j] < D[i][j]){
						D[i][j] = D[i][k] + D[k][j];
						Path[i][j] = k;
					}
				}
			}
		}
	}
	
	public ArrayList<RoadMapNode> getAllNode(){
		return randompoint;
	}
	
	public ArrayList<RoadMapLine> getAllLine() {
		return allline;
	}
	
	public void generateRoadmap(){
		if(npoint < 0 || length < 0){
			return;
		}
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
		//System.out.println(allline.size());
		this.notifyRoadmapStateListener();
		this.AllPairs();
		/*
		for(int i = 0; i < nodeGraph.length; i++){
			for(int j = 0; j < nodeGraph[i].length; j++){
				System.out.printf("%2d ", nodeGraph[i][j]);
			}
			System.out.println();
		}
		System.out.println();
		
		for(int i = 0; i < D.length; i++){
			for(int j = 0; j < D[i].length; j++){
				System.out.printf("%2f ", D[i][j]);
			}
			System.out.println();
		}
		System.out.println();
		
		for(int i = 0; i < Path.length; i++){
			for(int j = 0; j < Path[i].length; j++){
				System.out.printf("%2d ", Path[i][j]);
			}
			System.out.println();
		}
		*/
	}
	
	private void generateLine(){
		nodeGraph = new int[npoint][npoint];
		for(int i = 0; i < nodeGraph.length; i++){
			for(int j = 0; j < nodeGraph[i].length; j++){
				nodeGraph[i][j] = -1;
			}
		}
		for(int i = 0; i < randompoint.size(); i++){
			for(int j = i; j < randompoint.size(); j++){
				if(!randompoint.get(i).equals(randompoint.get(j)) && 
						randompoint.get(i).getPoint().distance(randompoint.get(j).getPoint()) <= length){
					Line2D.Double line = new Line2D.Double(randompoint.get(i).getPoint(), randompoint.get(j).getPoint());
					int k = 0;
					for(MyPolygon poly : allpoly){
						if(isCollision(poly, line)==Constant.FREE && 
								!poly.contains(randompoint.get(i).getPoint()) && 
								!poly.contains(randompoint.get(j).getPoint())){
							++k;
						}
					}
					if(k==allpoly.size()){
						int id = allline.size();
						allline.add(new RoadMapLine(id, line));
						nodeGraph[randompoint.get(i).getNodeID()][randompoint.get(j).getNodeID()] = id;
						nodeGraph[randompoint.get(j).getNodeID()][randompoint.get(i).getNodeID()] = id;
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
			int id = randompoint.size();
			randompoint.add(new RoadMapNode(id, new Point2D.Double(x,y)));
			bitmap[x][y] = Constant.POINTED;
		}
	}
	
	private void initialBitMap(){
		for(int i = 0; i < Constant.BITMAPSIZE; i++){
			for(int j = 0; j < Constant.BITMAPSIZE; j++){
				bitmap[i][j] = Constant.INIT;
				occurmap[i][j] = Constant.INIT;
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
							occurmap[a][b] = Constant.OBST;
					}
				}
			}
		}
	}
	
	public static int adjustRange(int test, int min, int max){
		if(test >= max){
			test = max - 1;
		}
		if(test < min){
			test = min;
		}
		return test;
	}
	
	public static double adjustRange(double test, int min, int max){
		if(test >= max){
			test = max - 1;
		}
		if(test < min){
			test = min;
		}
		return test;
	}
	
	public void addRoadmapStateListener(RoadmapStateListener r){
		this.rmsl.add(r);
	}
	
	public void removeRoadmapStateListener(RoadmapStateListener r){
		int i = this.rmsl.indexOf(r); 
		if(i >= 0){
			this.rmsl.remove(i);
		}
	}
	
	public void notifyRoadmapStateListener(){
		Iterator<RoadmapStateListener> it = rmsl.iterator();
		while(it.hasNext()){
			RoadmapStateListener t = it.next();
			t.updateRoadmap(allline, randompoint);
		}
	}
	
	public void addAgentStateListener(AgentStateListener a){
		this.asl.add(a);
	}
	
	public void removeAgentStateListener(AgentStateListener a){
		int i = this.asl.indexOf(a); 
		if(i >= 0){
			this.asl.remove(i);
		}
	}
	
	public void notifyAgentStateListener(){
		Iterator<AgentStateListener> it = asl.iterator();
		while(it.hasNext()){
			AgentStateListener t = it.next();
			t.updateAgent(agents);
		}
	}
	
	public void notifyAllListener(){
		notifyRoadmapStateListener();
		notifyAgentStateListener();
	}
}
