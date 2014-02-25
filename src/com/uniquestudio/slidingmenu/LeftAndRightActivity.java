package com.uniquestudio.slidingmenu;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import com.slidingmenu.lib.SlidingMenu;
import com.uniquestudio.R;
import com.uniquestudio.gps.GPSService;
import com.uniquestudio.refreshablelist.RefreshableListViewActivity;


public class LeftAndRightActivity extends BaseActivity implements IChangeFragment{
  protected	RefreshableListViewActivity mFrag;
  private ChangeAKFragment changeAKFragment;
  private GetDBFDirFragment getDBFDirFragment;
  private GetIMEIFragment getIMEIFragment;
	public LeftAndRightActivity() {
	    super(R.string.app_name);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//启动后台监控GPS坐标
		Intent intent = new Intent(getApplicationContext() , GPSService.class);
		getApplicationContext().startService(intent);
		    
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
		changeAKFragment = new ChangeAKFragment();
		getDBFDirFragment = new GetDBFDirFragment();
		getIMEIFragment = new GetIMEIFragment();
		setContentView(R.layout.content_frame);
		FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
		fragmentTransaction.add(R.id.content_frame,mFrag);
		fragmentTransaction.add(R.id.content_frame,changeAKFragment);
		fragmentTransaction.add(R.id.content_frame,getDBFDirFragment);
		fragmentTransaction.add(R.id.content_frame,getIMEIFragment);
		fragmentTransaction.hide(changeAKFragment);
		fragmentTransaction.hide(getDBFDirFragment);
		fragmentTransaction.hide(getIMEIFragment);
		fragmentTransaction.commit();
		
	}
	public void changeFragment(int position) {
		FragmentTransaction t = this.getSupportFragmentManager()
				.beginTransaction();
		t.hide(changeAKFragment).hide(getDBFDirFragment).hide(getIMEIFragment).hide(mFrag);
		switch(position){
		case 0:
		    t.show(mFrag);
			break;
		case 1:
		    //DBF
		    t.show(getDBFDirFragment);
			break;
		case 2:
		    //AK
		    t.show(changeAKFragment);
			break;
		case 3:
		    //iMEID
		    t.show(getIMEIFragment);
			break;
			default:
				break;
		}
		t.commit();
		getSlidingMenu().showContent();
	}
	
}
