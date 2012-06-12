package lo52.messaging.model;

import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Random;

import lo52.messaging.R;
import lo52.messaging.model.broadcast.MessageBroacast;
import lo52.messaging.services.NetworkService;
import lo52.messaging.util.LibUtil;
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

	@SuppressWarnings("unchecked")
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


	/**
	 * Génère une chaine de caractères contenant tous les messages de la conversation, pouvant être affichée dans l'EditText.
	 * La chaine retournée doit être interprétée avec Html.fromHTML();
	 * @return	String Une chaine HTML
	 */
	public String generateUserFriendlyConversationText(Context ctx) {
		String text = "";

		// Liste des messages
		ArrayList<Message> messages = getListMessage();

		// Liste des users
		Hashtable<Integer, User> users = NetworkService.getListUsers();

		int mySelf 	= NetworkService.getUser_me().getId();
		int color 	= 0;
		String name = "";

		for (Message m : messages) {
			String message = m.getMessage();

			// Traitement des messages faisant référence à un fichier
			if (message.startsWith(MessageBroacast.MESSAGE_FILE_IDENTIFIER)) {

				// Split de la chaine pour récupérer le chemin local
				String filenameArray[] = message.split(";");

				// Emetteur
				User u  = users.get(m.getClient_id());

				if ((new File(filenameArray[1])).exists()) {

					if (m.getClient_id() == mySelf) {
						color 	= ctx.getResources().getColor(R.color.conversation_myself);
						name	= ctx.getString(R.string.conversations_myself);
					} else {
						color 	= getUserNameColor(ctx, u.getId());
						name 	= u.getName();
					}

					// Cas où c'est une image
					String filenameExt[] = filenameArray[1].split("\\.");
					String extension = filenameExt[filenameExt.length-1];

					String filenameOnly[] = filenameArray[1].split("/");

					/*
					 * Images
					 */
					if (LibUtil.FILE_IMAGE_EXTENSIONS.contains(extension)) {
						text += "<font color=\"" + color + "\">" + name + ":</font><br><img src=\""+filenameArray[1]+"\"></img><br>" +
								"<font color=\""+ ctx.getResources().getColor(R.color.conversation_system) + "\"><small>" +  filenameOnly[filenameOnly.length-1] + "</small></font><br>";
					}
					/*
					 * Fichiers audio
					 */
					else if (LibUtil.FILE_AUDIO_EXTENSIONS.contains(extension)) {
						text += "<font color=\"" + color + "\">" + name + ":</font><br><sound>"+filenameArray[1]+"</sound><br>" +
								"<font color=\""+ ctx.getResources().getColor(R.color.conversation_system) + "\"><small>" +  filenameOnly[filenameOnly.length-1] + "</small></font><br>";
					}
					else {
						text += "<font color=\"" + color + "\">" + name + ":</font> Fichier non pris en charge ("+extension+")<br>";
					}

				} else {
					Log.e(TAG, "Fichier inexistant " + filenameArray[1]);
				}
			}

			// Messages texte
			else if (!m.getMessage().equals("")) {	// Pour vérifier que le message est du texte

				User u  = users.get(m.getClient_id());
				if (u != null) {

					// Attribution de la couleur et du nom en fonction de l'user
					if (u.getId() == mySelf) {
						color 	= ctx.getResources().getColor(R.color.conversation_myself);
						name	= ctx.getString(R.string.conversations_myself);
					}
					else {
						color 	= getUserNameColor(ctx, u.getId());
						name 	= u.getName();
					}

					text += "<font color=\"" + color + "\">" + name + ":</font> " + m.getMessage() + "<br>";
				} else {
					if(m.getClient_id() == mySelf){

						text += "<font color=\""+ ctx.getResources().getColor(R.color.conversation_myself) +"\">" + ctx.getString(R.string.conversations_myself) + ":</font> " + m.getMessage() + "<br>";

					} else {

						Log.e(TAG, "User inconnu par le service");
					}
				}
			} else Log.w(TAG, "Message vide");
		}
		return text;
	}


	public int getMessageCount() {
		return listMessage.size();
	}

	/**
	 * Génère la couleur à utiliser pour le nom d'un utilisateur en fonction de sa place dans la liste des utilisateurs
	 * @param ctx
	 * @param userId
	 * @return
	 */
	private int getUserNameColor(Context ctx, int userId) {
		int color = 0;
		// Numéro en fonction du rang de l'user dans la liste des membres de la conversation
		int rank = LibUtil.getValueRankInList(this.listIdUser, userId);

		// Au cas où l'user n'a pas été trouvé
		if (rank < 0) rank = 0;

		// Modulo
		rank = rank % 4;	// % nombre de couleurs différentes définies dans styles.xml

		// Attribution couleur
		if (rank == 0)		color = ctx.getResources().getColor(R.color.conversation_user1);
		else if (rank == 1)	color = ctx.getResources().getColor(R.color.conversation_user2);
		else if (rank == 2)	color = ctx.getResources().getColor(R.color.conversation_user3);
		else if (rank == 3)	color = ctx.getResources().getColor(R.color.conversation_user4);

		return color;
	}

}
