package mst;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import mst.Edge.SE;
import mst.Message.MessageType;

public class Node {	
	// public
	public int id;
	public double posX;
	public double posY;
	public double energy;
	final public double minBudget;
	public Queue<Message> messages = new LinkedList<Message>();
	public Queue<Message> inboxMessages = new LinkedList<Message>();
	
	private Bag<Edge> adjcentEdges;
	public enum SN { SLEEPING, FIND, FOUND };
	public SN stateNode;
	public double fragmentIdentity;
	public int levelNode;
	public Integer bestEdge;
	public double bestWeight;
	public Integer testEdge;
	public int findCount;
	public int inBranch;
	
	public Node(int id, double posX, double posY, double energy, double minBudget) {
		this.id = id;
		this.posX = posX;
		this.posY = posY;
		this.energy = energy;
		this.minBudget = minBudget;
		
		this.stateNode = SN.SLEEPING;
		
//		System.out.println("Node created: " + String.valueOf(this.id));
		
		this.discover();
		adjcentEdges = new Bag<Edge>();
	}
	
	/**
	 * return the Euclid Distance between this node and another node 
	 * @param node
	 * @return
	 */
	public double distanceToNode(Node node) {
		return this.distanceToPos(node.posX, node.posY);
	}
	
	public double distanceToPos(double posX2, double posY2) {
		double dist = Math.sqrt(Math.pow(this.posX - posX2, 2) + 
				Math.pow(this.posY - posY2, 2));
		
		return dist;
	}
	
	public void discover() {
		Message message = new Message(this.id, 0, MessageType.DISCOVER, "");
		this.messages.add(message);
	}
	
	public boolean hasMessageToSend() {
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
//		System.out.println("Got Message");
	}
	
	public boolean hasMessageToProcess() {
		return !inboxMessages.isEmpty();
	}
	
	public void sendMessage(Message message) {
		System.out.println("Queue sending: " + message.toString());
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
		
		System.out.println(String.format("\n*** PROCESS at node %d", this.id));
		int l = this.inboxMessages.size();
		int count = 0;
		while (count < l) {
			count += 1;
			
			message = this.getInboxMessage();
			if (message != null) {
				System.out.println("Processing: " + message.toString());
				
				if (message.type == MessageType.DISCOVER) {
					String messageContent = String.valueOf(this.posX) + "\t" + String.valueOf(this.posY);
					Message draft = new Message(this.id, message.senderId, MessageType.NEIGHBOR, messageContent);
					this.sendMessage(draft);
				}
				else if (message.type == MessageType.NEIGHBOR) {
					if (message.receiverId == this.id) {
						String[] content = message.content.split("\t");
						
						double posX = Double.valueOf(content[0]);
						double posY = Double.valueOf(content[1]);
						double weight = this.distanceToPos(posX, posY);
						
						Edge edge = new Edge(this.id, message.senderId, weight);
						this.adjcentEdges.add(edge);
						
					}
				}
				else if (message.type == MessageType.CONNECT) {
					this.processConnectMessage(message);
				}
				else if (message.type == MessageType.INITIATE) {
					this.processInitiateMessage(message);
				}
				else if (message.type == MessageType.TEST) {
					this.processTestMessage(message);
				}
			}
		}
	}
	
	/**
	 * Function number (3) in the SynchGHS paper
	 * @param message
	 */
	public void processConnectMessage(Message message) {
		int levelConnect = Integer.valueOf(message.content);
		Edge currentEdge = this.findEdge(message.senderId);
		
		if (currentEdge == null) return;
		
		System.out.println(currentEdge.toString());
		
		if (this.stateNode == SN.SLEEPING) {
			this.wakeUp();
			if (levelConnect < levelNode) {
				currentEdge.setStateEdge(SE.BRANCH);
				
				// TODO think about way to code the message
				int senderId = this.id;
				int receiverId = message.senderId;
				String content = String.format("%s\t%f\t%s", this.levelNode, 
						this.fragmentIdentity, this.stateNode);
				
				Message initiateMessage = new Message(senderId, receiverId,
						MessageType.CONNECT, content);
				this.sendMessage(initiateMessage);
				
				if (this.stateNode == SN.FIND) {
					this.findCount += 1;
				}
			}
		}
		else if (currentEdge.getStateEdge() == SE.BASIC) {
			System.out.println("Basic");
			this.receiveMessage(message); // put message on end of queue
		} else {
			// TODO think about way to code the message
			int senderId = this.id;
			int receiverId = message.senderId;
			// TODO check the pseudocode again
			double fragmentIdentity = currentEdge.weight();
			String content = String.format("%s\t%f\t%s", this.levelNode + 1, 
					fragmentIdentity, SN.FIND);
			
			Message initiateMessage = new Message(senderId, receiverId,
					MessageType.INITIATE, content);
			this.sendMessage(initiateMessage);
		}
	}
	
	/**
	 * Function number (4) in the SynchGHS paper
	 * @param message
	 */
	public void processInitiateMessage(Message message) {
		Edge currentEdge = this.findEdge(message.senderId);
		
		if (currentEdge == null) return;
		
		String[] splitedContent = message.content.split("\t");
		int L = Integer.valueOf(splitedContent[0]);
		double F = Double.valueOf(splitedContent[1]);		
		SN S = SN.valueOf(splitedContent[2]);
		
		this.levelNode = L;
		this.fragmentIdentity = F;
		System.out.println("process_initiate - FN = " + String.valueOf(this.fragmentIdentity));
		this.stateNode = S;
		
		this.inBranch = message.senderId;
		this.bestEdge = null;
		this.bestWeight = Double.POSITIVE_INFINITY;
		
		for (Edge edge : adjcentEdges) {
			System.out.println("Initiate - Edge type" + edge.toString());
			
			if (edge.equals(currentEdge)) {
				System.out.println("Equal Edge");
			}
			else {
				if (edge.getStateEdge() == SE.BRANCH) {
					// TODO think about way to code the message
					int senderId = this.id;
					int receiverId = edge.other(this.id);
					String content = String.format("%d\t%d\t%s", L, F, S);
					
					System.out.println("Content - " + content);
					
					Message initiateMessage = new Message(senderId, receiverId,
							MessageType.INITIATE, content);
					this.sendMessage(initiateMessage);
					
					if (S == SN.FIND) {
						this.findCount += 1;
					}
				}
			}
		}
		if (S == SN.FIND) {
			this.test();
		}
	}
	
	/**
	 * Function number (6) in the SynchGHS paper
	 */
	public void processTestMessage(Message message) {
		Edge currentEdge = this.findEdge(message.senderId);
		if (currentEdge == null) return;
		
		if (this.stateNode == SN.SLEEPING) this.wakeUp();
		
		String[] splitedContent = message.content.split("\t");
		int L = Integer.valueOf(splitedContent[0]);
		double F = Double.valueOf(splitedContent[1]);
		
		// TODO if error happens, then check whether to use receiveMessage() or sendMessage()
		// TODO remove println
		System.out.println(String.format("Test F %f FN %f", F, this.fragmentIdentity));
		System.out.println(String.format("Test L %d LN %d", L, this.levelNode));
		
		if (L > this.levelNode) { 
			System.out.println("== Test condition 1");
			this.receiveMessage(message);
		}
		else if (F != this.fragmentIdentity) {
			System.out.println("== Test condition 2");
			// TODO think about way to code the message
			int senderId = this.id;
			int receiverId = currentEdge.other(this.id);
			
			Message acceptMessage = new Message(senderId, receiverId,
					MessageType.ACCEPT, "");
			this.sendMessage(acceptMessage);
		}
		else {
			System.out.println("== Test condition 3");
			System.out.println(String.format("== Test TEST_EDGE: %d", this.testEdge));
			if (currentEdge.getStateEdge() == SE.BASIC) {
				currentEdge.setStateEdge(SE.REJECTED);
			}
			if (this.testEdge != (Integer) currentEdge.other(this.id)) {
				// TODO think about way to code the message
				int senderId = this.id;
				int receiverId = currentEdge.other(this.id);
				
				Message rejectMessage = new Message(senderId, receiverId, MessageType.REJECT, "");
				this.sendMessage(rejectMessage);
			}
			else {
				this.test();
			}
		}		
	}
	
	/**
	 * Function number (7) in the SynchGHS paper
	 * @param message
	 */
	public void processAcceptMessage(Message message) {
		Edge currentEdge = this.findEdge(message.senderId);
		if (currentEdge == null) return;
		
		int j = currentEdge.other(this.id);
		
		this.testEdge = null;
		if (currentEdge.weight() < this.bestWeight) {
			this.bestEdge = j;
			this.bestWeight = currentEdge.weight();
		}
		
		this.report();
	}
	
	/**
	 * Function number (8) in the SynchGHS paper
	 * @param message
	 */
	public void processRejectMessage(Message message) {
		Edge currentEdge = this.findEdge(message.senderId);
		if (currentEdge == null) return;
		
		if (currentEdge.getStateEdge() == SE.BASIC) {
			currentEdge.setStateEdge(SE.REJECTED);
			this.test();
		}
	}
	
	/**
	 * Function number (10) in the SynchGHS paper
	 * @param message
	 */
	public void processReportMessage(Message message) {
		Edge currentEdge = this.findEdge(message.senderId);
		if (currentEdge == null) return;
		
		int j = currentEdge.other(this.id);
		
		double w = Double.valueOf(message.content);
		
		if (j != this.inBranch) {
			this.findCount = this.findCount - 1;
			
			if (w < this.bestWeight) {
				this.bestWeight = w;
				this.bestEdge = j;
				
				this.report();
			}
			else if (this.stateNode == SN.FIND) {
					// TODO check whether send/receive
					this.receiveMessage(message);
			}
			else if (w > this.bestWeight) {
				this.changeRoot();
			}
			else if (w == this.bestWeight) {
				// TODO how to halt everything
				System.out.println("SERIOUS ERROR WITH THE SAME WEIGHT");
			}
		}
	}
	
	/**
	 * Function number (12) in the SynchGHS paper
	 * @param message
	 */
	public void processChangeRootMessage(Message message) {
		this.changeRoot();
	}
	
	public void printEdges() {
		System.out.println("Number of adjcent edges: " + String.valueOf(adjcentEdges.size()));
		for (Edge edge : adjcentEdges) {
			System.out.println(edge.toString());
		}
	}
	
	public Edge findMinAdjcentEdge() {
		double minWeight = Double.POSITIVE_INFINITY;
		Edge tmpEdge = null;
		
		for (Edge edge : adjcentEdges) {
			if (edge.weight() < minWeight) {
				minWeight = edge.weight();
				tmpEdge = edge;
			}
		}
		
//		System.out.println(String.format("Found Edge: %s", tmpEdge.toString()));
		return tmpEdge;
	}
	
	public Edge findEdge(int nodeId) {
		for (Edge edge : adjcentEdges) {
			if (edge.other(this.id) == nodeId) {
				return edge;
			}
		}
		return null;
	}
	
	public void wakeUp() {
		if (this.stateNode == SN.SLEEPING) {
			Edge minAdjEdge = this.findMinAdjcentEdge();
			if (minAdjEdge != null) {
				minAdjEdge.setStateEdge(Edge.SE.BRANCH);
			
				this.levelNode = 0;
				this.stateNode = SN.FOUND;
				this.findCount = 0;
				
				Message connectMessage = new Message(this.id, minAdjEdge.other(this.id), 
						MessageType.CONNECT, "0");
				
				this.sendMessage(connectMessage);
			}
		}
	}
	
	/**
	 * Function number (5) in the SynchGHS paper
	 */
	public void test() {
		Double minWeight = Double.POSITIVE_INFINITY;
		Edge tmpEdge = null;
		
		for (Edge edge : adjcentEdges) {
			if (edge.getStateEdge() == SE.BASIC) {
				if (edge.weight() < minWeight) {
					minWeight = edge.weight();
					tmpEdge = edge;
					System.out.println("Test BASIC Edge: " + edge.toString());
				}
			}
		}
		
		if (tmpEdge != null) {
			this.testEdge = tmpEdge.other(this.id);
			System.out.println("Test() - set testEdge" + String.valueOf(this.testEdge));
			
			// TODO think about way to code the message
			int senderId = this.id;
			int receiverId = tmpEdge.other(this.id);
			String content = String.format("%d\t%f", this.levelNode, this.fragmentIdentity);
			
			Message testMessage = new Message(senderId, receiverId,
					MessageType.TEST, content);
			
			this.sendMessage(testMessage);
		}
		else {
			this.testEdge = null;
			this.report();
		}
	}
	
	/**
	 * Function number (9) in the SynchGHS paper
	 */
	public void report() {
		if (this.findCount == 0 && this.testEdge == null) {
			this.stateNode = SN.FOUND;
			
			// TODO think about way to code the message
			int senderId = this.id;
			int receiverId = this.inBranch;
			String content = String.format("%f", this.bestWeight);
			
			Message reportMessage = new Message(senderId, receiverId,
					MessageType.REPORT, content);
			this.sendMessage(reportMessage);
		}
	}
	
	/**
	 * Function number (11) in the SynchGHS paper
	 */
	public void changeRoot() {
		Edge edge = this.findEdge(this.bestEdge);
		
		if (edge == null) {
			System.out.print("changeRoot() - NULL Edge");
		}
		
		if (edge.getStateEdge() == SE.BRANCH) {
			// TODO think about way to code the message
			int senderId = this.id;
			int receiverId = this.bestEdge;
			
			Message changeRootMessage = new Message(senderId, receiverId,
					MessageType.CHANGEROOT, "");
			this.sendMessage(changeRootMessage);
		}
		else {
			// TODO think about way to code the message
			int senderId = this.id;
			int receiverId = this.bestEdge;
			
			Message connectMessage = new Message(senderId, receiverId,
					MessageType.CONNECT, "");
			this.sendMessage(connectMessage);
			
			edge.setStateEdge(SE.BRANCH);
		}
	}
	
	public String toStringMST() {
		return String.format("Node %d - BestEdge %d - BestWeight %f - inBranch %d", this.id, this.bestEdge, this.bestWeight, this.inBranch);
	}

}
