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
		System.out.println("discover");
		Message message = new Message(this.id, 0, "discover", "");
		this.messages.add(message);
	}
	
	public boolean hasMessageToSend() {
		return !this.messages.isEmpty();
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
	
	public boolean hasMessageToProcess() {
		return this.inboxMessages.isEmpty();
	}
	
	public void sendMessage(Message message) {
		this.messages.add(message);
	}
	
	public Message getInboxMessage() {
		if (this.hasMessageToProcess()) {
			return this.inboxMessages.remove();
		}
		return null;
	}
	
	public void processMessages() {
		Message message = new Message();
		
		if (this.hasMessageToProcess()) {
			while ((message = this.getInboxMessage()) != null) {
				System.out.println("Process" + message.type);
				
				if (message.type == "discover") {
					String messageContent = String.valueOf(this.posX) + "\t" + String.valueOf(this.posY);
					Message draft = new Message(this.id, message.senderId, "neighbor", messageContent);
					this.sendMessage(draft);
				}
			}
		}
	}
}
