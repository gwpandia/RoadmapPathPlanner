package planner.data;

import java.awt.geom.Point2D;

public class RoadMapNode {
	private Point2D.Double point;
	private int nodeID;
	
	public RoadMapNode(int id, Point2D.Double p){
		this.nodeID = id;
		this.point = p;
	}

	public Point2D.Double getPoint() {
		return point;
	}

	public int getNodeID() {
		return nodeID;
	}
}
