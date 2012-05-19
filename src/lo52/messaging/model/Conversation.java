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
	
	private String conversation_name;
	
	private ArrayList<Message> listMessage = new ArrayList<Message>();
	
	private ArrayList<Integer> listIdUser = new ArrayList<Integer>();
	
	/**
	 * Permet de créer une NOUVELLE conversation avec un utilisateur
	 */
	public Conversation(String name, int idUser){
		Random rand = new Random();
		conversation_id = rand.nextInt();
		
		this.conversation_name = name;
		this.listIdUser.add(idUser);
	}
	
	/**
	 * Permet de créer une NOUVELLE conversation avec de multiples utilisateurs
	 */
	public Conversation(String name, int idUser, ArrayList<Integer> listIdUser ){
		Random rand = new Random();
		conversation_id = rand.nextInt();
		
		this.conversation_name = name;
		this.listIdUser = listIdUser;
	}
	
	/**
	 * Permet de récupérer localement une conversation déjà créé
	 * @param conversation_id
	 */
	public Conversation(int conversation_id, String conversation_name, ArrayList<Integer> listIdUser){
		this.conversation_id = conversation_id;
		this.setConversation_name(conversation_name);
		this.listIdUser = listIdUser;
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

	public String getConversation_name() {
		return conversation_name;
	}

	public void setConversation_name(String conversation_name) {
		this.conversation_name = conversation_name;
	}
	
	/**
	 * Permet d'ajouter un message à la conversation
	 * @param message
	 */
	public void addMessage(Message message){
		this.listMessage.add(message);
	}

	public ArrayList<Integer> getListIdUser() {
		return listIdUser;
	}

	public void setListIdUser(ArrayList<Integer> listIdUser) {
		this.listIdUser = listIdUser;
	}
	
	

}
