package lo52.messaging.fragments;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.ArrayAdapter;



public class ConversationListFragment extends ListFragment {

	int visibility = View.VISIBLE;
	
	String[] Values = {"Obama", "Gilles Bertrand", "Chuck Norris", "Matthieu Paray"};

	@Override
	public void onResume() {
		super.onResume();
		this.getView().setVisibility(visibility);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		// Utilisation d'un layout classique en 1 ligne, pas besoin de définir de layout (inclus dans android.R)
		setListAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, Values));
	}
	
	/**
	 * Utiliser View.GONE ou View.VISIBLE
	 * @param visibilty_constant
	 */
	public void setVisibility(int visibilty_constant) {
		visibility = visibilty_constant;
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
