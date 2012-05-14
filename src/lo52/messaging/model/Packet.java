package lo52.messaging.model;

import java.util.Random;

import android.os.BadParcelableException;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Packet Réseau, contient l'envoyeur, le destinataire, le type de packet, et le content
 * @author Matthieu Paret
 *
 */
public class Packet implements Parcelable{
	final static public int MESSAGE = 1;
	final static public int CREATION_GROUP = 2;
	final static public int HELLO = 3;
	final static public int ACK = 4;
	final static public int DISCONNECTED = 5;
	
	@SerializedName("content")
	private Content content;

	@SerializedName("user_envoyeur")
	private User user_envoyeur;
	
	@SerializedName("user_destinataire")
	private User user_destinataire;
	
	@SerializedName("type")
	public int type;
	
	@SerializedName("ramdom_identifiant")
	private int ramdom_identifiant;
	
	/**
	 * 
	 * @param content
	 * @param user_destinataire
	 * @param type
	 */
	public Packet(Content content, User user_destinataire, int type) {
		super();
		this.content = content;
		this.user_destinataire = user_destinataire;
		this.type = type;
		
		Random rand = new Random();
		this.setRamdom_identifiant(rand.nextInt());
	}
	
	/**
	 * Pour un packet sans content
	 * @param user_destinataire
	 * @param type
	 */
	public Packet( User user_destinataire, int type) {
		super();
		this.user_destinataire = user_destinataire;
		this.type = type;
		
		Random rand = new Random();
		this.setRamdom_identifiant(rand.nextInt());
	}

	public Packet(Parcel in) {
		try{
			type = in.readInt();
			user_destinataire = in.readParcelable(User.class.getClassLoader());
			content = in.readParcelable(Content.class.getClassLoader());
			user_envoyeur = in.readParcelable(User.class.getClassLoader());
		}catch(BadParcelableException e){
			e.printStackTrace();
		}

	}

	/**
	 * Utile pour l'enovoit d'un ACK
	 * @param type
	 * @param user_destinataire
	 * @param ramdom_identifiant
	 */
	public Packet(User user_destinataire,
			int ramdom_identifiant, int type) {
		super();
		this.type = type;
		this.user_destinataire = user_destinataire;
		this.setRamdom_identifiant(ramdom_identifiant);
	}

	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(type);
		dest.writeValue(user_destinataire);
		dest.writeValue(content);
		dest.writeValue(user_envoyeur);

	}


	public Content getMessage() {
		return content;
	}
	public void setMessage(Content message) {
		this.content = message;
	}

	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	public int getRamdom_identifiant() {
		return ramdom_identifiant;
	}

	public void setRamdom_identifiant(int ramdom_identifiant) {
		this.ramdom_identifiant = ramdom_identifiant;
	}

	public User getUser_envoyeur() {
		return user_envoyeur;
	}

	public void setUser_envoyeur(User user_envoyeur) {
		this.user_envoyeur = user_envoyeur;
	}

	public User getUser_destinataire() {
		return user_destinataire;
	}

	public void setUser_destinataire(User user_destinataire) {
		this.user_destinataire = user_destinataire;
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
