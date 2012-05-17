package lo52.messaging.activities;

import lo52.messaging.R;
import lo52.messaging.services.NetworkService;
import android.app.TabActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.widget.TabHost;

/**
 *	Activité regroupant plusieurs sous-activités au sein de différents onglets 
 */
public class LobbyActivity extends TabActivity {

	private static final String TAG = "LobbyActivity";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.lobby);
		
		//Enregistrement de l'intent filter
		IntentFilter filter = new IntentFilter();
		filter.addAction("NetWorkService.packet.message");
		registerReceiver(messageReceiver, filter);
		
		// Lancement du service
		startService(new Intent(LobbyActivity.this, NetworkService.class));

		Log.d(TAG, "Lancement activité lobby");
		
		// Récupération du TabHost
		TabHost tabHost = getTabHost();
		TabHost.TabSpec spec; 
		Intent intent;

		// Créer un intent (réutilisable) pour indiquer quelles activité lancer dans les différents tabs
		intent = new Intent().setClass(this, UserListActivity.class);

		// Initialiser un tabSpec pour chaque tab en donnant l'intent correspondant
		spec = tabHost.newTabSpec("tab1").setIndicator(getString(R.string.lobby_tab_users), getResources().getDrawable(R.drawable.icon_group)).setContent(intent);
		tabHost.addTab(spec);
		
		intent.setClass(this, TestActivity.class);
		spec = tabHost.newTabSpec("tab2").setIndicator(getString(R.string.lobby_tab_conversations), getResources().getDrawable(R.drawable.icon_chat)).setContent(intent);
		tabHost.addTab(spec);
	}
	
	
	/**
	 * Here there was a wtf error : class instead of BroadcastReceiver
	 */
	private BroadcastReceiver messageReceiver = new  BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			Log.i(TAG, "Réception broadcast");
		}
		
	};
}
