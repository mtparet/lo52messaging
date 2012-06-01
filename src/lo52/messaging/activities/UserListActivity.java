package lo52.messaging.activities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;

import lo52.messaging.R;
import lo52.messaging.adapters.UserListArrayAdapter;
import lo52.messaging.model.Conversation;
import lo52.messaging.model.User;
import lo52.messaging.services.NetworkService;
import lo52.messaging.views.UserListView;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

/**
 *	Activité servant à lister les utilisateurs 
 */
public class UserListActivity extends Activity {

	private static final String TAG = "UserListActivity";
	// Adapter utilisé pour mapper les données au layout de la liste
	UserListArrayAdapter adapter;
	
	UserListView userListView;
	
	//liste des users
	Hashtable<Integer, User> userList;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.useractivity);
		
		userListView = (UserListView) findViewById(R.id.listView1);
		
		ArrayList<String> values = new ArrayList<String>(Arrays.asList(""));
		
		// Utilisation de l'adapteur custom
		adapter = new UserListArrayAdapter(this, values);
		
		userListView.setAdapter(adapter);
		userListView.setOnItemClickListener(itemClickListenerList);
				
		// Enregistre le broadcast receiver permettant de mettre à jour la liste des users en direct
		IntentFilter userListUpdatefilter = new IntentFilter();
		userListUpdatefilter.addAction(NetworkService.UserListUpdated);
		registerReceiver(UserlistUpdate_BrdcReceiver, userListUpdatefilter);
	}
	

	@Override
	protected void onResume() {
		Log.d(TAG, "onResume");

		// on réenregistre le broadcast listener sur la liste des users
		IntentFilter userListUpdatefilter = new IntentFilter();
		userListUpdatefilter.addAction(NetworkService.UserListUpdated);
		registerReceiver(UserlistUpdate_BrdcReceiver, userListUpdatefilter);
		
		//on récupère la liste des users
		refreshUserList();
		
		super.onResume();
	}


	@Override
	protected void onPause() {
		
		// Désinscription du broadcast receiver
		unregisterReceiver(UserlistUpdate_BrdcReceiver);
				
		super.onPause();
	}


	@Override
	protected void onDestroy() {
		
		super.onDestroy();
	}

	private OnItemClickListener itemClickListenerList = new OnItemClickListener()
    {

		@Override
		public void onItemClick(AdapterView<?> listView, View view, int position, long row) {

    		String item = (String) listView.getAdapter().getItem(position);
    		
    		User user_selected = null;
    		for(User user : userList.values()){
    			if(user.getName() == item){
    				user_selected = user;
    			}
    		}
    		
    		if(user_selected == null){
    			Log.e(TAG,"user inconnu");
    			return;
    		}
    		
    		// ArrayList contenant l'ID de l'utilisateur
    		ArrayList<Integer> list = new ArrayList<Integer>();
    		list.add(user_selected.getId());
    		list.add(NetworkService.getUser_me().getId());
    		
    		// Création de la conversation avec en nom le nom de l'utilisateur, si elle n'existe pas déjà
    		if (!NetworkService.doesConversationExist(list)) {
    			
    			Conversation conversation = new Conversation(item, list);
    			conversation.sendToNetworkService(getApplicationContext());
    			
    			// Rend le tab des conversations actif
    			LobbyActivity parent = (LobbyActivity) getParent();
    			parent.setActiveTabByTag(LobbyActivity.TAG_TAB_CONVERSATIONS);
    		} else {
    			// Si la conversation existe déjà on rend le second tab actif et on essaye de switcher sur le fragment correspondant
    			LobbyActivity parent = (LobbyActivity) getParent();
    			parent.setSwitchToConversationFragment(list);
    			parent.setActiveTabByTag(LobbyActivity.TAG_TAB_CONVERSATIONS);
    		}		
		}
    };


	
	
	/**
	 * Gestion du menu option et des actions associées
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.user_list_activity_menu, menu);
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		int menuItemId = item.getItemId();
		if (menuItemId == R.id.item_refresh) {
			refreshUserList();
			//Toast.makeText(this, "", Toast.LENGTH_SHORT).show();
		}
		else {
			Log.e(TAG, "Item non pris en charge");
		}
		return true;
	}
	
	/**
	 * Met à jour la liste des utilisateurs en faisant appel au service
	 */
	public void refreshUserList() {
		userList = NetworkService.getListUsers();
		adapter.clearValues();
		for(User user : userList.values()){
			adapter.add(user.getName());
		}
		Log.d(TAG, "Liste users mise à jour");
		
		// Affiche ou cache le texte Pas d'utilisateur
		TextView tv = (TextView) findViewById(R.id.no_user);
		if (adapter.countValues() > 0) tv.setVisibility(View.GONE);
		else tv.setVisibility(View.VISIBLE);
	}
	
	
	/**
	 * Broadcast receiver qui reçoit une notification comme quoi la liste des utilisateurs doit être mise à jour
	 */
	private BroadcastReceiver UserlistUpdate_BrdcReceiver = new BroadcastReceiver(){

		@Override
		public void onReceive(Context context, Intent intent) {
			Log.d(TAG, "réception d'un broadcast pour rafraichir la liste des users");
			refreshUserList();
		}
		
	};
	
}
