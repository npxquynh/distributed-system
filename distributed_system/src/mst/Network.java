package mst;

import java.util.HashMap;
import java.util.Map;

public class Network {
	// public
	public double minBudget;
	public Map<Integer, Node> nodesMap;
	
	/**
	 * default constructor
	 */
	public Network() {
		nodesMap = new HashMap<Integer, Node>();
	}
	
	
	
	public double getMinBudget() {
		return minBudget;
	}



	public void setMinBudget(double minBudget) {
		this.minBudget = minBudget;
	}



	/**
	 * add node to the network
	 * @param node
	 */
	public void addNode(Node node) {
		nodesMap.put(node.id, node);
	}
	
	/**
	 * 
	 * @param nodeId
	 * @return
	 */
	public Node getNode(int nodeId) {
		return nodesMap.get(nodeId);
	}
	
}
