package lo52.messaging.model.network;

import java.util.ArrayList;
import java.util.Random;

import lo52.messaging.model.User;
import lo52.messaging.services.NetworkService;
import android.os.BadParcelableException;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Packet Réseau, contient l'envoyeur, le destinataire, le type de packet, et le content
 * @author Matthieu Paret
 *
 */
public class PacketNetwork implements Parcelable{
	final static public int MESSAGE = 1;
	final static public int CREATION_GROUP = 2;
	final static public int HELLO = 3;
	final static public int ACK = 4;
	final static public int DISCONNECTED = 5;
	final static public int ALIVE = 6;
	final static public int LOCALISATION = 7;


	@SerializedName("content")
	private ContentNetwork content;

	@SerializedName("user_envoyeur")
	private User user_envoyeur;

	@SerializedName("user_destinataire")
	private User user_destinataire;

	@SerializedName("type")
	public int type;

	@SerializedName("ramdom_identifiant")
	private int ramdom_identifiant;
	
	@SerializedName("ramdom_identifiant_groupe")
	private int ramdom_identifiant_groupe = 0;
	
	@SerializedName("nb_packet_groupe")
	private int nb_packet_groupe = 0;

	@SerializedName("next_packet")
	private int next_packet;

	@SerializedName("previous_packet")
	private int previous_packet;
	
	//en millisecondes
	@SerializedName("date_send")
	private int date_send;

	/**
	 * 
	 * @param content
	 * @param user_destinataire
	 * @param type
	 */
	public PacketNetwork(ContentNetwork content, User user_destinataire, int type) {
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
	public PacketNetwork( User user_destinataire, int type) {
		super();
		this.user_destinataire = user_destinataire;
		this.type = type;

		Random rand = new Random();
		this.setRamdom_identifiant(rand.nextInt());
	}

	/**
	 * Pour un packet Hello
	 * @param type
	 */
	public PacketNetwork( int type) {
		super();
		this.type = type;

		Random rand = new Random();
		this.setRamdom_identifiant(rand.nextInt());
	}

	/**
	 * Utile pour l'enovoit d'un ACK
	 * @param type
	 * @param user_destinataire
	 * @param ramdom_identifiant
	 */
	public PacketNetwork(User user_destinataire,
			int ramdom_identifiant, int type) {
		super();
		this.type = type;
		this.user_destinataire = user_destinataire;
		this.setRamdom_identifiant(ramdom_identifiant);
	}

	public PacketNetwork(Parcel in) {
		try{
			type = in.readInt();
			user_destinataire = in.readParcelable(User.class.getClassLoader());
			content = in.readParcelable(ContentNetwork.class.getClassLoader());
			user_envoyeur = in.readParcelable(User.class.getClassLoader());
		}catch(BadParcelableException e){
			e.printStackTrace();
		}

	}


	public PacketNetwork(PacketNetwork pn_base) {
		this.content = new ContentNetwork(pn_base.getContent());
		this.next_packet = pn_base.next_packet;
		this.previous_packet = pn_base.previous_packet;
		this.ramdom_identifiant = pn_base.ramdom_identifiant;
		this.type = pn_base.type;
		this.user_destinataire = pn_base.user_destinataire;
		this.user_envoyeur = pn_base.user_envoyeur;
		this.ramdom_identifiant_groupe = pn_base.ramdom_identifiant_groupe;
		this.nb_packet_groupe = pn_base.nb_packet_groupe;
		this.date_send = pn_base.date_send;
	}

	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(type);
		dest.writeValue(user_destinataire);
		dest.writeValue(content);
		dest.writeValue(user_envoyeur);

	}


	public ContentNetwork getContent() {
		return content;
	}
	public void setContent(ContentNetwork message) {
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

	public int getNext_packet() {
		return next_packet;
	}

	public void setNext_packet(int next_packet) {
		this.next_packet = next_packet;
	}

	public static final Parcelable.Creator<PacketNetwork> CREATOR= new Parcelable.Creator<PacketNetwork>() {
		public PacketNetwork createFromParcel(Parcel in) {
			return new PacketNetwork(in);
		}

		public PacketNetwork[] newArray(int size) {
			return new PacketNetwork[size];
		}
	};

	/*
	 * Fonction a appelé lorsque l'on détecte que la taille du packet est trop grand.
	 */
	public static ArrayList<PacketNetwork> division(PacketNetwork packet){
		/*   on va supposer que toute la partie "texte" n'occupe pas plus de 2000 bytes.
		 */
		int size_dispo = (NetworkService.BUFFER_SIZE)/2 - 4000;
		int size_byte_content = packet.getContent().getByte_content().length;

		int nb_packet = (size_byte_content / size_dispo) + 1;

		ArrayList<PacketNetwork> listPacketNetworks = new ArrayList<PacketNetwork>();

		int size_new_content = size_byte_content / nb_packet;

		Random rand = new Random();
		int[] random_numbers = new int[nb_packet];

		for(int i = 0; i < nb_packet; i++){
			random_numbers[i] = rand.nextInt();
		}
		
		ContentNetwork cn = new ContentNetwork();
		cn.setClient_id(packet.getContent().getClient_id());
		cn.setConversation_id(packet.getContent().getConversation_id());
		cn.setMessage(packet.getContent().getMessage());
		cn.setFile_name(packet.getContent().getFile_name());
				
		PacketNetwork pn_base = new PacketNetwork(packet.type);
		pn_base.setContent(cn);
		pn_base.setNext_packet(0);
		pn_base.setPrevious_packet(0);
		pn_base.setRamdom_identifiant(0);
		pn_base.setRamdom_identifiant_groupe(rand.nextInt());
		pn_base.type = packet.type;
		pn_base.setUser_envoyeur(packet.user_envoyeur);
		pn_base.setUser_destinataire(packet.user_destinataire);
		pn_base.setNb_packet_groupe(nb_packet);

		for(int i = 0; i < nb_packet; i++){
			int start_copy;
			int end_copy;
			PacketNetwork pn = new PacketNetwork(pn_base);
			pn.setRamdom_identifiant(random_numbers[i]);
			start_copy = i * size_new_content;
			//Si c'est le dernier packet on le remplit avec les bytes restant
			if(i ==  (nb_packet -1)){
				end_copy = size_byte_content;
				pn.next_packet = 0;
				pn.setPrevious_packet(random_numbers[i-1]);
			}else{
				end_copy = (i+1) * size_new_content;
				pn.next_packet = random_numbers[i+1];

				if(i == 0){
					pn.setPrevious_packet(0);

				}else{
					pn.setPrevious_packet(random_numbers[i-1]);
				}
			}

			byte[] new_content_byte = new byte[end_copy - start_copy];

			System.arraycopy(packet.getContent().getByte_content(), start_copy, new_content_byte, 0, (end_copy - start_copy));

			pn.getContent().setByte_content(new_content_byte);

			listPacketNetworks.add(pn);
		}

		return listPacketNetworks;
	}

	/*
	 * Fonction a appelé lorsque l'on détecte que la taille du packet est trop grand.
	 */
	public static PacketNetwork reassemble(ArrayList<PacketNetwork> listPacket){
		/*
		 * on re tri la liste dans le bon ordre
		 */

		int new_size = 0;
		for(PacketNetwork packet : listPacket){
				new_size += packet.getContent().getByte_content().length;
		}

		byte[] new_content = new byte[new_size];

		// on boucle en commençant par la fin
		int start_copy = 0;
		PacketNetwork current = null;
		
		for(PacketNetwork packet : listPacket){
			if(packet.previous_packet == 0){
				current = packet;
			}
		}

		//faire avec le previous aussi
		for(int i = 0; i < listPacket.size(); i++){
			System.arraycopy(current.getContent().getByte_content(), 0, new_content, start_copy, current.getContent().getByte_content().length);
			start_copy += current.getContent().getByte_content().length;

			PacketNetwork next = null;
			for(PacketNetwork packet : listPacket){
				if(packet.getRamdom_identifiant() == current.getNext_packet()){
					next = packet;
				}
			}	
			
			current = next;

		}
		

		PacketNetwork packet = listPacket.get(0);
		packet.getContent().setByte_content(new_content);
		packet.setNext_packet(0);
		packet.setPrevious_packet(0);
		packet.setRamdom_identifiant_groupe(0);
		return packet;
	}

	public int getPrevious_packet() {
		return previous_packet;
	}

	public void setPrevious_packet(int previous_packet) {
		this.previous_packet = previous_packet;
	}

	public int getRamdom_identifiant_groupe() {
		return ramdom_identifiant_groupe;
	}

	public void setRamdom_identifiant_groupe(int ramdom_identifiant_groupe) {
		this.ramdom_identifiant_groupe = ramdom_identifiant_groupe;
	}

	public int getNb_packet_groupe() {
		return nb_packet_groupe;
	}

	public void setNb_packet_groupe(int nb_packet_groupe) {
		this.nb_packet_groupe = nb_packet_groupe;
	}

	public int getDate_send() {
		return date_send;
	}

	public void setDate_send(int date_send) {
		this.date_send = date_send;
	}
}
