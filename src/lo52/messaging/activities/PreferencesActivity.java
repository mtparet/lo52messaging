package lo52.messaging.activities;


import lo52.messaging.R;
import android.os.Bundle;
import android.preference.PreferenceActivity;

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
