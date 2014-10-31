package mst;

public class Message {
	public int senderId;
	public int receiverId;
	public String type;
	public String content;
	
	public Message() {
		
	}
	
	public Message(int senderId, int receiverId, String type, String content) {
		this.senderId = senderId;
		this.receiverId = receiverId;
		this.type = type;
		this.content = content;
	}

}
