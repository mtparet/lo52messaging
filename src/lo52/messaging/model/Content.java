package lo52.messaging.model;

import java.util.ArrayList;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.SerializedName;

public class Content implements Parcelable{
	
	@SerializedName("conversaton_id")
	private int conversation_id;
	
	public int getConversation_id() {
		return conversation_id;
	}

	public void setConversation_id(int conversation_id) {
		this.conversation_id = conversation_id;
	}

	public Content( int conversation_id) {
		this.conversation_id = conversation_id;
	}

	public Content(Parcel in) {
		this.conversation_id = in.readInt();
		this.name = in.readString();
		userList = new ArrayList<User>();
		in.readTypedList(userList, User.CREATOR);
		this.message = in.readString();
	}

	public static final Parcelable.Creator<Content> CREATOR= new Parcelable.Creator<Content>() {
		public Content createFromParcel(Parcel in) {
			return new Content(in);
		}

		public Content[] newArray(int size) {
			return new Content[size];
		}
	};
	
	@SerializedName("name")
	private String name;
	
	private ArrayList<User> userList;
	
	/**
	 * 
	 * @param conversation_id
	 * @param name
	 * @param userList
	 */
	public Content(int conversation_id, String name, ArrayList<User> userList) {
		this.conversation_id = conversation_id;
		this.name = name;
		this.userList = userList;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
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
		dest.writeString(name);
		dest.writeList(userList);
		dest.writeString(message);


	}
	
	@SerializedName("message")
	private String message;
	
	public Content(int conversation_id, String message) {
		this.conversation_id = conversation_id;
		this.message = message;
		// TODO Auto-generated constructor stub
	}
	
	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
	


}
