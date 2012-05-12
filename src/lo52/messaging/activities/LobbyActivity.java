package lo52.messaging.activities;

import lo52.messaging.R;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

public class LobbyActivity extends Activity {

	private static final String TAG = "LobbyActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.lobby);
		Log.d(TAG, "Lancement activité lobby");
	}
}
