package lo52.messaging.model;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Random;

import lo52.messaging.services.NetworkService;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

/**
 * Classe qui stocke une conversation: son id, name, et les différents Message
 * ATTENTION : la parcelablisation ne s'effectue pour le moment pas pour listMessage
 * @author SYSTEMMOI
 *
 */
public class Conversation implements Parcelable {

	private int conversation_id;

	private String conversation_name;

	private ArrayList<Message> listMessage = new ArrayList<Message>();

	private ArrayList<Integer> listIdUser = new ArrayList<Integer>();

	private static final String TAG = "Conversation";

	/**
	 * Permet de créer une NOUVELLE conversation avec un utilisateur
	 */
	public Conversation(String name, int idUser) {
		Random rand = new Random();
		conversation_id = Math.abs(rand.nextInt());

		this.conversation_name = name;
		this.listIdUser.add(idUser);

		// Ajout de l'utilisateur local
		if (!listIdUser.contains(NetworkService.getUser_me().getId()))
			this.listIdUser.add(NetworkService.getUser_me().getId());
	}

	/**
	 * Permet de créer une NOUVELLE conversation avec de multiples utilisateurs
	 */
	public Conversation(String name, ArrayList<Integer> listIdUser) {
		Random rand = new Random();
		conversation_id = Math.abs(rand.nextInt());

		this.conversation_name = name;
		this.listIdUser = listIdUser;

		// Ajout de l'utilisateur local
		if (!listIdUser.contains(NetworkService.getUser_me().getId()))
			this.listIdUser.add(NetworkService.getUser_me().getId());
	}

	/**
	 * Permet de récupérer localement une conversation déjà créé
	 * @param conversation_id
	 */
	public Conversation(int conversation_id, String conversation_name, ArrayList<Integer> listIdUser) {
		this.conversation_id = conversation_id;
		this.setConversation_name(conversation_name);
		this.listIdUser = listIdUser;

		// Ajout de l'utilisateur local
		if (!listIdUser.contains(NetworkService.getUser_me().getId()))
			this.listIdUser.add(NetworkService.getUser_me().getId());
	}

	public Conversation(Parcel in) {
		conversation_id = in.readInt();
		conversation_name = in.readString();
		listIdUser = in.readArrayList(Integer.class.getClassLoader());

		// Ajout de l'utilisateur local
		if (!listIdUser.contains(NetworkService.getUser_me().getId()))
			this.listIdUser.add(NetworkService.getUser_me().getId());
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
	public void addMessage(Message message) {
		this.listMessage.add(message);
	}

	public ArrayList<Integer> getListIdUser() {
		return listIdUser;
	}

	public void setListIdUser(ArrayList<Integer> listIdUser) {
		this.listIdUser = listIdUser;
	}

	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(conversation_id);
		dest.writeString(conversation_name);
		dest.writeArray(listIdUser.toArray());

	}

	public static final Parcelable.Creator<Conversation> CREATOR= new Parcelable.Creator<Conversation>() {
		public Conversation createFromParcel(Parcel in) {
			return new Conversation(in);
		}

		public Conversation[] newArray(int size) {
			return new Conversation[size];
		}
	};

	public int sendToNetworkService(Context ctx){
		Intent broadcastIntent = new Intent(NetworkService.ReceiveConversation);
		Bundle bundle = new Bundle();

		bundle.putParcelable("conversation", this);
		broadcastIntent.putExtra("conversation", bundle);

		ctx.sendBroadcast(broadcastIntent);

		// Indique à l'activité ConversationPagerActivity qu'il devra créer un fragment correspondant
		Log.d("TAG", "setting to create " + getConversation_id());
		NetworkService.setHasLocalConversationToCreate(this);

		return getConversation_id();

	}

	/**
	 * Génère un nom de conversation en fonction des membres de la conversation.
	 * @return	Le nom de la conversation
	 */
	public String generateConversationName() {
		String name = "";
		// On récupère la liste des noms connus par le service
		Hashtable<Integer, User> allUsers = NetworkService.getListUsers();

		// On récupère l'ID de l'user local
		User user_me = NetworkService.getUser_me();
		int user_meID = 0;
		if (user_me != null) {
			user_meID = user_me.getId();
		} else {
			Log.e(TAG, "User_me is null");
		}

		// On loop sur chaque utilisateur associé à cette conversation
		for (int id : listIdUser) {
			if (id != user_meID) {
				if (allUsers.containsKey(id)) {

					// On récupère le nom de l'utilisteur
					String n = allUsers.get(id).getName();
					if (name.equals("")) {
						name += n;
					} else {
						name += ", " + n;	// Si le nom n'est pas nul on ajoute une virgule devant
					}
				} else {
					Log.e(TAG, "Erreur: un utilisateur associé à une conversation est inconnu du service");
				}
			}
		}
		return name;
	}

}
