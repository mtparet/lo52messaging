package lo52.messaging.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Message implements Parcelable{
	private String message;
	private int client_id;

	public Message(int client_id, String message) {
		this.message = message;
		this.client_id = client_id;
	}

	public Message(Parcel in) {
		this.message = in.readString();
		this.client_id = in.readInt();
	}

	public static final Parcelable.Creator<Message> CREATOR= new Parcelable.Creator<Message>() {
		public Message createFromParcel(Parcel in) {
			return new Message(in);
		}

		public Message[] newArray(int size) {
			return new Message[size];
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

	}

}
