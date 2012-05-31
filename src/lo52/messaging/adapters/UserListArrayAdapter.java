package lo52.messaging.adapters;

import java.util.ArrayList;

import lo52.messaging.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 *	Adapter pour la ListView associée à cette activité.
 *	Fait automatiquement le mapping entre les données à insérer dans la liste et le layout.
 */
public class UserListArrayAdapter extends ArrayAdapter<String> {
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
	
	/**
	 * Permet d'ajouter un utilisateur à la liste
	 * @param username	Le nom de l'utilisateur à ajouter
	 */
	public void addUser(String username) {
		values.add(username);
		this.notifyDataSetChanged();
	}
	
	/**
	 * Permet d'ajouter une liste d'utilisateurs à la liste
	 * @param users
	 */
	public void addMultipleUsers(ArrayList<String> users) {
		values.addAll(users);
		this.notifyDataSetChanged();
	}

	/**
	 * Permet d'enlever un utilisateur de la liste
	 * @param username	Le nom de l'utilisateur
	 */
	public void removeUser(String username) {
		values.remove(username);
		this.notifyDataSetChanged();
	}

	public void clearValues() {
		values.clear();
		this.notifyDataSetChanged();
		
	}
	
}
