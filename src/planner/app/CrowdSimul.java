package planner.app;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import planner.algo.RoadMapPlanner;
import planner.data.Constant;
import planner.data.SimpleModel;
import planner.ui.PlannerUI;
import planner.utils.*;

public class CrowdSimul{
	public static void main(String [] args){
		//Robot [] robots = RobotReader.getRobotData("DAT/Robots/robot03.dat");
		//Obstacle [] obstacles = ObstacleReader.getObstacleData("DAT/Obstacles/map03.dat");
		//JOptionPane jop = new JOptionPane();
		//int num = Integer.parseInt(jop.showInputDialog("Input number of point"));
		//int len = Integer.parseInt(jop.showInputDialog("Input length threshold"));
		//System.out.println(num+" "+len);
		//SimpleModel sm = new SimpleModel("DAT/Robots/robot03.dat", "DAT/Obstacles/map04.dat");
		SimpleModel sm = new SimpleModel();
		RoadMapPlanner rmp = new RoadMapPlanner(sm);
		PlannerUI pui = new PlannerUI(sm.getRobots(), sm.getObstacles());
		pui.setPreferredSize(new Dimension(Constant.HC, Constant.HC));
		pui.addMouseMotionListener(pui);
		sm.addObstacleStateListener(pui);
		rmp.getRoadMapModel().addRoadmapStateListener(pui);
		rmp.getRoadMapModel().addAgentStateListener(pui);
		//rmp.getRoadMapModel().generateRoadmap();
		//rmp.getRoadMapModel().notifyAllListener();
		//rmp.getRoadMapModel().simulate();
		JFrame f = new JFrame("Simple Crowd Simulation");
		//System.out.println(rmp.getOperationPanel().size());
		//f.setSize(600, 600);
		f.getContentPane().add(pui, BorderLayout.CENTER);
		f.getContentPane().add(rmp.getOperationPanel(), BorderLayout.EAST);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.pack();
		f.setVisible(true);
	}
}
