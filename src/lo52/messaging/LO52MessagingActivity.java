package lo52.messaging;


import android.app.Activity;
import android.os.Bundle;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

public class LO52MessagingActivity extends Activity {
    /** Called when the activity is first created. */
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }
    
    private BroadcastReceiver fromService = new BroadcastReceiver(){

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			
		}
    	
    };
    
    @Override
    protected void onResume() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(SendBroadcast.BROADCAST_ACTION);
        registerReceiver(fromService, filter);

        super.onResume();
    }

    @Override
    protected void onPause() {
        unregisterReceiver(fromService);
        super.onPause();
    }

}