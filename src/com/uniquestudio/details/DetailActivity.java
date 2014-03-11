package com.uniquestudio.details;

import static com.uniquestudio.bluetooth.BluetoothConstant.*;
import static com.uniquestudio.stringconstant.SpinnerString.*;
import static com.uniquestudio.stringconstant.StringConstant.*;


import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
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
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.ArrayAdapter;
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

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.uniquestudio.R;
import com.uniquestudio.AnalyseTxt.AnalyseTxtUtil;
import com.uniquestudio.DBFRW.ParseDbf2Map;
import com.uniquestudio.DBFRW.WriteDbfFile;
import com.uniquestudio.bluetooth.BluetoothChatService;
import com.uniquestudio.bluetooth.BluetoothSppClient;
import com.uniquestudio.bluetooth.DiscoveryDevicesActivity;
import com.uniquestudio.bluetooth.PecData;
import com.uniquestudio.gps.GPSManager;
import com.uniquestudio.refreshablelist.DBOpenHelper;
import com.uniquestudio.refreshablelist.DataBaseService;
import com.uniquestudio.refreshablelist.MyData;
import com.uniquestudio.refreshablelist.RefreshableListViewActivity;
import com.uniquestudio.spinneredittext.SpinnerEditText;

public class DetailActivity extends Activity {
    // Debugging
    private static final String TAG = "DetailActivity";
    private static final boolean D = true;
    
    Resources resources;
    SharedPreferences sharedPreferences;
    DataBaseService dataBaseService;
    HashMap<String, Object> missionData;//存放此次所有输入的数据
    HashMap<String, List<String>> configTxtData;
    private String Zc_id = "";
    private String Task_id = "";
    private String Cons_No = "";//是CONS_NO的detail
    private String wiringMode = "";
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
    private List<Map<String, String>> GPSSearchMap;//搜索对应的gps数据
    private Button setNavigation;
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
    private Spinner clockDeviationConclusion;
    private Button electriClockDate;
    private Spinner electriClockDateConclusion;
    private Button electriClockEventButton;
    private AutoCompleteTextView electirClockEvent;
    // electriClock
    private static final int electriClockID = 4;
    private EditText positiveTotalPower;
    private EditText positivePeak;
    private EditText positiveValley;
    private EditText positiveAverage;
    private TextView combinationDeviation;
    private Button otherPowerButton;
    private Spinner voltPhase;
    private EditText testLaps;
    private CheckBox electriClockExceptionErrorOne;
    private CheckBox electriClockExceptionErrorTwo;
    private Boolean isClockExceptionTwo = false;//isInputData?
    // scene verify
    private static final int sceneVerifyID = 5;
    private EditText verifyTemperature;
    private EditText verifyHumidity;
    private ListView verifyTestTextView;
    private Button verifyReadMachine;
    private boolean s = false;
    private boolean canConnectDefDevice = true;
    private Spinner verifyMadeNumberSpinner;
    private BluetoothSppClient bluetoothSppClient;
    private Dialog waitReadMachineDialog;
    private Thread readMachineThread;
    // Name of the connected device
    private String mConnectedDeviceName = null;
    // Array adapter for the conversation thread
    private ArrayAdapter<String> mConversationArrayAdapter;
    // Local Bluetooth adapter
    private BluetoothAdapter mBluetoothAdapter = null;
    // Member object for the chat services
    private BluetoothChatService mChatService = null;

    private static final int inputVerifyDataID = 8;
    //手动inputData
    private TextView UanTextView;
    private TextView UbnTextView;
    private TextView UcnTextView;
    private TextView UanIaTextView;//Uan^Ia
    private TextView UbnIbTextView;
    private TextView UcnIcTextView;
    
    private EditText TemperatureEditText;
    private EditText HumidityEditText;
    private EditText UanEditText;
    private EditText UbnEditText;
    private EditText UcnEditText;
    private EditText IaEditText;
    private EditText IbEditText;
    private EditText IcEditText;
    private EditText UanIaEditText;//Uan^Ia
    private EditText UbnIbEditText;
    private EditText UcnIcEditText;
    private EditText paEditText;//A相功率
    private EditText pbEditText;
    private EditText pcEditText;
    private EditText powerRateEditText;//功率因素
    private EditText secondActivePowerEditText;//二次总有功功率
    private EditText errOneEditText;//电表误差
    private EditText errTwoEditText;
    private EditText secondPositivePowerEditText;//二次总无功功率
    
    // result confirm
    private static final int resultConfirmID = 6;
    private TextView confirmDateTime;
    private TextView confirmMachineNumber;
    private SpinnerEditText confirmCheckPeople;
    private SpinnerEditText confirmVerifyPeople;
    private Spinner confirmWorkMode;
    private SpinnerEditText confirmEquipmentRating;
    private Spinner confirmSeal;
    private Spinner confirmSecondaryLineConclusion;
    private Spinner confirmSceneVerifyConclusion;
    private Button confirmAddEventButton;
    private AutoCompleteTextView confirmAddEvenTextView;
    private static final int newSealID = 7;
    private SpinnerEditText newCabinetSealOne;
    private SpinnerEditText newCabinetSealTwo;
    private SpinnerEditText newTableSealOne;
    private SpinnerEditText newTableSealTwo;
    private SpinnerEditText newBoxSealOne;
    private SpinnerEditText newBoxSealTwo;

    @Override
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
	setContentView(R.layout.detail_acticity);
	getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
		R.layout.detail_activity_title);
	resources = getResources();
	sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
	
	dataBaseService = new MyData(getApplicationContext());
	missionData = new HashMap<String, Object>();
	//解析TXT配置文件获得的数据
	configTxtData = AnalyseTxtUtil.getDataFromConfigTXT(configTXTPath);
   	// first page
   	initViewAnimatior();
   	initTitleListener();
   	initMissionInfo();
   	// second page
   	initOldSeal();
   	// third page
   	initMissionStatus();
   	// four page
   	initSceneState();
   	// fifth page()
   	initElectriClock();
   	// six page
   	initSceneVerify();
   	initIputVerifyData();
   	// seven page
   	initResultConfirm();
   	// eight page
   	initNewSeal();
   	
	    Intent intentToRefresh = new Intent("com.unique.refresh");  
	    this.sendBroadcast(intentToRefresh);
    }

    // //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // ///////////////////////////////////// init initOldSeal
    // //////////////////////////////////////////////////////////
    // //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * 初始化展开选择EditText ，名字，展开的数据，点击的监听器
     */
    private void initOldSeal() {
	// TODO Auto-generated method stub
	cabinetSealOne = (SpinnerEditText) findViewById(R.id.st_cabinet_1);
	cabinetSealTwo = (SpinnerEditText) findViewById(R.id.st_cabinet_2);
	tableSealOne = (SpinnerEditText) findViewById(R.id.st_table_1);
	tableSealTwo = (SpinnerEditText) findViewById(R.id.st_table_2);
	boxSealOne = (SpinnerEditText) findViewById(R.id.st_box_1);
	boxSealTwo = (SpinnerEditText) findViewById(R.id.st_box_2);
	cabinetSealOne.init(configTxtData.get(OldCabinet), "柜封1:", true);
	cabinetSealTwo.init(configTxtData.get(OldCabinet), "柜封2:", true);
	tableSealOne.init(TABLE_SEAL_ARRAY, "表封1:", true);
	tableSealTwo.init(TABLE_SEAL_ARRAY, "表封2:", true);
	boxSealOne.init(configTxtData.get(OldBox), "盒封1:", true);
	boxSealTwo.init(configTxtData.get(OldBox), "盒封2:", true);
	tableSealOne.setOpenDialogListener(new myOpenSealInfoDialog(
		tableSealOne,"旧表封1"));
	tableSealTwo.setOpenDialogListener(new myOpenSealInfoDialog(
		tableSealTwo, "旧表封2"));
    }

    /**
     * @author luo
     *封口信息界面 点击可展开选择EditText时的监听器
     */
    class myOpenSealInfoDialog implements OnFocusChangeListener {
	private SpinnerEditText spinnerEditText;
	private String titleString;
	public myOpenSealInfoDialog(SpinnerEditText spinnerEditText , String title) {
	    this.spinnerEditText = spinnerEditText;
	    this.titleString = title;
	}

	@Override
	public void onFocusChange(View v, boolean hasFocus) {
	    // TODO Auto-generated method stub
	    if (hasFocus)
		openClickSealInfoDialog(
			this.titleString,
			this.spinnerEditText);
	}
    }

    // //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // ///////////////////////////////////// initOldSeal
    // //////////////////////////////////////////////////////////
    // //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    // //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // ///////////////////////////////////// init MissionInfo
    // //////////////////////////////////////////////////////////
    // //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
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
	
	initMissionInfoListView();
	initMissionInfoListener();
	GPSSearchMap = dataBaseService.viewMyData(GPS, CONS_NO,new String[] { Cons_No });
	if(GPSSearchMap.size() == 0)
	    setNavigation.setText("开启导航(无目的地信息)");
    }

    /**
     * 任务信息界面中更改联系人，获取地理位置，启动导航的监听器
     */
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
		Location nowLocation = gpsManager.WriteLocation(Cons_No);
		if( nowLocation != null ) { 
		    httpGetAdress(nowLocation.getLatitude() , nowLocation.getLongitude());
		    setNavigation.setText(resources.getString(R.string.set_navigation));
		}
		else 
		    getPositionTextView.setText("无法获取,请检查网络状况..");
	    }
	});
	setNavigation.setOnClickListener(new OnClickListener() {
	    @Override
	    public void onClick(View arg0) {
		// 检查gpsdbf里有无数据，无的话输入起点，有的话输入终点
		// 开启百度导航
		//默认武汉市坐标114.302661,30.592764
		String mLat1 = "30.598562"; 
	   	String mLon1 = "114.302661"; 
	   	String message = "请手动搜索终点位置..";
		GPSSearchMap = dataBaseService.viewMyData(GPS, CONS_NO,new String[] { Cons_No });
		if (GPSSearchMap.size() != 0) {
		    System.out.println("任务内有目的地GPS");
		    mLat1 = GPSSearchMap.get(0).get(LATITUDE);
		    mLon1 = GPSSearchMap.get(0).get(LONGITUDE);
		    message = "已定位到目的地,请点击‘到这去’..";
		}else {
			GPSManager gpsManager = new GPSManager(DetailActivity.this);
			Location localLocation = gpsManager.getMyLastKnownLocation();
			if(localLocation != null) {
			    mLat1 = localLocation.getLatitude() + "";
			    mLon1 = localLocation.getLongitude() + "";
			}
		}
		try {
		    Uri mUri = Uri.parse("geo:"+mLat1+","+ mLon1+"?z=12"/*+"&q="+message*/);
		    Intent mIntent = new Intent(Intent.ACTION_VIEW, mUri);
		    startActivity(mIntent);
		} catch (Exception e) {
		    System.out.println("没有地图");
		    new AlertDialog.Builder(DetailActivity.this).setTitle("警告")
		    .setMessage("未发现地图应用").setPositiveButton("确定",new DialogInterface.OnClickListener() {
		        @Override
		        public void onClick(DialogInterface dialog, int which) {
		    	dialog.dismiss();
		        }
		    }).show();
		}
	    }
	});
    }

    private void httpGetAdress(double latitude, double longitude) {
	    HashMap<String, String> params = new HashMap<String, String>();
	    params.put("location", latitude + "," + longitude);
	    params.put("output", "json");
	    params.put("key", "N7Gqr54UC1aqD9CBT3O8PsWx");
	    RequestParams requestParams = new RequestParams(params);
	    AsyncHttpClient httpClient = new AsyncHttpClient();
	    httpClient.get("http://api.map.baidu.com/geocoder", requestParams, new JsonHttpResponseHandler() {
		@Override
		public void onFailure(Throwable e, JSONObject errorResponse) {
		    getPositionTextView.setText("GPS信息已写入,解析失败");
		    super.onFailure(e, errorResponse);
		}
		@Override
		public void onSuccess(int statusCode, Header[] headers,
			org.json.JSONObject response) {
		    if(response.has("result")) {
			try {
			    JSONObject jsonArray = response
			    	.getJSONObject("result");
			    if(jsonArray.has("formatted_address")) {
				String formattedAddress = jsonArray.getString("formatted_address");
				System.out.println(formattedAddress);
				getPositionTextView.setText(formattedAddress);
				return ;
			    }
			    getPositionTextView.setText("GPS信息已写入,解析失败");
			} catch (JSONException e) {
			    e.printStackTrace();
			}
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
	//获取从任务列表传过来的详细任务信息,改为list，用于再listview中显示
	Map<String, Object> map = StringToMap.StringToMap(missionInfoString);
	//标志此次的详细信息是号码为Cons_No的
	Cons_No = (String) map.get(CONS_NO);
	Zc_id = (String) map.get(ZC_ID);
	Task_id = (String) map.get(TASKID);
	System.out.println(Cons_No+"-"+Zc_id + "-" +Task_id);
	//获取接线方式
	wiringMode = (String) map.get(WIRING);
//	System.out.println(wiringMode);
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

    // //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // ///////////////////////////////////// init MissionInfo
    // //////////////////////////////////////////////////////////
    // //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    // //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // ///////////////////////////////////// init MissionState
    // //////////////////////////////////////////////////////////
    // //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void initMissionStatus() {
	missionStatusGroup = (RadioGroup) findViewById(R.id.mission_status_radio_group);
	missionStatusOne = (RadioButton) findViewById(R.id.mission_status_radio1);
	missionStatusTwo = (RadioButton) findViewById(R.id.mission_status_radio2);
	missionStatusThree = (RadioButton) findViewById(R.id.mission_status_radio3);
	missionStatusGroup
		.setOnCheckedChangeListener(new OnCheckedChangeListener() {
		    @Override
		    public void onCheckedChanged(RadioGroup group, int checkedId) {
			if (checkedId == missionStatusOne.getId()) {
			    missionStatus = 1;
			    confirmMachineNumber.setText("无");
			    missionData.put(MD_NO, "");
			}
			else if (checkedId == missionStatusTwo.getId())
			    missionStatus = 2;
			else if (checkedId == missionStatusThree.getId())
			    missionStatus = 3;
		    }
		});
    }

    // //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // ///////////////////////////////////// init MissionState
    // //////////////////////////////////////////////////////////
    // //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    // //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // ///////////////////////////////////// init SceneState
    // /////////////////////////////////////////////////////////////
    // //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void initSceneState() {
	clockDeviation = (EditText) findViewById(R.id.clock_mistake_value);
	clockDeviationConclusion = (Spinner) findViewById(R.id.clock_mistake_conclusion);
	electriClockDate = (Button) findViewById(R.id.clock_date_value);
	Calendar calendar = Calendar.getInstance();
	int year = calendar.get(Calendar.YEAR);
	int monthOfYear = calendar.get(Calendar.MONTH)+1;
	int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
	electriClockDate.setText(year + "-" + monthOfYear + "-" + dayOfMonth);
	electriClockDateConclusion = (Spinner) findViewById(R.id.clock_date_conclusion);
	electriClockEventButton = (Button) findViewById(R.id.clock_event_button);
	electirClockEvent = (AutoCompleteTextView) findViewById(R.id.clock_event_textview);
	// 创建一个ArrayAdapter
	ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
		android.R.layout.simple_dropdown_item_1line,
		configTxtData.get("常用备注"));
	electirClockEvent.setAdapter(adapter);
	electirClockEvent.setOnFocusChangeListener(new showDropDownListener());
	initNormalSpinner(clockDeviationConclusion, CLOCK_MISTAKE_CONCLUSION);
	initNormalSpinner(electriClockDateConclusion, CLOCK_DATE_CONCLUSION);
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

    class showDropDownListener implements OnFocusChangeListener {
	@Override
	public void onFocusChange(View v, boolean hasFocus) {
	    AutoCompleteTextView view = (AutoCompleteTextView) v;
	    if (hasFocus) {
		view.showDropDown();
	    }
	}
    }

    // //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // ///////////////////////////////////// init SceneState
    // /////////////////////////////////////////////////////////////
    // //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    // //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // ///////////////////////////////////// initElectriClock
    // /////////////////////////////////////////////////////////////
    // //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void initElectriClock() { // electriClock
	positiveTotalPower = (EditText) findViewById(R.id.electri_total_power);
	positivePeak = (EditText) findViewById(R.id.electri_peak);
	positiveValley = (EditText) findViewById(R.id.electri_valley);
	positiveAverage = (EditText) findViewById(R.id.electri_average);
	combinationDeviation = (TextView) findViewById(R.id.electri_combination_deviation);
	voltPhase = (Spinner) findViewById(R.id.electri_volt_phase);
	testLaps = (EditText) findViewById(R.id.electri_test_laps);
	electriClockExceptionErrorOne = (CheckBox) findViewById(R.id.electri_radio_bt_one);
	electriClockExceptionErrorTwo = (CheckBox) findViewById(R.id.electri_radio_bt_two);
	otherPowerButton = (Button) findViewById(R.id.electir_other_power);
	initNormalSpinner(voltPhase, VOLT_PHASE_ARRAY);
	initElectriClockListener();
	// 组合误差：（总电量-峰-平-谷）/总电量;Format(_T("%6.4f"));
	// 组合误差的显示：组合误差的百分比方式
	// Format(_T("%.2f"),组合误差)*100)+_T("%"));
    }

    private void initElectriClockListener() {
	// textview listener
	class myTextWatcher implements TextWatcher {
	    @Override
	    public void afterTextChanged(Editable s) {}
	    @Override
	    public void beforeTextChanged(CharSequence s, int start, int count,
		    int after) {}
	    @Override
	    public void onTextChanged(CharSequence s, int start, int before,
		    int count) {
		boolean isTotalHave = !(positiveTotalPower.getText().toString().length() == 0);
		boolean isPeakHave = !(positivePeak.getText().toString().length() == 0);
		boolean isValleyHave = !(positiveValley.getText().toString().length() == 0);
		boolean isAverageHave = !(positiveAverage.getText().toString().length() == 0);
		if (isTotalHave && isPeakHave && isValleyHave && isAverageHave) {
		    int total = Integer.parseInt(positiveTotalPower.getText().toString());
		    int peak = Integer.parseInt(positivePeak.getText() .toString());
		    int valley = Integer.parseInt(positiveValley.getText().toString());
		    int average = Integer.parseInt(positiveAverage.getText().toString());
		    float deviation =(float) (total - peak - valley - average) / total;
		    deviation *= 100;
		    DecimalFormat dFormat = new DecimalFormat("0.00");
		    String deviationString = dFormat.format(deviation);
		    combinationDeviation.setText(deviationString+ "%");
		}else {
		    combinationDeviation.setText("");
		}
	    }
	};
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
				isClockExceptionTwo = false;
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
			isClockExceptionTwo = isChecked;
		    }
		});
    }

    // //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // ///////////////////////////////////// initElectriClock
    // /////////////////////////////////////////////////////////////
    // //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    // //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // ///////////////////////////////////// initSceneVerify
    // /////////////////////////////////////////////////////////////
    // //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void initSceneVerify() {
	verifyHumidity = (EditText) findViewById(R.id.verify_humidity);
	verifyTemperature = (EditText) findViewById(R.id.verify_temperature);
	verifyTestTextView = (ListView) findViewById(R.id.machine_data_list);
        mConversationArrayAdapter = new ArrayAdapter<String>(this, R.layout.bluetooth_message_item);
        verifyTestTextView.setAdapter(mConversationArrayAdapter);
        
	verifyReadMachine = (Button) findViewById(R.id.verify_read_machine);
	verifyMadeNumberSpinner = (Spinner) findViewById(R.id.verify_made_number);

	// 将可选内容与ArrayAdapter连接起来
	List<Map<String, String>> listMap = dataBaseService.listMyDataMaps(BZQJ, null);
	final List<String> list = new ArrayList<String>();
	if (listMap.size() != 0) {
	    for (int i = 1; i < listMap.size(); i++) {
		String string = listMap.get(i).get(MADE_NO);
		list.add(string);
	    }
	}
	initNomorlSpinner(verifyMadeNumberSpinner,list);
	verifyReadMachine.setOnClickListener(new OnClickListener() {
	    @Override
	    public void onClick(View v) {
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if (mBluetoothAdapter == null) {
		    Toast.makeText(getApplicationContext(), "蓝牙不可用..",
			    Toast.LENGTH_LONG).show();
		} else {
		    if (!mBluetoothAdapter.isEnabled()) {
			Intent enableIntent = new Intent(
				BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
		    } else if(bluetoothSppClient == null ){
			if(canConnectDefDevice) {
				// 新线程与蓝牙通信
			    String address = sharedPreferences.getString("def_device", "null");
			    if(address.equals("null"))
				searchDevice();
			    else {
				bluetoothSppClient = new BluetoothSppClient(DetailActivity.this, address, true , mreadMachineHandler);
				// 打开等待框
				readMachineWaitDialog();
				verifyReadMachine.setEnabled(false);
			    }
			}else    
			    searchDevice();
		    }
		}
	    }
	});
    }
 /** spinner   设备编号 工作方式 封印 二次线结论 现场校验结论 日期结论，时钟误差结论 电压相序
 * @param spinner
 */
private void initNomorlSpinner(Spinner spinner , List<String> data) {
	ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, data);
	// 设置下拉列表的风格
	adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	// 将adapter 添加到spinner中
	spinner.setAdapter(adapter);
	// 添加事件Spinner事件监听
	spinner.setOnItemSelectedListener(new MySpinnerListener(data));
    }
private void initNormalSpinner(Spinner spinner , String[] data) {
    List<String> list = new ArrayList<String>();
    for (int i = 0; i < data.length; i++) 
	list.add(data[i]);
    initNomorlSpinner(spinner, list);
}

/** spinner的监听器 设备编号 工作方式 封印 二次线结论 现场校验结论 日期结论，时钟误差结论 电压相序
 */ 
class MySpinnerListener implements OnItemSelectedListener{
    private List<String> datas;
    public MySpinnerListener(List<String> data) {
	this.datas = data;
    }
    @Override
    public void onItemSelected(AdapterView<?> parent, View view,
	    int position, long id) {
	if (parent.equals(verifyMadeNumberSpinner)) {
	    if(position == 0 && !s) {
		s = true;
		int pos = sharedPreferences.getInt("spinner_position", -1);
		if(pos != -1) 
		    position = pos;
	    }
	    if(s) {
		confirmMachineNumber.setText(datas.get(position));
		missionData.put(MD_NO, datas.get(position));
		sharedPreferences.edit().putInt("spinner_position", position).commit();
	    }

	}else if(parent.equals(confirmWorkMode)) {
	    if(position == 0 || missionData.containsKey(WORK_MODE))
		missionData.remove(WORK_MODE);
	    else
		missionData.put(WORK_MODE, position);
	}else if(parent.equals(confirmSeal)) {
	    missionData.put(SEAL_FLAG, datas.get(position));
	}else if(parent.equals(confirmSecondaryLineConclusion)) {
	    if(position == 0) 
		missionData.put(SND_CONC, "");
	    else if(position == 1 || position == 2)
		missionData.put(SND_CONC, position -1);
	    else
		missionData.put(SND_CONC, 9);
	}else if(parent.equals(confirmSceneVerifyConclusion)) {
	    if(position == 0 ) 
		missionData.put(TEST_RSLT, "");
	    else if(0 < position && position <= 2) 
		missionData.put(TEST_RSLT, position - 1);
	    else
		missionData.put(TEST_RSLT, position + 1);
	}else if(parent.equals(clockDeviationConclusion)) {
	    if(position == 0 ) missionData.put(T_ERR_CONC, "");
	    else 	missionData.put(T_ERR_CONC, position-1);
	}else if (parent.equals(electriClockDateConclusion)) {
	    if(position == 0 ) missionData.put(DATE_CONC, "");
	    else 	missionData.put(DATE_CONC, position-1);
	}else if(parent.equals(voltPhase)) {
	    if(position == 0 || missionData.containsKey(PHASE_CODE)) { 
		missionData.remove(PHASE_CODE); }
	    else {
		missionData.put(PHASE_CODE, datas.get(position));}
	}
	((TextView) view).setText(datas.get(position));
    }
    @Override
    public void onNothingSelected(AdapterView<?> arg0) {}
 }
    /**
     * 打开搜索蓝牙设备界面
     */
    private void searchDevice() {
	System.out.println("setupChat---------->");
	// mConversationArrayAdapter = new ArrayAdapter<String>(this,
	// R.layout.bluetooth_message_item);
	// Initialize the buffer for outgoing messages
	Intent serverIntent = null;
	serverIntent = new Intent(this, DiscoveryDevicesActivity.class);
	startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_SECURE);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
	if (D)
	    Log.d(TAG, "onActivityResult " + resultCode);
	switch (requestCode) {
	case REQUEST_CONNECT_DEVICE_SECURE:
	    // When DeviceListActivity returns with a device to connect
	    if (resultCode == Activity.RESULT_OK) {
		String address = data.getExtras().getString(
			EXTRA_DEVICE_ADDRESS);
		sharedPreferences.edit().putString("def_device", address).commit();
		canConnectDefDevice = true;
		// 新线程与蓝牙通信
		bluetoothSppClient = new BluetoothSppClient(
			DetailActivity.this, address, mreadMachineHandler);
		// 打开等待框
		readMachineWaitDialog();
		verifyReadMachine.setEnabled(false);
		System.out.println("REQUEST_CONNECT_DEVICE_SECURE------>OK");
	    } else {
		System.out
			.println("REQUEST_CONNECT_DEVICE_SECURE------>CANCLE");
	    }
	    break;
	case REQUEST_ENABLE_BT:
	    // When the request to enable Bluetooth returns
	    if (resultCode == Activity.RESULT_OK)
		searchDevice();
	    else
		Toast.makeText(this, "无法打开蓝牙", Toast.LENGTH_SHORT).show();
	    // User did not enable Bluetooth or an error occured
	}
    }
    /**
     * 处理与仪器校验时的等待工作
     * 
     */
    Handler mreadMachineHandler = new Handler() {
	@Override
	public void handleMessage(Message msg) {
	    // TODO Auto-generated method stub
	    super.handleMessage(msg);
	    switch (msg.what) {
	    case 0:
		System.out.println("mreadMachineHandler---0");
		// 与机器校验过程中出错时，弹出提示dialog
		new AlertDialog.Builder(DetailActivity.this)
			.setTitle("Error")
			.setMessage("校验出错，请关闭蓝牙重试!")
			.setCancelable(false)
			.setPositiveButton("确定",
				new DialogInterface.OnClickListener() {
				    public void onClick(DialogInterface dialog,
					    int whichButton) {
					dialog.dismiss();
				    }
				}).show();
		break;
	    case 1:
		// 接收完了
		System.out.println("mreadMachineHandler---1");
		verifyReadMachine.setText("读取完成");
		waitReadMachineDialog.dismiss();
		readMachineThread.interrupt();
		PecData pecData = bluetoothSppClient.getPecData();
		HashMap<String , Object> hashMap = pecData.toList();
		String stTimer = hashMap.get("Timer").toString();
		mConversationArrayAdapter.add("Time:" + stTimer);
		for(int i =0;i<PecDataItem.length;i++) {
		    String key = PecDataItem[i];
		    System.out.println(key);
		    mConversationArrayAdapter.add(key + ":" + hashMap.get(key));
		}
		//填充missionData数据
		String[] needData = {UA,IA,UB,IB,UC,IC,A_PF,B_PF,C_PF,I1,I2,I3,SND_PF,SND_RPF
			,U12_U1,U2,U32_U3,ERR1,ERR2};
		for(int i =0;i<needData.length;i++) {
		    String key = needData[i];
		    missionData.put(key,hashMap.get(key));
		}
		break;
	    case 2:
		// error
		System.out.println("mreadMachineHandler---error");
		Toast.makeText(DetailActivity.this, "连接失败..", Toast.LENGTH_LONG)
			.show();
		waitReadMachineDialog.dismiss();
		readMachineThread.interrupt();
		break;
	    case 3:
		//default device connect error
		System.out.println("mreadMachineHandler---error");
		Toast.makeText(DetailActivity.this, "连接不上默认设备,搜索其他设备..", Toast.LENGTH_LONG)
			.show();
		waitReadMachineDialog.dismiss();
		readMachineThread.interrupt();
		canConnectDefDevice = false;
//		sharedPreferences.edit().putString("def_device" , "").commit();//清除这个默认的mac地址
		break;
	    }
	    bluetoothSppClient.close();
	    bluetoothSppClient = null;
	    verifyReadMachine.setEnabled(true);
	    if(msg.what == 3)
		searchDevice();
	}
    };

    // //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // ///////////////////////////////////// initSceneVerify
    // ////////////////////////////////////////////////////////////
    // //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    // //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // ///////////////////////////////////// initIputVerifyData
    // ////////////////////////////////////////////////////////////
    // //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    

    private void initIputVerifyData() {
	UanTextView = (TextView) findViewById(R.id.Uan);
	UbnTextView = (TextView) findViewById(R.id.Ubn);
	UcnTextView = (TextView) findViewById(R.id.Ucn);
	UanIaTextView = (TextView) findViewById(R.id.Uan_Ia);
	UbnIbTextView = (TextView) findViewById(R.id.Ubn_Ib);
	UcnIcTextView = (TextView) findViewById(R.id.Ucn_Ic);
	if (!wiringMode.equals("03")) {
	    UanTextView.setText("Uab:");
	    UbnTextView.setText("Uac:");
	    UcnTextView.setText("Ucb:");
	    UanIaTextView.setText("Uab^Ia:");
	    UbnIbTextView.setText("Uac^Ib");
	    UcnIcTextView.setText("Ucb^Ic");
	}
	TemperatureEditText = (EditText) findViewById(R.id.wendu_et);
	HumidityEditText = (EditText) findViewById(R.id.shidu_et);
	UanEditText = (EditText ) findViewById(R.id.Uan_et);
	UbnEditText = (EditText) findViewById(R.id.Ubn_et);
	UcnEditText = (EditText) findViewById(R.id.Ucn_et);
	IaEditText = (EditText) findViewById(R.id.Ia_et);
	IbEditText = (EditText) findViewById(R.id.Ib_et);
	IcEditText = (EditText) findViewById(R.id.Ic_et);
	UanIaEditText = (EditText) findViewById(R.id.Uan_Ia_et);
	UbnIbEditText = (EditText) findViewById(R.id.Ubn_Ib_et);
	UcnIcEditText = (EditText) findViewById( R.id.Ucn_Ic_et);
	paEditText = (EditText) findViewById(R.id.a_phase_et);
	pbEditText = (EditText) findViewById(R.id.b_phase_et);
	pcEditText = (EditText) findViewById(R.id.c_phase_et);
	powerRateEditText = (EditText) findViewById(R.id.power_rate_et);
	secondActivePowerEditText = (EditText) findViewById(R.id.second_active_power_et);
	secondPositivePowerEditText = (EditText) findViewById(R.id.second_positive_power_et);
	errOneEditText = (EditText) findViewById(R.id.dianbiao_deviation_et1);
	errTwoEditText = (EditText) findViewById(R.id.dianbiao_deviation_et2);
	
    }
    // //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // ///////////////////////////////////// initIputVerifyData
    // ////////////////////////////////////////////////////////////
    // //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    
    // //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // ////////////////////////////////// initResultConfirm
    // /////////////////////////////////////////////////////////////
    // //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void initResultConfirm() {
	confirmDateTime = (TextView) findViewById(R.id.confirm_date_textview);
	confirmMachineNumber = (TextView) findViewById(R.id.confirm_machine_number);
	confirmCheckPeople = (SpinnerEditText) findViewById(R.id.confirm_check_people);
	confirmVerifyPeople = (SpinnerEditText) findViewById(R.id.confirm_verify_people);
	confirmWorkMode = (Spinner) findViewById(R.id.confirm_work_model);
	confirmEquipmentRating = (SpinnerEditText) findViewById(R.id.confirm_equipment_rating);
	confirmSeal = (Spinner) findViewById(R.id.confirm_seal);
	confirmSecondaryLineConclusion = (Spinner) findViewById(R.id.confirm_conclusion_secondary_line);
	confirmSceneVerifyConclusion = (Spinner) findViewById(R.id.confirm_scene_verify_conclusion);
	confirmAddEventButton = (Button) findViewById(R.id.confirm_add_event);
	confirmAddEvenTextView = (AutoCompleteTextView) findViewById(R.id.confirm_event_textview);
	// 创建一个ArrayAdapter
	ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
		android.R.layout.simple_dropdown_item_1line,
		configTxtData.get("常用备注"));
	confirmAddEvenTextView.setAdapter(adapter);
	confirmAddEvenTextView
		.setOnFocusChangeListener(new showDropDownListener());
	// 显示当前日期
	SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd");
	String currentDate = sDateFormat.format(new java.util.Date());
	confirmDateTime.setText(currentDate);
	// 校验人 审核人 设备评级 解析TXT
	confirmCheckPeople.init(configTxtData.get("校验人"),
		resources.getString(R.string.check_people), false);
	confirmVerifyPeople.init(configTxtData.get("审核人"),
		resources.getString(R.string.verify_people), false);
	confirmEquipmentRating.init(configTxtData.get("设备评级"),
		resources.getString(R.string.equipment_ratings), false);
	// 事件点击
	initNormalSpinner(confirmWorkMode , WORK_MODE_ARRAY);
	initNormalSpinner(confirmSeal, SEAL_ARRAY);
	initNormalSpinner(confirmSecondaryLineConclusion, SECONDARY_LINE_CONCLUSION_ARRAY);
	initNormalSpinner(confirmSceneVerifyConclusion, SCENE_VERIFY_ARRAY);
	confirmAddEventButton.setOnClickListener(new OnClickListener() {
	    @Override
	    public void onClick(View arg0) {
		if (confirmAddEvenTextView.getVisibility() == View.VISIBLE) {
		    confirmAddEvenTextView.setVisibility(View.GONE);
		} else {
		    confirmAddEvenTextView.setVisibility(View.VISIBLE);
		}
	    }
	});
    }

    // //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // ////////////////////////////////// initResultConfirm
    // /////////////////////////////////////////////////////////////
    // //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    // //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // ////////////////////////////////// initNewSeal
    // /////////////////////////////////////////////////////////////
    // //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private void initNewSeal() {
	newCabinetSealOne = (SpinnerEditText) findViewById(R.id.new_st_cabinet_1);
	newCabinetSealTwo = (SpinnerEditText) findViewById(R.id.new_st_cabinet_2);
	newTableSealOne = (SpinnerEditText) findViewById(R.id.new_st_table_1);
	newTableSealTwo = (SpinnerEditText) findViewById(R.id.new_st_table_2);
	newBoxSealOne = (SpinnerEditText) findViewById(R.id.new_st_box_1);
	newBoxSealTwo = (SpinnerEditText) findViewById(R.id.new_st_box_2);
	newCabinetSealOne.init(configTxtData.get(NewCabinet), "柜封1:", true);
	newCabinetSealTwo.init(configTxtData.get(NewCabinet), "柜封2:", true);
	newTableSealOne.init(TABLE_SEAL_ARRAY, "表封1:", true);
	newTableSealTwo.init(TABLE_SEAL_ARRAY, "表封2:", true);
	newBoxSealOne.init(configTxtData.get(NewBox), "盒封1:", true);
	newBoxSealTwo.init(configTxtData.get(NewBox), "盒封2:", true);
	newTableSealOne.setOpenDialogListener(new myOpenSealInfoDialog(
		newTableSealOne, "新表封1"));
	newTableSealTwo.setOpenDialogListener(new myOpenSealInfoDialog(
		newTableSealTwo, "新表封2"));
    }
    
    // //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // ////////////////////////////////// initNewSeal
    // /////////////////////////////////////////////////////////////
    // //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    
    /**
     * 写dbf文件前，把输入好的数据收集起来
     */
    public void collectMissionData() {
		missionData.put(ZC_ID, Zc_id);
		missionData.put(CONS_NO, Cons_No);
		missionData.put(TASKID, Task_id);
	    //计量封印装拆信息(jlfyzcxx.dbf)
	    collectSealData(tableSealOne, "old", "");
	    collectSealData(tableSealTwo, "old", "2");
	    collectSealData(newTableSealOne, "new", "");
	    collectSealData(newTableSealTwo, "new", "2");
	    missionData.put(EQUIPCATEG, 01);
	    missionData.put(EQUIP_ID, Zc_id);//检查
	    missionData.put(HANDLEDATE, confirmDateTime.getText().toString());
	    missionData.put(CHG_REASON, resources.getString(R.string.app_name));
	    missionData.put(HANDLER_NO, confirmCheckPeople.getType());
	    //电能表校验结果(dnbxysj.dbf)
	    missionData.put(APP_NO, Task_id);
	    missionData.put(METER_ID, Zc_id);
	    missionData.put(TESTER_NO, confirmCheckPeople.getType());
	    missionData.put(TEST_DATE, confirmDateTime.getText().toString());
	    missionData.put(CHECKER_NO, confirmVerifyPeople.getType());
	    addRemarkData();//添加remark
	    //任务状态为2或3时，才保存如下数据
	    if(missionStatus == 2 || missionStatus == 3) {
		if(!isClockExceptionTwo) {
		missionData.put(TEMP, verifyTemperature.getText().toString());
		missionData.put(HUMIDITY, verifyHumidity.getText().toString());
		}else {
		    collectManualInput();//手动输入数据界面
		}
		missionData.put(TEST_CIRCLE_QRY, testLaps.getText().toString());
		//电能表校验多功能数据(dnbdglxx.dbf)
		missionData.put(TIME_ERR, clockDeviation.getText().toString());
		missionData.put(METET_DATE, electriClockDate.getText().toString());
	    	String combination = combinationDeviation.getText().toString();
	    	if(combination.length() >1) {
	    	    combination = combination.substring(0, combination.length()-1);
	    	    double c = Double.parseDouble(combination);
	    	    if(c<10)
	    		missionData.put(C_ERR_CONC, 1);//组合误差<0.1
	    	    else
	    		missionData.put(C_ERR_CONC, 0);
	    	}
	    	//电能表现场检验示数(dnbxcssxx.dbf)
	    	missionData.put(ACTIVE_TOTAL, positiveTotalPower.getText().toString());
	    	missionData.put(ACTIVE_PEAK, positivePeak.getText().toString());
	    	missionData.put(ACTIVE_VALLEY, positiveValley.getText().toString());
	    	missionData.put(ACTIVE_AVERAGE, positiveAverage.getText().toString());
	    }
    }
    
    /**
     * 保存新旧封口处输入的数据
     */
    public void collectSealData(SpinnerEditText sp , String newOrOld , String nullOrSecond) {
	String oldTableType = sp.getType();
	if(oldTableType.equals("/")) 	return;
	if(oldTableType.equals("普通")) missionData.put(newOrOld+SEAL_TYPE+nullOrSecond, 01);
	if(oldTableType.equals("防盗")) missionData.put(newOrOld+SEAL_TYPE+nullOrSecond, 02);
	if(oldTableType.equals("印模")) missionData.put(newOrOld+SEAL_TYPE+nullOrSecond, 03);
	missionData.put(newOrOld+SEAL_ID+nullOrSecond, sp.getValue());
	int oldTableStatus = sp.getStatus();
	if(oldTableStatus == -1) {
	    missionData.put(newOrOld+VALID_FLAG+nullOrSecond, 01); 
	    missionData.put(newOrOld+INTACTFLAG+nullOrSecond, 01);
	} else if(oldTableStatus == 1) {
	    missionData.put(newOrOld+VALID_FLAG+nullOrSecond, 02); 
	    missionData.put(newOrOld+INTACTFLAG+nullOrSecond, 01);
	}else {
	    missionData.put(newOrOld+VALID_FLAG+nullOrSecond, 01); 
	    missionData.put(newOrOld+INTACTFLAG+nullOrSecond, 02);
	}
    }
    /**
     * 生成 remark 此列所需数据
     */
    private void addRemarkData() {
	String oldCabinet = cabinetSealOne.getType().equals("/") ?
		"" : "柜旧封1:"+cabinetSealOne.getType() +cabinetSealOne.getStatus()+ ";" ;
	String oldCabinet2 = cabinetSealTwo.getType().equals("/") ? 
		"" : "柜旧封2:"+cabinetSealTwo.getType() +cabinetSealTwo.getStatus()+ ";" ;
	String oldTable = tableSealOne.getType().equals("/") ? 
		"" : "电能表旧封1:"+tableSealOne.getType() +tableSealOne.getStatus()+ ";" ;
	String oldTable2 = tableSealTwo.getType().equals("/") ? 
		"" : "电能表旧封2:"+tableSealTwo.getType() +tableSealTwo.getStatus()+ ";" ;
	String oldBox = boxSealOne.getType().equals("/") ? 
		"" : "端子盒旧封1:"+boxSealOne.getType() +boxSealOne.getStatus()+ ";" ;
	String oldBox2 = boxSealTwo.getType().equals("/") ? 
		"" : "端子盒旧封2:"+boxSealTwo.getType() +boxSealTwo.getStatus()+ ";" ;
	
	String newCabinet = newCabinetSealOne.getType().equals("/") ?
		"" : "柜新封1:"+newCabinetSealOne.getType() +newCabinetSealOne.getStatus()+ ";" ;
	String newCabinet2 = newCabinetSealTwo.getType().equals("/") ?
		"" : "柜新封2:"+newCabinetSealTwo.getType() +newCabinetSealTwo.getStatus()+ ";" ;
	String newTable = newTableSealOne.getType().equals("/") ? 
		"" : "电能表新封1:"+newTableSealOne.getType() +newTableSealOne.getStatus()+ ";" ;
	String newTable2 = newTableSealTwo.getType().equals("/") ? 
		"" : "电能表新封2:"+newTableSealTwo.getType() +newTableSealTwo.getStatus()+ ";" ;
	String newBox = newBoxSealOne.getType().equals("/") ?
		"" : "端子盒新封1:"+newBoxSealOne.getType() +newBoxSealOne.getStatus()+ ";" ;
	String newBox2 = newBoxSealTwo.getType().equals("/") ? 
		"" : "端子盒新封2:"+newBoxSealTwo.getType() +newBoxSealTwo.getStatus()+ ";" ;
	missionData.put(REMARK, oldCabinet+oldCabinet2+oldTable+oldTable2+oldBox+oldBox2+
		"\n"+newCabinet+newCabinet2+newTable+newTable2+newBox+newBox2);
    }
    
    /**
     * 无法机器校验时手动输入处的数据
     */
    private void collectManualInput() {
	missionData.put(TEMP, TemperatureEditText.getText().toString());
	missionData.put(HUMIDITY, HumidityEditText.getText().toString());
	missionData.put(UA, UanEditText.getText().toString());
	missionData.put(UB, UbnEditText.getText().toString());
	missionData.put(UC, UcnEditText.getText().toString());
	missionData.put(IA, IaEditText.getText().toString());
	missionData.put(IB, IbEditText.getText().toString());
	missionData.put(IC, IcEditText.getText().toString());
	missionData.put(I1, UanIaEditText.getText().toString());
	missionData.put(I2, UbnIbEditText.getText().toString());
	missionData.put(I3, UcnIcEditText.getText().toString());
	missionData.put(A_PF, paEditText.getText().toString());
	missionData.put(B_PF, pbEditText.getText().toString());
	missionData.put(C_PF, pcEditText.getText().toString());
	missionData.put(PF	, powerRateEditText.getText().toString());
	missionData.put(SND_PF, secondActivePowerEditText.getText().toString());
	missionData.put(SND_RPF, secondPositivePowerEditText.getText().toString());
	missionData.put(ERR1, errOneEditText.getText().toString());
	missionData.put(ERR2, errTwoEditText.getText().toString());
    }
    // //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // ////////////////////////////////// init titlebar
    // /////////////////////////////////////////////////////////////
    // //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
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
			    if (!isClockExceptionTwo) {
				title.setText(resources
					.getString(R.string.scene_verify));
				showPreviousWithAnimation(slideInRight,
					slideOutRight);

			    } else {
				//回到手动输入数据
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
			    isjump = isClockExceptionTwo;
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
			//弹出确认对话框，后台执行数据库的更新和dbf文件的读写
			// 存储数据 async
			collectMissionData();
			new MyWriteDataTask(DetailActivity.this).execute(missionData);
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

    // //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // ////////////////////////////////// init titlebar
    // /////////////////////////////////////////////////////////////
    // //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    // //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // ////////////////////////////////// init openDialogMethod
    // /////////////////////////////////////////
    // //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
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
	String date = dateButton.getText().toString();
	String[] dates = date.split("-");
	int year = Integer.parseInt(dates[0]);
	int monthOfYear = Integer.parseInt(dates[1]);
	int dayOfMonth = Integer.parseInt(dates[2]);
	datePicker.init(year, monthOfYear-1, dayOfMonth,
		new OnDateChangedListener() {
		    public void onDateChanged(DatePicker view, int year,
			    int monthOfYear, int dayOfMonth) {
			dateButton.setText(year + "-" + (monthOfYear + 1) + "-"
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
		String contact = dialogContactEditText.getText().toString();
		String phone = dialogPhoneEditText.getText().toString();
		personText.setText(contact);
		phoneText.setText(phone);
		updateContactInFile(contact , phone);
		dialog.dismiss();
	    }
	});
	dialog.getWindow().setContentView(myView);
	dialog.show();
    }

    private void updateContactInFile(String contact, String phone) {
	dataBaseService.updateMyData(RW, ZC_ID, Zc_id, 
		new String[] {CONTACT , PHONE},
		new String[] {contact ,phone});
	List<Map<String, String>> listMap = dataBaseService.listMyDataMaps(RW, null);
	if(WriteDbfFile.creatDbfFile(rwPath, RW_ITEM, listMap))
	    Toast.makeText(getApplicationContext(), "联系人信息成功更新", Toast.LENGTH_LONG).show();
	else
	    Toast.makeText(getApplicationContext(), "联系人信息成功更新", Toast.LENGTH_LONG).show();
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
	final EditText dialogTotalPower = (EditText) myView.findViewById(R.id.dialog_total_power);
	final EditText dialogPeakPower = (EditText) myView.findViewById(R.id.dialog_peak);
	final EditText dialogValleyPower = (EditText) myView.findViewById(R.id.dialog_valley);
	final EditText dialogAveragePower = (EditText) myView.findViewById(R.id.dialog_average);
	final EditText dialogPositiveReactive = (EditText) myView.findViewById(R.id.dialog_positive_reactive);
	final EditText dialogNegetiveReactive = (EditText) myView.findViewById(R.id.dialog_negative_reactive);
	final EditText dialogMaxDemand = (EditText) myView.findViewById(R.id.dialog_maximum_active_demand);
	dialogConfirmButton.setOnClickListener(new OnClickListener() {
	    @Override
	    public void onClick(View v) {
		missionData.put(REACTIVE_TOTAL, dialogPositiveReactive.getText().toString());
		missionData.put(REACTIVE_REVERSE, dialogNegetiveReactive.getText().toString());
		missionData.put(MAX_NEED, dialogMaxDemand.getText().toString());
		missionData.put(ACTIVE_REVERSE_TOTAL, dialogTotalPower.getText().toString());
		missionData.put(ACTIVE_REVERSE_PEAK, dialogPeakPower.getText().toString());
		missionData.put(ACTIVE_REVERSE_VALLEY, dialogValleyPower.getText().toString());
		missionData.put(ACTIVE_REVERSE_AVERAGE, dialogAveragePower.getText().toString());
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
    private void readMachineWaitDialog() {
	// 创建自定义dialog
	waitReadMachineDialog = new Dialog(this, R.style.transparentDialog);
	final View myView = LayoutInflater.from(DetailActivity.this).inflate(
		R.layout.read_machine_wait_dialog, null);
	myView.findFocus();
	// 获取控件
	waitReadMachineDialog.setCancelable(false);
	waitReadMachineDialog.getWindow().setContentView(myView);
	waitReadMachineDialog.show();
	// 启动线程，连接失败时中断。
	readMachineThread = new Thread(new Runnable() {
	    @Override
	    public void run() {
		try {
		    Thread.sleep(20 * 1000);
		    waitReadMachineDialog.dismiss();
		    DetailActivity.this.mreadMachineHandler.sendEmptyMessage(0);
		} catch (InterruptedException e) {
		    e.printStackTrace();
		}
	    }
	});
	readMachineThread.start();
    }


    // //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // ////////////////////////////////// init openDialogMethod
    // /////////////////////////////////////////
    // //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /*
     * (non-Javadoc) 拦截返回键
     * 
     * @see android.app.Activity#onKeyDown(int, android.view.KeyEvent)
     */
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

    @Override
    protected void onDestroy() {
	if (mChatService != null)
	    mChatService.stop();
//	
//	BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
//	if (bluetoothAdapter.getState() == BluetoothAdapter.STATE_ON) 
//	    bluetoothAdapter.disable();
	super.onDestroy();
    }
    
}
