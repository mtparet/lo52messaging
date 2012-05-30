package lo52.messaging.activities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import lo52.messaging.R;
import lo52.messaging.fragments.ConversationFragment;
import lo52.messaging.fragments.ConversationListFragment;
import lo52.messaging.model.Conversation;
import lo52.messaging.model.Message;
import lo52.messaging.model.broadcast.MessageBroacast;
import lo52.messaging.services.NetworkService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TabHost.TabContentFactory;
import android.widget.TextView;


/**
 * Activité permettant de regrouper les conversations dans une vue en "fragments".
 * Pour ajouter/supprimer des fragments merci d'utiliser addFragment() / removeFragmentAt()
 * 
 */
public class ConversationPagerActivity extends FragmentActivity implements TabHost.OnTabChangeListener, ViewPager.OnPageChangeListener {

	// Hauteur des tabs de titre des conversation
	private static final int TAB_HEIGHT = 40;

	// Longueur maximale, en caractères, pour le nom d'un fragment
	private static final int TAB_NAME_MAX_LENGTH = 7;

	private static final String TAG = "ConversationPagerActivity";

	// Constantes des menus d'option (valeurs aléatoires mais uniques)
	private static final int MENU_ITEM_CLOSE_CONV = 1001;

	private TabHost mTabHost;
	private ViewPager mViewPager;
	private HashMap<String, TabInfo> mapTabInfo = new HashMap<String, ConversationPagerActivity.TabInfo>();
	private MPagerAdapter mPagerAdapter;
	private ConversationListFragment conversationListFragment;

	/**
	 * Maintains extrinsic info of a tab's construct
	 */
	private class TabInfo {
		private String tag;
		@SuppressWarnings("unused")
		private Class<?> clss;
		@SuppressWarnings("unused")
		private Bundle args;
		@SuppressWarnings("unused")
		private Fragment fragment;
		TabInfo(String tag, Class<?> clazz, Bundle args) {
			this.tag = tag;
			this.clss = clazz;
			this.args = args;
		}
	}

	/**
	 * A simple factory that returns dummy views to the Tabhost
	 * @author mwho
	 */
	class TabFactory implements TabContentFactory {

		private final Context mContext;

		/**
		 * @param context
		 */
		public TabFactory(Context context) {
			mContext = context;
		}

		/** (non-Javadoc)
		 * @see android.widget.TabHost.TabContentFactory#createTabContent(java.lang.String)
		 */
		public View createTabContent(String tag) {
			View v = new View(mContext);
			v.setMinimumWidth(0);
			v.setMinimumHeight(0);
			return v;
		}
	}

	/** (non-Javadoc)
	 * @see android.support.v4.app.FragmentActivity#onCreate(android.os.Bundle)
	 */
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		// Inflate the layout
		setContentView(R.layout.conversations_viewpager);

		// Initialise the TabHost
		this.initialiseTabHost(savedInstanceState);

		if (savedInstanceState != null) {
			mTabHost.setCurrentTabByTag(savedInstanceState.getString("tab"));
		}

		// Intialision du ViewPager
		this.intialiseViewPager();

		/*
		 * on lance les exemples, utilisation d'un timer pour attendre que tout le reste soit en place
		 */
		//Timer timer = new Timer();
		//timer.schedule(new SendExempletimeTask(),4000);
		
		/**
		 * DEBUG
		 */
		/*ArrayList<Integer> list1 = new ArrayList<Integer>();
		list1.add(1234);
		addFragment(new Conversation(123, "name", list1), true);*/
	}

	/** (non-Javadoc)
	 * @see android.support.v4.app.FragmentActivity#onSaveInstanceState(android.os.Bundle)
	 */
	protected void onSaveInstanceState(Bundle outState) {
		outState.putString("tab", mTabHost.getCurrentTabTag()); //save the tab selected
		super.onSaveInstanceState(outState);
	}

	/**
	 * Initialise ViewPager
	 */
	private void intialiseViewPager() {

		List<Fragment> fragments = new Vector<Fragment>();

		// Initialise le ConversationListFragment
		conversationListFragment = (ConversationListFragment) Fragment.instantiate(this, ConversationListFragment.class.getName());
		// Et le cache
		//conversationListFragment.setVisibility(View.GONE);
		setListFragmentVisibility(View.GONE);
		fragments.add(conversationListFragment);

		this.mPagerAdapter  = new MPagerAdapter(super.getSupportFragmentManager(), fragments);
		this.mViewPager = (ViewPager)super.findViewById(R.id.viewpager);
		this.mViewPager.setAdapter(this.mPagerAdapter);
		this.mViewPager.setOnPageChangeListener(this);
	}

	/**
	 * Initialise the Tab Host
	 */
	private void initialiseTabHost(Bundle args) {
		mTabHost = (TabHost)findViewById(android.R.id.tabhost);
		mTabHost.setup();
		TabInfo tabInfo = null;
		ConversationPagerActivity.AddTab(this, this.mTabHost, this.mTabHost.newTabSpec("tab0").setIndicator(getString(R.string.conversations_tab_name)), (tabInfo = new TabInfo("tab0", ConversationListFragment.class, args)));
		this.mapTabInfo.put(tabInfo.tag, tabInfo);
		mTabHost.setOnTabChangedListener(this);
	}

	/**
	 * Add Tab content to the Tabhost
	 * @param activity
	 * @param tabHost
	 * @param tabSpec
	 * @param clss
	 * @param args
	 */

	private static void AddTab(ConversationPagerActivity activity, TabHost tabHost, TabHost.TabSpec tabSpec, TabInfo tabInfo) {
		// Attach a Tab view factory to the spec
		tabSpec.setContent(activity.new TabFactory(activity));
		tabHost.addTab(tabSpec);
		int totalTabs = tabHost.getTabWidget().getChildCount();

		// Fix : on fixe la hauteur des tabs en dur pour éviter qu'ils occupent trop de place verticalement
		((RelativeLayout)tabHost.getTabWidget().getChildTabViewAt(totalTabs-1)).removeViewAt(0);
		((TextView)((RelativeLayout)tabHost.getTabWidget().getChildTabViewAt(totalTabs-1)).getChildAt(0)).setHeight(30);
		tabHost.getTabWidget().getChildAt(totalTabs-1).getLayoutParams().height = TAB_HEIGHT;
	}

	/** (non-Javadoc)
	 * @see android.widget.TabHost.OnTabChangeListener#onTabChanged(java.lang.String)
	 */
	public void onTabChanged(String tag) {
		//TabInfo newTab = this.mapTabInfo.get(tag);
		int pos = this.mTabHost.getCurrentTab();
		this.mViewPager.setCurrentItem(pos);
	}

	/* (non-Javadoc)
	 * @see android.support.v4.view.ViewPager.OnPageChangeListener#onPageScrolled(int, float, int)
	 */
	public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

	}

	/* (non-Javadoc)
	 * @see android.support.v4.view.ViewPager.OnPageChangeListener#onPageSelected(int)
	 */
	public void onPageSelected(int position) {
		this.mTabHost.setCurrentTab(position);
	}

	/* (non-Javadoc)
	 * @see android.support.v4.view.ViewPager.OnPageChangeListener#onPageScrollStateChanged(int)
	 */
	public void onPageScrollStateChanged(int state) {
	}


	/**
	 * Création du menu d'options. Crafté à la main ici et non pas selon un .xml car le menu varie selon le nombre de conversations ouvertes
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(1, 1, 1, "(Dev) Ajouter un tab");
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// On supprime aussi l'option "Fermer la conversation courante" quand on est sur la liste des convers (premier tab)
		if (mTabHost.getCurrentTab() == 0) {
			menu.removeGroup(MENU_ITEM_CLOSE_CONV);
		} else {
			boolean itemFound = false;
			for(int i = 0; i < menu.size(); i++) {
				if (menu.getItem(i).getItemId() == MENU_ITEM_CLOSE_CONV) itemFound = true;
			}
			if (!itemFound) {
				menu.add(MENU_ITEM_CLOSE_CONV, MENU_ITEM_CLOSE_CONV, 2, getString(R.string.conversations_close_current));
				menu.getItem(1).setIcon(android.R.drawable.ic_menu_delete);
			}
		}

		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		if (item.getItemId() == 1) {

			Log.d(TAG, "DESACTIVE");
			//addFragment(0123456, false);

			/**
			 * MODIF D'UN FRAGMENT
			 */
			//MPagerAdapter mp = (MPagerAdapter) mViewPager.getAdapter();
			//ConversationFragment f0 = (ConversationFragment) mp.getFragmentsList().get(0);
			//f0.setTextViewText("modif du fragment 0");


		} else if (item.getItemId() == MENU_ITEM_CLOSE_CONV) {
			// Suppression
			removeFragmentAt(this.mTabHost.getCurrentTab());
		}
		return true;
	}


	/**
	 * Créé un fragment
	 * @param conversation_id	Id de la conversation associée
	 * @param autoSwitchOnFragment	Changer la vue pour le fragment créé
	 */
	public void addFragment(Conversation conversation, boolean autoSwitchOnFragment) {

		// Ajout d'un tab
		TabInfo tabInfo = null;

		// Génère le nom du tab. S'il est trop long, on le raccourcit
		String tabName = conversation.generateConversationName();
		if (tabName.length() > TAB_NAME_MAX_LENGTH) tabName = tabName.substring(0, TAB_NAME_MAX_LENGTH) + "...";

		ConversationPagerActivity.AddTab(this, this.mTabHost, this.mTabHost.newTabSpec("Tab"+(this.mPagerAdapter.getCount()+1)).setIndicator(tabName), ( tabInfo = new TabInfo("Tab"+(this.mPagerAdapter.getCount()+1), ConversationFragment.class, null)));
		this.mapTabInfo.put(tabInfo.tag, tabInfo);

		mPagerAdapter.addFragment(this);


		// Set le conversation id
		MPagerAdapter mp = (MPagerAdapter) mViewPager.getAdapter();
		ConversationFragment f_last = (ConversationFragment) mp.getFragmentsList().get(this.mPagerAdapter.getCount()-1);
		f_last.setConversation(conversation);
		f_last.setConversation_id(conversation.getConversation_id());
		f_last.setConversName(conversation.generateConversationName());

		// Cacher le TV
		if (this.mPagerAdapter.getCount() == 2) {
			TextView tv = (TextView) findViewById(R.id.no_conversation);
			tv.setVisibility(View.GONE);
			// Affiche la liste des conversations
			setListFragmentVisibility(View.VISIBLE);
		}

		if (autoSwitchOnFragment) {
			this.mViewPager.setCurrentItem(this.mPagerAdapter.getCount());
		}
	}


	/**
	 * Retourne un fragment en fonction de son Id
	 * @param id
	 * @return
	 */
	public ConversationFragment getFragmentById(int id) {

		MPagerAdapter mp = (MPagerAdapter) mViewPager.getAdapter();

		for (Fragment fragment : mp.getFragmentsList()) {
			// On ne vérifie que les ConversationFragment (le premier fragment est de type ConversationListFragment)
			if (fragment instanceof ConversationFragment) {
				if (((ConversationFragment) fragment).getConversation_id() == id) {
					return (ConversationFragment) fragment; 
				}
			}
		}
		return null;
	}

	/**
	 * Supprime le fragment à l'index donné
	 * @param index
	 * @deprecated : utilisé pour les tests, doit être réimplémenté pour quitter une conversation proprement...
	 *
	 *	FIXME semble être buggé quand on supprime plusieurs fragments et qu'au lieu de swiper, on clique sur leur titre (peut générer un force close)
	 */
	public void removeFragmentAt(int index) {

		if (mPagerAdapter.getFragmentsList().size() == 0) return;

		// Supprimer le fragment de l'adapter
		mPagerAdapter.getFragmentsList().remove(index);

		if (this.mTabHost.getCurrentTab() == index) {
			// Si on est sur le tab 0 on va sur le suivant
			if (index == 0)
				this.mViewPager.setCurrentItem(this.mTabHost.getCurrentTab()+1);
			// Sinon sur le précédent
			else 
				this.mViewPager.setCurrentItem(this.mTabHost.getCurrentTab()-1);
		}

		// Supprimer le tab physiquement
		mTabHost.getTabWidget().removeView(mTabHost.getTabWidget().getChildTabViewAt(index));

		// Afficher le textview "pas de conversation en cours bla bla"
		if (this.mPagerAdapter.getCount() == 1) {
			TextView tv = (TextView) findViewById(R.id.no_conversation);
			tv.setVisibility(View.VISIBLE);
			setListFragmentVisibility(View.GONE);
		}
	}


	/**
	 * Appelé au click sur le bouton envoi d'un fragment de conversation
	 * @param textMessage
	 * @param conversation_id
	 */
	public void onFragmentSendButtonClick(String textMessage, int conversation_id) {
		Log.d(TAG, "Envoi depuis fragment " + mTabHost.getCurrentTab());
		MessageBroacast messageBroad = new MessageBroacast(textMessage, conversation_id);
		messageBroad.sendToNetWorkService(getApplicationContext());
	}


	/**
	 * Visibilité d'un fragment de conversation
	 * @param visibility_constant	Utiliser les constantes de View.
	 */
	public void setListFragmentVisibility(int visibility_constant) {
		// Met à jour la visibilité en attribut du fragment
		conversationListFragment.setVisibility(visibility_constant);
		// Essaye de directement modifier la vue du fragment, a une meilleure chance de la rafraichir en direct
		if (conversationListFragment.getView() != null) {
			conversationListFragment.getView().setVisibility(visibility_constant);
		}
		// On "aide" le fragment à rafraichir sa liste de conversations
		conversationListFragment.updateLocalConversationsList();
	}

	/**
	 * Switche la vue sur la conversation donnée
	 * @param number
	 */
	public void goToConversationNumber(int number) {
		try {
			mTabHost.setCurrentTab(number);
		} catch (Exception e) {
			Log.e(TAG, "Fragment " + number + " inexistant...");
			Log.e(TAG, e.getMessage());
		}
	}


	/**
	 * To receive message
	 */
	private BroadcastReceiver messageReceiver = new  BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			Log.d(TAG, "Réception d'un nouveau message");
			Bundle bundle = intent.getBundleExtra("message");
			MessageBroacast message = bundle.getParcelable(MessageBroacast.tag_parcelable);
			Log.d(TAG, "message_client_id:" + message.getClient_id() + "message_convers_id:" + message.getConversation_id() + "message_message " + message.getMessage());

			// On récupère le fragment correspondant à l'id de la conversatoin
			ConversationFragment frag = getFragmentById(message.getConversation_id());
			// Et on set le texte
			//frag.appendConversationText(message.getMessage());
			
			/**
			 * FIXME : C'EST PAS PROPRE D'AJOUTER LE MESSAGE A LA CONVERSATION ICI.
			 * XXX 1
			 */
			frag.getConversation().addMessage(new Message(message.getClient_id(), message.getMessage()));
			frag.tryTextRefresh();
			/**
			 *  //===
			 */
			mPagerAdapter.notifyDataSetChanged();
		}
	};

	/**
	 * Recoit les nouvelles conversation
	 */
	private BroadcastReceiver conversationReceiver = new  BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			Log.d(TAG, "Réception d'une création de conversation");
			Bundle bundle = intent.getBundleExtra("conversation");
			Conversation conversation = bundle.getParcelable("conversation");
			Log.d(TAG, "conversation_id:" + conversation.getConversation_id() + "conversation_name:" + conversation.getConversation_name());
			Log.w(TAG, "Membres de la conversations: " + conversation.getListIdUser().size());
			for (int i : conversation.getListIdUser()) {
				Log.d(TAG, "> Membre : " + i);
			}
			Log.d(TAG, "(Pour info, User_me = " + NetworkService.getUser_me().getId() + ")");


			// Ajout du fragment
			addFragment(conversation, false);
			// On récupère le nouveau fragment pour pouvoir setter son nom
			ConversationFragment lastFrag = getFragmentById(conversation.getConversation_id());
			lastFrag.setConversName(conversation.generateConversationName());


			//mPagerAdapter.notifyDataSetChanged();
			//lastFrag.setConversText("Bidule vient d'ouvrir une conversation avec vous.");
		}
	};

	@Override
	protected void onResume() {
		super.onResume();
		//Enregistrement de l'intent filter
		IntentFilter filter = new IntentFilter();
		filter.addAction(NetworkService.SendMessage);
		registerReceiver(messageReceiver, filter);

		//Enregistrement de l'intent filter
		IntentFilter filter2 = new IntentFilter();
		filter2.addAction(NetworkService.SendConversation);
		registerReceiver(conversationReceiver, filter2);

		// Récupère la liste des conversations qui n'ont pas encore de fragment UI
		ArrayList<Conversation> conversations = NetworkService.getLocalConversationsToCreate();
		for (Conversation conv : conversations) {
			addFragment(conv, true);
		}

		// On rafraichit la liste des conversations
		conversationListFragment.updateLocalConversationsList();
	}

	@Override
	protected void onPause() {
		unregisterReceiver(messageReceiver);
		unregisterReceiver(conversationReceiver);
		super.onPause();
	}

	@Override
	protected void onStop() {
		super.onStop();
	}


	/*class SendExempletimeTask extends TimerTask {

		@Override
		public void run() {
			sendExemple();
		}
	}*/

	/**
	 * Exemple pour envoyer un message / une création de conversation
	 */
	/*void sendExemple(){
		String conversation_name = "conversation_1";
		ArrayList<Integer> users_conversation = new ArrayList<Integer>(NetworkService.getListUsers().keySet());
		int conversation_id = createConversation(conversation_name,users_conversation);

		sendMessage("bonjour c'est moi1", conversation_id);

		sendMessage("bonjour c'est moi2", conversation_id);
		Log.d(TAG, "données exemple lancées");

	}*/

}
