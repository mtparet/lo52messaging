package lo52.messaging;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Hashtable;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;

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
    	 * enregistrer l'intent permettant de recevoir les infos de connexions
    	 */
        IntentFilter filter2 = new IntentFilter();
        filter.addAction("ReceiveInfoActivity");
        registerReceiver(InfoFromActivity, filter2);
        
		/*
		 * Exemple pour transmettre un message récupéré à l'activity
		 */
		Intent broadcastIntent = new Intent("ReceiveMessage");
		Bundle bundle = new Bundle();
		Message message = new Message(2233,"blabla");
		bundle.putParcelable("Message", message);
		broadcastIntent.putExtras(bundle);
		
        this.sendBroadcast(broadcastIntent);
    
	}
	
	 /*
     * Recoit un message à envoyer à un client
     */
    private BroadcastReceiver SendMessage = new BroadcastReceiver(){

		@Override
		public void onReceive(Context context, Intent intent) {
			Bundle bundle = intent.getExtras();
			Message message = bundle.getParcelable("Message");
			User user = bundle.getParcelable("User");
			Packet packet = new Packet(message,user);
			// TODO envoyer le message avec le réseau avec une asynctask			
		}
    	
    };
    
    /*
     * Permet de recevoir les messages depuis le service
     * 
     */
    private BroadcastReceiver InfoFromActivity = new BroadcastReceiver(){

		@Override
		public void onReceive(Context context, Intent intent) {
			Bundle bundle = intent.getExtras();
			String action = bundle.getString("action").toString();
			String what = bundle.getString("what").toString();
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
	 */
	 private class sendSocket extends AsyncTask <Packet, Integer, Long> {
	     protected Long doInBackground(Packet... packets) {
	    	 
	    	 Message message = packets[0].getMessage();
	    	 User user = packets[0].getUser();
	    	 InetAddress inetAddres = hashtableUser.get(user);
	    	 
	         /*TODO initier la socket correspondant à l'utilisateur et envoyer un packet 
	    	 
	    	 	DatagramSocket datagramSocket = new DatagramSocket();

				byte[] buffer = message.getMessage().getBytes();
				
				DatagramPacket packet = new DatagramPacket(buffer, buffer.length, inetAddres, port_a_déterminer);
				datagramSocket.send(packet);
				*/
	    	 
	    	 return null;
	     }
	     
	     protected void onPostExecute(Long result) {
	    	 // TODO  
	     }
	 }
	
	/*
	 * AsyncTask pour écouter la socket sur le port local
	 */
	 private class listenSocket extends AsyncTask <Integer, Integer, Long> {
	     protected Long doInBackground(Integer... integers) {
	    	 /*
	    	  * TODO initier et écouter sur la socket qui bind le port local et récupérer les données
	    	 DatagramSocket datagramSocket = new DatagramSocket(port_à_déterminer);

	    	 byte[] buffer = new byte[10];
	    	 DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

	    	 datagramSocket.receive(packet);
	    	 
	    	 byte[] buffer = packet.getData();  
  
	    	 */
			return null;

	     }
	     
	     protected void onPostExecute(Long result) {
	    	 // TODO envoyer le message 
	     }
	 }
	 
	

}
