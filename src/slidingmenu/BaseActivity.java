package slidingmenu;

import java.util.ArrayList;

import refreshablelist.RefreshableListViewActivity;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import baidumapsdk.demo.R;

import com.actionbarsherlock.view.MenuItem;
import com.slidingmenu.lib.SlidingMenu;
import com.slidingmenu.lib.app.SlidingFragmentActivity;

public class BaseActivity extends SlidingFragmentActivity {

    private int mTitleRes;

    protected ListFragment mFrag;

    public BaseActivity(int titleRes) {
	mTitleRes = titleRes;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setTitle(mTitleRes);

	ArrayList<String> mItems = new ArrayList<String>();
	mItems.add("Mission");
	mItems.add("User");
	mItems.add("Setting");
	Bundle args = new Bundle();
	args.putStringArrayList("title", mItems);

	// set the Behind View
	// 此处设置左边菜单
	setBehindContentView(R.layout.menu_frame);
	FragmentTransaction t = this.getSupportFragmentManager()
		.beginTransaction();
	mFrag = new SampleListFragment();

	mFrag.setArguments(args);

	t.replace(R.id.menu_frame, mFrag);
	t.commit();

	// customize the SlidingMenu
	SlidingMenu sm = getSlidingMenu();
	sm.setShadowWidthRes(R.dimen.shadow_width);
	sm.setShadowDrawable(R.drawable.shadow);
	sm.setBehindOffsetRes(R.dimen.slidingmenu_offset);
	sm.setFadeDegree(0.35f);
	sm.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);

	getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
	switch (item.getItemId()) {
	case android.R.id.home:
	    toggle();
	    return true;
	}
	return super.onOptionsItemSelected(item);
    }

}
