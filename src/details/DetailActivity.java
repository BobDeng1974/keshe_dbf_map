package details;

import static refreshablelist.StringConstant.CONTACT;
import static refreshablelist.StringConstant.MISSION_DETAIL;
import static refreshablelist.StringConstant.MISSION_INFO_ITEM_01;
import static refreshablelist.StringConstant.MISSION_INFO_ITEM_02;
import static refreshablelist.StringConstant.PHONE;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import GPS.GPSManager;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.os.Bundle;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewAnimator;
import baidumapsdk.demo.R;

public class DetailActivity extends Activity {
    // title
    private TextView title;
    private Button leftBtn;
    private Button rightBtn;
    // viewAnimation
    private ViewAnimator animator;
    private Animation slideInLeft;
    private Animation slideInRight;
    private Animation slideOutLeft;
    private Animation slideOutRight;
    // mission_info
    private LinearLayout setNavigation;
    private TextView setNavigationTextView;
    private LinearLayout changeContact;
    private TextView changeContactPerson;
    private TextView changeContactPhone;
    private LinearLayout getPosition;
    private TextView getPositionTextView;
    private ListView missionInfoListView;
    private String[] missionItem1 = MISSION_INFO_ITEM_01;
    private String[] missionItem2 = MISSION_INFO_ITEM_02;
    private String contactPerson = "";
    private String contactPhone = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
	setContentView(R.layout.detail_acticity);
	getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
		R.layout.detail_activity_title);
	initViewAnimatior();
	initTitleListener();
	initMissionInfo();
    }

    /**
     * 初始化任务信息界面
     */
    private void initMissionInfo() {
	// TODO Auto-generated method stub
	setNavigation = (LinearLayout) findViewById(R.id.set_navigation);
	changeContact = (LinearLayout) findViewById(R.id.change_contact);
	getPosition = (LinearLayout) findViewById(R.id.get_position);
	changeContactPerson = (TextView) findViewById(R.id.change_contact_person);
	changeContactPhone = (TextView) findViewById(R.id.change_contact_phone);
	getPositionTextView = (TextView) findViewById(R.id.get_position_text);
	
	missionInfoListView = (ListView) findViewById(R.id.mission_info_listview);
	initMissionInfoListener();
	initMissionInfoListView();
    }

    private void initMissionInfoListener() {
	// TODO Auto-generated method stub
	changeContact.setOnClickListener(new OnClickListener() {
	    @Override
	    public void onClick(View arg0) {
		openMyDialog("联系人", changeContactPerson.getText().toString(),
			changeContactPhone.getText().toString(),
			changeContactPerson, changeContactPhone);
	    }
	});
	getPosition.setOnClickListener(new OnClickListener() {
	    @Override
	    public void onClick(View v) {

		GPSManager gpsManager = new GPSManager(DetailActivity.this);
		List<Address> gps = gpsManager.getGPS();
//		System.out.println("GPS------------>" + gps);
		if (gps == null) {
		    Toast.makeText(getApplicationContext(), "无法获取,请检查网络状况..",
			    Toast.LENGTH_LONG).show();
		} else {
		    Address address = gps.get(0);
		    getPositionTextView.setText(address.getAddressLine(0));
		}
	    }
	});
    }

    /**
     * 初始化任务信息列表
     */
    private void initMissionInfoListView() {
	// TODO Auto-generated method stub
	Intent intent = getIntent();
	String missionInfoString = intent.getStringExtra(MISSION_DETAIL);
	contactPerson = intent.getStringExtra(CONTACT);
	contactPhone = intent.getStringExtra(PHONE);
	changeContactPerson.setText(contactPerson);
	changeContactPhone.setText(contactPhone);
	StringToMap stringToMap = new StringToMap();
	Map<String, Object> map = stringToMap.StringToMap(missionInfoString);
	System.out.println(map);
	List<HashMap<String, Object>> maps = new ArrayList<HashMap<String, Object>>();
	if (intent.hasExtra(CONTACT)) {
	    for (int z = 0; z < missionItem1.length; z++) {
		HashMap<String, Object> smallMap = new HashMap<String, Object>();
		smallMap.put("title", missionItem1[z]);
		if (missionItem1[z].equals(CONTACT))
		    smallMap.put("values", contactPerson.trim().equals("")? "无" : contactPerson);
		else if (missionItem1[z].equals(PHONE))
		    smallMap.put("values", contactPhone.trim().equals("")? "无" : contactPhone);
		else
		    smallMap.put("values", map.get(missionItem1[z]));

		maps.add(smallMap);
	    }
	} else {
	    for (int i = 0; i < missionItem2.length; i++) {
		HashMap<String, Object> smallMap = new HashMap<String, Object>();
		smallMap.put("title", missionItem2[i]);
		smallMap.put("values", map.get(missionItem2[i]).toString()
			.trim());
		maps.add(smallMap);
	    }
	}
	SimpleAdapter adapter = new SimpleAdapter(this, maps,
		R.layout.mission_info_list_item, new String[] { "title",
			"values" }, new int[] { R.id.mission_info_title,
			R.id.mission_info_values });
	missionInfoListView.setAdapter(adapter);
    }

    /**
     * 初始化titlebar控件
     */
    private void initViewAnimatior() {
	// TODO Auto-generated method stub
	title = (TextView) findViewById(R.id.detail_title);
	leftBtn = (Button) findViewById(R.id.title_left_btn);
	rightBtn = (Button) findViewById(R.id.title_right_btn);
	title.setText(getResources().getString(R.string.mission_info));
	leftBtn.setText(getResources().getString(R.string.confirm));
	rightBtn.setText(getResources().getString(R.string.title_next));
	animator = (ViewAnimator) findViewById(R.id.animator);
	slideInLeft = AnimationUtils
		.loadAnimation(this, R.anim.i_slide_in_left);
	slideInRight = AnimationUtils.loadAnimation(this,
		R.anim.i_slide_in_right);
	slideOutLeft = AnimationUtils.loadAnimation(this,
		R.anim.i_slide_out_left);
	slideOutRight = AnimationUtils.loadAnimation(this,
		R.anim.i_slide_out_right);
    }

    /**
     * 初始化titlebar控件的监听响应
     */
    private void initTitleListener() {
	// TODO Auto-generated method stub
	leftBtn.setOnClickListener(new OnClickListener() {
	    public void onClick(View v) {
		switch (animator.getDisplayedChild()) {
		case 0:
		    finish();
		    break;
		case 1:
		    title.setText(getResources().getString(
			    R.string.mission_info));
		    leftBtn.setText(getResources().getString(
			    R.string.title_back));
		    rightBtn.setText(getResources().getString(
			    R.string.title_next));
		    break;
		case 2:
		    title.setText(getResources().getString(
			    R.string.fengkou_info));
		    leftBtn.setText(getResources().getString(
			    R.string.title_back));
		    rightBtn.setText(getResources().getString(
			    R.string.title_next));
		    break;
		default:
		    break;
		}
		animator.setInAnimation(slideInRight);
		animator.setOutAnimation(slideOutRight);
		animator.showPrevious();
	    }
	});
	rightBtn.setOnClickListener(new OnClickListener() {
	    public void onClick(View v) {
		switch (animator.getDisplayedChild()) {
		case 0:
		    title.setText(getResources().getString(
			    R.string.fengkou_info));
		    leftBtn.setText(getResources().getString(
			    R.string.title_back));
		    rightBtn.setText(getResources().getString(
			    R.string.title_next));
		    break;
		case 1:
		    title.setText(getResources().getString(
			    R.string.mission_status));
		    leftBtn.setText(getResources().getString(
			    R.string.title_back));
		    rightBtn.setText(getResources().getString(R.string.confirm));
		    break;
		case 2:
		    String msg = "任务完成";
		    new AlertDialog.Builder(DetailActivity.this)
			    .setMessage(msg).setTitle("hmmm...")
			    .setIcon(android.R.drawable.ic_dialog_info)
			    .setPositiveButton("ok", null).show();
		    break;
		default:
		    break;
		}
		animator.setInAnimation(slideInLeft);
		animator.setOutAnimation(slideOutLeft);
		animator.showNext();
	    }
	});
    }

    private void openMyDialog(String title, String personTextString,
	    String phoneTextString, final TextView personText,
	    final TextView phoneText) {
	// 创建自定义dialog
	final Dialog dialog = new Dialog(this, R.style.dialog);
	final View myView = LayoutInflater.from(DetailActivity.this).inflate(
		R.layout.dialog, null);
	myView.findFocus();
	// 获取控件
	TextView titleTextView = (TextView) myView
		.findViewById(R.id.dialog_title);
	final EditText dialogContactEditText = (EditText) myView
		.findViewById(R.id.dialog_contact);
	final EditText dialogPhoneEditText = (EditText) myView
		.findViewById(R.id.dialog_phone);
	Button dialogConfirmButton = (Button) myView
		.findViewById(R.id.dialog_confirm_bt);
	Button dialogCancleButton = (Button) myView
		.findViewById(R.id.dialog_cancle_bt);
	dialogPhoneEditText.setInputType(InputType.TYPE_CLASS_PHONE);
	titleTextView.setText(title);
	dialogContactEditText.setText(personTextString);
	dialogPhoneEditText.setText(phoneTextString);
	dialogContactEditText.selectAll();
	dialogPhoneEditText.selectAll();
	dialogCancleButton.setOnClickListener(new OnClickListener() {
	    @Override
	    public void onClick(View v) {
		dialog.dismiss();
	    }
	});
	dialogConfirmButton.setOnClickListener(new OnClickListener() {
	    @Override
	    public void onClick(View v) {
		// TODO Auto-generated method stub
		personText.setText(dialogContactEditText.getText().toString());
		phoneText.setText(dialogPhoneEditText.getText().toString());
		Toast.makeText(
			getApplicationContext(),
			dialogContactEditText.getText().toString() + " "
				+ dialogPhoneEditText.getText().toString(),
			Toast.LENGTH_SHORT).show();
		dialog.dismiss();
	    }
	});
	dialog.getWindow().setContentView(myView);
	dialog.show();
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK) { //监控/拦截/屏蔽返回键
            new AlertDialog.Builder(this).setTitle(
		    "退出确认").setMessage("确认退出任务？").setNegativeButton("取消",
		    new DialogInterface.OnClickListener() {
		    @Override
		    public void onClick(DialogInterface dialog, int which) {
		    }
		    }).setPositiveButton("确定",
		    new DialogInterface.OnClickListener() {
		    public void onClick(DialogInterface dialog, int whichButton) {
			    finish();
		    }
		    }).show();
            return true;
        } 
        return super.onKeyDown(keyCode, event);
    }
}
