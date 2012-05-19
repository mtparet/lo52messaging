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
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TabHost.TabContentFactory;
import android.widget.TextView;


/**
 * Activité permettant de regrouper les conversations dans une vue en "fragments"
 * 
 * @see http://thepseudocoder.wordpress.com/2011/10/13/android-tabs-viewpager-swipe-able-tabs-ftw/
 * @see https://github.com/JakeWharton/Android-ViewPagerIndicator
 * @see http://android-developers.blogspot.fr/2011/08/horizontal-view-swiping-with-viewpager.html?m=1
 * <br/>==========<br/>
 * The <code>ConversationPagerActivity</code> class implements the Fragment activity that maintains a TabHost using a ViewPager.
 * @author mwho
 * 
 */
public class ConversationPagerActivity extends FragmentActivity implements TabHost.OnTabChangeListener, ViewPager.OnPageChangeListener {

	// Hauteur des tabs de titre des conversation
	private static final int TAB_HEIGHT = 40;

	private static final String TAG = "ConversationPagerActivity";

	private TabHost mTabHost;
	private ViewPager mViewPager;
	private HashMap<String, TabInfo> mapTabInfo = new HashMap<String, ConversationPagerActivity.TabInfo>();
	private PagerAdapter mPagerAdapter;
	/**
	 *
	 * @author mwho
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
			mTabHost.setCurrentTabByTag(savedInstanceState.getString("tab")); //set the tab as per the saved state
		}
		// Intialise ViewPager
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
		/*fragments.add(Fragment.instantiate(this, ConversationFragment.class.getName()));
		fragments.add(Fragment.instantiate(this, ConversationFragment.class.getName()));
		fragments.add(Fragment.instantiate(this, ConversationFragment.class.getName()));*/
		this.mPagerAdapter  = new PagerAdapter(super.getSupportFragmentManager(), fragments);
		//
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
		/*TabInfo tabInfo = null;
		ConversationPagerActivity.AddTab(this, this.mTabHost, this.mTabHost.newTabSpec("Tab1").setIndicator("Tab 1"), ( tabInfo = new TabInfo("Tab1", ConversationFragment.class, args)));
		this.mapTabInfo.put(tabInfo.tag, tabInfo);
		ConversationPagerActivity.AddTab(this, this.mTabHost, this.mTabHost.newTabSpec("Tab2").setIndicator("Tab 2"), ( tabInfo = new TabInfo("Tab2", ConversationFragment.class, args)));
		this.mapTabInfo.put(tabInfo.tag, tabInfo);
		ConversationPagerActivity.AddTab(this, this.mTabHost, this.mTabHost.newTabSpec("Tab3").setIndicator("Tab 3"), ( tabInfo = new TabInfo("Tab3", ConversationFragment.class, args)));
		this.mapTabInfo.put(tabInfo.tag, tabInfo);*/
		// Default to first tab
		//this.onTabChanged("Tab1");
		//
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
	@Override
	public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

	}

	/* (non-Javadoc)
	 * @see android.support.v4.view.ViewPager.OnPageChangeListener#onPageSelected(int)
	 */
	@Override
	public void onPageSelected(int position) {
		this.mTabHost.setCurrentTab(position);
	}

	/* (non-Javadoc)
	 * @see android.support.v4.view.ViewPager.OnPageChangeListener#onPageScrollStateChanged(int)
	 */
	@Override
	public void onPageScrollStateChanged(int state) {
	}


	/**
	 * The <code>PagerAdapter</code> serves the fragments when paging.
	 * @author mwho
	 */
	public class PagerAdapter extends FragmentPagerAdapter {

		private List<Fragment> fragments;
		/**
		 * @param fm
		 * @param fragments
		 */
		public PagerAdapter(FragmentManager fm, List<Fragment> fragments) {
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
			fragments.add(Fragment.instantiate(ctx, ConversationFragment.class.getName()));			
		}
	}

	
	/**
	 *	FIXME
	 *
	 * 	Ajout crado d'un menu en dur pour pouvoir tester l'ajout/suppression de fragments à l'intérieur de l'activité.
	 * 
	 * 	A supprimer une fois fonctionnel
	 */
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(1, 1, 1, "Ajouter un tab");
		menu.add(1, 2, 2, "Retirer un tab");
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		if (item.getItemId() == 1) {
			// Ajout d'un tab
			Log.d(TAG, "Nombre tabs : " + this.mPagerAdapter.getCount());
			
			TabInfo tabInfo = null;
			ConversationPagerActivity.AddTab(this, this.mTabHost, this.mTabHost.newTabSpec("Tab"+(this.mPagerAdapter.getCount()+1)).setIndicator("Tab " + (this.mPagerAdapter.getCount()+1)), ( tabInfo = new TabInfo("Tab"+(this.mPagerAdapter.getCount()+1), ConversationFragment.class, null)));
			this.mapTabInfo.put(tabInfo.tag, tabInfo);
			
			mPagerAdapter.addFragment(this);
			
		} else if (item.getItemId() == 2) {
			// Suppression
			if (this.mPagerAdapter.getCount() > 1) {
				
				// Supprimer le fragment de l'adapter
				mPagerAdapter.getFragmentsList().remove(mPagerAdapter.getCount()-1);
				
				if (this.mTabHost.getCurrentTab() == this.mPagerAdapter.getCount()) {
					Log.d(TAG, "ok");
					this.mViewPager.setCurrentItem(this.mTabHost.getCurrentTab()-1);
				}
				
				// Supprimer le tab physiquement
				mTabHost.getTabWidget().removeView(mTabHost.getTabWidget().getChildTabViewAt(this.mPagerAdapter.getCount()));

			} else {
				Log.e(TAG, "Impossible de supprimer le seul fragment restant !");
			}
		}
		
		return true;
	}
}


