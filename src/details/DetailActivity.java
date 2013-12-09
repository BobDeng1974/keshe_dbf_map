package details;

import static stringconstant.SpinnerString.CLOCK_DATE_CONCLUSION;
import static stringconstant.SpinnerString.CLOCK_MISTAKE_CONCLUSION;
import static stringconstant.SpinnerString.SCENE_VERIFY_ARRAY;
import static stringconstant.SpinnerString.SEAL_ARRAY;
import static stringconstant.SpinnerString.SECONDARY_LINE_CONCLUSION_ARRAY;
import static stringconstant.SpinnerString.TABLE_SEAL_ARRAY;
import static stringconstant.SpinnerString.VOLT_PHASE_ARRAY;
import static stringconstant.SpinnerString.WORK_MODE_ARRAY;
import static stringconstant.StringConstant.CONTACT;
import static stringconstant.StringConstant.MISSION_DETAIL;
import static stringconstant.StringConstant.MISSION_INFO_ITEM_01_CHINESE;
import static stringconstant.StringConstant.MISSION_INFO_ITEM_01_noCONTACTandPHONE;
import static stringconstant.StringConstant.MISSION_INFO_ITEM_02;
import static stringconstant.StringConstant.MISSION_INFO_ITEM_02_CHINESE;
import static stringconstant.StringConstant.PHONE;

import gps.GPSManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import spinneredittext.SpinnerEditText;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.location.Address;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewAnimator;
import baidumapsdk.demo.NaviDemo;
import baidumapsdk.demo.R;

import com.baidu.mapapi.navi.BaiduMapAppNotSupportNaviException;
import com.baidu.mapapi.navi.BaiduMapNavigation;
import com.baidu.mapapi.navi.NaviPara;
import com.baidu.platform.comapi.basestruct.GeoPoint;

public class DetailActivity extends Activity {
    Resources resources;
    // title
    private TextView title;
    private Button leftBtn;
    private Button rightBtn;
    // viewAnimation
    private Boolean isAnimating = false;
    private ViewAnimator animator;
    private Animation slideInLeft;
    private Animation slideInRight;
    private Animation slideOutLeft;
    private Animation slideOutRight;
    // mission_info
    private static final int missionInfoID = 0;
    private Button setNavigation;
    private TextView setNavigationTextView;
    private LinearLayout changeContact;
    private TextView changeContactPerson;
    private TextView changeContactPhone;
    private LinearLayout getPosition;
    private TextView getPositionTextView;
    private ListView missionInfoListView;
    private String[] missionItem1 = MISSION_INFO_ITEM_01_noCONTACTandPHONE;
    private String[] missionItem2 = MISSION_INFO_ITEM_02;
    private String contactPerson = "";
    private String contactPhone = "";
    // seal info
    private static final int sealInfoID = 1;
    private static final int STATUS_INVALID = 1; // 无效
    private static final int STATUS_DAMAGE = 2;// 损坏
    private SpinnerEditText cabinetSealOne;
    private SpinnerEditText cabinetSealTwo;
    private SpinnerEditText tableSealOne;
    private SpinnerEditText tableSealTwo;
    private SpinnerEditText boxSealOne;
    private SpinnerEditText boxSealTwo;
    // mission status
    private static final int missionStatusID = 2;
    private RadioGroup missionStatusGroup;
    private RadioButton missionStatusOne;
    private RadioButton missionStatusTwo;
    private RadioButton missionStatusThree;
    private int missionStatus = -1;
    // scene state
    private static final int sceneStateID = 3;
    private EditText clockDeviation;
    private SpinnerEditText clockDeviationConclusion;
    private Button electriClockDate;
    private SpinnerEditText electriClockDateConclusion;
    private Button electriClockEventButton;
    private EditText electirClockEvent;
    // electriClock
    private static final int electriClockID = 4;
    private EditText positiveTotalPower;
    private EditText positivePeak;
    private EditText positiveValley;
    private EditText positiveAverage;
    private TextView combinationDeviation;
    private Button otherPowerButton;
    private SpinnerEditText voltPhase;
    private EditText testLaps;
    private CheckBox electriClockExceptionErrorOne;
    private CheckBox electriClockExceptionErrorTwo;
    private Boolean isClockExceptionOne = true;
    // scene verify
    private static final int sceneVerifyID = 5;
    private EditText verifyTemperature;
    private EditText verifyHumidity;
    private TextView verifyTestTextView;
    private Button verifyReadMachine;
    private Spinner verifyMadeNumberSpinner;
    // result confirm
    private static final int resultConfirmID = 6;
    private TextView confirmDateTime;
    private TextView confirmMachineNumber;
    private SpinnerEditText confirmCheckPeople;
    private SpinnerEditText confirmVerifyPeople;
    private SpinnerEditText confirmWorkMode;
    private SpinnerEditText confirmEquipmentRating;
    private SpinnerEditText confirmSeal;
    private SpinnerEditText confirmSecondaryLineConclusion;
    private SpinnerEditText confirmSceneVerifyConclusion;
    private Button confirmAddEventButton;
    private static final int newSealID = 7;
    private static final int inputVerifyDataID = 8;

    @Override
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
	setContentView(R.layout.detail_acticity);
	getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
		R.layout.detail_activity_title);
	resources = getResources();
	// first page
	initViewAnimatior();
	initTitleListener();
	initMissionInfo();
	// second page
	initSpinnerEdittext();
	// third page
	initMissionStatus();
	// four page
	initSceneState();
	// fifth page()
	initElectriClock();
	// six page
	initSceneVerify();
	// seven page
	initResultConfirm();
    }

    private void initSpinnerEdittext() {
	// TODO Auto-generated method stub
	cabinetSealOne = (SpinnerEditText) findViewById(R.id.st_cabinet_1);
	cabinetSealTwo = (SpinnerEditText) findViewById(R.id.st_cabinet_2);
	tableSealOne = (SpinnerEditText) findViewById(R.id.st_table_1);
	tableSealTwo = (SpinnerEditText) findViewById(R.id.st_table_2);
	boxSealOne = (SpinnerEditText) findViewById(R.id.st_box_1);
	boxSealTwo = (SpinnerEditText) findViewById(R.id.st_box_2);
	cabinetSealOne.init(TABLE_SEAL_ARRAY, "柜封1:", true);
	cabinetSealTwo.init(TABLE_SEAL_ARRAY, "柜封2:", true);
	tableSealOne.init(TABLE_SEAL_ARRAY, "表封1:", true);
	tableSealTwo.init(TABLE_SEAL_ARRAY, "表封2:", true);
	boxSealOne.init(TABLE_SEAL_ARRAY, "盒封1:", true);
	boxSealTwo.init(TABLE_SEAL_ARRAY, "盒封2:", true);
	cabinetSealOne.setOpenDialogListener(new myOpenSealInfoDialog(
		cabinetSealOne));
	cabinetSealTwo.setOpenDialogListener(new myOpenSealInfoDialog(
		cabinetSealTwo));
	tableSealOne.setOpenDialogListener(new myOpenSealInfoDialog(
		tableSealOne));
	tableSealTwo.setOpenDialogListener(new myOpenSealInfoDialog(
		tableSealTwo));
	boxSealOne.setOpenDialogListener(new myOpenSealInfoDialog(boxSealOne));
	boxSealTwo.setOpenDialogListener(new myOpenSealInfoDialog(boxSealTwo));
    }

    class myOpenSealInfoDialog implements OnFocusChangeListener {
	private SpinnerEditText spinnerEditText;

	public myOpenSealInfoDialog(SpinnerEditText spinnerEditText) {
	    this.spinnerEditText = spinnerEditText;
	    // title
	}

	@Override
	public void onFocusChange(View v, boolean hasFocus) {
	    // TODO Auto-generated method stub
	    if (hasFocus)
		openClickSealInfoDialog(
			resources.getString(R.string.seal_info),
			spinnerEditText);
	}
    }

    /**
     * 初始化任务信息界面
     */
    private void initMissionInfo() {
	// TODO Auto-generated method stub
	setNavigation = (Button) findViewById(R.id.set_navigation);
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
		openChangeContactDialog("联系人", changeContactPerson.getText()
			.toString(), changeContactPhone.getText().toString(),
			changeContactPerson, changeContactPhone);
	    }
	});
	getPosition.setOnClickListener(new OnClickListener() {
	    @Override
	    public void onClick(View v) {

		GPSManager gpsManager = new GPSManager(DetailActivity.this);
		Location location = gpsManager.getMyLastKnownLocation();
		List<Address> gps = gpsManager.getAddresses(location);
		// System.out.println("GPS------------>" + gps);
		if (gps == null || gps.size() == 0) {
		    Toast.makeText(getApplicationContext(), "无法获取,请检查网络状况..",
			    Toast.LENGTH_LONG).show();
		} else {
		    Log.e("GPS--------->Address----->", gps.size()+"");
		    Address address = gps.get(0);
		    getPositionTextView.setText(address.getAddressLine(0));
		}
	    }
	});
	setNavigation.setOnClickListener(new OnClickListener() {
	    @Override
	    public void onClick(View arg0) {
		// 开启百度导航
		startNavi();
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
		smallMap.put("title", MISSION_INFO_ITEM_01_CHINESE[z]);
		if (missionItem1[z].equals(CONTACT))
		    smallMap.put("values",
			    contactPerson.trim().equals("") ? "无"
				    : contactPerson);
		else if (missionItem1[z].equals(PHONE))
		    smallMap.put("values", contactPhone.trim().equals("") ? "无"
			    : contactPhone);
		else
		    smallMap.put("values", map.get(missionItem1[z]));

		maps.add(smallMap);
	    }
	} else {
	    for (int i = 0; i < missionItem2.length; i++) {
		HashMap<String, Object> smallMap = new HashMap<String, Object>();
		smallMap.put("title", MISSION_INFO_ITEM_02_CHINESE[i]);
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

    private void initMissionStatus() {
	missionStatusGroup = (RadioGroup) findViewById(R.id.mission_status_radio_group);
	missionStatusOne = (RadioButton) findViewById(R.id.mission_status_radio1);
	missionStatusTwo = (RadioButton) findViewById(R.id.mission_status_radio2);
	missionStatusThree = (RadioButton) findViewById(R.id.mission_status_radio3);
	missionStatusGroup
		.setOnCheckedChangeListener(new OnCheckedChangeListener() {
		    @Override
		    public void onCheckedChanged(RadioGroup group, int checkedId) {
			if (checkedId == missionStatusOne.getId())
			    missionStatus = 1;
			else if (checkedId == missionStatusTwo.getId())
			    missionStatus = 2;
			else if (checkedId == missionStatusThree.getId())
			    missionStatus = 3;
		    }
		});
    }

    private void initSceneState() {
	clockDeviation = (EditText) findViewById(R.id.clock_mistake_value);
	clockDeviationConclusion = (SpinnerEditText) findViewById(R.id.clock_mistake_conclusion);
	electriClockDate = (Button) findViewById(R.id.clock_date_value);
	electriClockDateConclusion = (SpinnerEditText) findViewById(R.id.clock_date_conclusion);
	electriClockEventButton = (Button) findViewById(R.id.clock_event_button);
	electirClockEvent = (EditText) findViewById(R.id.clock_event_textview);
	clockDeviationConclusion.init(CLOCK_MISTAKE_CONCLUSION,
		resources.getString(R.string.clock_mistake_conclusion), false);
	electriClockDateConclusion.init(CLOCK_DATE_CONCLUSION,
		resources.getString(R.string.date_conclusion), false);
	electriClockDate.setOnClickListener(new OnClickListener() {
	    @Override
	    public void onClick(View arg0) {
		openDatePickerDialog(electriClockDate);
	    }
	});
	electriClockEventButton.setOnClickListener(new OnClickListener() {
	    @Override
	    public void onClick(View arg0) {
		if (electirClockEvent.getVisibility() == View.VISIBLE) {
		    electirClockEvent.setVisibility(View.GONE);
		} else {
		    electirClockEvent.setVisibility(View.VISIBLE);
		}
	    }
	});
    }

    private void initElectriClock() { // electriClock
	positiveTotalPower = (EditText) findViewById(R.id.electri_total_power);
	positivePeak = (EditText) findViewById(R.id.electri_peak);
	positiveValley = (EditText) findViewById(R.id.electri_valley);
	positiveAverage = (EditText) findViewById(R.id.electri_average);
	combinationDeviation = (TextView) findViewById(R.id.electri_combination_deviation);
	voltPhase = (SpinnerEditText) findViewById(R.id.electri_volt_phase);
	testLaps = (EditText) findViewById(R.id.electri_test_laps);
	electriClockExceptionErrorOne = (CheckBox) findViewById(R.id.electri_radio_bt_one);
	electriClockExceptionErrorTwo = (CheckBox) findViewById(R.id.electri_radio_bt_two);
	otherPowerButton = (Button) findViewById(R.id.electir_other_power);
	voltPhase.init(VOLT_PHASE_ARRAY,
		resources.getString(R.string.vol_phase_sequence), false);
	initElectriClockListener();
	// 组合误差：（总电量-峰-平-谷）/总电量;Format(_T("%6.4f"));
	// 组合误差的显示：组合误差的百分比方式
	// Format(_T("%.2f"),组合误差)*100)+_T("%"));
    }

    private void initElectriClockListener() {
	// textview listener
	class myTextWatcher implements TextWatcher {
	    @Override
	    public void afterTextChanged(Editable s) {
	    }

	    @Override
	    public void beforeTextChanged(CharSequence s, int start, int count,
		    int after) {
	    }

	    @Override
	    public void onTextChanged(CharSequence s, int start, int before,
		    int count) {
		boolean isTotalHave = !positiveTotalPower.getText().toString()
			.equals("");
		boolean isPeakHave = !positivePeak.getText().toString()
			.equals("");
		boolean isValleyHave = !positiveValley.getText().toString()
			.equals("");
		boolean isAverageHave = !positiveAverage.getText().toString()
			.equals("");
		if (isTotalHave && isPeakHave && isValleyHave && isAverageHave) {
		    int total = Integer.parseInt(positiveTotalPower.getText()
			    .toString());
		    int peak = Integer.parseInt(positivePeak.getText()
			    .toString());
		    int valley = Integer.parseInt(positiveValley.getText()
			    .toString());
		    int average = Integer.parseInt(positiveAverage.getText()
			    .toString());
		    combinationDeviation
			    .setText((total - peak - valley - average) / total
				    + "");
		}
	    }
	}
	;
	positiveTotalPower.setInputType(InputType.TYPE_CLASS_PHONE);
	positivePeak.setInputType(InputType.TYPE_CLASS_PHONE);
	positiveAverage.setInputType(InputType.TYPE_CLASS_PHONE);
	positiveValley.setInputType(InputType.TYPE_CLASS_PHONE);
	positiveTotalPower.addTextChangedListener(new myTextWatcher());
	positivePeak.addTextChangedListener(new myTextWatcher());
	positiveAverage.addTextChangedListener(new myTextWatcher());
	positiveValley.addTextChangedListener(new myTextWatcher());
	otherPowerButton.setOnClickListener(new OnClickListener() {
	    @Override
	    public void onClick(View arg0) {
		openElectriOtherPowerDialog(resources
			.getString(R.string.electric_clock));
	    }
	});
	electriClockExceptionErrorOne
		.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
		    @Override
		    public void onCheckedChanged(CompoundButton buttonView,
			    boolean isChecked) {
			if (isChecked) {
			    electriClockExceptionErrorTwo.setChecked(false);
			    isClockExceptionOne = true;
			}
		    }
		});
	electriClockExceptionErrorTwo
		.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
		    @Override
		    public void onCheckedChanged(CompoundButton buttonView,
			    boolean isChecked) {
			if (isChecked)
			    electriClockExceptionErrorOne.setChecked(false);
			isClockExceptionOne = !isChecked;
		    }
		});
    }

    private void initSceneVerify() {
	verifyTestTextView = (TextView) findViewById(R.id.verify_test_text);
	verifyReadMachine = (Button) findViewById(R.id.verify_read_machine);
	verifyReadMachine.setOnClickListener(new OnClickListener() {
	    @Override
	    public void onClick(View v) {
		// 等待界面且更新testTextview
		readMachineWaitDialog(verifyTestTextView);
		verifyTestTextView.setText("检验中...");
	    }
	});
    }

    private void initResultConfirm() {
	confirmDateTime = (TextView) findViewById(R.id.confirm_date_textview);
	confirmMachineNumber = (TextView) findViewById(R.id.confirm_machine_number);
	confirmCheckPeople = (SpinnerEditText) findViewById(R.id.confirm_check_people);
	confirmVerifyPeople = (SpinnerEditText) findViewById(R.id.confirm_verify_people);
	confirmWorkMode = (SpinnerEditText) findViewById(R.id.confirm_work_model);
	confirmEquipmentRating = (SpinnerEditText) findViewById(R.id.confirm_equipment_rating);
	confirmSeal = (SpinnerEditText) findViewById(R.id.confirm_seal);
	confirmSecondaryLineConclusion = (SpinnerEditText) findViewById(R.id.confirm_conclusion_secondary_line);
	confirmSceneVerifyConclusion = (SpinnerEditText) findViewById(R.id.confirm_scene_verify_conclusion);
	// 显示当前日期
	SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd");
	String currentDate = sDateFormat.format(new java.util.Date());
	confirmDateTime.setText(currentDate);
	// 校验人 审核人 设备评级 解析TXT
	confirmCheckPeople.init(SEAL_ARRAY,
		resources.getString(R.string.check_people), false);
	confirmVerifyPeople.init(SEAL_ARRAY,
		resources.getString(R.string.verify_people), false);
	confirmCheckPeople.init(SEAL_ARRAY,
		resources.getString(R.string.check_people), false);
	confirmEquipmentRating.init(SEAL_ARRAY,
		resources.getString(R.string.equipment_ratings), false);
	// 事件点击
	confirmSceneVerifyConclusion.init(SCENE_VERIFY_ARRAY,
		resources.getString(R.string.scene_verify_conclusion), false);
	confirmWorkMode.init(WORK_MODE_ARRAY,
		resources.getString(R.string.work_mode), false);
	confirmSeal.init(SEAL_ARRAY, resources.getString(R.string.seal), false);
	confirmSecondaryLineConclusion.init(SECONDARY_LINE_CONCLUSION_ARRAY,
		resources.getString(R.string.conclusion_secondary_line), false);
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
	leftBtn.setText(getResources().getString(R.string.exit));
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
	// 设置监听器
	slideInLeft.setAnimationListener(new myAnimationListener());
	slideInRight.setAnimationListener(new myAnimationListener());
	slideOutLeft.setAnimationListener(new myAnimationListener());
	slideOutRight.setAnimationListener(new myAnimationListener());
    }

    /**
     * 初始化titlebar控件的监听响应
     */
    private void initTitleListener() {
	// TODO Auto-generated method stub
	leftBtn.setOnClickListener(new OnClickListener() {
	    public void onClick(View v) {
		if (!isAnimating) {

		    switch (animator.getDisplayedChild()) {
		    case missionInfoID:
			// 退出
			finish();
			break;
		    case sealInfoID:
			// 回到任务信息
			ChangeTitleText(
				resources.getString(R.string.mission_info),
				resources.getString(R.string.exit),
				resources.getString(R.string.title_next),
				false, true);
			break;
		    case missionStatusID:
			// 回到封口信息
			ChangeTitleText(
				resources.getString(R.string.seal_info),
				resources.getString(R.string.title_back),
				resources.getString(R.string.title_next),
				false, true);
			break;
		    case sceneStateID:
			// 回到任务状态
			ChangeTitleText(
				resources.getString(R.string.mission_status),
				resources.getString(R.string.title_back),
				resources.getString(R.string.title_next),
				false, true);
			break;
		    case electriClockID:
			// 回到现场情况
			ChangeTitleText(
				resources.getString(R.string.scene_state),
				resources.getString(R.string.title_back),
				resources.getString(R.string.title_next),
				false, true);
			break;
		    case sceneVerifyID:
			// 回到电能表止码
			ChangeTitleText(
				resources.getString(R.string.electric_clock),
				resources.getString(R.string.title_back),
				resources.getString(R.string.title_next),
				false, true);
			break;
		    case resultConfirmID:
			if (missionStatus != 3) {
			    // 回到任务状态
			    int to = missionStatus == 2 ? electriClockID
				    : missionStatusID;
			    String titleJump = missionStatus == 2 ? resources
				    .getString(R.string.electric_clock)
				    : resources
					    .getString(R.string.mission_status);
			    showNextFromTo(false, to, resultConfirmID,
				    titleJump,
				    resources.getString(R.string.scene_verify),
				    true);
			} else {
			    // 回到现场检验
			    if (isClockExceptionOne) {
				title.setText(resources
					.getString(R.string.scene_verify));
				showPreviousWithAnimation(slideInRight,
					slideOutRight);

			    } else {
				title.setText(resources
					.getString(R.string.input_verify_data));
				showNextNoAnimation();
				showNextWithAnimation(slideInRight,
					slideOutRight);

			    }
			}
			break;
		    case newSealID:
			// 回到结果确认
			ChangeTitleText(
				resources.getString(R.string.result_confirm),
				resources.getString(R.string.title_back),
				resources.getString(R.string.title_next),
				false, true);
			break;
		    case inputVerifyDataID:
			showNextFromTo(
				false,
				electriClockID,
				inputVerifyDataID,
				resources.getString(R.string.electric_clock),
				resources.getString(R.string.input_verify_data),
				true);
			break;
		    default:
			break;
		    }
		}

	    }
	});
	rightBtn.setOnClickListener(new OnClickListener() {
	    public void onClick(View v) {
		if (!isAnimating) {

		    switch (animator.getDisplayedChild()) {
		    case missionInfoID:
			// 切换到封口信息
			ChangeTitleText(
				resources.getString(R.string.seal_info),
				resources.getString(R.string.title_back),
				resources.getString(R.string.title_next), true,
				true);
			break;
		    case sealInfoID:
			// 切换到任务状态
			ChangeTitleText(
				resources.getString(R.string.mission_status),
				resources.getString(R.string.title_back),
				resources.getString(R.string.title_next), true,
				true);
			break;
		    case missionStatusID:
			// 切换到现场情况
			if (missionStatus == -1) {
			    new AlertDialog.Builder(DetailActivity.this)
				    .setTitle("提示")
				    .setMessage("请选择一个状态")
				    .setNegativeButton(
					    "确定",
					    new DialogInterface.OnClickListener() {
						@Override
						public void onClick(
							DialogInterface dialog,
							int which) {
						}
					    }).show();
			} else {
			    Boolean isJump = (missionStatus == 1);
			    showNextFromTo(
				    true,
				    missionStatusID,
				    resultConfirmID,
				    resources
					    .getString(R.string.result_confirm),
				    resources.getString(R.string.scene_state),
				    isJump);
			}
			break;
		    case sceneStateID:
			// 切换到电能表止码
			ChangeTitleText(
				resources.getString(R.string.electric_clock),
				resources.getString(R.string.title_back),
				resources.getString(R.string.title_next), true,
				true);
			break;
		    case electriClockID:
			// 切换到现场校验
			// 如果无电压且电池正常，则直接到结果确认
			Boolean isjump = true;
			int to = resultConfirmID;
			String titleJump = resources
				.getString(R.string.result_confirm);
			if (missionStatus == 3) {
			    to = inputVerifyDataID;
			    titleJump = resources
				    .getString(R.string.input_verify_data);
			    isjump = !isClockExceptionOne;
			}
			showNextFromTo(true, electriClockID, to, titleJump,
				resources.getString(R.string.scene_verify),
				isjump);
			break;
		    case sceneVerifyID:
			// 切换到结果确认
			ChangeTitleText(
				resources.getString(R.string.result_confirm),
				resources.getString(R.string.title_back),
				resources.getString(R.string.title_next), true,
				true);
			break;
		    case resultConfirmID:
			// 切换到新封口
			ChangeTitleText(
				resources.getString(R.string.new_seal_info),
				resources.getString(R.string.title_back),
				resources.getString(R.string.confirm), true,
				true);
			break;
		    case newSealID:
			// 新封口再右点，即提示完成任务,并处理数据,返回任务列表主界面
			new AlertDialog.Builder(DetailActivity.this)
				.setMessage("任务完成")
				.setTitle("hmmm...")
				.setIcon(android.R.drawable.ic_dialog_info)
				.setPositiveButton("ok",
					new DialogInterface.OnClickListener() {
					    @Override
					    public void onClick(
						    DialogInterface dialog,
						    int which) {
						// 存储数据
						finish();
					    }
					}).show();
			break;
		    case inputVerifyDataID:
			ChangeTitleText(
				resources.getString(R.string.result_confirm),
				resources.getString(R.string.title_back),
				resources.getString(R.string.confirm), true,
				false);
			showPreviousNoAnimation();
			showPreviousWithAnimation(slideInLeft, slideOutLeft);
			break;
		    default:
			break;
		    }
		}
	    }
	});
    }

    /**
     * @param isRight
     *            是否向右切换
     * @param from
     *            从第几个页面
     * @param to
     *            到第几个页面
     * @param isJump
     *            是否需要跳转，如果不是直接切换到next，如果是就用to - from进行多次跳转
     * @param titleJump
     *            跳转时需要切换的title
     * @param titleNext
     *            不跳转时显示的title
     */
    private void showNextFromTo(Boolean isRight, int from, int to,
	    String titleJump, String titleNext, Boolean isJump) {
	String titleString = "";
	if (isJump) {
	    for (int i = 0; i < (to - from) - 1; i++) {
		if (isRight)
		    showNextNoAnimation();
		else
		    showPreviousNoAnimation();
		// 注意此时的from和to应该交换
	    }
	    titleString = titleJump;
	} else {
	    titleString = titleNext;
	}
	if (isRight)
	    showNextWithAnimation(slideInLeft, slideOutLeft);
	else
	    showPreviousWithAnimation(slideInRight, slideOutRight);
	ChangeTitleText(titleString, resources.getString(R.string.title_back),
		resources.getString(R.string.title_next), true, false);
    }

    /**
     * 打开datepicker选择日期
     * 
     * @param dateButton
     */
    private void openDatePickerDialog(final Button dateButton) {
	// 创建自定义dialog
	final Dialog dialog = new Dialog(this, R.style.dialog);
	final View myView = LayoutInflater.from(DetailActivity.this).inflate(
		R.layout.datapicker_dialog, null);
	myView.findFocus();
	// 获取控件
	DatePicker datePicker = (DatePicker) myView
		.findViewById(R.id.dialog_datepicker);
	Button dialogConfirmButton = (Button) myView
		.findViewById(R.id.datepicker_confirm_bt);
	Calendar calendar = Calendar.getInstance();
	int year = calendar.get(Calendar.YEAR);
	int monthOfYear = calendar.get(Calendar.MONTH);
	int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
	dateButton.setText(year + "/" + monthOfYear + "/" + dayOfMonth);
	datePicker.init(year, monthOfYear, dayOfMonth,
		new OnDateChangedListener() {
		    public void onDateChanged(DatePicker view, int year,
			    int monthOfYear, int dayOfMonth) {
			dateButton.setText(year + "/" + (monthOfYear + 1) + "/"
				+ dayOfMonth);
		    }
		});
	dialogConfirmButton.setOnClickListener(new OnClickListener() {
	    @Override
	    public void onClick(View v) {
		dialog.dismiss();
	    }
	});
	dialog.setCancelable(false);
	dialog.getWindow().setContentView(myView);
	dialog.show();
    }

    /**
     * 修改联系人的对话框
     */
    private void openChangeContactDialog(String title, String personTextString,
	    String phoneTextString, final TextView personText,
	    final TextView phoneText) {
	// 创建自定义dialog
	final Dialog dialog = new Dialog(this, R.style.dialog);
	final View myView = LayoutInflater.from(DetailActivity.this).inflate(
		R.layout.change_contact_dialog, null);
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
	dialog.setCancelable(false);
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

    /**
     * 封口信息处， 点击输入的dialog
     * 
     * @param title
     * @param spinnerEditText
     */
    private void openClickSealInfoDialog(String title,
	    final SpinnerEditText spinnerEditText) {
	// 创建自定义dialog
	final Dialog dialog = new Dialog(this, R.style.dialog);
	final View myView = LayoutInflater.from(DetailActivity.this).inflate(
		R.layout.seal_detail_dailog, null);
	myView.findFocus();
	// 获取控件
	TextView titleTextView = (TextView) myView
		.findViewById(R.id.seal_dialog_title);
	final EditText sealNumber = (EditText) myView
		.findViewById(R.id.dialog_seal_number);
	final CheckBox sealInfoRadioButtonInvalid = (CheckBox) myView
		.findViewById(R.id.seal_detail_radio_invalid);
	final CheckBox sealInfoRadioButtonDamage = (CheckBox) myView
		.findViewById(R.id.seal_detail_radio_damage);
	Button dialogCancleButton = (Button) myView
		.findViewById(R.id.seal_dialog_cancle_bt);
	Button dialogConfirmButton = (Button) myView
		.findViewById(R.id.seal_dialog_confirm_bt);
	// init
	titleTextView.setText(title);
	sealNumber.setInputType(InputType.TYPE_CLASS_PHONE);
	sealNumber.setText(spinnerEditText.getValue().toString());
	if (spinnerEditText.getStatus() == STATUS_INVALID)
	    sealInfoRadioButtonInvalid.setChecked(true);
	if (spinnerEditText.getStatus() == STATUS_DAMAGE)
	    sealInfoRadioButtonDamage.setChecked(true);
	sealInfoRadioButtonInvalid
		.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
		    @Override
		    public void onCheckedChanged(CompoundButton buttonView,
			    boolean isChecked) {
			if (isChecked) {
			    sealInfoRadioButtonDamage.setChecked(false);
			    spinnerEditText.setStatus(STATUS_INVALID);
			} else {
			    spinnerEditText.setStatus(0);
			}
		    }
		});
	sealInfoRadioButtonDamage
		.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
		    @Override
		    public void onCheckedChanged(CompoundButton buttonView,
			    boolean isChecked) {
			if (isChecked) {
			    sealInfoRadioButtonInvalid.setChecked(false);
			    spinnerEditText.setStatus(STATUS_DAMAGE);
			} else {
			    spinnerEditText.setStatus(0);
			}
		    }
		});
	dialogCancleButton.setOnClickListener(new OnClickListener() {
	    @Override
	    public void onClick(View v) {
		dialog.dismiss();
	    }
	});
	dialogConfirmButton.setOnClickListener(new OnClickListener() {
	    @Override
	    public void onClick(View v) {
		spinnerEditText.setValue(sealNumber.getText().toString());
		dialog.dismiss();
	    }
	});
	dialog.setCancelable(false);
	dialog.getWindow().setContentView(myView);
	dialog.show();
    }

    /**
     * 打开输入电能表电压的dialog
     * 
     * @param title
     */
    private void openElectriOtherPowerDialog(String title) {
	// 创建自定义dialog
	final Dialog dialog = new Dialog(this, R.style.dialog);
	final View myView = LayoutInflater.from(DetailActivity.this).inflate(
		R.layout.electir_other_power_dialog, null);
	myView.findFocus();
	// 获取控件
	TextView titleTextView = (TextView) myView
		.findViewById(R.id.electir_dialog_title);
	Button dialogConfirmButton = (Button) myView
		.findViewById(R.id.dialog_electir_confirm);
	titleTextView.setText(title);

	dialogConfirmButton.setOnClickListener(new OnClickListener() {
	    @Override
	    public void onClick(View v) {
		// TODO Auto-generated method stub
		dialog.dismiss();
	    }
	});
	dialog.setCancelable(false);
	dialog.getWindow().setContentView(myView);
	dialog.show();
    }

    /*
     * 显示等待的dialog
     * 
     * @param testTextView
     */
    private void readMachineWaitDialog(TextView testTextView) {
	// 创建自定义dialog
	testTextView.setText("校验成功....数据...");
	final Dialog dialog = new Dialog(this, R.style.dialog);
	final View myView = LayoutInflater.from(DetailActivity.this).inflate(
		R.layout.read_machine_wait_dialog, null);
	myView.findFocus();
	// 获取控件
	dialog.setCancelable(false);
	dialog.getWindow().setContentView(myView);
	dialog.show();
	//启动线程，连接失败时中断。
	Thread thread = new Thread(new Runnable() {
	    @Override
	    public void run() {
		try {
		    Thread.sleep(5000);
			dialog.dismiss();
			DetailActivity.this.mhandler.sendEmptyMessage(0);
		} catch (InterruptedException e) {
		    e.printStackTrace();
		}
	    }
	});
	thread.start();
    }
    Handler mhandler = new Handler() {
	@Override
	public void handleMessage(Message msg) {
	    // TODO Auto-generated method stub
	    super.handleMessage(msg);
	    switch (msg.what) {
	    case 0:
		//与机器校验过程中出错时，弹出提示dialog
		new AlertDialog.Builder(DetailActivity.this).setTitle("Error")
			.setMessage("校验出错，请检查!").setCancelable(false)
			.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			    public void onClick(DialogInterface dialog, int whichButton) {
				dialog.dismiss();
			    }
			}).show();
		break;
	    }
	}
    };
    // 打开导航
    public void startNavi() {
	// 天安门坐标
	double mLat1 = 39.915291;
	double mLon1 = 116.403857;
	// 百度大厦坐标
	double mLat2 = 40.056858;
	double mLon2 = 116.308194;
	int lat = (int) (mLat1 * 1E6);
	int lon = (int) (mLon1 * 1E6);
	GeoPoint pt1 = new GeoPoint(lat, lon);
	lat = (int) (mLat2 * 1E6);
	lon = (int) (mLon2 * 1E6);
	GeoPoint pt2 = new GeoPoint(lat, lon);
	// 构建 导航参数
	NaviPara para = new NaviPara();
	para.startPoint = pt1;
	para.startName = "从这里开始";
	para.endPoint = pt2;
	para.endName = "到这里结束";
	try {
	    BaiduMapNavigation.openBaiduMapNavi(para, this);
	} catch (BaiduMapAppNotSupportNaviException e) {
	    e.printStackTrace();
	    AlertDialog.Builder builder = new AlertDialog.Builder(this);
	    builder.setMessage("您尚未安装百度地图app或app版本过低");
	    builder.setTitle("提示");
	    builder.setPositiveButton("确认",
		    new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
			    dialog.dismiss();
			}
		    });
	    builder.setNegativeButton("取消",
		    new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
			    dialog.dismiss();
			}
		    });
	    builder.create().show();
	}
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
	if (keyCode == KeyEvent.KEYCODE_BACK) { // 监控/拦截/屏蔽返回键
	    new AlertDialog.Builder(this)
		    .setTitle("退出确认")
		    .setMessage("确认退出任务？")
		    .setCancelable(false)
		    .setNegativeButton("取消",
			    new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog,
					int which) {
				}
			    })
		    .setPositiveButton("确定",
			    new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,
					int whichButton) {
				    finish();
				}
			    }).show();
	    return true;
	}
	return super.onKeyDown(keyCode, event);
    }

    private void ChangeTitleText(String titleString, String leftButtonString,
	    String rightButtonString, Boolean isRight, Boolean isNeedAnimation) {
	title.setText(titleString);
	leftBtn.setText(leftButtonString);
	rightBtn.setText(rightButtonString);
	if (isNeedAnimation) {
	    if (isRight) {
		showNextWithAnimation(slideInLeft, slideOutLeft);
	    } else {
		showPreviousWithAnimation(slideInRight, slideOutRight);
	    }
	}
    }

    private void showNextNoAnimation() {
	animator.clearAnimation();
	animator.showNext();
    }

    private void showNextWithAnimation(Animation slideIn, Animation slideOut) {
	animator.setInAnimation(slideIn);
	animator.setOutAnimation(slideOut);
	animator.showNext();
    }

    private void showPreviousNoAnimation() {
	animator.clearAnimation();
	animator.showPrevious();
    }

    private void showPreviousWithAnimation(Animation slideIn, Animation slideOut) {
	animator.setInAnimation(slideIn);
	animator.setOutAnimation(slideOut);
	animator.showPrevious();
    }

    class myAnimationListener implements AnimationListener {
	@Override
	public void onAnimationEnd(Animation animation) {
	    isAnimating = false;
	}

	@Override
	public void onAnimationRepeat(Animation animation) {
	}

	@Override
	public void onAnimationStart(Animation animation) {
	    isAnimating = true;
	}
    }
}
