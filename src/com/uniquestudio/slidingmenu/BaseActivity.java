package com.uniquestudio.slidingmenu;

import android.os.Bundle;

import com.actionbarsherlock.view.MenuItem;
import com.slidingmenu.lib.SlidingMenu;
import com.slidingmenu.lib.app.SlidingFragmentActivity;
import com.uniquestudio.R;

public class BaseActivity extends SlidingFragmentActivity {

    private int mTitleRes;

    public BaseActivity(int titleRes) {
	mTitleRes = titleRes;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setTitle(mTitleRes);

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
