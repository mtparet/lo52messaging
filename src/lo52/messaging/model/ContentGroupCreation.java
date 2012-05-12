package lo52.messaging.model;

import java.util.ArrayList;

import com.google.gson.annotations.SerializedName;

import android.os.Parcel;

public class ContentGroupCreation extends Content {

	@SerializedName("name")
	private String name;
	
	private ArrayList<User> userList;
	
	public ContentGroupCreation(int conversation_id, String name, ArrayList<User> userList) {
		super(conversation_id);
		this.name = name;
		this.userList = userList;
	}
	
	public ContentGroupCreation(Parcel in) {
		super(in);
		this.name = in.readString();
		userList = new ArrayList<User>();
		in.readTypedList(userList, User.CREATOR);
		// TODO Auto-generated constructor stub
	}
	
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(name);
		dest.writeList(userList);
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

}
