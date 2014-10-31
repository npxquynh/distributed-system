package mst;

import java.util.LinkedList;
import java.util.Queue;

public class Node {	
	// public
	public int id;
	public double posX;
	public double posY;
	public double energy;
	final public double minBudget;
	public Queue<Message> messages = new LinkedList<Message>();
	public Queue<Message> inboxMessages = new LinkedList<Message>();
	public enum SN { SLEEPING, FIND, FOUND };
	public enum SE { BASIC, BRANCH, REJECTED };
	public int fragmentIdentity;
	public int level;
	public int bestEdge;
	public int bestWeight;
	public int testEdge;
	public int inBranch;
	public int findCount;
	
	public Node(int id, double posX, double posY, double energy, double minBudget) {
		this.id = id;
		this.posX = posX;
		this.posY = posY;
		this.energy = energy;
		this.minBudget = minBudget;
		
		System.out.println("Node created: " + String.valueOf(this.id));
		
		this.discover();
	}
	
	/**
	 * return the Euclid Distance between this node and another node 
	 * @param node
	 * @return
	 */
	public double distanceToNode(Node node) {
		double dist = Math.sqrt(Math.pow(this.posX - node.posX, 2) + 
				Math.pow(this.posY - node.posY, 2));
		
		return dist;
	}
	
	public void discover() {
		System.out.println("discovery");
		Message message = new Message(this.id, 0, "discovery", "");
		this.messages.add(message);
	}
	
	public boolean hasMessage() {
		System.out.print(!messages.isEmpty());
		return !messages.isEmpty();
	}
	
	public Message getMessage() {
		if (!messages.isEmpty()) {
			return messages.remove();
		}
		return null;
	}
	
	public void receiveMessage(Message message) {
		this.inboxMessages.add(message);
		System.out.println("Got Message");
	}
}
