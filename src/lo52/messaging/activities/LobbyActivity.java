package lo52.messaging.activities;

import java.util.ArrayList;

import lo52.messaging.R;
import lo52.messaging.services.NetworkService;
import android.app.TabActivity;
import android.content.Intent;
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

	// Utilisé pour indiquer à l'activité ConversationPagerActivity qu'elle doit aller sur un fragment de conversation particulier
	private ArrayList<Integer> switchToConversation;
	
	// Tags pour les différents onglets de l'activité
	public static final String	TAG_TAB_USERLIST 		= "tab1";
	public static final String	TAG_TAB_CONVERSATIONS 	= "tab2";
	public static final String	TAG_TAB_MAP			 	= "tab3";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.lobby);
		
		Log.d(TAG, "Lancement activité lobby");

		// Lancement du service Network
		networkService = new Intent(LobbyActivity.this, NetworkService.class);
		startService(networkService);

		// Initialisation préférences
		preferences = PreferenceManager.getDefaultSharedPreferences(this);
		preferences.registerOnSharedPreferenceChangeListener(null);

		// Récupération du TabHost
		TabHost tabHost = getTabHost();
		TabHost.TabSpec spec; 
		Intent intent;
		
		switchToConversation = new ArrayList<Integer>();

		/**
		 * Création du tab de la liste des users (UserListActivity)
		 **/
		intent = new Intent().setClass(this, UserListActivity.class);
		spec = tabHost.newTabSpec(TAG_TAB_USERLIST).setIndicator(getString(R.string.lobby_tab_users), getResources().getDrawable(R.drawable.icon_group)).setContent(intent);
		tabHost.addTab(spec);

		/**
		 * Création du tab de la liste des conversations (ConversationPagerActivity)
		 **/
		intent.setClass(this, ConversationPagerActivity.class);
		spec = tabHost.newTabSpec(TAG_TAB_CONVERSATIONS).setIndicator(getString(R.string.conversations_tab_name), getResources().getDrawable(R.drawable.icon_chat)).setContent(intent);
		tabHost.addTab(spec);
		
		/**
		 * Création du tab de carte de géolocalisation des utilisateurs (LocalizationMapActivity)
		 **/
		intent = new Intent().setClass(this, LocalizationMapActivity.class);
		spec = tabHost.newTabSpec(TAG_TAB_MAP).setIndicator(getString(R.string.lobby_tab_map), getResources().getDrawable(R.drawable.icon_chat)).setContent(intent);
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
				stopService(networkService);
			} catch (Exception e) {
				Log.e(TAG, "Service non arrêté");
			}
		}
	}

	/**
	 * @deprecated
	 * @param tabNumber
	 */
	public void setActiveTab(int tabNumber) {
		Log.d(TAG, "Setting active tab");
		TabHost tabHost = getTabHost();
		tabHost.setCurrentTab(tabNumber);
	}

	/**
	 * Switche sur le tab indiqué. Pour les tags, utiliser les static définies plus haut (TAG_TAB_xxx)
	 * @param tabTag
	 */
	public void setActiveTabByTag(String tabTag) {
		TabHost tabHost = getTabHost();
		tabHost.setCurrentTabByTag(tabTag);
	}
	
	
	/**
	 * Indique à l'activité ConversationPagerActivity qu'elle doit changer la vue vers une conversation précise quand elle onResume()
	 * @param userIds
	 */
	public void setSwitchToConversationFragment(ArrayList<Integer> userIds) {
		switchToConversation = userIds;
	}
	
	
	/**
	 * Utilisé par l'activité ConversationPagerActivity pour savoir si elle doit aller sur un fragment particulier quand elle reprend
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public ArrayList<Integer> getSwitchToConversationFragmentStatus() {
		Log.d(TAG, "Récupération de la liste de la convers sur laquelle switcher");
		
		ArrayList<Integer> list = (ArrayList<Integer>) switchToConversation.clone();
		switchToConversation.clear();
		return list;
	}

}
