package mst;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Network {
	// public
	public double minBudget;
	public double R;
	public Map<Integer, Node> nodesMap;
	
	/**
	 * default constructor
	 */
	public Network() {
		this.R = 10;
		nodesMap = new HashMap<Integer, Node>();
	}
	
	public void simulate() {
		int i = 0;
		
		boolean ongoingFlag = true;
		while (ongoingFlag) {
			Iterator it = nodesMap.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry<Integer, Node> pairs = (Map.Entry<Integer, Node>) it.next();
				Integer nodeId = pairs.getKey();
				Node node = pairs.getValue();
				
				if (node.hasMessage()) {
					Message message = node.getMessage();
					this.processMessage(message);					
				}
				
				it.remove();
			}
		}
	}	
	
	public void processMessage(Message message) {
		if (message.type == "discovery") {
			Node currentNode = this.getNode(message.senderId);
			List<Node> nodesWithinRange = this.findNodeWithinRange(currentNode);
			
			for (Node node : nodesWithinRange) {
				node.receiveMessage(message);
			}
		}
	}
	
	public List<Node> findNodeWithinRange(Node currentNode) {
		System.out.println("Find Range");
		List<Node> result = new ArrayList<Node>();
		
		Iterator it = nodesMap.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<Integer, Node> pairs = (Map.Entry<Integer, Node>) it.next();
			Node node = pairs.getValue();
			
			if (node.id != currentNode.id){
				double distance = currentNode.distanceToNode(node);
				
				if (distance < this.R){
					result.add(node);
				}
			}
		}
		return result;
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
