package lo52.messaging.model.broadcast;

import lo52.messaging.services.NetworkService;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Contient un message dans un conversation, le client_id de l'émetteur, l'id de conversation, et le corps du message
 */
public class MessageBroacast implements Parcelable {
	
	private String message;
	
	//éventuel lien vers un fichier
	private String link_file = null;

	private int client_id;
	
	private int conversation_id;
	
	public final static String tag_parcelable = "message";

	/**
	 * Util pour la réception de message
	 * @param client_id de qui l'on reçoit le message
	 * @param message le message à envoyer
	 * @param conversation_id l'id de la conversation
	 */
	public MessageBroacast(int client_id, String message, int conversation_id) {
		this.message = message;
		this.client_id = client_id;
		this.setConversation_id(conversation_id);
	}
	
	/**
	 * Utile pour l'envoit d'un message
	 * @param conversation_id à qui l'on envoit le message
	 * @param message le message à envoyer
	 */
	public MessageBroacast(String message, int conversation_id) {
		this.message = message;
		this.setConversation_id(conversation_id);
	}

	public MessageBroacast(Parcel in) {
		this.message = in.readString();
		this.client_id = in.readInt();
		this.conversation_id = in.readInt();
		this.link_file = in.readString();
	}

	public static final Parcelable.Creator<MessageBroacast> CREATOR= new Parcelable.Creator<MessageBroacast>() {
		public MessageBroacast createFromParcel(Parcel in) {
			return new MessageBroacast(in);
		}

		public MessageBroacast[] newArray(int size) {
			return new MessageBroacast[size];
		}
	};

	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public int getClient_id() {
		return client_id;
	}
	public void setClient_id(int client_id) {
		this.client_id = client_id;
	}
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(message);
		dest.writeInt(client_id);
		dest.writeInt(conversation_id);
		dest.writeString(link_file);

	}

	public int getConversation_id() {
		return conversation_id;
	}

	public void setConversation_id(int conversation_id) {
		this.conversation_id = conversation_id;
	}
	
	/**
	 * Envoyer un message dans une conversation
	 * @param message
	 * @param id_conversation
	 */
	public void sendToNetWorkService(Context ctx){
		Intent broadcastIntent = new Intent(NetworkService.ReceiveMessage);
		Bundle bundle = new Bundle();

		bundle.putParcelable("message", this);
		broadcastIntent.putExtra("message", bundle);

		ctx.sendBroadcast(broadcastIntent);
	}
	
	public String getLink_file() {
		return link_file;
	}

	public void setLink_file(String link_file) {
		this.link_file = link_file;
	}

}
