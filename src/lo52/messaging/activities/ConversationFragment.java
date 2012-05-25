package lo52.messaging.activities;

import lo52.messaging.R;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Contenu d'un "tab" (fragment) à l'intérieur de ConversationPagerActivity.
 */
public class ConversationFragment extends Fragment {

	private static final String TAG = "ConversationFragment";
	final ConversationFragment thisFrag = this;
	
	/**
	 * TODO : pour le moment il y a juste le layout test
	 */
	TextView conversName_tv;
	EditText conversText_edit;
	EditText conversUserText_edit;
	Button conversMedia_btn;
	Button conversSend_btn;
	View v;
	
	OnClickListener mediaButtonClickListener;
	OnClickListener sendButtonClickListener;
	
	ConversationPagerActivity procreator;

	@Override
	public void onResume() {
		super.onResume();
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		// Inflate le layout depuis le xml
		v = inflater.inflate(R.layout.conversation, container, false);
		
		// Initialise les différents éléments du layout
		conversName_tv 			= (TextView) v.findViewById(R.id.conversation_name);
		conversText_edit		= (EditText) v.findViewById(R.id.conversation_content);
		conversUserText_edit	= (EditText) v.findViewById(R.id.conversation_usermessage);
		conversMedia_btn 		= (Button) v.findViewById(R.id.conversation_media_button);
		conversSend_btn 		= (Button) v.findViewById(R.id.conversation_send_button);
		
		// Création et assignation des onClickListerners
		mediaButtonClickListener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.d(TAG, "Click bouton media");
			}
		};
		
		sendButtonClickListener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.d(TAG, "Click bouton envoi");
				procreator.onFragmentSendButtonClick();
			}
		};
		
		conversMedia_btn.setOnClickListener(mediaButtonClickListener);
		conversSend_btn.setOnClickListener(sendButtonClickListener);

		procreator = (ConversationPagerActivity) getActivity();
		
		return v;
	}

	public View getFragmentView() {
		return v;
	}
	
	public ConversationFragment getThisFrag() {
		return thisFrag;
	}
}