package lo52.messaging.activities;

import java.util.ArrayList;
import java.util.Hashtable;

import lo52.messaging.R;
import lo52.messaging.model.Conversation;
import lo52.messaging.model.Localisation;
import lo52.messaging.model.User;
import lo52.messaging.model.broadcast.MessageBroacast;
import lo52.messaging.services.NetworkService;
import lo52.messaging.services.PosUpdateService;
import android.app.AlertDialog;
import android.app.TabActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.TabHost;
import android.widget.Toast;

/**
 *	Activité regroupant plusieurs sous-activités au sein de différents onglets 
 */
public class LobbyActivity extends TabActivity {

	private Intent networkService;
	private Intent posUpdateService;
	private static final String TAG = "LobbyActivity";
	private SharedPreferences preferences;

	// Utilisé pour indiquer à l'activité ConversationPagerActivity qu'elle doit aller sur un fragment de conversation particulier
	private ArrayList<Integer> switchToConversation;

	// Listes pour indiquer à l'activité ConversationPager qu'elle doit activer le spinner de chargement sur certaines conversations
	ArrayList<Integer> fileTransferStarted;
	ArrayList<Integer> fileTransferFinished;

	// Tags pour les différents onglets de l'activité
	public static final String	TAG_TAB_USERLIST 		= "tab1";
	public static final String	TAG_TAB_CONVERSATIONS 	= "tab2";
	public static final String	TAG_TAB_MAP			 	= "tab3";

	// Durée de la vibration pour la notification de messages, en ms
	public static final long VIBRATOR_NOTIFICATION_DURATION	= 300;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.lobby);

		Log.d(TAG, "Lancement activité lobby");

		// Lancement du service Network
		networkService = new Intent(LobbyActivity.this, NetworkService.class);
		startService(networkService);

		// Lancement du service PosUpdate
		posUpdateService = new Intent(LobbyActivity.this, PosUpdateService.class);
		startService(posUpdateService);

		// Initialisation préférences
		preferences = PreferenceManager.getDefaultSharedPreferences(this);
		preferences.registerOnSharedPreferenceChangeListener(null);

		// Récupération du TabHost
		TabHost tabHost = getTabHost();
		TabHost.TabSpec spec; 
		Intent intent;

		// Initialisations diverses
		switchToConversation 	= new ArrayList<Integer>();
		fileTransferStarted		= new ArrayList<Integer>();
		fileTransferFinished	= new ArrayList<Integer>();

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
		spec = tabHost.newTabSpec(TAG_TAB_MAP).setIndicator(getString(R.string.lobby_tab_map), getResources().getDrawable(R.drawable.icon_worldmap)).setContent(intent);
		tabHost.addTab(spec);

	}


	@Override
	protected void onStop(){
		super.onStop();
		

		// Arrêt du service Network en fonction des préférences de l'utilisateur
		if (!preferences.getBoolean("prefs_networkServiceKeepAlive", false)) {
			Log.d(TAG, "arret service network");
			// onPause est aussi appellée avant onDestroy ici donc le messageReceiver peut déjà avoir été dé-registered
			try {
				// Unregister du broadcastReceiver & arret du service
				stopService(networkService);
				stopService(posUpdateService);
			} catch (Exception e) {
				Log.e(TAG, "Service non arrêté");
			}
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();

	}

	@Override
	protected void onResume() {
		super.onResume();

		//Enregistrement de l'intent filter
		IntentFilter filter = new IntentFilter();
		filter.addAction(NetworkService.SendMessage);
		registerReceiver(messageReceiver, filter);

		//Enregistrement de l'intent filter
		IntentFilter filter2 = new IntentFilter();
		filter2.addAction(NetworkService.SendConversation);
		registerReceiver(conversationReceiver, filter2);

		IntentFilter filter3 = new IntentFilter();
		filter3.addAction(NetworkService.FileTransferStart);
		registerReceiver(fileTransferStart, filter3);

		IntentFilter filter4 = new IntentFilter();
		filter4.addAction(NetworkService.FileTransferFinish);
		registerReceiver(fileTransferFinish, filter4);

		IntentFilter userListUpdatefilter = new IntentFilter();
		userListUpdatefilter.addAction(NetworkService.UserListUpdated);
		registerReceiver(UserlistUpdate_BrdcReceiver, userListUpdatefilter);
	}

	@Override
	protected void onPause() {
		unregisterReceiver(conversationReceiver);
		unregisterReceiver(messageReceiver);
		unregisterReceiver(UserlistUpdate_BrdcReceiver);
		unregisterReceiver(fileTransferStart);
		unregisterReceiver(fileTransferFinish);
		super.onPause();
	}

	@Override
	public void onBackPressed() {

		if (preferences.getBoolean("prefs_confirmToQuit", true)) {

			// Demande à l'utilisateur s'il veut vraiment quitter
			new AlertDialog.Builder(this).setIcon(android.R.drawable.ic_dialog_alert)
			.setTitle(R.string.lobby_alert_quit)
			.setMessage(R.string.lobby_alert_quit_msg)
			.setPositiveButton(R.string.generic_yes, new DialogInterface.OnClickListener() {

				public void onClick(DialogInterface dialog, int which) {
					// TODO:
					// Envoyer un paquet de déconnexion aux utilisateurs connus, pour qu'ils retirent cet user de leur liste

					LobbyActivity.super.onBackPressed();
				}
			})
			.setNegativeButton(R.string.generic_no, null)
			.show();

		} else 
			LobbyActivity.super.onBackPressed();
	}


	/**
	 * Recoit les nouvelles conversations. Affiche un toast si l'utilisateur n'est pas sur l'onglet des conversations
	 */
	private BroadcastReceiver conversationReceiver = new  BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {

			/*
			 * Principe : si le tab des conversations est inactif, on affiche un toast
			 */

			TabHost tabhost = getTabHost();
			if (tabhost.getCurrentTabTag() != TAG_TAB_CONVERSATIONS) {

				if(preferences.getBoolean("prefs_notifToast", true)) {
					Bundle bundle = intent.getBundleExtra("conversation");
					Conversation conversation = bundle.getParcelable("conversation");

					// Phrase au pluriel ou singulier selon le nombre de personnes dans la conversation
					String phrase = (conversation.getListIdUser().size() > 2) ? getString(R.string.conversation_creation_toast_plur) : getString(R.string.conversation_creation_toast_sing) ;

					// Affichage du toast
					Toast.makeText(context, conversation.generateConversationName() + " " + phrase, Toast.LENGTH_LONG).show();
				}

				// Vibration, si les préférences sont configurées pour
				if(preferences.getBoolean("prefs_notifVibrator", true)) {
					Vibrator vibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
					vibrator.vibrate(VIBRATOR_NOTIFICATION_DURATION);
				}
			}
		}
	};


	/**
	 * Nouveau message : affiche un toast si l'utilisateur n'est pas sur l'onglet des conversations
	 */
	private BroadcastReceiver messageReceiver = new  BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {

			TabHost tabhost = getTabHost();
			if (tabhost.getCurrentTabTag() != TAG_TAB_CONVERSATIONS) {

				if(preferences.getBoolean("prefs_notifToast", true)) {
					Bundle bundle = intent.getBundleExtra("message");
					MessageBroacast message = bundle.getParcelable(MessageBroacast.tag_parcelable);
					String userName = "";
					if (NetworkService.getListUsers().get(message.getClient_id()) != null) 
						userName = NetworkService.getListUsers().get(message.getClient_id()).getName();

					// Affichage du toast
					Toast.makeText(context, userName + " " + getString(R.string.conversation_user_received_message), Toast.LENGTH_LONG).show();
				}

				// Vibration, si les préférences sont configurées pour
				if(preferences.getBoolean("prefs_notifVibrator", true)) {
					Vibrator vibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
					vibrator.vibrate(VIBRATOR_NOTIFICATION_DURATION);
				}

			}
		}
	};


	/**
	 * Nouvel utilisateur
	 * Broadcast receiver qui reçoit une notification comme quoi la liste des utilisateurs doit être mise à jour
	 * Affiche un toast si l'on est pas sur l'onglet UserList
	 */
	private BroadcastReceiver UserlistUpdate_BrdcReceiver = new BroadcastReceiver(){

		@Override
		public void onReceive(Context context, Intent intent) {

			TabHost tabhost = getTabHost();
			if (tabhost.getCurrentTabTag() != TAG_TAB_USERLIST) {

				if(preferences.getBoolean("prefs_notifToast", true)) {
					// On récupère le nom de l'user depuis le bundle pour afficher un toast
					Bundle userInfo = intent.getBundleExtra("new_user");
					if (userInfo != null && userInfo.getString("new_user") != null) {
						Toast.makeText(context, userInfo.getString("new_user") + " " + getString(R.string.userlist_new_connection), Toast.LENGTH_SHORT).show();
					}
				}
			}
		}
	};


	/**
	 * Envoyé par le network service quand le premier paquet d'un fichier est reçu ou envoyé
	 */
	private BroadcastReceiver fileTransferStart = new  BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			int conv_id = intent.getIntExtra("conversation_id", 0);

			// On affiche le toast seulement si on est la personne qui reçoit le fichier
			if (intent.getBooleanExtra("isReceiver", false))
				Toast.makeText(context, getString(R.string.conversation_file_receive_begin), Toast.LENGTH_SHORT).show();

			// Ajout de l'id de conversation à la liste de celles en cours de transfert
			fileTransferStarted.add(conv_id);
		}
	};

	/**
	 * Envoyé par le network service quand le dernier paquet d'un fichier est reçu ou envoyé
	 */
	private BroadcastReceiver fileTransferFinish = new  BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {

			int conv_id = intent.getIntExtra("conversation_id", 0);

			// Ajout de l'id de conversation à la liste de celles en cours de transfert
			fileTransferFinished.add(conv_id);
			if (fileTransferStarted.contains(conv_id)) fileTransferStarted.remove(fileTransferStarted.indexOf(conv_id));
		}
	};

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

		ArrayList<Integer> list = (ArrayList<Integer>) switchToConversation.clone();
		switchToConversation.clear();
		return list;
	}


	/**
	 * Retourne la liste des conversations qui ont un transfert de fichier commencé
	 * @return
	 */
	public ArrayList<Integer> getConversationsWithTransferStarted() {
		@SuppressWarnings("unchecked")
		ArrayList<Integer> list = (ArrayList<Integer>) fileTransferStarted.clone();
		fileTransferStarted.clear();
		return list;
	}

	/**
	 * Retourne la liste des conversations qui ont un transfert de fichier terminé
	 * @return
	 */
	public ArrayList<Integer> getConversationsWithTransferFinished() {
		@SuppressWarnings("unchecked")
		ArrayList<Integer> list = (ArrayList<Integer>) fileTransferFinished.clone();
		fileTransferFinished.clear();
		return list;
	}

}
