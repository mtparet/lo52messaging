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
	int conversation_id;
	TextView conversName_tv;
	EditText conversText_edit;
	EditText conversUserText_edit;
	Button conversMedia_btn;
	Button conversSend_btn;
	View v;
	
	String conversationName_str = "";
	String conversationText_str = "";
	
	OnClickListener mediaButtonClickListener;
	OnClickListener sendButtonClickListener;
	
	ConversationPagerActivity parentActivity;

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
		
		conversName_tv.setText(conversationName_str);
		conversText_edit.setText(conversationText_str);
		
		parentActivity = (ConversationPagerActivity) getActivity();
		
		// Création et assignation des onClickListerners
		mediaButtonClickListener = new OnClickListener() {
			public void onClick(View v) {
				Log.d(TAG, "Click bouton media");
			}
		};
		
		sendButtonClickListener = new OnClickListener() {
			public void onClick(View v) {
				Log.d(TAG, "Click bouton envoi");
				parentActivity.onFragmentSendButtonClick();
			}
		};
		
		conversMedia_btn.setOnClickListener(mediaButtonClickListener);
		conversSend_btn.setOnClickListener(sendButtonClickListener);
		
		return v;
	}

	
	/**
	 * 
	 * Getters / Setters
	 * 
	 */
	
	public int getConversation_id() {
		return conversation_id;
	}
	
	public void setConversation_id(int conversation_id) {
		this.conversation_id = conversation_id;
	}

	public String getConversName() {
		return (String) conversName_tv.getText();
	}

	public void setConversName(String conversName) {
		conversationName_str = conversName;
		//TextView tv = (TextView) v.findViewById(R.id.conversation_name);
		//Log.d(TAG, "Test TV " + tv);
		//tv.setText(conversName);
		//this.conversName_tv.setText(conversName);
	}

	public String getConversText() {
		return conversText_edit.getText().toString();
	}

	/**
	 * Met du texte dans le champ de conversation de la vue. Attention, si du texte est déjà présent il sera écrasé.
	 * @param conversText
	 * @see appendConversationText()
	 */
	public void setConversText(String conversText) {
		//this.conversText_edit.setText(conversText);
		this.conversationText_str = conversText;
	}

	public String getConversUserText() {
		return conversUserText_edit.getText().toString();
	}

	/**
	 * Set le texte dans le champ dans lequel l'uilisateur écrit. Ne devrait pas être utilisé directement.
	 * @param conversUserText
	 */
	public void setConversUserText(String conversUserText) {
		this.conversUserText_edit.setText(conversUserText);
	}
	
	
	/**
	 * Ajoute du texte à la suite du texte présent dans le champ de conversation sans écraser le texte présent
	 * @param text
	 */
	public void appendConversationText(String text) {
		//this.setConversText(this.getConversText() + " " + text);
		this.conversationText_str += " " + text;
	}

	
	public View getFragmentView() {
		return v;
	}
	
	public ConversationFragment getThisFrag() {
		return thisFrag;
	}
	
}