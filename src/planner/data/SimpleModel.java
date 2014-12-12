package planner.data;


import java.util.ArrayList;
import java.util.Iterator;
import planner.interfaces.*;
import planner.utils.ObstacleReader;
import planner.utils.RobotReader;

public class SimpleModel{
	private ArrayList<ObstacleStateListener> osl;
	private ArrayList<RobotStateListener> rsl;
	private Obstacle [] obstacles;
	private Robot [] robots;
	
	public SimpleModel(String rf, String of){
		osl = new ArrayList<ObstacleStateListener>();
		rsl = new ArrayList<RobotStateListener>();
		robots = RobotReader.getRobotData(rf);
		obstacles = ObstacleReader.getObstacleData(of);
	}
	
	public SimpleModel(){
		osl = new ArrayList<ObstacleStateListener>();
		rsl = new ArrayList<RobotStateListener>();
		robots = null;
		obstacles = null;
	}
	
	public Obstacle[] getObstacles() {
		return obstacles;
	}

	public Robot[] getRobots() {
		return robots;
	}
	
	public void setRobots(Robot [] r){
		this.robots = r;
		notifyRobotStateListener();
	}
	
	public void setObstacle(Obstacle [] o){
		this.obstacles = o;
		notifyObstacleStateListener();
	}

	public void addObstacleStateListener(ObstacleStateListener o){
		this.osl.add(o);
	}
	
	public void addRobotStateListener(RobotStateListener r){
		this.rsl.add(r);
	}
	
	public void removeObstacleStateListener(ObstacleStateListener o){
		int i = osl.indexOf(o);
		if(i >= 0){
			osl.remove(i);
		}
	}
	
	public void removeRobotStateListener(RobotStateListener r){
		int i = rsl.indexOf(r);
		if(i >= 0){
			rsl.remove(i);
		}
	}
	
	public void notifyAllListener(){
		this.notifyRobotStateListener();
		this.notifyObstacleStateListener();
	}
	
	public void notifyRobotStateListener(){
		Iterator<RobotStateListener> it = rsl.iterator();
		while(it.hasNext()){
			RobotStateListener t = it.next();
			t.updateRobot(robots);
		}
	}
	
	public void notifyObstacleStateListener(){
		Iterator<ObstacleStateListener> it = osl.iterator();
		while(it.hasNext()){
			ObstacleStateListener t = it.next();
			t.updateObstacle(obstacles);
		}
	}
	
}