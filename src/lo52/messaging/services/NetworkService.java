package lo52.messaging.services;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;

import lo52.messaging.model.Content;
import lo52.messaging.model.Conversation;
import lo52.messaging.model.Packet;
import lo52.messaging.model.User;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.google.gson.Gson;

/**
 * Service qui g�re les communcations r�seaux, stocke les diff�rentes donn�es,
 * re�oit des actions depuis les activity, leur ennvoit des messages/cr�ation de groupe,
 * met � disposition la liste des utilisateurs, conversations.
 * 
 * @author SYSTEMMOI
 *
 */
public class NetworkService extends Service {

	// Contient la liste des utilisateur
	private static HashMap<Integer,User> listUsers = new HashMap<Integer,User>();

	// contient la liste des conversations
	private static HashMap<Integer,Conversation> listConversations = new HashMap<Integer,Conversation>();

	//liste des packets en attente d'ACK
	private HashMap<Integer,Packet> packetListACK = new HashMap<Integer,Packet>();

	private static User user_me;


	public NetworkService() {

	}

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
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
		filter.addAction("SendMessage");
		registerReceiver(SendMessageBroadcast, filter);


		/*
		 * Exemple pour transmettre un message récupéré à l'activity
		 */
		Intent broadcastIntent = new Intent("SendMessage");
		Bundle bundle = new Bundle();

		InetSocketAddress inetAddres = new InetSocketAddress("127.0.0.1",5008);


		User user = new User("cesttoi");
		user.setInetSocketAddressLocal(inetAddres);

		user_me = new User("cestmoi");

		Content message = new Content(3535,"client1");
		Packet packet = new Packet(message,user, Packet.HELLO);

		Gson gson = new Gson();
		String json = gson.toJson(packet);

		broadcastIntent.putExtra("json", json);

		sendBroadcast(broadcastIntent);

		ListenSocket r1 = new ListenSocket();
		r1.execute(null);

	}

	/*
	 * Recoit un message à envoyer à un client depuis une activity
	 */
	private BroadcastReceiver SendMessageBroadcast = new BroadcastReceiver(){

		@Override
		public void onReceive(Context context, Intent intent) {
			String json = intent.getStringExtra("json");

			Gson gson = new Gson();
			Packet packet = gson.fromJson(json, Packet.class);
			SendMessage(packet);

		}

	};


	/**
	 * Fonction qui finit la contruction du packet et l'envoit suivant son type
	 * @param packet
	 */
	private void SendMessage(Packet packet){

		packet.setUser_envoyeur(user_me);
		
		/**
		 * Dans le cas de l'annonciation de l'arrivée dans le réseau (broadcast)
		 */
		if(packet.type == Packet.HELLO && packet.getUser_destinataire() == null){
			//Création de l'asyncTask pour envoyer le packet
			BroadcastSocket broadcastSocket = new BroadcastSocket();
			Packet[] packets = new Packet[1];
			packets[0] = packet;

			//Exécution de l'asyncTask
			broadcastSocket.execute(packets);

		}else{
			/**
			 * Dans le cas de l'envoit d'un message
			 */
			//Création de l'asyncTask pour envoyer le packet
			SendSocket sendSocket = new SendSocket();
			Packet[] packets = new Packet[1];
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

	/*
	 * AsyncTask pour envoyer un message à un utilisateur
	 * Socket qui reçoit un paquet déjà tout emballé
	 * Dans ce paquet il prend le client, regarde dans table son adresse ip et lui envoit
	 */
	private class SendSocket extends AsyncTask <Packet, Integer, Long> {
		protected Long doInBackground(Packet... packets) {

			Packet packet = packets[0];

			InetSocketAddress inetAddres = packet.getUser_destinataire().getInetSocketAddressLocal();

			// au cas où la local addrese est nulle on utilise celle publique
			if(inetAddres == null){
				inetAddres = packet.getUser_destinataire().getInetSocketAddressPublic();
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
				Log.d("NetworkService", "envoyé:" + json + "a : " + inetAddres.toString());

				//on l'ajoute dans la liste des paquets envoyé
				packetListACK.put( packet.getRamdom_identifiant(), packet);

			} catch (IOException e) {
				// TODO Auto-generated catch block
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
	private class BroadcastSocket extends AsyncTask <Packet, Integer, Long> {
		protected Long doInBackground(Packet... packets) {

			Packet packet = packets[0];
			InetSocketAddress inetAddres = packet.getUser_destinataire().getInetSocketAddressLocal();



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
				// TODO Auto-generated catch block
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
			 * TODO initier et écouter sur la socket qui bind le port local et récupérer les données*/
			DatagramSocket datagramSocket;
			try {
				datagramSocket = new DatagramSocket(5008);

				do{
					byte[] buffer2 = new byte[300000];
					DatagramPacket dataPacket = new DatagramPacket(buffer2, buffer2.length);

					try {
						datagramSocket.receive(dataPacket);
						analysePacket(dataPacket);

					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}while(true);


			} catch (SocketException e) {
				// TODO Auto-generated catch block
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
		Log.d("NetWorkService", json);

		Gson gson = new Gson();
		Packet packetReceive = gson.fromJson(json, Packet.class);

		/*
		 * si l'utilisateur n'a pas spécifié son adresse local et que l'adresse est local, on l'ajoute avec le port par défault de l'application
		 */
		if(packetReceive.getUser_envoyeur().getInetSocketAddressLocal() == null &&  dataPacket.getAddress().getHostName().matches("localhost")){
			User user_envoyeur  = packetReceive.getUser_envoyeur();
			user_envoyeur.setInetSocketAddressLocal(new InetSocketAddress(dataPacket.getAddress(), 5008));
			packetReceive.setUser_envoyeur(user_envoyeur);

			/*
			 * si l'utilisateur n'a pas d'adresse public mais une local, on va d'ajouter à la public on vérifie que ce n'est déjà pas la local
			 */

		}else if(packetReceive.getUser_envoyeur().getInetSocketAddressPublic() == null ){
			User user_envoyeur  = packetReceive.getUser_envoyeur();

			InetSocketAddress inetSocket = new InetSocketAddress(dataPacket.getAddress(), dataPacket.getPort());

			//si ce n'est pas la même adresse que son adresse local alors on l'ajoute dans public
			if(inetSocket != user_envoyeur.getInetSocketAddressLocal()){
				user_envoyeur.setInetSocketAddressPublic(inetSocket);
				packetReceive.setUser_envoyeur(user_envoyeur);
			}
		}


		/**
		 * Si le packet n'est pas un ACK, on envoit un ACK
		 */
		if(packetReceive.type != Packet.ACK ){

			 //On envoit un ACK
			Packet packetSend = new Packet(packetReceive.getUser_envoyeur(),Packet.ACK, packetReceive.getRamdom_identifiant());

			SendMessage(packetSend);

		}


		/*
		 * on exécute les traitement à faire au niveau de la couche réseau
		 */
		switch (packetReceive.type){
		case Packet.ACK : paquetACK(packetReceive);
		break;
		case Packet.CREATION_GROUP : paquetCreationGroup(packetReceive);
		break;
		case Packet.DISCONNECTED : paquetDisconnecter(packetReceive);
		break;
		case Packet.HELLO : paquetHello(packetReceive);
		break;
		case Packet.MESSAGE : paquetMessage(packetReceive);
		break;

		}


	}

	/**
	 * Traite un message reçu, le fait suivre à l'activity correspondant
	 * Met l'état de l'utilisateur à "en ligne"
	 * @param packetReceive
	 */
	private void paquetMessage(Packet packetReceive) {
		// TODO Auto-generated method stub


		sendToActivity(packetReceive,"lo52.messaging.activities.");
	}

	/**
	 * Traite un message hello 
	 * Vérfie que l'utilisateur existe sinon l'utilisateur est rajouté à la liste
	 * Met l'état de l'utilisateur à "en ligne"
	 * @param packetReceive
	 */
	private void paquetHello(Packet packetReceive) {

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

			//Et on lui envoit nous m�me un Hello 
			Packet packet = new Packet(packetReceive.getUser_envoyeur(), Packet.HELLO);
			
			SendMessage(packet);
		}


	}


	/**
	 * Traite un message disconnected
	 * Met l'état de l'utilisateur à disconnected
	 * @param packetReceive
	 */
	private void paquetDisconnecter(Packet packetReceive) {
		// TODO Auto-generated method stub

		//on le fait le suivre à l'activity qui gère la liste des users (et peut être aussi à l'activity qui gère les conversations)
		sendToActivity(packetReceive,"lo52.messaging.activities.UserListActivity");

	}

	/**
	 * Traite la la création d'un groupe
	 * Ajoute un groupe avec sa liste de User
	 * Vérifie que tout les users sont connus sinon on les rajoute dans notre liste
	 * @param packetReceive
	 */
	private void paquetCreationGroup(Packet packetReceive) {
		// TODO Auto-generated method stub

		sendToActivity(packetReceive,"lo52.messaging.activities.");

	}

	/**
	 * Traite un ACK
	 * Retire le paquet de la liste des paquets en attente d'ACK
	 * TODO: voir si on renvoit quelque chose à l'activity
	 * 
	 * @param packetReceive
	 */
	private void paquetACK(Packet packetReceive) {

		packetListACK.remove(packetReceive.getRamdom_identifiant());
		sendToActivity(packetReceive,"lo52.messaging.activities.");

	}

	/**
	 * Envoit en broadcast le paquet pour être traité
	 * @param packetReceive
	 * @param action
	 */
	private void sendToActivity(Packet packetReceive, String action) {
		/*
		 * Exemple pour transmettre un message récupéré à l'activity
		 */
		Intent broadcastIntent = new Intent(action);

		Gson gson = new Gson();
		String json = gson.toJson(packetReceive);

		broadcastIntent.putExtra("json", json);

		sendBroadcast(broadcastIntent);

	}

	public static HashMap<Integer, User> getListUsers() {
		return listUsers;
	}

	public static void setListUsers(HashMap<Integer, User> listUsersE) {
		listUsers = listUsersE;
	}

	public static HashMap<Integer, Conversation> getListConversations() {
		return listConversations;
	}

	public static void setListConversations(HashMap<Integer, Conversation> listConversations) {
		NetworkService.listConversations = listConversations;
	}

	public static User getUser_me() {
		return user_me;
	}

	public static void setUser_me(User user_me) {
		NetworkService.user_me = user_me;
	}

}
