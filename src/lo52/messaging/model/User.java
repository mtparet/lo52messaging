package lo52.messaging.model;

import java.net.InetAddress;
import java.net.InetSocketAddress;

import android.os.Parcel;
import android.os.Parcelable;

public class User implements  Parcelable{
	
	private InetSocketAddress inetSocketAddress;
	private String name;
	private int id;

    public User(int id, String name, InetSocketAddress inetAddress) {
        this.name = name;
        this.id = id;
        this.inetSocketAddress = inetAddress;
    }

	public User(Parcel in) {
        this(in.readInt(),in.readString(),(InetSocketAddress) in.readValue(InetSocketAddress.class.getClassLoader()));
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
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(name);
		dest.writeInt(id);
		dest.writeValue(inetSocketAddress);
		
	}

	public InetSocketAddress getInetSocketAddress() {
		return inetSocketAddress;
	}

	public void setInetSocketAddress(InetSocketAddress inetAddress) {
		this.inetSocketAddress = inetAddress;
	}
	

}
