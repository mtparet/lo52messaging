package lo52.messaging.model;

import java.net.InetAddress;

public class Packet {
	private Message message;
	private User user;
	
	
	public Packet(Message message, User user) {
		super();
		this.message = message;
		this.user = user;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public Message getMessage() {
		return message;
	}
	public void setMessage(Message message) {
		this.message = message;
	}

}
