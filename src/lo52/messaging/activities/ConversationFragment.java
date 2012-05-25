package lo52.messaging.activities;

import lo52.messaging.R;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Contenu d'un "tab" (fragment) à l'intérieur de ConversationPagerActivity.
 */
public class ConversationFragment extends Fragment {

	@SuppressWarnings("unused")
	private static final String TAG = "ConversationFragment";
	final ConversationFragment thisFrag = this;
	
	/**
	 * TODO : pour le moment il y a juste le layout test
	 */
	TextView tvTest;
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

	public void setTextViewText(String s) {
		// FIXME vérifier pour pouvoir directement utiliser un TV setté dans onCreateView
		TextView tv1 = (TextView) v.findViewById(R.id.test_textView1);
		tv1.setText(s);
	}
	
	public View getFragmentView() {
		return v;
	}
	
	public ConversationFragment getThisFrag() {
		return thisFrag;
	}
}