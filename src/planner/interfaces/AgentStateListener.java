package planner.interfaces;

import java.util.ArrayList;
import planner.data.Agent;

public interface AgentStateListener {
	public void updateAgent(ArrayList<Agent> agents);
}
