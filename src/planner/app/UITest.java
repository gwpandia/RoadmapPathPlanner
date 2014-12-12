package planner.app;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import planner.data.*;
import planner.ui.PlannerUI;
import planner.utils.*;

public class UITest{
	
	
	public static void main(String [] args){
		//Robot [] robots = RobotReader.getRobotData("DAT/Robots/robot03.dat");
		//Obstacle [] obstacles = ObstacleReader.getObstacleData("DAT/Obstacles/map03.dat");
		JOptionPane jop = new JOptionPane();
		int num = Integer.parseInt(jop.showInputDialog("Input number of point"));
		int len = Integer.parseInt(jop.showInputDialog("Input length threshold"));
		//System.out.println(num+" "+len);
		SimpleModel sm = new SimpleModel("DAT/Robots/robot03.dat", "DAT/Obstacles/map03.dat");
		SimplePlanner sp = new SimplePlanner(sm, num, len);
		sp.generateRoadmap();
		//PlannerUI pui = new PlannerUI(sm.getRobots(), sm.getObstacles(), sp.getAllline());
		JFrame f = new JFrame("UITest");
		f.setSize(500, 500);
		//f.getContentPane().add(pui, BorderLayout.CENTER);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setVisible(true);
	}
}