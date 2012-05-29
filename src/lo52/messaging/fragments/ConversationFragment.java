package lo52.messaging.fragments;

import lo52.messaging.R;
import lo52.messaging.activities.ConversationPagerActivity;
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
	//TextView conversName_tv;
	//EditText conversText_edit;
	//EditText conversUserText_edit;
	//Button conversMedia_btn;
	//Button conversSend_btn;
	View v;
	
	String conversationName_str = "";
	String conversationText_str = "";
	
	ConversationPagerActivity parentActivity;
	
	@Override
	public void onResume() {
		super.onResume();
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// Inflate le layout depuis le xml
		v = inflater.inflate(R.layout.conversation, container, false);
		
		// Initialise les différents éléments du layout
		TextView conversName_tv 		= (TextView) v.findViewById(R.id.conversation_name);
		EditText conversText_edit		= (EditText) v.findViewById(R.id.conversation_content);
		//EditText conversUserText_edit	= (EditText) v.findViewById(R.id.conversation_usermessage);
		Button conversMedia_btn 	= (Button) v.findViewById(R.id.conversation_media_button);
		Button conversSend_btn 		= (Button) v.findViewById(R.id.conversation_send_button);
		
		// Initialise leur valeur
		conversName_tv.setText(conversationName_str);
		conversText_edit.setText(conversationText_str);
		
		parentActivity = (ConversationPagerActivity) getActivity();
		
		conversMedia_btn.setOnClickListener(mediaButtonClickListener);
		conversSend_btn.setOnClickListener(sendButtonClickListener);
		
		return v;
	}
	
	// Création et assignation des onClickListerners
	OnClickListener mediaButtonClickListener = new OnClickListener() {
		public void onClick(View v) {
			Log.d(TAG, "Click bouton media");
		}
	};
	
	OnClickListener sendButtonClickListener = new OnClickListener() {
		public void onClick(View v) {
			Log.d(TAG, "Click bouton envoi");
			parentActivity.onFragmentSendButtonClick(getConversUserText(), getConversation_id());
		}
	};

	
	/**
	 * 
	 * Getters / Setters
	 * 
	 */
	
	/**
	 * Retourne l'ID associé à cette conversation
	 * @return
	 */
	public int getConversation_id() {
		return conversation_id;
	}
	
	/**
	 * Attribue un ID à la conversation. Doit (devrait) être appelé une fois que le fragment de la conversation a été créé.
	 * @param conversation_id
	 */
	public void setConversation_id(int conversation_id) {
		this.conversation_id = conversation_id;
	}

	/**
	 * Met à jour le nom de la conversation, affiché en haut du fragment de conversation 
	 * @param conversName
	 */
	public void setConversName(String conversName) {
		Log.d(TAG, "nom modifié");
		conversationName_str = conversName;
	}

	/**
	 * Retourne le texte contenu dans le champ de conversation
	 * @return
	 */
	public String getConversText() {
		return conversationText_str;
	}

	/**
	 * Met du texte dans le champ de conversation de la vue. Attention, si du texte est déjà présent il sera écrasé.
	 * @param conversText
	 * @see appendConversationText()
	 */
	public void setConversText(String conversText) {
		this.conversationText_str = conversText;
	}

	
	/**
	 * Retourne le texte entré par l'utilisateur
	 * @return
	 */
	public String getConversUserText() {
		EditText conversUserText_edit = (EditText) v.findViewById(R.id.conversation_usermessage);
		return conversUserText_edit.getText().toString();
	}

	
	/**
	 * Set le texte dans le champ dans lequel l'uilisateur écrit. Ne devrait pas être utilisé directement.
	 * @param conversUserText
	 */
	/*public void setConversUserText(String conversUserText) {
		this.conversUserText_edit.setText(conversUserText);
	}*/
	
	
	/**
	 * Ajoute du texte à la suite du texte présent dans le champ de conversation sans écraser le texte présent
	 * @param text
	 */
	public void appendConversationText(String text) {
		//this.setConversText(this.getConversText() + " " + text);
		this.conversationText_str += " " + text;
	}

	
	/**
	 * @deprecated
	 * @return
	 */
	public View getFragmentView() {
		return v;
	}
	
	/**
	 * @deprecated
	 * @return
	 */
	public ConversationFragment getThisFrag() {
		return thisFrag;
	}
	
}