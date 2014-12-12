package planner.ui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.*;
import java.util.ArrayList;

import javax.swing.JComponent;

import planner.data.*;
import planner.interfaces.AgentStateListener;
import planner.interfaces.ObstacleStateListener;
import planner.interfaces.RoadmapStateListener;
import planner.interfaces.RobotStateListener;
import planner.utils.CoordinateTrans;
import planner.utils.PolygonMaker;

public class PlannerUI extends JComponent implements MouseMotionListener, RobotStateListener, ObstacleStateListener, 
	RoadmapStateListener, AgentStateListener{
	private Robot [] robots;
	private Obstacle [] obstacles;
	private ArrayList<Agent> agents;
	private ArrayList<RoadMapLine> roadmap;
	private ArrayList<RoadMapNode> roadnode;
	static protected Graphics2D g2;
	private int mx = 0, my = 0;
	
	public PlannerUI(Robot [] robots, Obstacle [] obstacles){
		this.robots = robots;
		this.obstacles = obstacles;
	}
	
	public void updateRobot(Robot[] robots) {
		this.robots = robots;
		this.repaint();
	}
	
	public void updateRoadmap(ArrayList<RoadMapLine> lines, ArrayList<RoadMapNode> nodes) {
		this.roadmap = lines;
		this.roadnode = nodes;
		this.repaint();
	}


	public void updateObstacle(Obstacle[] obstacles) {
		this.obstacles = obstacles;
		this.repaint();
	}
	
	public void updateAgent(ArrayList<Agent> agents) {
		this.agents = agents;
		this.repaint();
	}

	
	public void paintComponent(Graphics g){
		g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setPaint(Color.white);
		g2.fillRect(0, 0, Constant.HC, Constant.HC);
		/*
		if(robots != null){
			for(int i = 0; i < robots.length; i++){
				MyPolygon [] poly = robots[i].getPolygons();
				for(int j = 0; j < poly.length; j++){
					//CoordinateTrans tran = new CoordinateTrans(maps[i].getInitConfiguration(),maps[i].getPolygon(j),Constant.SWP,Constant.SPC);
					
					g2.setPaint(Color.BLUE);
					g2.fill(PolygonMaker.toPolygon(CoordinateTrans.toCanvasPoint(robots[i].getInitc(), poly[j].getPoints(), Constant.SWP, Constant.SPC)));
					//g2.draw(tran.toCanvasPoly());
				}
			}
		}*/
		
		if(obstacles != null){
			for(int i = 0; i < obstacles.length; i++){
				MyPolygon [] poly = obstacles[i].getPolygons();
				for(int j = 0; j < poly.length; j++){
					//CoordinateTrans tran = new CoordinateTrans(maps[i].getInitConfiguration(),maps[i].getPolygon(j),Constant.SWP,Constant.SPC);				
					g2.setPaint(/*new Color(255,255,51)*/Color.GRAY);
					g2.fill(PolygonMaker.toPolygon(CoordinateTrans.toCanvasPoint(obstacles[i].getInitc(), poly[j].getPoints(), Constant.SWP, Constant.SPC)));
					//g2.draw(tran.toCanvasPoly());
				}
			}
		}
		
		if(roadmap != null){
			for(int i = 0; i < roadmap.size(); i++){
				int x1 = (int)(roadmap.get(i).getLine().x1*Constant.SPC*Constant.SWP);
				int x2 = (int)(roadmap.get(i).getLine().x2*Constant.SPC*Constant.SWP);
				int y1 = (int)(Constant.HC-roadmap.get(i).getLine().y1*Constant.SPC*Constant.SWP);
				int y2 = (int)(Constant.HC-roadmap.get(i).getLine().y2*Constant.SPC*Constant.SWP);
				g2.setPaint(Color.RED);
				g2.drawLine(x1, y1, x2, y2);
				g2.setPaint(Color.BLUE);
				g2.drawString(Integer.toString(roadmap.get(i).getLineID()), (x1+x2)/2, (y1+y2)/2);
			}
		}
		
		if(agents != null){
			for(int i = 0; i < agents.size(); i++){
				if(!agents.get(i).isGoal()){
					g2.setPaint(Color.YELLOW);
					g2.fill(CoordinateTrans.toCanvasCircle(agents.get(i).getBody(), agents.get(i).getCurrentConfig()));
					g2.setPaint(new Color(135, 206, 250, 128));
					g2.fill(CoordinateTrans.toCanvasArc(agents.get(i).getViewfield(), agents.get(i).getCurrentConfig()));
					g2.setPaint(Color.BLACK);
					g2.drawString(Integer.toString(i), 
						(int)CoordinateTrans.toCanvasCircle(agents.get(i).getBody(), agents.get(i).getCurrentConfig()).x, 
						(int)CoordinateTrans.toCanvasCircle(agents.get(i).getBody(), agents.get(i).getCurrentConfig()).y);
				}
				else{
					g2.setPaint(Color.GRAY);
					g2.fill(CoordinateTrans.toCanvasCircle(agents.get(i).getBody(), agents.get(i).getCurrentConfig()));
				}
			}
		}
		g2.setPaint(Color.RED);
		g2.drawString("X: "+mx+" Y:"+my, 0, 10);
	}

	@Override
	public void mouseDragged(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		mx = (int)Math.round(e.getX()/Constant.SPC);
		my = (int)Math.round((Constant.HC - e.getY())/Constant.SPC);
		repaint();
	}
}