package lo52.messaging.model;

import java.net.InetSocketAddress;
import java.util.Random;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * D�crit un utilisateur. 
 * De base contient seulement un nom et un id g�n�r� pseudo al�atoirement
 * Son adresse ip local/public doit �tre mise � jour ensuite
 * @author Matthieu Paret
 *
 */
public class User implements  Parcelable{
	

	private InetSocketAddress inetSocketAddressPublic;
	private boolean alive = false;
	private String name;
	private int id;

	/**
	 * 
	 * @param id
	 * @param name
	 * @param inetAddress
	 */
    public User(String name) {
        this.name = name;
        
        //on génère son id aléatoirement
        Random rand = new Random();
        this.id = rand.nextInt();
    }

	public User(Parcel in) {
		this.name = in.readString();
		this.id = in.readInt();
		this.inetSocketAddressLocal = (InetSocketAddress) in.readValue(InetSocketAddress.class.getClassLoader());
		this.inetSocketAddressPublic = (InetSocketAddress) in.readValue(InetSocketAddress.class.getClassLoader());
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
		dest.writeValue(inetSocketAddressLocal);
		dest.writeValue(inetSocketAddressPublic);
		
	}

	public InetSocketAddress getInetSocketAddressPublic() {
		return inetSocketAddressPublic;
	}

	public void setInetSocketAddressPublic(InetSocketAddress inetSocketAddressPublic) {
		this.inetSocketAddressPublic = inetSocketAddressPublic;
	}
	
	private InetSocketAddress inetSocketAddressLocal;
	public InetSocketAddress getInetSocketAddressLocal() {
		return inetSocketAddressLocal;
	}

	public void setInetSocketAddressLocal(InetSocketAddress inetSocketAddressLocal) {
		this.inetSocketAddressLocal = inetSocketAddressLocal;
	}

	public boolean isAlive() {
		return alive;
	}

	public void setAlive(boolean alive) {
		this.alive = alive;
	}

}
