package lo52.messaging.model.broadcast;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Contient un message dans un conversation, le client_id de l'émetteur, l'id de conversation, et le corps du message
 * @author SYSTEMMOI
 *
 */
public class MessageBroacast implements Parcelable{
	
	private String message;
	
	private int client_id;
	
	private int conversation_id;

	/**
	 * 
	 * @param client_id
	 * @param message
	 */
	public MessageBroacast(int client_id, String message, int conversation_id) {
		this.message = message;
		this.client_id = client_id;
		this.setConversation_id(conversation_id);
	}

	public MessageBroacast(Parcel in) {
		this.message = in.readString();
		this.client_id = in.readInt();
		this.conversation_id = in.readInt();
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

	}

	public int getConversation_id() {
		return conversation_id;
	}

	public void setConversation_id(int conversation_id) {
		this.conversation_id = conversation_id;
	}

}
