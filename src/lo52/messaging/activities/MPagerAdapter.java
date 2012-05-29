package lo52.messaging.activities;

import java.util.List;

import lo52.messaging.fragments.ConversationFragment;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;
import android.view.ViewGroup;

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
		super.destroyItem(container, position, object);
		Log.d("mPager adapter", "Destroying fragment " + position);
	}
}
