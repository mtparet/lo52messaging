package lo52.messaging.activities;

import lo52.messaging.R;
import android.app.Activity;
import android.os.Bundle;

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
