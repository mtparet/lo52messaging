package lo52.messaging.fragments;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

import lo52.messaging.activities.ConversationPagerActivity;
import lo52.messaging.model.Conversation;
import lo52.messaging.services.NetworkService;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;



public class ConversationListFragment extends ListFragment {

	private static final String TAG = "ConversationListFragment";

	int visibility = View.VISIBLE;

	// Table des conversations
	Hashtable<Integer,Conversation> conversationsTable;

	// Liste des noms des conversations
	ArrayList<String> conversationNames;

	// Activité parente
	ConversationPagerActivity parentActivity;


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Log.d(TAG, "CreateView");

		// Initialise la liste des conversations
		conversationsTable = new Hashtable<Integer, Conversation>();

		// Initialise la liste des noms des conversations
		conversationNames = new ArrayList<String>();

		// Met à jour la liste des noms des conversations
		updateLocalConversationsList();
		
		setListAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, conversationNames));

		parentActivity = (ConversationPagerActivity) getActivity();

		return super.onCreateView(inflater, container, savedInstanceState);
	}


	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		// Met à jour la liste des noms des conversations
		updateLocalConversationsList();

		// Utilisation d'un layout classique en 1 ligne, pas besoin de définir de layout (inclus dans android.R)
		setListAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, conversationNames));
	}


	@Override
	public void onResume() {
		super.onResume();

		// Met à jour la liste des noms de conversations
		updateLocalConversationsList();

		this.getView().setVisibility(visibility);
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		if (parentActivity != null) {
			parentActivity.goToConversationNumber(position + 1); // + 1 car le fragment 0 est ce fragment
		} else {
			Log.e(TAG, "Parent null");
		}
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

		// Null proofing : cette méthode peut être appelée avant que la liste des conversations n'ait été
		// initialisée (si le second tab n'a pas été ouvert).
		if (conversationNames == null) return;

		Log.d(TAG, "Updating conversation list... ");
		// Vide la liste actuelle des noms de conversations
		conversationNames.clear();

		// Liste des conversations
		Hashtable<Integer,Conversation> c = NetworkService.getListConversations();

		Iterator<?> it = c.entrySet().iterator();
		while (it.hasNext()) {
			@SuppressWarnings("rawtypes")
			Map.Entry pairs = (Map.Entry)it.next();
			//int conversId = (Integer) pairs.getKey();
			Conversation conv = (Conversation) pairs.getValue();

			String conversationName = conv.generateConversationName();

			Log.d(TAG, "Nom généré : " + conversationName + " : ajouté à la liste");
			conversationNames.add(conversationName);

			it.remove();
		}
	}
}
