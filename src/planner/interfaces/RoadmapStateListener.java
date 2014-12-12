package planner.interfaces;

import java.util.ArrayList;
import planner.data.RoadMapLine;
import planner.data.RoadMapNode;

public interface RoadmapStateListener {
	public void updateRoadmap(ArrayList<RoadMapLine> lines, ArrayList<RoadMapNode> nodes);
}
