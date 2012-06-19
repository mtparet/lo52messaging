package fr.utbm.lo52.messaging.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import fr.utbm.lo52.messaging.R;
import fr.utbm.lo52.messaging.util.LibUtil;


/**
 * 			LO52Messaging
 * Projet de LO52 - UTBM - Printemps 2012
 *
 * Another Android Instant Messenger : application Android de messagerie sans serveur
 * intérmédiaire, permettant à plusieurs utilisateurs de communiquer, s'échanger des fichiers
 * et se localiser les uns par rapport aux autres.
 * 
 * 
 * Activité launcher, démarrée en premier lors du lancement de l'application.
 * 
 * 
 * @author Adrien Baud			adrien.baud@utbm.fr
 * @author Francois Laithier	francois.laithier@utbm.fr
 * @author Matthieu Paret		matthieu.paret@utbm.fr
 *
 * @version 1.0
 */
public class LauncherActivity extends Activity {

	/*
	 * Attributs
	 */
	private static final String TAG = "LO52MessagingActivity";
	private SharedPreferences preferences;
	private Button connexionBtn;
	private TextView usernameErrorTv;

	/*
	 * Méthodes
	 */

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		// Récupération des préférences
		preferences = PreferenceManager.getDefaultSharedPreferences(this);
		preferences.registerOnSharedPreferenceChangeListener(null);

		// Initialisation des boutons, textviews...
		connexionBtn 	= (Button) findViewById(R.id.launcher_connection_btn);
		usernameErrorTv	= (TextView) findViewById(R.id.username_error_tv);
		
		// On vérifie si le wifi est actif
		if (!LibUtil.isWifiActive(this))
			Toast.makeText(this, getString(R.string.launcher_wifi_warning) , Toast.LENGTH_LONG).show();

		// Ajout d'un onClickListener sur le bouton de connexion
		connexionBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				startLobbyActivity();
			}
		});
		
		// Si le username n'est pas setté, on désactive le bouton de connexion et on affiche le message
		if (preferences.getString("prefs_userName", "").equals("")) {
			Log.d(TAG, "not set");
			usernameErrorTv.setText(R.string.launcher_username_error_tv);
			connexionBtn.setEnabled(false);
			usernameErrorTv.setOnClickListener(onNoUserNameClick);
		} else {
			// On met un message de bienvenue
			usernameErrorTv.setText(getString(R.string.launcher_welcome) + " " + preferences.getString("prefs_userName", "") + " !");
			usernameErrorTv.setOnClickListener(null);
			
			// Si l'utilisateur a choisi la connexion auto, on lance l'activité Lobby
			if (preferences.getBoolean("prefs_autoLogin", false)) {
				startLobbyActivity();
			}
		}
		
		// On rend le logo un peu transparent
		ImageView logo = (ImageView) findViewById(R.id.imageView1);
		logo.setAlpha(220);	// Entre 0 et 255
	}

	/**
	 * onResume, on revérifie l'username, au cas où l'utilisateur vient de le changer dans les préférences
	 */
	@Override
	protected void onResume() {
		super.onResume();
		if (preferences.getString("prefs_userName", "").equals("")) {
			usernameErrorTv.setText(R.string.launcher_username_error_tv);
			connexionBtn.setEnabled(false);
			usernameErrorTv.setOnClickListener(onNoUserNameClick);
		} else {
			usernameErrorTv.setText(getString(R.string.launcher_welcome) + " " + preferences.getString("prefs_userName", "") + " !");
			connexionBtn.setEnabled(true);
			usernameErrorTv.setOnClickListener(null);
		}
	}


	/**
	 * Appui sur le bouton Menu : ouverture d'un menu pour accéder aux préférences de l'appli
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.preferences_menu, menu);
		return true;
	}


	/**
	 * Gestion du choix d'un des items du menu
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.itemPreferences:
			startActivity(new Intent(this, PreferencesActivity.class));
			break;
		default:
			Log.w(TAG, "Choix non pris en charge");
			break;
		}
		return true;
	}
	
	private OnClickListener onNoUserNameClick = new OnClickListener() {
		
		public void onClick(View v) {
			startActivity(new Intent(LauncherActivity.this, PreferencesActivity.class));
		}
	};


	/**
	 * Lance l'activité "Lobby"
	 */
	private void startLobbyActivity() {
		startActivity(new Intent(this, LobbyActivity.class));
	}
}