package lo52.messaging.services;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.Hashtable;

import lo52.messaging.activities.LobbyActivity;
import lo52.messaging.model.Conversation;
import lo52.messaging.model.Message;
import lo52.messaging.model.User;
import lo52.messaging.model.broadcast.MessageBroacast;
import lo52.messaging.model.network.ContentNetwork;
import lo52.messaging.model.network.PacketNetwork;
import lo52.messaging.util.Network;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.wifi.WifiConfiguration.GroupCipher;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.gson.Gson;

/**
 * Service qui gère les communcations réseau, stocke les différentes données,
 * reçoit des actions depuis les activity, leur ennvoit des messages/création de groupe,
 * met à disposition la liste des utilisateurs, conversations.
 * 
 * Principe de base:
 * 
 * *L'activity envoit:
 * 
 * Hello
 * Creation group
 * Message
 * Alive
 * Disconnected
 * 
 * *Le service envoit:
 * 
 * Message d'une conversation
 * 
 * *L'activity récupère:
 * 
 * La liste des utilisateurs
 * La listes des conversations
 * 
 * 
 * 
 * @author SYSTEMMOI
 *
 */
public class NetworkService extends Service {
	
	// Contient la liste des utilisateur
	private static Hashtable<Integer,User> listUsers = new Hashtable<Integer,User>();

	// contient la liste des conversations
	private static Hashtable<Integer,Conversation> listConversations = new Hashtable<Integer,Conversation>();

	//liste des packets en attente d'ACK
	private Hashtable<Integer,PacketNetwork> packetListACK = new Hashtable<Integer,PacketNetwork>();

	private static User user_me;
	
	private static final String TAG = "NetworkService";
	
	public static final String ReceivePacket = "NetworkService.receive.Packet";
	
	public static final String ReceiveMessage = "NetworkService.receive.Message";

	public static final String SendMessage = "NetworkService.receive.Message";

	private static final int PORT = 5008;

	public NetworkService() {

	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void onCreate()
	{

		super.onCreate();

		/*
		 * enregistrer l'intent permettant de recevoir les messages
		 */
		IntentFilter filter = new IntentFilter();
		filter.addAction(ReceivePacket);
		registerReceiver(SendMessageBroadcast, filter);
		
		/*
		 * enregistrer l'intent permettant de recevoir les messages
		 */
		IntentFilter filter2 = new IntentFilter();
		filter2.addAction(ReceiveMessage);
		registerReceiver(Message, filter2);
		
		/*
		 * création de l'utilisateur actuel
		 */

		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		String user_name = preferences.getString("prefs_userName", "default");
		
		user_me = new User(user_name);
		
		InetAddress addres = null;
		try {
			addres = Network.getWifiAddress(getApplicationContext());
		} catch (IOException e) {
			Log.e(TAG, "not possible to get wifi addresse");
			e.printStackTrace();
			return;
		}
		
		/**
		 * TODO : si le wifi est déconnecté reconnecté: intercepter les changement avec un broadcastreceiver
		 * http://stackoverflow.com/questions/5165099/android-how-to-handle-change-in-network-from-gprs-to-wi-fi-and-vice-versa-whi
		 */
		
		InetSocketAddress inetAddres = new InetSocketAddress(addres, PORT);
		
		user_me.setInetSocketAddressLocal(inetAddres);
		
		/*
		 * on lance la socket d'écoute sur le réseau 
		 */
		
		ListenSocket listenSocket = new ListenSocket();
		listenSocket.execute(null);
		
		/*
		 * On s'annonce sur le réseau
		 */
		sendBroadcastHelloNetwork();		
		
	}

	/**
	 * Permet d'envoyer un broadcast Hello
	 */
	private void sendBroadcastHelloNetwork() {
		
		PacketNetwork packet = new PacketNetwork(PacketNetwork.HELLO);
		packet.setUser_envoyeur(user_me);		
		SendPacket(packet);
		
	}

	/*
	 * Recoit un autre type de packet
	 */
	private BroadcastReceiver SendMessageBroadcast = new BroadcastReceiver(){

		@Override
		public void onReceive(Context context, Intent intent) {
			String json = intent.getStringExtra("json");

			Gson gson = new Gson();
			PacketNetwork packet = gson.fromJson(json, PacketNetwork.class);
			
			packet.setUser_envoyeur(user_me);
			
			//permet d'enregistrer dans le service ce qui se passe sur l'activity
			analysePacket(packet);
			
			SendPacket(packet);
		}
	};
	
	/*
	 * Recoit un message à envoyer à un client depuis une activity
	 */
	private BroadcastReceiver Message = new BroadcastReceiver(){

		@Override
		public void onReceive(Context context, Intent intent) {
			Bundle bundle = intent.getBundleExtra("message");
			MessageBroacast message = bundle.getParcelable(MessageBroacast.tag_parcelable);
			
			
			User user_destinataire = listUsers.get(message.getClient_id());
			
			ContentNetwork content = new ContentNetwork(message.getConversation_id(), message.getMessage());
			PacketNetwork packet = new PacketNetwork(content, user_destinataire, PacketNetwork.MESSAGE);
			
			packet.setUser_envoyeur(user_me);
			
			//permet d'enregistrer dans le service ce qui se passe sur l'activity
			analysePacket(packet);
			
			SendPacket(packet);
		}
	};


	/**
	 * Fonction qui finit la contruction du packet et l'envoit suivant son type
	 * @param packet
	 */
	private void SendPacket(PacketNetwork packet){
		
		/**
		 * Dans le cas de l'annonciation de l'arrivée dans le réseau (broadcast)
		 */
		if(packet.type == PacketNetwork.HELLO && packet.getUser_destinataire() == null){
			//Création de l'asyncTask pour envoyer le packet
			BroadcastSocket broadcastSocket = new BroadcastSocket();
			PacketNetwork[] packets = new PacketNetwork[1];
			packets[0] = packet;

			//Exécution de l'asyncTask
			broadcastSocket.execute(packets);

		}else{
			/**
			 * Dans le cas de l'envoit d'un message
			 */
			//Création de l'asyncTask pour envoyer le packet
			SendSocket sendSocket = new SendSocket();
			PacketNetwork[] packets = new PacketNetwork[1];
			packets[0] = packet;

			//Exécution de l'asyncTask
			sendSocket.execute(packets);
		}

	}


	@Override
	public int onStartCommand(Intent intent, int flags, int startId)
	{
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy()
	{
		super.onDestroy();
	}

	/**
	 * AsyncTask pour envoyer un message à un utilisateur.
	 * Socket qui reçoit un paquet déjà tout emballé
	 * Dans ce paquet il prend le client, regarde dans table son adresse ip et lui envoit
	 */
	private class SendSocket extends AsyncTask <PacketNetwork, Integer, Long> {

		protected Long doInBackground(PacketNetwork... packets) {

			PacketNetwork packet = packets[0];
			
			/*
			 * Vérification du paquet
			 */
			
			if(packet.getUser_destinataire() == null || packet.getUser_envoyeur() == null ){
				Log.e(TAG, "Error about user_dest or user_env inside packet");
				Log.e(TAG,packet.toString());
			}

			InetSocketAddress inetAddres = packet.getUser_destinataire().getInetSocketAddressLocal();

			// au cas où la local addrese est nulle on utilise celle publique
			if(inetAddres == null){
				inetAddres = packet.getUser_destinataire().getInetSocketAddressPublic();
			}
			
			if(inetAddres == null){
				Log.e(TAG, "Error, user sans addrese" + packet.toString());
				return null;
			}


			/*TODO remplacer l'User du packet par soi-même */

			DatagramSocket datagramSocket = null;
			try {
				datagramSocket = new DatagramSocket();
			} catch (SocketException e1) {
				e1.printStackTrace();
			}

			Gson gson = new Gson();
			String json = gson.toJson(packet);

			byte[] buffer = json.getBytes();

			DatagramPacket dataPacket = null;
			try {
				dataPacket = new DatagramPacket(buffer, buffer.length, inetAddres);
				datagramSocket.send(dataPacket);
				Log.d(TAG, "envoyé:" + json + "a : " + inetAddres.toString());

				//on l'ajoute dans la liste des paquets envoyé
				packetListACK.put(packet.getRamdom_identifiant(), packet);

			} catch (IOException e) {
				e.printStackTrace();
			}


			return null;
		}

		protected void onPostExecute(Long result) {
			// TODO  
		}
	}


	/*
	 * AsyncTask pour envoyer un message broadcast à tout le réseau
	 * Socket qui reçoit un paquet déjà tout emballé
	 * TODO a faire, et tester si le wifi dispo, sinon à ne pas faire
	 */
	private class BroadcastSocket extends AsyncTask <PacketNetwork, Integer, Long> {
		protected Long doInBackground(PacketNetwork... packets) {

			PacketNetwork packet = packets[0];
			
			/*
			 * Vérification du paquet
			 */
			
			if(packet.getUser_envoyeur() == null ){
				Log.e(TAG, "Error about user_env inside packet");
				Log.e(TAG,packet.toString());
			}
			
			InetAddress addres = null;
			try {
				addres = Network.getBroadcastAddress(getApplicationContext());
			} catch (IOException e2) {
				// TODO Auto-generated catch block
				Log.e(TAG, "Echec de la construction de l'adresse de broadcast");
				e2.printStackTrace();
				return (long) 0;
			}

			InetSocketAddress inetAddres = new InetSocketAddress(addres, PORT);

			/*TODO remplacer l'User du packet par soi-même */

			DatagramSocket datagramSocket = null;
			try {
				datagramSocket = new DatagramSocket();
			} catch (SocketException e1) {
				e1.printStackTrace();
			}

			Gson gson = new Gson();
			String json = gson.toJson(packet);

			byte[] buffer = json.getBytes();

			DatagramPacket dataPacket = null;
			try {
				dataPacket = new DatagramPacket(buffer, buffer.length, inetAddres);
				datagramSocket.send(dataPacket);

				//on l'ajoute dans la liste des paquets envoyé
				packetListACK.put( packet.getRamdom_identifiant(), packet);

			} catch (IOException e) {
				e.printStackTrace();
			}


			return null;
		}

		protected void onPostExecute(Long result) {
			// TODO  
		}
	}

	/**
	 * AsyncTask pour écouter la socket sur le port local
	 * 
	 * Integer non utiliser pour le moment
	 *
	 */
	private class ListenSocket extends AsyncTask <Integer, Integer, Long> {
		protected Long doInBackground(Integer... integers) {

			/*
			 *  initier et écouter sur la socket qui bind le port local et récupérer les données*/
			DatagramSocket datagramSocket;
			try {
				datagramSocket = new DatagramSocket(PORT);

				do{
					byte[] buffer2 = new byte[300000]; //TODO vérifier à l'envoit que la taille du packet n'excède pas la taille du buffer
					DatagramPacket dataPacket = new DatagramPacket(buffer2, buffer2.length);

					try {
						datagramSocket.receive(dataPacket);
						analysePacket(dataPacket);

					} catch (IOException e) {
						e.printStackTrace();
					}

				}while(true);


			} catch (SocketException e) {
				e.printStackTrace();
			}
			return null;
		}

		protected void onPostExecute(Long result) {
			// TODO envoyer le message 
		}
	}


	/**
	 *	 
	 * Foncton déclenché pour chaque Paquet reçu,
	 * 
	 * A un switch sur les le type de paquet et va exécuter les fonctions nécessaires
	 * 
	 * @param dataPacket, contient un packet
	 */
	private void analysePacket(DatagramPacket dataPacket){

		String json = new String(dataPacket.getData(), 0, dataPacket.getLength());  
		Log.d(TAG, "Analyse packet:" + json);

		Gson gson = new Gson();
		PacketNetwork packetReceive = gson.fromJson(json, PacketNetwork.class);




		/**
		 * Si le packet n'est pas un ACK, on envoit un ACK
		 */
		if(packetReceive.type != PacketNetwork.ACK ){

			 //On envoit un ACK
			PacketNetwork packetSend = new PacketNetwork(packetReceive.getUser_envoyeur(), packetReceive.getRamdom_identifiant(),PacketNetwork.ACK);

			packetSend.setUser_envoyeur(user_me);

			SendPacket(packetSend);

		}

		analysePacket(packetReceive);

	}
	
	/**
	 *	 
	 * Foncton déclenché pour chaque Paquet reçu,
	 * 
	 * A un switch sur les le type de paquet et va exécuter les fonctions nécessaires
	 * 
	 * @param packet, contient un packet
	 */
	private void analysePacket(PacketNetwork packet){
		/*
		 * on exécute les traitement à faire au niveau de la couche réseau
		 */
		switch (packet.type){
		case PacketNetwork.ACK : paquetACK(packet);
		break;
		case PacketNetwork.CREATION_GROUP : paquetCreationGroup(packet);
		break;
		case PacketNetwork.DISCONNECTED : paquetDisconnecter(packet);
		break;
		case PacketNetwork.HELLO : paquetHello(packet);
		break;
		case PacketNetwork.MESSAGE : paquetMessage(packet);
		break;
		default: paquetInconnu(packet);
		break;

		}
	}


	/**
	 * Traite un message reçu, le fait suivre à l'activity correspondant
	 * Met l'état de l'utilisateur à "en ligne"
	 * @param packetReceive
	 */
	private void paquetMessage(PacketNetwork packetReceive) {
		// TODO Auto-generated method stub

		if(listConversations.containsKey(packetReceive.getContent().getConversation_id())){
			Message message = new Message(packetReceive.getContent().getClient_id(),packetReceive.getContent().getMessage());
			listConversations.get(packetReceive.getContent().getConversation_id()).addMessage(message);
			
			if(packetReceive.getUser_envoyeur() != user_me){
				Intent broadcastIntent = new Intent(NetworkService.ReceiveMessage);
				Bundle bundle = new Bundle();

				MessageBroacast messageBroad = new MessageBroacast(message.getClient_id(), message.getMessage(), packetReceive.getContent().getConversation_id());
				bundle.putParcelable("message", messageBroad);
				broadcastIntent.putExtra(MessageBroacast.tag_parcelable, bundle);

				sendBroadcast(broadcastIntent);
			}

			
		}else{
			Log.e(TAG, "Conversation non existante");
		}
		
		// on met le user à alive
		listUsers.get(packetReceive.getUser_envoyeur().getId()).setAlive(true);

	}

	/**
	 * Traite un message hello 
	 * Vérfie que l'utilisateur existe sinon l'utilisateur est rajouté à la liste
	 * Met l'état de l'utilisateur à "en ligne"
	 * @param packetReceive
	 */
	private void paquetHello(PacketNetwork packetReceive) {

		if(packetReceive.getUser_envoyeur().getId() == user_me.getId()){
			return;
		}
		
		// on teste si l'user est connu
		if( listUsers.containsKey(packetReceive.getUser_envoyeur().getId()) ){

			// on teste si il est différent de celui stocké
			if(listUsers.get(packetReceive.getUser_envoyeur().getId()) != packetReceive.getUser_envoyeur()){

				//alors on le met à ajour
				listUsers.remove(packetReceive.getUser_envoyeur().getId());
				listUsers.put(packetReceive.getUser_envoyeur().getId(), packetReceive.getUser_envoyeur());
			}

		}else{
			//on l'ajoute à la liste
			listUsers.put(packetReceive.getUser_envoyeur().getId(), packetReceive.getUser_envoyeur());
		}
		
		// on le met à alive
		listUsers.get(packetReceive.getUser_envoyeur().getId()).setAlive(true);

	}


	/**
	 * Traite un message disconnected
	 * Met l'état de l'utilisateur à disconnected
	 * @param packetReceive
	 */
	private void paquetDisconnecter(PacketNetwork packetReceive) {
		// TODO Auto-generated method stub
		if(packetReceive.getUser_envoyeur().getId() == user_me.getId()){
			return;
		}
		// on teste si l'user est connu
		if( listUsers.containsKey(packetReceive.getUser_envoyeur().getId()) ){
			// on le met à no alive
			listUsers.get(packetReceive.getUser_envoyeur().getId()).setAlive(false);
		}
		
		//on le fait le suivre à l'activity qui gère la liste des users (et peut être aussi à l'activity qui gère les conversations)
		//sendToActivity(packetReceive,"lo52.messaging.activities.LobbyActivity");

	}

	/**
	 * Traite la la création d'un groupe
	 * Ajoute un groupe avec sa liste de User
	 * Vérifie que tout les users sont connus sinon on les rajoute dans notre liste
	 * @param packetReceive
	 */
	private void paquetCreationGroup(PacketNetwork packetReceive) {
		if(listConversations.containsKey(packetReceive.getContent().getConversation_id())){
			
			//Si le nom a changé, on le met à jour
			if( !(listConversations.get(packetReceive.getContent().getConversation_id()).getConversation_name() ==  packetReceive.getContent().getConversation_name())){
				Conversation conversation = listConversations.get(packetReceive.getContent().getConversation_id());
				conversation.setConversation_name(packetReceive.getContent().getConversation_name());
				
				listConversations.remove(packetReceive.getContent().getConversation_id());
				
				listConversations.put(conversation.getConversation_id(), conversation);
			}
			
		}else{
			Conversation conversation = new Conversation(packetReceive.getContent().getConversation_id(), packetReceive.getContent().getConversation_name());
			listConversations.put(conversation.getConversation_id(),conversation);
			
		}

		Log.w(TAG, "Envoi d'un broadcast de création de groupe");
		
		//sendToActivity(packetReceive,"lo52.messaging.activities.");
		//sendToActivity(packetReceive,"lo52.messaging.activities.LobbyActivity");

	}

	/**
	 * Traite un ACK
	 * Retire le paquet de la liste des paquets en attente d'ACK
	 * TODO: voir si on renvoit quelque chose à l'activity
	 * 
	 * @param packetReceive
	 */
	private void paquetACK(PacketNetwork packetReceive) {

		packetListACK.remove(packetReceive.getRamdom_identifiant());
		
		//on considère un ACK comme un hello le cas échéant
		paquetHello(packetReceive);

		//sendToActivity(packetReceive,"lo52.messaging.activities.LobbyActivity");

	}
	
	/**
	 * Pour traiter un paquet d'un type inconnu
	 * @param packet
	 */
	private void paquetInconnu(PacketNetwork packet) {
		// TODO Auto-generated method stub
		Log.w(TAG, "Packet inconnu");
	}

	
	public static Hashtable<Integer, User> getListUsers() {
		return listUsers;
	}

	public static void setListUsers(Hashtable<Integer, User> listUsersE) {
		listUsers = listUsersE;
	}

	public static Hashtable<Integer, Conversation> getListConversations() {
		return listConversations;
	}

	public static void setListConversations(Hashtable<Integer, Conversation> listConversations) {
		NetworkService.listConversations = listConversations;
	}

	public static User getUser_me() {
		return user_me;
	}

	public static void setUser_me(User user_me) {
		NetworkService.user_me = user_me;
	}
	
	private void checkAddresseLocalPublic(DatagramPacket packetReceive){
		/*
		 * si l'utilisateur n'a pas spécifié son adresse local et que l'adresse est local, on ajoute son adrresse publique avec le port par défault de l'application
		 *
		if(packetReceive.getUser_envoyeur().getInetSocketAddressLocal() == null ){
			
			//TODO à suprimer, on suppose que l'envoyeur à toujours spécifier la bonne addresse local
			//User user_envoyeur  = packetReceive.getUser_envoyeur();
			//user_envoyeur.setInetSocketAddressLocal(new InetSocketAddress(dataPacket.getAddress(), PORT));
			//packetReceive.setUser_envoyeur(user_envoyeur);

			/*
			 * si l'utilisateur n'a pas d'adresse public mais une local, on va l'ajouter à la public on vérifie que ce n'est déjà pas la local
			 *

		}else if(packetReceive.getUser_envoyeur().getInetSocketAddressPublic() == null ){
			User user_envoyeur  = packetReceive.getUser_envoyeur();

			InetSocketAddress inetSocket = new InetSocketAddress(dataPacket.getAddress(), dataPacket.getPort());

			//si ce n'est pas la même adresse que son adresse local alors on l'ajoute dans public
			if(inetSocket != user_envoyeur.getInetSocketAddressLocal()){
				user_envoyeur.setInetSocketAddressPublic(inetSocket);
				packetReceive.setUser_envoyeur(user_envoyeur);
			}
		}*/
	}

}
