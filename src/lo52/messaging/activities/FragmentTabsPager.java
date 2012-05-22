package lo52.messaging.activities;

import java.util.ArrayList;

import lo52.messaging.R;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;


/**
 * Activité regroupant les conversations dans des Fragments, qu'on peut "swiper" pour accéder aux autres.
 */
public class FragmentTabsPager extends FragmentActivity {

	// Hauteur des tabs de titre des conversation
	private static final int TAB_HEIGHT = 40;

	// Constantes des menus d'option (valeurs aléatoires mais uniques)
	private static final int MENU_ITEM_CLOSE_CONV = 1001;

	// Nombre de conversations en cours à tout moment
	protected int conversationsNumber;

	private static final String TAG = "FragmentTabsPager";

	TabHost mTabHost;
	ViewPager  mViewPager;
	static TabsAdapter mTabsAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.fragment_tabs_pager);
		mTabHost = (TabHost)findViewById(android.R.id.tabhost);
		mTabHost.setup();

		mViewPager = (ViewPager)findViewById(R.id.pager);

		mTabsAdapter = new TabsAdapter(this, mTabHost, mViewPager);

		conversationsNumber = 0;

		// Ajout d'un tab *obligatoire*, sinon NullPointerException sous 2.1
		mTabsAdapter.addTab(mTabHost.newTabSpec("dummy").setIndicator(getString(R.string.conversations_tab_name)), ConversationFragment.class, null);

		/*mTabsAdapter.addTab(mTabHost.newTabSpec("contacts").setIndicator("Contacts"), ConversationFragment.class, null);
		/*mTabsAdapter.addTab(mTabHost.newTabSpec("custom").setIndicator("Custom"), ConversationFragment.class, null);
		mTabsAdapter.addTab(mTabHost.newTabSpec("throttle").setIndicator("Throttle"), ConversationFragment.class, null);*/

		if (savedInstanceState != null) {
			mTabHost.setCurrentTabByTag(savedInstanceState.getString("tab"));
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString("tab", mTabHost.getCurrentTabTag());
	}


	public class TabsAdapter extends FragmentPagerAdapter implements TabHost.OnTabChangeListener, ViewPager.OnPageChangeListener {

		private final Context mContext;
		private final TabHost mTabHost;
		private final ViewPager mViewPager;
		private final ArrayList<TabInfo> mTabs = new ArrayList<TabInfo>();

		private final class TabInfo {
			@SuppressWarnings("unused")
			private final String tag;
			private final Class<?> clss;
			private final Bundle args;

			TabInfo(String _tag, Class<?> _class, Bundle _args) {
				tag = _tag;
				clss = _class;
				args = _args;
			}
		}

		private class DummyTabFactory implements TabHost.TabContentFactory {
			private final Context mContext;

			public DummyTabFactory(Context context) {
				mContext = context;
			}

			@Override
			public View createTabContent(String tag) {
				View v = new View(mContext);
				v.setMinimumWidth(0);
				v.setMinimumHeight(0);
				return v;
			}
		}

		public TabsAdapter(FragmentActivity activity, TabHost tabHost, ViewPager pager) {
			super(activity.getSupportFragmentManager());
			mContext = activity;
			mTabHost = tabHost;
			mViewPager = pager;
			mTabHost.setOnTabChangedListener(this);
			mViewPager.setAdapter(this);
			mViewPager.setOnPageChangeListener(this);
		}

		public void addTab(TabHost.TabSpec tabSpec, Class<?> clss, Bundle args) {
			tabSpec.setContent(new DummyTabFactory(mContext));
			String tag = tabSpec.getTag();

			TabInfo info = new TabInfo(tag, clss, args);
			mTabs.add(info);
			mTabHost.addTab(tabSpec);
			notifyDataSetChanged();


			// Fix : on fixe la hauteur des tabs en dur pour éviter qu'ils occupent trop de place verticalement
			int totalTabs = mTabHost.getTabWidget().getChildCount();
			((RelativeLayout)mTabHost.getTabWidget().getChildTabViewAt(totalTabs-1)).removeViewAt(0);
			((TextView)((RelativeLayout)mTabHost.getTabWidget().getChildTabViewAt(totalTabs-1)).getChildAt(0)).setHeight(30);
			mTabHost.getTabWidget().getChildAt(totalTabs-1).getLayoutParams().height = TAB_HEIGHT;
		}

		@Override
		public int getCount() {
			return mTabs.size();
		}

		@Override
		public Fragment getItem(int position) {
			TabInfo info = mTabs.get(position);
			return Fragment.instantiate(mContext, info.clss.getName(), info.args);
		}

		@Override
		public void onTabChanged(String tabId) {
			int position = mTabHost.getCurrentTab();
			mViewPager.setCurrentItem(position);
		}

		@Override
		public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
		}

		@Override
		public void onPageSelected(int position) {
			TabWidget widget = mTabHost.getTabWidget();
			int oldFocusability = widget.getDescendantFocusability();
			widget.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
			mTabHost.setCurrentTab(position);
			widget.setDescendantFocusability(oldFocusability);
		}

		@Override
		public void onPageScrollStateChanged(int state) {
		}


		public void addFragment(FragmentTabsPager fragmentTabsPager) {
			int nbTabs = mTabsAdapter.getCount();
			Log.d(TAG, "add Fragment");
			mTabsAdapter.addTab(mTabHost.newTabSpec("tab"+nbTabs).setIndicator("Tab " + nbTabs), ConversationFragment.class, null);


			Log.d(TAG, "Tab ajouté " + mTabsAdapter.getCount());
			
			// On récupère le fragment
			ConversationFragment f = (ConversationFragment)mTabsAdapter.getItem(nbTabs);
			/**
			 * XXX
			 * simple test pour accéder aux éléments du layout du fragment et pouvoir les modifier
			 * 
			 * @see http://stackoverflow.com/questions/6495898/findviewbyid-in-fragment-android 
			 */
			f.fuckingShit();
			
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
		// Menu supprimer: ajouté quand il y a exactement 1 conversation (sinon il est créé plusieurs fois), ...
		if (mTabsAdapter.getCount() == 1) {
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
		else if (mTabsAdapter.getCount() == 0 && menu.size() >= 1) {
			for (int i = 0; i < menu.size(); i++) {
				if (menu.getItem(i).getItemId() == MENU_ITEM_CLOSE_CONV) {
					menu.removeGroup(MENU_ITEM_CLOSE_CONV);
				}
			}
		}
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		if (item.getItemId() == 1) {

			/**
			 * < FIXME Francois>
			 * utiliser addFragment() à la place
			 */
			// Ajout d'un tab
			/*TabInfo tabInfo = null;
			ConversationPagerActivity.AddTab(this, this.mTabHost, this.mTabHost.newTabSpec("Tab"+(this.mPagerAdapter.getCount()+1)).setIndicator("Tab " + (this.mPagerAdapter.getCount()+1)), ( tabInfo = new TabInfo("Tab"+(this.mPagerAdapter.getCount()+1), ConversationFragment.class, null)));
			this.mapTabInfo.put(tabInfo.tag, tabInfo);*/

			mTabsAdapter.addFragment(this);

			// Cacher le TV
			Log.d(TAG, "Count tabs " + mTabsAdapter.getCount());
			if (mTabsAdapter.getCount() >= 1) {
				TextView tv = (TextView) findViewById(R.id.no_conversation);
				tv.setVisibility(View.GONE);
			}
			/**
			 * 	</Fin_FIXME>
			 */

		} else if (item.getItemId() == MENU_ITEM_CLOSE_CONV) {
			// Suppression
			removeFragmentAt(this.mTabHost.getCurrentTab());
		}
		return true;
	}

	private void removeFragmentAt(int currentTab) {
		Log.d(TAG, "pas encore réimplémenté");
	}





}