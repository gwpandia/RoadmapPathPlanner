package planner.data;

public class AgentGroup {
	private int goalNode;
	private int initNode;
	
	public AgentGroup(int i, int g){
		initNode = i;
		goalNode = g;
	}
	
	public int getGoalNode(){
		return goalNode;
	}
	
	public int getInitNode(){
		return initNode;
	}
}
