package lo52.messaging.activities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import lo52.messaging.R;
import lo52.messaging.model.User;
import lo52.messaging.services.NetworkService;
import android.app.ListActivity;
import android.content.Context;
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
	HashMap<Integer, User> userList;
	ArrayList<String> values;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		values = new ArrayList<String>(Arrays.asList("Pierre", "Paul", "Jacques",
				"Michelle", "Gros connard", "Jean-René", "Superman", "Chuck Norris",
				"Jean Claude Van Dam", "Yoda"));

		// Utilisation de l'adapteur custom
		adapter = new UserListArrayAdapter(this, values);
		setListAdapter(adapter);
		
		// Ajout/suppression d'items dans la liste
		addUser("test connard");
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
			Toast.makeText(this, "Refresh list", Toast.LENGTH_SHORT).show();
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
	
}
