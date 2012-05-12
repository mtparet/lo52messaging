package lo52.messaging.activities;

import lo52.messaging.R;
import lo52.messaging.services.NetworkService;
import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TabHost;

public class LobbyActivity extends TabActivity {

	private static final String TAG = "LobbyActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.lobby);
		
		//lancement du service
		startService(new Intent(LobbyActivity.this, NetworkService.class));

		Log.d(TAG, "Lancement activité lobby");


		TabHost tabHost = getTabHost();  // The activity TabHost
		TabHost.TabSpec spec;  // Resusable TabSpec for each tab
		Intent intent;  // Reusable Intent for each tab

		// Create an Intent to launch an Activity for the tab (to be reused)
		intent = new Intent().setClass(this, TestActivity.class);

		// Initialize a TabSpec for each tab and add it to the TabHost
		spec = tabHost.newTabSpec("tab1").setIndicator("Tab1 bla bla", null).setContent(intent);
		tabHost.addTab(spec);
		
		spec = tabHost.newTabSpec("tab2").setIndicator("Tab2 bla bla", null).setContent(intent);
		tabHost.addTab(spec);
		
		spec = tabHost.newTabSpec("tab3").setIndicator("Tab3 bla bla", null).setContent(intent);
		tabHost.addTab(spec);
		
		spec = tabHost.newTabSpec("tab4").setIndicator("Tab4 bla bla", null).setContent(intent);
		tabHost.addTab(spec);
		
		spec = tabHost.newTabSpec("tab5").setIndicator("Tab5 bla bla", null).setContent(intent);
		tabHost.addTab(spec);
		
		spec = tabHost.newTabSpec("tab6").setIndicator("Tab6 bla bla", null).setContent(intent);
		tabHost.addTab(spec);


	}
}
