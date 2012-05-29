package lo52.messaging.fragments;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

import lo52.messaging.model.Conversation;
import lo52.messaging.model.User;
import lo52.messaging.services.NetworkService;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;



public class ConversationListFragment extends ListFragment {

	private static final String TAG = "ConversationListFragment";

	int visibility = View.VISIBLE;

	Hashtable<Integer,Conversation> conversationsTable;

	//String[] Values = {};
	ArrayList<String> conversationNames;

	@Override
	public void onResume() {
		super.onResume();
		this.getView().setVisibility(visibility);

		// Met à jour la liste des noms de conversations
		updateLocalConversationsList();
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		// Initialise la liste des noms des conversations
		conversationNames = new ArrayList<String>();
		conversationNames.add("User 1");
		conversationNames.add("User 2");

		// Initialise la liste des conversations
		conversationsTable = new Hashtable<Integer, Conversation>();
				
		// Met à jour la liste des noms des conversations
		updateLocalConversationsList();
		
		// Utilisation d'un layout classique en 1 ligne, pas besoin de définir de layout (inclus dans android.R)
		setListAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, conversationNames));


	}

	/**
	 * Utiliser View.GONE ou View.VISIBLE
	 * @param visibilty_constant
	 */
	public void setVisibility(int visibilty_constant) {
		visibility = visibilty_constant;
	}


	/**
	 * Met à jour la liste des conversations. Récupère la hastable depuis le NetworkService.
	 */
	public void updateLocalConversationsList() {

		Log.d(TAG, "Updating conversation list... ");
		// Vide la liste actuelle des noms de conversations
		conversationNames.clear();

		// Liste des conversations
		Hashtable<Integer,Conversation> c = NetworkService.getListConversations();
		
		Iterator<?> it = c.entrySet().iterator();
		while (it.hasNext()) {
			@SuppressWarnings("rawtypes")
			Map.Entry pairs = (Map.Entry)it.next();
			int conversId = (Integer) pairs.getKey();
			Conversation conv = (Conversation) pairs.getValue();

			String conversationName = "";

			// DEBUG
			Log.d(TAG, "Conversation : " + conversId);
						
			// Liste des IDs des membres de la conversations
			ArrayList<Integer> conversMembersIds = conv.getListIdUser();
			// Liste des membres connus par l'appli
			Hashtable<Integer, User> userList = NetworkService.getListUsers();

			// Pour chaque Id de la liste des conversation, on cherche le nom de l'utilisateur correspondant
			for (int memberId : conversMembersIds) {
				
				Log.d(TAG, "recherche membre " + memberId);
				
				User u = userList.get(memberId);
				
				if (u == null) Log.e(TAG, "Utilisateur non trouvé");
				else {
					conversationName += u.getName() + " ";
				}
			}

			// Ajout du nom dans la liste globale
			conversationNames.add(conversationName);

			it.remove();
		}
	}


	/*public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		/*if (container == null) {
			tvTest = null;
            return null;
        }*/


	/*	v = inflater.inflate(R.layout.test, container, false);
		return v;
	}*/

}
