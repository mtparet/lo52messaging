package lo52.messaging.activities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;

import lo52.messaging.R;
import lo52.messaging.model.Conversation;
import lo52.messaging.model.User;
import lo52.messaging.services.NetworkService;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
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
import android.widget.Toast;

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
	}
	

	

	@Override
	protected void onResume() {
		
		/**
		 * TODO voir comment on réalise la synchronisation:
		 * - soit manuelle : à chaque fois que l'on remet l'activity en premier plan et avec un bouton rafraichir
		 * - soit automatique : implique de créer un nouveau type de paquet (avec action = REMOVE_USER et ADD_USER)
		 */
		//on récupère la liste des users
		userList = NetworkService.getListUsers();
		values.clear();
		for( User user : userList.values()){
			values.add(user.getName());
		}
		adapter.notifyDataSetChanged();
		
		super.onResume();
	}


	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		String item = (String) getListAdapter().getItem(position);
		Toast.makeText(this, item + " selected", Toast.LENGTH_SHORT).show();
		
		// oui et bien moi j'ai des TODO, mais toi t'as des FIXME !
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
		
		int newConvers = createConversation("conversation test", list);

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
			// TODO : refresh liste
			Toast.makeText(this, "Refresh list to do", Toast.LENGTH_SHORT).show();
		}
		else {
			Log.e(TAG, "Item non pris en charge");
		}
		return true;
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
	 * Créé une conversation
	 * @param conversation_name
	 * @param userList
	 * @return le numéro de la conversation créé
	 */
	private int createConversation(String conversation_name, ArrayList<Integer> userListId){
		Conversation conversation = new Conversation(conversation_name, userListId);

		Intent broadcastIntent = new Intent(NetworkService.ReceiveConversation);
		Bundle bundle = new Bundle();

		bundle.putParcelable("conversation", conversation);
		broadcastIntent.putExtra("conversation", bundle);

		sendBroadcast(broadcastIntent);

		// Indique à l'activité ConversationPagerActivity qu'il devra créer un fragment correspondant
		Log.d(TAG, "setting to create " + conversation.getConversation_id());
		NetworkService.setHasLocalConversationToCreate(conversation.getConversation_id());
		
		return conversation.getConversation_id();
	}
	
}
