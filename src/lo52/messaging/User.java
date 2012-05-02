package lo52.messaging;

import android.os.Parcel;
import android.os.Parcelable;

public class User implements  Parcelable{
	
	private String name;
	private int id;

    public User(int id, String name) {
        this.name = name;
        this.id = id;
    }

	public User(Parcel in) {
        this(in.readInt(),in.readString());
    }
	
	public static final Parcelable.Creator<User> CREATOR= new Parcelable.Creator<User>() {
		public User createFromParcel(Parcel in) {
			return new User(in);
		}

		public User[] newArray(int size) {
			return new User[size];
		}
	};
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(name);
		dest.writeInt(id);
		
	}
	

}
