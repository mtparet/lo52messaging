package lo52.messaging.activities;

import lo52.messaging.R;
import lo52.messaging.model.broadcast.MessageBroacast;
import lo52.messaging.services.NetworkService;
import android.app.TabActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.TabHost;

/**
 *	Activité regroupant plusieurs sous-activités au sein de différents onglets 
 */
public class LobbyActivity extends TabActivity {

	private Intent networkService;
	private static final String TAG = "LobbyActivity";
	private SharedPreferences preferences;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.lobby);

		Log.d(TAG, "Lancement activité lobby");

		// Lancement du service Network
		networkService = new Intent(LobbyActivity.this, NetworkService.class);
		startService(networkService);

		//Enregistrement de l'intent filter
		IntentFilter filter = new IntentFilter();
		filter.addAction(NetworkService.SendMessage);
		registerReceiver(messageReceiver, filter);

		// Initialisation préférences
		preferences = PreferenceManager.getDefaultSharedPreferences(this);
		preferences.registerOnSharedPreferenceChangeListener(null);

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


	@Override
	protected void onDestroy() {
		super.onDestroy();

		// Arrêt du service Network en fonction des préférences de l'utilisateur
		if (!preferences.getBoolean("prefs_networkServiceKeepAlive", false)) {
			Log.d(TAG, "arret service network");
			// onPause est aussi appellée avant onDestroy ici donc le messageReceiver peut déjà avoir été dé-registered
			try {
				// Unregister du broadcastReceiver & arret du service
				unregisterReceiver(messageReceiver);
				stopService(networkService);
			} catch (Exception e) {}
		}
	}


	@Override
	protected void onResume() {
		super.onResume();
		//Enregistrement de l'intent filter
		IntentFilter filter = new IntentFilter();
		filter.addAction(NetworkService.SendMessage);
		registerReceiver(messageReceiver, filter);
	}


	@Override
	protected void onPause() {
		unregisterReceiver(messageReceiver);
		super.onPause();
	}


	/**
	 * Here there was a wtf error : class instead of BroadcastReceiver
	 */
	private BroadcastReceiver messageReceiver = new  BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			Log.i(TAG, "Réception broadcast");
			Bundle bundle = intent.getBundleExtra("message");
			MessageBroacast message = bundle.getParcelable(MessageBroacast.tag_parcelable);

		}

	};

	private void sendMessage(String message, int id_conversation){
		Intent broadcastIntent = new Intent(NetworkService.ReceiveMessage);
		Bundle bundle = new Bundle();

		MessageBroacast messageBroad = new MessageBroacast(message, id_conversation);
		bundle.putParcelable("message", messageBroad);
		broadcastIntent.putExtra("message", bundle);

		sendBroadcast(broadcastIntent);
	}
}
