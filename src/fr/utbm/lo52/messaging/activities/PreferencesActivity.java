package fr.utbm.lo52.messaging.activities;


import android.os.Bundle;
import android.preference.PreferenceActivity;
import fr.utbm.lo52.messaging.R;

/**
 *	Activité d'édition des préférences globale de l'application
 *	Construit automatiquement à partir de xml/preferences.xml 
 */
public class PreferencesActivity extends PreferenceActivity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Plutot que setViewContent()
		addPreferencesFromResource(R.xml.preferences);
	}

}
