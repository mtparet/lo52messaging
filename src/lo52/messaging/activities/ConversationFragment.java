package lo52.messaging.activities;

import lo52.messaging.R;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Contenu d'un "tab" (fragment) à l'intérieur de ConversationPagerActivity.
 */
public class ConversationFragment extends Fragment {
	
	private static final String TAG = "FragmentDeMerde";
	
	
	
	/**
	 * TODO : pour le moment il y a juste le layout test
	 */
	TextView tvTest;
	View v;
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		/*if (container == null) {
			tvTest = null;
            return null;
        }*/
		
		v = inflater.inflate(R.layout.test, container, false);
		
		/**
		 * Ici v est bien setté
		 * En gros ici on pourrait modifier le contenu du fragment, mais juste ici, à sa création.
		 */
		Log.d(TAG, "** View: " + v);
		return v;

		
	}
	
	public void fuckingShit() {
		/**
		 * 	Et ici v est null. Je pense que c'est parce qu'il recycle la view quand elle est pas affichée et il la regénère quand on reswitch dessus.
		 * 	getView() aussi fait une NullPointerException 
		 * 
		 */
		Log.d(TAG, "** View2: " + v);
	}
	
}