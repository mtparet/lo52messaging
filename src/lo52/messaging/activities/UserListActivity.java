package lo52.messaging.activities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;

import lo52.messaging.R;
import lo52.messaging.model.Conversation;
import lo52.messaging.model.User;
import lo52.messaging.services.NetworkService;
import android.app.ListActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

/**
 *	Activité servant à lister les utilisateurs 
 */
public class UserListActivity extends ListActivity {

	private static final String TAG = "UserListActivity";
	// Adapter utilisé pour mapper les données au layout de la liste
	UserListArrayAdapter adapter;
	
	//liste des users
	Hashtable<Integer, User> userList;
	ArrayList<String> values;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		values = new ArrayList<String>(Arrays.asList(""));

		// Utilisation de l'adapteur custom
		adapter = new UserListArrayAdapter(this, values);
		setListAdapter(adapter);
		
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
		
		// Désinscription du broadcast receiver
		unregisterReceiver(UserlistUpdate_BrdcReceiver);
		
		super.onDestroy();
	}


	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		String item = (String) getListAdapter().getItem(position);
		
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
		
		ArrayList<Integer> list = new ArrayList<Integer>();
		
		list.add(user_selected.getId());
		
		// Création de la conversation avec en nom le nom de l'utilisateur
		Conversation conversation = new Conversation(item, list);
		conversation.sendToNetworkService(getApplicationContext());

		// Rend le tab des conversations actif
		LobbyActivity parent = (LobbyActivity) getParent();
		parent.setActiveTabByTag(LobbyActivity.TAG_TAB_CONVERSATIONS);
	}

	/**
	 * Permet d'ajouter un utilisateur à la liste
	 * @param username	Le nom de l'utilisateur à ajouter
	 */
	public void addUser(String username) {
		values.add(username);
		adapter.notifyDataSetChanged();
	}
	
	/**
	 * Permet d'ajouter une liste d'utilisateurs à la liste
	 * @param users
	 */
	public void addMultipleUsers(ArrayList<String> users) {
		values.addAll(users);
		adapter.notifyDataSetChanged();
	}

	/**
	 * Permet d'enlever un utilisateur de la liste
	 * @param username	Le nom de l'utilisateur
	 */
	public void removeUser(String username) {
		values.remove(username);
		adapter.notifyDataSetChanged();
	}
	
	
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
		values.clear();
		for(User user : userList.values()){
			values.add(user.getName());
		}
		adapter.notifyDataSetChanged();
		Log.d(TAG, "Liste users mise à jour");
	}
	

	/**
	 *	Adapter pour la ListView associée à cette activité.
	 *	Fait automatiquement le mapping entre les données à insérer dans la liste et le layout.
	 */
	private class UserListArrayAdapter extends ArrayAdapter<String> {
		private final Context context;
		private final ArrayList<String> values;

		public UserListArrayAdapter(Context context, ArrayList<String> values) {
			super(context, R.layout.userlist, values);
			this.context = context;
			this.values = values;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View rowView = inflater.inflate(R.layout.userlist, parent, false);
			// Récupérer l'accès aux différents éléments du layout
			TextView textView = (TextView) rowView.findViewById(R.id.label);
			ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);

			// Attribution des valeurs
			textView.setText(values.get(position));
			imageView.setImageResource(R.drawable.ic_launcher);
			return rowView;
		}
	}
	
	
	/**
	 * Broadcast receiver qui reçoit une notification comme quoi la liste des utilisateurs doit être mise à jour
	 */
	private BroadcastReceiver UserlistUpdate_BrdcReceiver = new BroadcastReceiver(){

		@Override
		public void onReceive(Context context, Intent intent) {
			Log.d(TAG, "réception d'un broadcast pour rafraichir la liste des users !");
			refreshUserList();
		}
		
	};
	
}
