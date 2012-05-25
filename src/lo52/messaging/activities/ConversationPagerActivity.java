package lo52.messaging.activities;

import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import lo52.messaging.R;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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

	private static final String TAG = "ConversationPagerActivity";

	// Constantes des menus d'option (valeurs aléatoires mais uniques)
	private static final int MENU_ITEM_CLOSE_CONV = 1001;

	private TabHost mTabHost;
	private ViewPager mViewPager;
	private HashMap<String, TabInfo> mapTabInfo = new HashMap<String, ConversationPagerActivity.TabInfo>();
	private MPagerAdapter mPagerAdapter;

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

		ConversationListFragment f = (ConversationListFragment) Fragment.instantiate(this, ConversationListFragment.class.getName());
		fragments.add(f);

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
	 * The <code>PagerAdapter</code> serves the fragments when paging.
	 */
	public class MPagerAdapter extends FragmentPagerAdapter {

		private List<Fragment> fragments;

		/**
		 * @param fm
		 * @param fragments
		 */
		public MPagerAdapter(FragmentManager fm, List<Fragment> fragments) {
			super(fm);
			this.fragments = fragments;
		}

		/* (non-Javadoc)
		 * @see android.support.v4.app.FragmentPagerAdapter#getItem(int)
		 */
		@Override
		public Fragment getItem(int position) {
			return this.fragments.get(position);
		}

		/* (non-Javadoc)
		 * @see android.support.v4.view.PagerAdapter#getCount()
		 */
		@Override
		public int getCount() {
			return this.fragments.size();
		}

		public int getItemPosition(Object object) {
			return POSITION_NONE;
		}

		/**
		 * Retourne la liste des fragments dans l'adapter
		 * @return
		 */
		public List<Fragment> getFragmentsList() {
			return fragments;
		}

		/**
		 * Ajoute un fragment dans l'adapter
		 * @param f
		 */
		public void addFragment(Context ctx) {
			ConversationFragment f = (ConversationFragment) Fragment.instantiate(ctx, ConversationFragment.class.getName());
			fragments.add(f);
		}

		/**
		 * Surcharge de destroyItem, dans laquelle on n'appelle pas la méthode super, sinon les fragments
		 * sont resettés à leur View par défaut par le GC.
		 */
		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			//super.destroyItem(container, position, object);
			Log.d(TAG, "NOT destroying item " + position);
		}
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
		
		// TODO refactoriser ce code pour prendre en compte le tab 0 (liste)
		
		/*// Menu supprimer: ajouté quand il y a exactement 1 conversation (sinon il est créé plusieurs fois), ...
		if (this.mPagerAdapter.getCount() == 2) {
			// On vérifie que l'item n'est pas déjà dans le menu
			if (menu.size() >= 1) {
				boolean itemFound = false;
				for(int i = 0; i < menu.size(); i++) {
					if (menu.getItem(i).getItemId() == MENU_ITEM_CLOSE_CONV) itemFound = true;
				}
				if (!itemFound) {
					// Fix: mettre un groupId différent, pour faciliter la suppression du menuitem 
					menu.add(MENU_ITEM_CLOSE_CONV, MENU_ITEM_CLOSE_CONV, 2, getString(R.string.conversations_close_current));
					menu.getItem(1).setIcon(android.R.drawable.ic_menu_delete);
				}
			}
		}
		// ... supprimé quand il y en a 0 et que l'item est présent.
		else if (this.mPagerAdapter.getCount() == 0 && menu.size() >= 1) {
			for (int i = 0; i < menu.size(); i++) {
				if (menu.getItem(i).getItemId() == MENU_ITEM_CLOSE_CONV) {
					menu.removeGroup(MENU_ITEM_CLOSE_CONV);
				}
			}
		}*/
		
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

			Log.d(TAG, "Ajout fragment");
			addFragment();
			Log.d(TAG, "Frag ajouté");

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
	 * TODO Francois
	 * voir pour les paramètres nécessaires (nom du tab, contenu de base...)
	 */
	public void addFragment() {

		// Ajout d'un tab
		TabInfo tabInfo = null;
		ConversationPagerActivity.AddTab(this, this.mTabHost, this.mTabHost.newTabSpec("Tab"+(this.mPagerAdapter.getCount()+1)).setIndicator("Tab " + (this.mPagerAdapter.getCount()+1)), ( tabInfo = new TabInfo("Tab"+(this.mPagerAdapter.getCount()+1), ConversationFragment.class, null)));
		this.mapTabInfo.put(tabInfo.tag, tabInfo);
		
		mPagerAdapter.addFragment(this);

		// Cacher le TV
		if (this.mPagerAdapter.getCount() == 2) {
			TextView tv = (TextView) findViewById(R.id.no_conversation);
			tv.setVisibility(View.GONE);
		}
	}

	/**
	 * Supprime le fragment à l'index donné
	 * @param index
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
		}
	}
	
	
	
	public void onFragmentSendButtonClick() {
		Log.d(TAG, "Envoi depuis fragment " + mTabHost.getCurrentTab());
	}
}
