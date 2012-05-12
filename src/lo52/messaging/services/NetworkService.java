package lo52.messaging.services;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

import com.google.gson.Gson;

import lo52.messaging.model.Content;
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

public class NetworkService extends Service {

	/*
	 * HashTable est synchronisé donc pas de soucis pour les threads
	 * InetAddress est un objet pour contenir l'adresse ip de l'utilisateur, utile pour toute connexion hors bluetooth
	 */

	private HashMap<Integer,Packet> packetListACK = new HashMap<Integer,Packet>();

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
		registerReceiver(SendMessage, filter);


		/*
		 * Exemple pour transmettre un message récupéré à l'activity
		 */
		Intent broadcastIntent = new Intent("SendMessage");
		Bundle bundle = new Bundle();

		ArrayList<User> userlist = new ArrayList<User>();
		InetSocketAddress inetAddres = new InetSocketAddress("127.0.0.1",5008);

		User user = new User(253634, "cestmoi",inetAddres);
		userlist.add(user);
		userlist.add(user);

		Content message = new Content(3535,"bonjour",userlist);
		Packet packet = new Packet(message,user, user.getId(), Packet.CREATION_GROUP);

		Gson gson = new Gson();
		String json = gson.toJson(packet);

		broadcastIntent.putExtra("json", json);

		sendBroadcast(broadcastIntent);

		//ListenSocket r1 = new ListenSocket();
		//r1.execute(null);

	}

	/*
	 * Recoit un message à envoyer à un client
	 */
	private BroadcastReceiver SendMessage = new BroadcastReceiver(){

		@Override
		public void onReceive(Context context, Intent intent) {
			String json = intent.getStringExtra("json");

			Gson gson = new Gson();
			Packet packet = gson.fromJson(json, Packet.class);

			/**
			 * Dans le cas de l'annonciation de l'arrivée dans le réseau (broadcast)
			 */
			if(packet.type == Packet.HELLO){
				//Création de l'asyncTask pour envoyer le packet
				BroadcastSocket sendSocket = new BroadcastSocket();
				Packet[] packets = new Packet[1];
				packets[0] = packet;

				//Exécution de l'asyncTask
				sendSocket.execute(packets);
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

	};



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
			InetSocketAddress inetAddres = packet.getUser().getInetSocketAddress();



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
	

	/*
	 * AsyncTask pour envoyer un message broadcast à tout le réseau
	 * Socket qui reçoit un paquet déjà tout emballé
	 * TODO a faire, et tester si le wifi dispo, sinon à ne pas faire
	 */
	private class BroadcastSocket extends AsyncTask <Packet, Integer, Long> {
		protected Long doInBackground(Packet... packets) {

			Packet packet = packets[0];
			InetSocketAddress inetAddres = packet.getUser().getInetSocketAddress();



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

	/*
	 * AsyncTask pour écouter la socket sur le port local
	 */
	private class ListenSocket extends AsyncTask <Integer, Integer, Long> {
		protected Long doInBackground(Integer... integers) {


			/*
			 * TODO initier et écouter sur la socket qui bind le port local et récupérer les données*/
			DatagramSocket datagramSocket;
			try {
				datagramSocket = new DatagramSocket(5008);

				do{
					byte[] buffer2 = new byte[30000];
					DatagramPacket dataPacket = new DatagramPacket(buffer2, buffer2.length);

					try {
						datagramSocket.receive(dataPacket);
						

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

	/*
	 * Foncton déclenché pour chaque Paquet reçu,
	 * 
	 * A un switch sur les le type de paquet et va exécuter les fonctions nécessaires
	 * 
	 */
	private void analysePacket(DatagramPacket dataPacket){
		
		String json = new String(dataPacket.getData(), 0, dataPacket.getLength());  
		Log.d("NetWorkService", json);

		Gson gson = new Gson();
		Packet packetReceive = gson.fromJson(json, Packet.class);

		if(packetReceive.type != Packet.ACK){
			/*
			 * On envoit un ACK
			 */
			
			Packet packetSend = new Packet(Packet.ACK,packetReceive.getRamdom_identifiant());
			
			//Création de l'asyncTask pour envoyer le packet
			SendSocket sendSocket = new SendSocket();
			Packet[] packets = new Packet[1];
			packets[0] = packetSend;

			//Exécution de l'asyncTask
			sendSocket.execute(packets);
			
		}else{
			packetListACK.remove(packetReceive.getRamdom_identifiant());
		}

	}


}
