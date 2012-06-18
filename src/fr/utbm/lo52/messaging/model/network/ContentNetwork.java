package fr.utbm.lo52.messaging.model.network;

import java.util.ArrayList;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import fr.utbm.lo52.messaging.model.User;

/**
 * Contient le contenu d'un paquet, à remplir suivant le type.
 * type ACK, HELLO, DISCONNECTED, ALIVE => content == null
 * type MESSAGE : conversation_id + conversation_name + message
 * type CREATION_GROUP : conversation_id + conversation_name + userList
 *
 */
public class ContentNetwork implements Parcelable{
	
	//Détermine le type du content si c'est un message
	public static final int TEXT = 1;
	public static final int JPEG = 2;
	public static final int MP3 = 3;

	@SerializedName("conversation_id")
	private int conversation_id;
	
	@SerializedName("conversation_name")
	private String conversation_name;
	
	@SerializedName("message")
	private String message;
	
	@SerializedName("client_id")
	private int client_id;
	
	@SerializedName("lon")
	private float lon;
	
	@SerializedName("lat")
	private float lat;
	
	//Sert à stocker du contenu de grande taille (image ou son ou autres fichiers), peut nécessité d'être réassemblé
	@SerializedName("byte_content")
	private byte[] byte_content;
	
	
	//Sert à stocker le nom du fichier envoyé
	@SerializedName("file_name")
	private String file_name;
	
	// List des utilisateur dans la conversation
	private ArrayList<User> userList;
	
	/**
	 * Pour envoyer un message
	 * @param conversation_id
	 * @param message
	 * @param client_id 
	 */
	public ContentNetwork(int conversation_id, String message, int client_id) {
		this.conversation_id = conversation_id;
		this.message = message;
		this.client_id = client_id;
		// TODO Auto-generated constructor stub
	}

	/**
	 * pour les infos de localisation
	 * @param lat
	 * @param lon
	 * @param client_id
	 */
	public ContentNetwork(float lat, float lon, int client_id) {
		this.lat = lat;
		this.lon = lon;
		this.client_id = client_id;
	}
	


	public ContentNetwork( int conversation_id) {
		this.conversation_id = conversation_id;
	}
	
	/**
	 * Pour créer une conversation
	 * @param conversation_id
	 * @param name
	 * @param userList
	 */
	public ContentNetwork(int conversation_id, String name, ArrayList<User> userList) {
		this.conversation_id = conversation_id;
		this.conversation_name = name;
		this.userList = userList;
	}
	
	public ContentNetwork(Parcel in) {
		this.conversation_id = in.readInt();
		this.conversation_name = in.readString();
		userList = new ArrayList<User>();
		in.readTypedList(userList, User.CREATOR);
		this.message = in.readString();
	}

	public ContentNetwork() {
		// TODO Auto-generated constructor stub
	}

	public ContentNetwork(int conversation_id, String conversation_name,
			String message, int client_id, float lon, float lat,
			byte[] byte_content, ArrayList<User> userList, String file_name) {
		super();
		this.conversation_id = conversation_id;
		this.conversation_name = conversation_name;
		this.message = message;
		this.client_id = client_id;
		this.lon = lon;
		this.lat = lat;
		this.byte_content = byte_content;
		this.userList = userList;
		this.file_name = file_name;
	}

	public ContentNetwork(ContentNetwork content) {
		this(content.conversation_id,content.conversation_name,content.message,content.client_id,content.lon,content.lat,content.byte_content,content.userList, content.file_name);
	}

	public static final Parcelable.Creator<ContentNetwork> CREATOR= new Parcelable.Creator<ContentNetwork>() {
		public ContentNetwork createFromParcel(Parcel in) {
			return new ContentNetwork(in);
		}

		public ContentNetwork[] newArray(int size) {
			return new ContentNetwork[size];
		}
	};
	

	public int getConversation_id() {
		return conversation_id;
	}

	public void setConversation_id(int conversation_id) {
		this.conversation_id = conversation_id;
	}

	public String getConversation_name() {
		return conversation_name;
	}
	public void setConversation_name(String name) {
		this.conversation_name = name;
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
		dest.writeString(conversation_name);
		dest.writeList(userList);
		dest.writeString(message);


	}
	
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
	
	
	public float getLon() {
		return lon;
	}

	public void setLon(float lon) {
		this.lon = lon;
	}

	public float getLat() {
		return lat;
	}

	public void setLat(float lat) {
		this.lat = lat;
	}

	public byte[] getByte_content() {
		return byte_content;
	}

	public void setByte_content(byte[] byte_content) {
		this.byte_content = byte_content;
	}

	public String getFile_name() {
		return file_name;
	}

	public void setFile_name(String file_name) {
		this.file_name = file_name;
	}

}
