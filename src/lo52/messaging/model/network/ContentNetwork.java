package lo52.messaging.model.network;

import java.util.ArrayList;

import lo52.messaging.model.User;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.SerializedName;

/**
 * Contient le contenu d'un paquet, à remplir suivant le type.
 * type ACK, HELLO, DISCONNECTED, ALIVE => content == null
 * type MESSAGE : conversation_id + conversation_name + message
 * type CREATION_GROUP : conversation_id + conversation_name + userList
 * @author Matthieu Paret
 *
 */
public class ContentNetwork implements Parcelable{
	
	@SerializedName("conversation_id")
	private int conversation_id;
	
	@SerializedName("conversation_name")
	private String conversation_name;
	
	@SerializedName("message")
	private String message;
	
	@SerializedName("client_id")
	private int client_id;
	
	// List des utilisateur dans la conversation
	private ArrayList<User> userList;
	
	/**
	 * 
	 * @param conversation_id
	 * @param message
	 */
	public ContentNetwork(int conversation_id, String message) {
		this.conversation_id = conversation_id;
		this.message = message;
		// TODO Auto-generated constructor stub
	}

	public ContentNetwork( int conversation_id) {
		this.conversation_id = conversation_id;
	}
	
	/**
	 * 
	 * @param conversation_id
	 * @param name
	 * @param userList
	 */
	public ContentNetwork(int conversation_id, String name, ArrayList<User> userList) {
		this.conversation_id = conversation_id;
		this.conversation_name = name;
		this.userList = userList;
	}
	
	public ContentNetwork(Parcel in) {
		this.conversation_id = in.readInt();
		this.conversation_name = in.readString();
		userList = new ArrayList<User>();
		in.readTypedList(userList, User.CREATOR);
		this.message = in.readString();
	}

	public static final Parcelable.Creator<ContentNetwork> CREATOR= new Parcelable.Creator<ContentNetwork>() {
		public ContentNetwork createFromParcel(Parcel in) {
			return new ContentNetwork(in);
		}

		public ContentNetwork[] newArray(int size) {
			return new ContentNetwork[size];
		}
	};
	

	public int getConversation_id() {
		return conversation_id;
	}

	public void setConversation_id(int conversation_id) {
		this.conversation_id = conversation_id;
	}

	public String getConversation_name() {
		return conversation_name;
	}
	public void setConversation_name(String name) {
		this.conversation_name = name;
	}
	public ArrayList<User> getUserList() {
		return userList;
	}
	public void setUserList(ArrayList<User> userList) {
		this.userList = userList;
	}


	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(conversation_id);
		dest.writeString(conversation_name);
		dest.writeList(userList);
		dest.writeString(message);


	}
	
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
	
	


}
