package slidingmenu;

import java.util.ArrayList;

import refreshablelist.RefreshableListViewActivity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import baidumapsdk.demo.R;

import com.slidingmenu.lib.SlidingMenu;


public class LeftAndRightActivity extends BaseActivity implements IChangeFragment{

  protected	RefreshableListViewActivity mFrag;
	public LeftAndRightActivity() {
		super(R.string.left_and_right);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getSlidingMenu().setMode(SlidingMenu.LEFT);
		getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		
		// set the Behind View
		// 此处设置左边菜单
		SampleListFragment sf = new SampleListFragment(this.getSupportFragmentManager());
		setBehindContentView(R.layout.menu_frame);
		sf.setChangeFragmentListener(this);
		getSupportFragmentManager()
		.beginTransaction()
		.replace(R.id.menu_frame,sf)
		.commit();
		
		//此处设置中间content内容部分
		mFrag = new RefreshableListViewActivity();
		setContentView(R.layout.content_frame);
		getSupportFragmentManager()
		.beginTransaction()
		.replace(R.id.content_frame,mFrag)
		.commit();
		
//		SampleListFragment sf = new SampleListFragment();
//		ArrayList<String> mItems = new ArrayList<String>();
//		mItems.add("Personal Data");
//		Bundle args = new Bundle();
//		args.putStringArrayList("title", mItems);
//		sf.setArguments(args);
//		//此处设置右侧菜单
//		getSlidingMenu().setSecondaryMenu(R.layout.menu_frame_two);
//		getSlidingMenu().setSecondaryShadowDrawable(R.drawable.shadowright);
//		getSupportFragmentManager()
//		.beginTransaction()
//		.replace(R.id.menu_frame_two, sf)
//		.commit();
	}
	public void changeFragment(int position) {
		FragmentTransaction t = this.getSupportFragmentManager()
				.beginTransaction();
		Fragment fragment = null;
		switch(position){
		case 0:
			fragment = new RefreshableListViewActivity();
			break;
		case 1:
			fragment  = new ChangeAKFragment();
			break;
		case 2:
//			fragment  = new TiesFragment();
			break;
		case 3:
//			fragment  = new PicsFragment();
			break;
			default:
				break;
			
		}
		t.replace(R.id.content_frame, fragment);
		t.commit();
		getSlidingMenu().showContent();
	}
}
