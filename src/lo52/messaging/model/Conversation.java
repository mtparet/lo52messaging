package lo52.messaging.model;

import java.util.ArrayList;
import java.util.Random;

/**
 * Classe qui stocke une conversation: son id, name, et les différents Message
 * @author SYSTEMMOI
 *
 */
public class Conversation {
	
	private int conversation_id;
	
	private String name;
	
	private ArrayList<Message> listMessage = new ArrayList<Message>();
	
	/**
	 * Permet de créer une NOUVELLE conversation
	 */
	public Conversation(String name){
		Random rand = new Random();
		conversation_id = rand.nextInt();
		
		this.name = name;
	}
	
	/**
	 * Permet de récupérer localement un conversation déjà créé
	 * @param conversation_id
	 */
	public Conversation(int conversation_id, String name){
		this.conversation_id = conversation_id;
		this.setName(name);
	}
	
	public int getConversation_id() {
		return conversation_id;
	}
	public void setConversation_id(int conversation_id) {
		this.conversation_id = conversation_id;
	}
	public ArrayList<Message> getListMessage() {
		return listMessage;
	}
	public void setListMessage(ArrayList<Message> listMessage) {
		this.listMessage = listMessage;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public void addMessage(Message message){
		this.listMessage.add(message);
	}
	
	

}
