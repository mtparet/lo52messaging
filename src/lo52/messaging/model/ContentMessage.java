package lo52.messaging.model;

import com.google.gson.annotations.SerializedName;

import android.os.Parcel;

public class ContentMessage extends Content {
	
	@SerializedName("message")
	private String message;
	
	public ContentMessage(int conversation_id, String message) {
		super(conversation_id);
		this.message = message;
		// TODO Auto-generated constructor stub
	}

	public ContentMessage(Parcel in) {
		super(in);
		this.message = in.readString();
		// TODO Auto-generated constructor stub
	}
	
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(message);
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
	

}
