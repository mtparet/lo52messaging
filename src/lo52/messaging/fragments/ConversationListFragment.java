package lo52.messaging.fragments;

import lo52.messaging.R;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ConversationListFragment extends Fragment {
	
	View v;

	@Override
	public void onResume() {
		super.onResume();
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		/*if (container == null) {
			tvTest = null;
            return null;
        }*/

		v = inflater.inflate(R.layout.test, container, false);
		return v;
	}

}
