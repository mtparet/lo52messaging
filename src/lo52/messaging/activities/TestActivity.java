package lo52.messaging.activities;

import lo52.messaging.R;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

/**
 * Activité de test, vide, qui sert juste à tester les onglets
 *
 */
public class TestActivity extends Activity {
	
	private static final String TAG = "TestActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, "Lancement activité test");
		setContentView(R.layout.test);
	}

}
