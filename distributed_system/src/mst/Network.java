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
	
	/**
	 * There are 2 stages that happen once after the other.
	 * 1st: exchanging messages stage
	 * 2nd: processing messages stage
	 */
	public void simulate() {
		int i = 0;
		
		while (this.isCreatingNetwork()) {
			/*
			 * 1st stage: sending message toward different nodes
			 */
			this.stageCommunicating();
			
			/*
			 * 2nd stage: processing messages within each nodes
			 */
			this.stageProcessing();
		}
	}	
	
	/**
	 * @return
	 * - True: Nodes are currently in the process of getting information
	 * from nearby nodes.
	 * - False: After nodes finished creating getting all information
	 * of their neighbours.
	 */
	public boolean isCreatingNetwork() {
		for (Node node : this.nodesMap.values()) {
			if (node.hasMessageToProcess() || node.hasMessageToSend()) {
				return true;
			}
		}
		return false;
	}
	
	public void stageCommunicating() {
		Iterator it = nodesMap.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<Integer, Node> pairs = (Map.Entry<Integer, Node>) it.next();
			Integer nodeId = pairs.getKey();
			Node node = pairs.getValue();
			
			while (node.hasMessageToSend()) {
				Message message = node.getMessage();
				this.sendMessage(message);					
			}
			
			it.remove();
		}
	}
	
	public void sendMessage(Message message) {
		System.out.println("Sending" + message.type);
		
		if (message.type == "discover") {
			Node currentNode = this.getNode(message.senderId);
			List<Node> nodesWithinRange = this.findNodeWithinRange(currentNode);
			
			for (Node node : nodesWithinRange) {
				System.out.println("Node within range: " + String.valueOf(node.id));
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
	
	public void stageProcessing() {
		boolean ongoingFlag = true;
		for (Node node : nodesMap.values()) {
			node.processMessages();
		}
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
