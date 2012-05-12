package lo52.messaging.model;

import java.net.InetAddress;

import com.google.gson.annotations.SerializedName;

import android.os.Parcel;
import android.os.Parcelable;


public class Packet implements Parcelable{
	final static public int MESSAGE = 1;
	final static public int CREATION_GROUP = 2;
	final static public int HELLO = 3;
	final static public int ACK = 4;
	final static public int DISCONNECTED = 5;
	
	@SerializedName("content")
	private Content content;

	@SerializedName("user")
	private User user;
	
	@SerializedName("client_id")
	private int client_id;
	
	@SerializedName("type")
	public int type;

	public Packet(Content content, User user, int client_id, int type) {
		super();
		this.content = content;
		this.user = user;
		this.client_id = client_id;
		this.type = type;
	}

	public Packet(Parcel in) {
		type = in.readInt();
		client_id = in.readInt();
		user = in.readParcelable(User.class.getClassLoader());

		switch (type){
		case MESSAGE : content = in.readParcelable(ContentMessage.class.getClassLoader());
		break;
		case CREATION_GROUP : content = in.readParcelable(ContentGroupCreation.class.getClassLoader());
		break;
		}
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(type);
		dest.writeInt(client_id);
		dest.writeValue(user);
		dest.writeValue(content);

	}

	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public Content getMessage() {
		return content;
	}
	public void setMessage(Content message) {
		this.content = message;
	}
	public int getClient_id() {
		return client_id;
	}
	public void setClient_id(int client_id) {
		this.client_id = client_id;
	}
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	public static final Parcelable.Creator<Packet> CREATOR= new Parcelable.Creator<Packet>() {
		public Packet createFromParcel(Parcel in) {
			return new Packet(in);
		}

		public Packet[] newArray(int size) {
			return new Packet[size];
		}
	};

}
