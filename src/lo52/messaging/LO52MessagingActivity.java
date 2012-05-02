package lo52.messaging;


import android.app.Activity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

public class LO52MessagingActivity extends Activity {
	
	private EditText messageAffi;
	
    /** Called when the activity is first created. */
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        messageAffi = (EditText)findViewById(R.id.editText1);
        User user;
        		
        startService(new Intent(LO52MessagingActivity.this, NetworkService.class));
    }
    
    /*
     * Permet de recevoir les messages depuis le service
     * 
     */
    private BroadcastReceiver messageFromService = new BroadcastReceiver(){

		@Override
		public void onReceive(Context context, Intent intent) {
			Bundle bundle = intent.getExtras();
			Message messageRecu = bundle.getParcelable("Message");
			messageAffi.setText("client = " + messageRecu.getClient_id() + " message = " + messageRecu.getMessage());
		}
    	
    };
    
    /*
     * Permet de recevoir les informations de services:
     *  un client se connecte/déconnect
     *  aucun client déjà connu (ce qui nécessite de lui renvoyer une adresse ip)
     */
    private BroadcastReceiver InfoFromService = new BroadcastReceiver(){

		@Override
		public void onReceive(Context context, Intent intent) {
			Bundle bundle = intent.getExtras();
			String action = bundle.getString("action").toString();
			String what = bundle.getString("what").toString();
			
		}
    	
    };
    
    @Override
    protected void onResume() {
    	
    	/*
    	 * enregistrer l'intent permettant de recevoir les messages
    	 */
        IntentFilter filter = new IntentFilter();
        filter.addAction("ReceiveMessage");
        registerReceiver(messageFromService, filter);
        
    	/*
    	 * enregistrer l'intent permettant de recevoir les infos de connexions
    	 */
        IntentFilter filter2 = new IntentFilter();
        filter.addAction("ReceiveInfoService");
        registerReceiver(InfoFromService, filter2);

        super.onResume();
    }

    @Override
    protected void onPause() {
        unregisterReceiver(messageFromService);
        unregisterReceiver(InfoFromService);
        super.onPause();
    }

}