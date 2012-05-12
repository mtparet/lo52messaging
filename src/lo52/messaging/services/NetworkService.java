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
	
	private Hashtable<User,InetAddress> hashtableUser = new Hashtable<User,InetAddress>();
   
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
		Intent broadcastIntent = new Intent("ReceiveMessage");
		Bundle bundle = new Bundle();
        
        ArrayList<User> userlist = new ArrayList<User>();
        InetSocketAddress inetAddres = new InetSocketAddress("127.0.0.1",5005);
        
        User user = new User(253634, "cestmoi",inetAddres);
        userlist.add(user);
        userlist.add(user);
        
        Content message = new Content(3535,"bonjour",userlist);
        Packet packet = new Packet(message,user, user.getId(), Packet.CREATION_GROUP);
        
        bundle.putParcelable("packet", packet);
        
        Gson gson = new Gson();
        String json = gson.toJson(packet);
        
        Packet packet2 = gson.fromJson(json, packet.getClass());
        
        Packet packet3 = bundle.getParcelable("packet");
		ListenSocket r1 = new ListenSocket();
		r1.execute(null);
		

				
        this.sendBroadcast(broadcastIntent);
    
	}
	
	 /*
     * Recoit un message à envoyer à un client
     */
    private BroadcastReceiver SendMessage = new BroadcastReceiver(){

		@Override
		public void onReceive(Context context, Intent intent) {
			Bundle bundle = intent.getExtras();
			
			//on reçoit le packet émit par l'activity
			Packet packet = bundle.getParcelable("Packet");
			
			//Création de l'asyncTask pour envoyer le packet
			SendSocket sendSocket = new SendSocket();
			Packet[] packets = new Packet[1];
			packets[0] = packet;
			
			//Exécution de l'asyncTask
			sendSocket.execute(packets);
			
			// TODO envoyer le message avec le réseau avec une asynctask			
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
	    	 
	    	 //Message message = packets[0].getMessage();
	    	 //User user = packets[0].getUser();
	    	 //InetAddress inetAddres = hashtableUser.get(user);
	    	 
	         /*TODO initier la socket correspondant à l'utilisateur et envoyer un packet */
	    	 
	    	 	DatagramSocket datagramSocket = null;
				try {
					datagramSocket = new DatagramSocket();
				} catch (SocketException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

				byte[] buffer = "Bonjour".getBytes();
				InetSocketAddress inetaddres = new InetSocketAddress("127.0.0.1", 5005);
				
				DatagramPacket packet = new DatagramPacket(buffer, buffer.length, inetaddres.getAddress(), 5005);
				try {
					datagramSocket.send(packet);
					byte[] buffer2 = new byte[30000];
					DatagramPacket packet2 = new DatagramPacket(buffer2,30000);
					
					datagramSocket.receive(packet2);
					
					String json = new String(packet2.getData(), 0, packet2.getLength());

					    Log.d("receive", json);
					Log.d("remote_adress", packet2.getAddress().getCanonicalHostName());
					Log.d("remote_adress", String.valueOf(packet2.getPort()));
					Log.d("local_adress", String.valueOf(datagramSocket.getLocalAddress().toString()));
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
				datagramSocket = new DatagramSocket(5005);
		    	 byte[] buffer2 = new byte[30000];
		    	 DatagramPacket packet2 = new DatagramPacket(buffer2, buffer2.length);

		    	 try {
					datagramSocket.receive(packet2);
					String json = new String(packet2.getData(), 0, packet2.getLength());  
					 Log.d("receive_receiver", json);
					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		    	 
		    	 
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
	 private void analysePacket(){
		 
	 }
	

}
