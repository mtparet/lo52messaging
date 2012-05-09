package lo52.messaging.activities;

import lo52.messaging.R;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;


/**
 * LO52Messaging
 * Activité principale (launcher)
 */
public class LO52Messaging extends Activity {

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
		
		// Si le username n'est pas setté, on désactive le bouton de connexion et on affiche le message
		if (preferences.getString("prefs_userName", "").equals("")) {
			Log.d(TAG, "not set");
			usernameErrorTv.setText(R.string.launcher_username_error_tv);
			connexionBtn.setEnabled(false);
		} else {
			usernameErrorTv.setText("");
			
			// TODO : lire la valeur de prefs_autoLogin et éventuellement effectuer la connexion si true.
		}
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
			usernameErrorTv.setText("");
			connexionBtn.setEnabled(true);
			
			// TODO : lire la valeur de prefs_autoLogin et éventuellement effectuer la connexion si true.
		}
	}


	/**
	 * Appui sur le bouton Menu : ouverture d'un menu pour accéder aux préférences de l'appli
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.preferencesmenu, menu);
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
}