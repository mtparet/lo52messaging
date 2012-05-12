package lo52.messaging.model;

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
	}

	public static final Parcelable.Creator<Content> CREATOR= new Parcelable.Creator<Content>() {
		public Content createFromParcel(Parcel in) {
			return new Content(in);
		}

		public Content[] newArray(int size) {
			return new Content[size];
		}
	};

	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(conversation_id);

	}

}
