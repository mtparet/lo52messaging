package fr.utbm.lo52.messaging.activities;

import android.app.Activity;
import android.os.Bundle;
import fr.utbm.lo52.messaging.R;

/**
 *	Activité "A propos", affichant des informations sur l'application 
 */
public class AboutActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Layout
		setContentView(R.layout.about);
	}
}
