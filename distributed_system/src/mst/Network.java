package mst;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import mst.Message.MessageType;

public class Network {
	// public
	public double minBudget;
	public double R;
	private String stage;
	private Map<Integer, Node> nodesMap;
	private EdgeWeightedGraph graph;
	
	/**
	 * default constructor
	 */
	public Network() {
		this.R = 10;
		nodesMap = new HashMap<Integer, Node>();
		graph = new EdgeWeightedGraph(nodesMap.size());
	}
	
	/**
	 * There are 2 stages that happen once after the other.
	 * 1st: exchanging messages stage
	 * 2nd: processing messages stage
	 */
	public void simulate() {
		int i = 0;
		
		/*
		 * TASK 1: CREATE NETWORK
		 */
		while (this.isCreatingNetwork()) {
			System.out.println("-----Communicating---------");
			/*
			 * 1st stage: sending message toward different nodes
			 */
			this.stageCommunicating();
			
			System.out.println("-----Processing---------");
			/*
			 * 2nd stage: processing messages within each nodes
			 */
			this.stageProcessing();
		}
		
		// TODO remove later
		// test the edge creation
		for (Node node : this.nodesMap.values()) {
			node.printEdges();
		}
		
		/*
		 * TASK 2: CREATE MINIMUM SPANNING TREE
		 */
		
		this.wakeUpAllNodes();
		
		while (this.isCreatingMST()) {
			System.out.println("-----Communicating---------");
			/*
			 * 1st stage: sending message toward different nodes
			 */
			this.stageCommunicating();
			
			System.out.println("-----Processing---------");
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
	
	/**
	 * TODO didn't finish it yet
	 * factor with isCreatingNetwork?
	 * @return
	 */
	public boolean isCreatingMST() {
		for (Node node : this.nodesMap.values()) {
			if (node.hasMessageToProcess() || node.hasMessageToSend()) {
				return true;
			}
		}
		return false;
	}
	
	public void stageCommunicating() {
		for (Node node : nodesMap.values()) {			
			while (node.hasMessageToSend()) {
				Message message = node.getMessage();
				this.sendMessage(message);					
			}
		}
	}
	
	public void sendMessage(Message message) {
		System.out.println("Sending: " + message.type);
		
		if (message.type == MessageType.DISCOVER) {
			Node currentNode = this.getNode(message.senderId);
			List<Node> nodesWithinRange = this.findNodeWithinRange(currentNode);
			
			for (Node node : nodesWithinRange) {
				System.out.println("Node within range: " + String.valueOf(node.id));
				node.receiveMessage(message);
			}
		} else {
			Node node = nodesMap.get(message.receiverId);
			node.receiveMessage(message);
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
		for (Node node : nodesMap.values()) {
//			System.out.println("stage processing node - " + String.valueOf(node.id));
			node.processMessages();
		}
	}
	
	public void wakeUpAllNodes() {
		for (Node node : nodesMap.values()) {
			node.wakeUp();
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
