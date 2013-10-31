package slidingmenu;

import java.util.ArrayList;

import refreshablelist.RefreshableListViewActivity;
import android.os.Bundle;
import baidumapsdk.demo.R;

import com.slidingmenu.lib.SlidingMenu;


public class LeftAndRightActivity extends BaseActivity {

  protected	RefreshableListViewActivity mFrag;
	public LeftAndRightActivity() {
		super(R.string.left_and_right);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getSlidingMenu().setMode(SlidingMenu.LEFT_RIGHT);
		getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		
		//此处设置中间content内容部分
		mFrag = new RefreshableListViewActivity();
		setContentView(R.layout.content_frame);
		getSupportFragmentManager()
		.beginTransaction()
		.replace(R.id.content_frame,mFrag)
		.commit();
		
		SampleListFragment sf = new SampleListFragment();
		ArrayList<String> mItems = new ArrayList<String>();
		mItems.add("Personal Data");
		Bundle args = new Bundle();
		args.putStringArrayList("title", mItems);
		sf.setArguments(args);
		//此处设置右侧菜单
		getSlidingMenu().setSecondaryMenu(R.layout.menu_frame_two);
		getSlidingMenu().setSecondaryShadowDrawable(R.drawable.shadowright);
		getSupportFragmentManager()
		.beginTransaction()
		.replace(R.id.menu_frame_two, sf)
		.commit();
	}

}
