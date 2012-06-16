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
import fr.utbm.lo52.messaging.R;


/**
 * LO52Messaging
 * Activité principale (launcher)
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

		Log.d(TAG, "Lecture préférences : nom=" + preferences.getString("prefs_userName", "valeur_defaut") + " - connexion auto=" + preferences.getBoolean("prefs_autoLogin", false));

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
		} else {
			// On met un message de bienvenur
			usernameErrorTv.setText(getString(R.string.launcher_welcome) + " " + preferences.getString("prefs_userName", "") + " !");

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
		} else {
			usernameErrorTv.setText(getString(R.string.launcher_welcome) + " " + preferences.getString("prefs_userName", "") + " !");
			connexionBtn.setEnabled(true);

			// Si l'utilisateur a choisi la connexion auto, on lance l'activité Lobby
			/*if (preferences.getBoolean("prefs_autoLogin", false)) {
				startLobbyActivity();
			}*/
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
			Log.d(TAG, "Démarrage activité préférences");
			startActivity(new Intent(this, PreferencesActivity.class));
			break;
		default:
			Log.e(TAG, "Choix non pris en charge");
			break;
		}
		return true;
	}


	/**
	 * Lance l'activité "Lobby"
	 */
	private void startLobbyActivity() {
		startActivity(new Intent(this, LobbyActivity.class));
	}
}