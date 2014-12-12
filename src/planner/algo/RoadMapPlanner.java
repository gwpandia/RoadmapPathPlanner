package planner.algo;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.*;
import java.awt.geom.*;
import java.io.File;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import planner.data.*;
import planner.interfaces.Planner;
import planner.utils.CoordinateTrans;
import planner.utils.ObstacleReader;
import planner.utils.PolygonMaker;

public class RoadMapPlanner implements Planner, ActionListener{
	private RoadMapModel rmm;
	private JTextField roadMapPointText;
	private JTextField roadMapLengthText;
	private JTextField agentVelocityText;
	private JTextField agentViewAngleText;
	private JTextField agentBodySizeText;
	private JTextField agentViewDistText;
	private JTextField genNAgentText;
	private JCheckBox collisionAvoidanceCheckBox;
	private JButton genRoadMapButton;
	private JButton genAgentButton;
	private JButton genRandomAgentButton;
	private JButton clearAgentButton;
	private JButton startSimulateButton;
	private JButton loadMapButton;
	private JPanel PlannerPanel;
	
	public RoadMapPlanner(SimpleModel sm){
		PlannerPanel = new JPanel();
		PlannerPanel.setLayout(new FlowLayout());
		PlannerPanel.setPreferredSize(new Dimension(300, Constant.HC));
		rmm = new RoadMapModel(sm);
		createGUI();
	}
	
	public void createGUI(){
		roadMapPointText = new JTextField("200");
		roadMapPointText.setColumns(11);
		roadMapLengthText = new JTextField("20");
		roadMapLengthText.setColumns(11);
		agentVelocityText = new JTextField("1");
		agentVelocityText.setColumns(6);
		agentViewAngleText = new JTextField("120");
		agentViewAngleText.setColumns(4);
		agentBodySizeText = new JTextField("1");
		agentBodySizeText.setColumns(4);
		agentViewDistText = new JTextField("10");
		agentViewDistText.setColumns(4);
		genNAgentText = new JTextField("1");
		genNAgentText.setColumns(11);
		collisionAvoidanceCheckBox = new JCheckBox("Collision Avoidance");
		collisionAvoidanceCheckBox.addActionListener(this);
		genRoadMapButton = new JButton("Generate RoadMap");
		genRoadMapButton.addActionListener(this);
		genAgentButton = new JButton("Generate Agent");
		genAgentButton.addActionListener(this);
		genRandomAgentButton = new JButton("Random N Agent");
		genRandomAgentButton.addActionListener(this);
		clearAgentButton = new JButton("Clear All Agent");
		clearAgentButton.addActionListener(this);
		startSimulateButton = new JButton("Start");
		startSimulateButton.addActionListener(this);
		loadMapButton = new JButton("Load Map");
		loadMapButton.addActionListener(this);
		JPanel p1 = new JPanel();
		p1.setBorder(BorderFactory.createTitledBorder("RoadMap Points"));
		p1.add(roadMapPointText);
		JPanel p2 = new JPanel();
		p2.setBorder(BorderFactory.createTitledBorder("RoadMap Length"));
		p2.add(roadMapLengthText);
		JPanel p3 = new JPanel();
		p3.setBorder(BorderFactory.createTitledBorder("Agent Velocity"));
		p3.add(agentVelocityText);
		JPanel p4 = new JPanel();
		p4.setBorder(BorderFactory.createTitledBorder("Agent ViewAngle"));
		p4.add(agentViewAngleText);
		JPanel p5 = new JPanel();
		p5.setBorder(BorderFactory.createTitledBorder("Agent BodySize"));
		p5.add(agentBodySizeText);
		JPanel p6 = new JPanel();
		p6.setBorder(BorderFactory.createTitledBorder("Agent ViewDist"));
		p6.add(agentViewDistText);
		JPanel p7 = new JPanel();
		p7.setBorder(BorderFactory.createTitledBorder("Agent Number"));
		p7.add(genNAgentText);
		JPanel rmpanel = new JPanel();
		rmpanel.setBorder(BorderFactory.createTitledBorder("RoadMap Setting"));
		rmpanel.setLayout(new GridLayout(1,2));
		rmpanel.add(p1);
		rmpanel.add(p2);
		PlannerPanel.add(rmpanel);
		PlannerPanel.add(genRoadMapButton);
		JPanel apanel = new JPanel();
		apanel.setBorder(BorderFactory.createTitledBorder("Agent Setting"));
		apanel.setLayout(new GridLayout(3,2));
		apanel.add(p3);
		apanel.add(p4);
		apanel.add(p5);
		apanel.add(p6);
		apanel.add(p7);
		//apanel.add(genRandomAgentButton);
		PlannerPanel.add(loadMapButton);
		PlannerPanel.add(apanel);
		PlannerPanel.add(genAgentButton);
		//PlannerPanel.add(collisionAvoidanceCheckBox);
		PlannerPanel.add(clearAgentButton);
		PlannerPanel.add(startSimulateButton);
	}
	
	public RoadMapModel getRoadMapModel(){
		return rmm;
	}
	
	public JComponent getOperationPanel() {
		return PlannerPanel;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == genRoadMapButton){
			int np = Integer.parseInt(roadMapPointText.getText());
			if(np < 2){
				roadMapPointText.setText("Value should > 1");
				return;
			}
			int len = Integer.parseInt(roadMapLengthText.getText());
			if(len <= 0){
				roadMapLengthText.setText("Value should > 0");
				return;
			}
			rmm.setNPoint(np);
			rmm.setRoadMapLength(len);
			rmm.generateRoadmap();
		}
		else if(e.getSource() == genRandomAgentButton){
			double velo = Math.random() * 3;
			double angle = Math.random() * 360;
			double size = Math.random() * 10;
			double dist = Math.random() * 20;
			int num = Integer.parseInt(genNAgentText.getText());
			if(num <= 0){
				genNAgentText.setText("Value shoud > 0");
				return;
			}
			rmm.generateAgent(velo, angle, dist, size, num);
		}
		else if(e.getSource() == genAgentButton){
			double velo = Double.parseDouble(agentVelocityText.getText());
			if(velo <= 0){
				agentVelocityText.setText("Value should > 0");
				return;
			}
			double angle = Double.parseDouble(agentViewAngleText.getText());
			if(angle < 0 || angle > 360){
				agentViewAngleText.setText("Value should be 0 ~ 360");
				return;
			}
			double size = Double.parseDouble(agentBodySizeText.getText());
			if(size <= 0){
				agentBodySizeText.setText("Value shoud > 0");
				return;
			}
			double dist = Double.parseDouble(agentViewDistText.getText());
			if(dist <= 0){
				agentViewDistText.setText("Value shoud > 0");
				return;
			}
			int num = Integer.parseInt(genNAgentText.getText());
			if(num <= 0){
				genNAgentText.setText("Value shoud > 0");
				return;
			}
			//System.out.printf("%f %f %f %f %d\n", velo, angle, dist, size, num);
			rmm.generateAgent(velo, angle, dist, size, num);
		}
		else if(e.getSource() == clearAgentButton){
			rmm.cleanAgent();
		}
		else if(e.getSource() == startSimulateButton){
			rmm.simulate();
		}
		else if(e.getSource() == collisionAvoidanceCheckBox){
			rmm.enableCA = collisionAvoidanceCheckBox.isSelected();
		}
		else if(e.getSource() == loadMapButton){
			Obstacle [] o;
			JFileChooser chooser = new JFileChooser();
			FileNameExtensionFilter filter = new FileNameExtensionFilter("Map files (*.dat, *.txt)", "dat", "txt");
			chooser.setFileFilter(filter);
			chooser.setCurrentDirectory(new File("./DAT/Obstacles"));
			chooser.setDialogTitle("Load Image1");
			int option = chooser.showOpenDialog(PlannerPanel);
			if(option == JFileChooser.APPROVE_OPTION){
				String openfilename = chooser.getSelectedFile().getPath();
				o = ObstacleReader.getObstacleData(openfilename);
				rmm.sm.setObstacle(o);
			}
		}
	}

}
