package mst;

import java.util.ArrayList;

public class Message {
	public int senderId;
	public int receiverId;
	public enum MessageType {DISCOVER, NEIGHBOR, CONNECT, INITIATE, TEST, ACCEPT, REJECT, CHANGEROOT, REPORT};
	public MessageType type;
	public String content;
	
	public Message() {
		
	}
	
	public Message(int senderId, int receiverId, MessageType type, String content) {
		this.senderId = senderId;
		this.receiverId = receiverId;
		this.type = type;
		this.content = content;
	}
	
	public ArrayList<Integer> parseContent() {
		String[] splitedContent = this.content.split("\t");
		ArrayList<Integer> result = new ArrayList<Integer>();
		for (String str : splitedContent) {
			result.add(Integer.valueOf(str));
		}
		
		return result;
	}

}
