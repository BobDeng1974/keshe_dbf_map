package com.uniquestudio.gps;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.uniquestudio.httpclient.LocationHttpClient;
import com.uniquestudio.slidingmenu.LeftAndRightActivity;
import com.uniquestudio.stringconstant.StringConstant;

import android.R;
import android.R.string;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo.State;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

public class GPSService extends Service {
    private String myAK;
    private String imei = "-1";
    private PendingIntent pIntent;
    private boolean isFirstStart = true;
    private boolean isFirstGPS = true;

    private int geoTableID = -1;
    private long lastUpdateTime = System.currentTimeMillis();//上次上传的时间
    private String lastPosTime = "" ;//上次获得的坐标点附带的时间属性（字符串）
    private BDLocation lastPostGPS = null;//最新获得的有效GPS上传点
    private List<RequestParams> linkedGPSList = Collections.synchronizedList(new LinkedList<RequestParams>());//存放有效GPS点的数组
//    private boolean stopTimerThread = false;
    private GPSService gpsService = null;
    
    public LocationClient mLocationClient = null;
    public BDLocationListener myLocationListener = new MyLocationListener();
//    private GPSManager gpsManager = null;
//    private LocationListener locationListener = null;
    private HashMap<String, String> paramMap = null;
    private RequestParams requestParams = null;

    private boolean lastNetworkState = true;// 记录上此网络变化的状态
    public static final String NET_ACTION = "android.net.conn.CONNECTIVITY_CHANGE";
    public static final String ACTION = "com.uniquestudio.gpsService";
    /**
     * 最久4分钟上传一次
     */
    public  int postMinute;
    /**
     * 触发坐标上传的半径大小
     */
    private int minMeters;

//    Handler handler = new Handler() {
//	@Override
//	public void handleMessage(Message msg) {
//	    super.handleMessage(msg);
//	    switch (msg.what) {
//	    case 1:
//		httpPostToUpdateMyLocation(null);
//		break;
//	    case 2:
//		removeCallbacks(timerRunnable);
//		break;
//	    default:
//		break;
//	    }
////	    handler.removeCallbacks(Time);
//	}
//    };

    @Override
    public void onCreate() {
	super.onCreate();
	this.gpsService = this;
	// 注册网络状态监听
//	IntentFilter netFilter = new IntentFilter(NET_ACTION);
//	registerReceiver(netConnectReceiver, netFilter);

	System.out.println("GpsService---------->onCreat");
	mLocationClient = new LocationClient(getApplicationContext());
	mLocationClient.registerLocationListener(myLocationListener);
	setLocationClientOption(mLocationClient);
	if(mLocationClient != null ) {
	    if(!mLocationClient.isStarted())
		mLocationClient.start();
	    mLocationClient.requestLocation();
	    }
	else
	    System.out.println("client not started");
	
	
	// 更新GPS坐标相关
//	gpsManager = new GPSManager(gpsService);
//	locationListener = new myLocationListener();
	
//	gpsManager.setRequestLocationUpdates(locationListener);
//	new Thread(timerRunnable).start();
//	stopTimerThread = false;
	// 获取AK值
	SharedPreferences sharedPreferences = gpsService.getSharedPreferences(
		StringConstant.PREFS_NAME, Context.MODE_PRIVATE);
	myAK = sharedPreferences.getString("AK", StringConstant.DEF_AK);
	postMinute = sharedPreferences.getInt("postMinute", StringConstant.DEF_MIN_MINUTES);
	minMeters = sharedPreferences.getInt("minMeters", StringConstant.DEF_MIN_DISTANCE);
	// 获取手机设备号
	TelephonyManager telephonyManager = (TelephonyManager) this
		.getSystemService(Context.TELEPHONY_SERVICE);
	imei = telephonyManager.getDeviceId();

	httpGetTableId();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
	 if (isFirstStart) {
	 // 为了防止service被手机杀掉,在OnDestory中要stopForeground
	 Notification notification = new Notification(
	 com.uniquestudio.R.drawable.ic_launcher, "GPS后台服务启动..",
	 System.currentTimeMillis());
	 pIntent = PendingIntent.getService(this, 0, intent, 0);
	 notification.setLatestEventInfo(this, "电能表校验", "GPS后台服务正在运行..",
	 pIntent);
	
	 // 让该service前台运行，避免手机休眠时系统自动杀掉该服务
	 // 如果 id 为 0 ，那么状态栏的 notification 将不会显示。
	 startForeground(startId, notification);
	 isFirstStart = false;
	 }
	System.out.println("GpsService---------->onStart");

	// 使用这个返回值时，如果在执行完onStartCommand后，服务被异常kill掉，系统会自动重启该服务，并将Intent的值传入。
	return Service.START_REDELIVER_INTENT;
    }

    @Override
    public IBinder onBind(Intent arg0) {
	return null;
    }

    @Override
    public boolean onUnbind(Intent intent) {
	return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
	System.out.println("GpsService---------->onDestroy");
//	if (gpsManager != null && locationListener != null)
//	    gpsManager.removeLocationListener(locationListener);
//	
//	stopTimerThread = true;
//	unregisterReceiver(netConnectReceiver);
	if(mLocationClient != null && mLocationClient.isStarted()) {
	    mLocationClient.unRegisterLocationListener(myLocationListener);
	    mLocationClient.stop();
	    mLocationClient = null;
	}
	 stopForeground(true);
	super.onDestroy();
    }

//    class myLocationListener implements LocationListener {
//	@Override
//	public void onLocationChanged(Location location) {
//	    System.out.println("GpsService---------->onLocationChanged");
//	    gpsManager.printNewLocation(location);
//	    httpPostToUpdateMyLocation(location);
//	}
//
//	@Override
//	public void onProviderDisabled(String arg0) {
//	    // updateLocationListener();
//	    System.out.println("GpsService---------->onProviderDisabled");
//	    creatNotification("GPS设置未打开", "点此打开\"基于网络的位置服务\"", 0);
//	    GpsLog.writeLogFile("坐标提供者disabled");
//	    Toast.makeText(gpsService, "GPS位置服务未打开", Toast.LENGTH_LONG).show();
//	}
//
//	@Override
//	public void onProviderEnabled(String arg0) {
//	    System.out.println("GpsService---------->onProviderEnabled");
//	    GpsLog.writeLogFile("坐标提供者enabled");
//	    updateLocationListener();
//	}
//
//	@Override
//	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
//	    System.out.println("GpsService---------->onStatusChanged");
//	}
//    }

//    public void updateLocationListener() {
//	if(gpsManager != null && locationListener != null) {
//	gpsManager.updateProvider();
//	gpsManager.removeLocationListener(locationListener);
//	gpsManager.setRequestLocationUpdates(locationListener);
//	}
//    }

    /**
     * @param location
     * 上传坐标及其参数的生成
     */
    public void httpPostToUpdateMyLocation(BDLocation location) {
	// 如果查詢的到对应的表
	if (geoTableID != -1) {
	    Log.e("httpPostToUpdateMyLocation", "生成参数");
	    paramMap = new HashMap<String, String>();
	    paramMap.put("ak", myAK);
	    paramMap.put("geotable_id", geoTableID + "");
	    paramMap.put("time", System.currentTimeMillis() + "");
	    paramMap.put("coord_type", 1 + "");
	    paramMap.put("title", null);
//	    if (location == null) {
//		location = gpsManager.getMyLastKnownLocation();
		if (location == null) {
		    creatNotification("GPS设置未打开", "点此打开\"基于网络的位置服务\"", 0);
		    Toast.makeText(gpsService, "GPS位置服务未打开", Toast.LENGTH_LONG)
			    .show();
		    GpsLog.writeLogFile("上传坐标：GPS获取失败");
		    return;
		}
//	    }
	    
	    GpsLog.writeLogFile("坐标："+location.getLongitude()+","+location.getLatitude()+"上传中");
	    paramMap.put("longitude", location.getLongitude() + "");
	    paramMap.put("latitude", location.getLatitude() + "");
	    requestParams = new RequestParams(paramMap);
	    
	    linkedGPSList.add(requestParams);
	    lastPostGPS = location;
	    
	    httpPostGPS();//上传坐标点
	}
    }

    /**
     * 上传时的post操作
     */
    private void httpPostGPS() {
	if(linkedGPSList == null || linkedGPSList.size() == 0 ) 
	    return;
	    System.out.println("取点上传");
	    System.out.println(linkedGPSList.size() +":"+linkedGPSList.get(0));
	LocationHttpClient.post("geodata/v2/poi/create", linkedGPSList.get(0),
		    new JsonHttpResponseHandler() {
			@Override
			public void onFailure(Throwable e,
				JSONObject errorResponse) {
			    Log.e("json-------------->", "upload onFailure");
			    creatNotification("坐标上传error..");
			    GpsLog.writeLogFile("上传坐标：连接失败");
			    super.onFailure(e, errorResponse);
			}

			@Override
			public void onSuccess(int statusCode, Header[] headers,
				org.json.JSONObject response) {
			    if (statusCode == 200) {
				Log.e("json-------------->", "onSuccess"
					+ "-->" + response);
				// 检查是否返回的是上传成功
				try {
				    if (response.has("message")) {
					String message = "";
					message = response.getString("message");

					if (message.equals("成功")) {
					    lastUpdateTime = System.currentTimeMillis();
					    linkedGPSList.remove(0);
					    httpPostGPS();//继续上传其他点
					    creatNotification("成功上传一个坐标.");
					} else {
					    creatNotification("坐标上传失败.");
					}
				    } else {
					creatNotification("坐标上传失败.");
				    }
				} catch (Exception e) {
				    e.printStackTrace();
				}
			    } else {
				creatNotification("坐标上传失败.");
			    }
			    GpsLog.writeLogFile("上传坐标：" + response);
			}

		    });
    }
    
    /**
     * 查询表格ID
     */
    public void httpGetTableId() {
	if (geoTableID != -1)
	    return;
	HashMap<String, String> paramTable = new HashMap<String, String>();
	// 创建表传递的参数
	paramTable.put("ak", myAK);
	paramTable.put("name", "staff_" + imei);
	requestParams = new RequestParams(paramTable);
	LocationHttpClient.get("geodata/v2/geotable/list", requestParams,
		new JsonHttpResponseHandler() {
		    @Override
		    // public void onSuccess(int statusCode, Header[] headers,
		    // org.json.JSONObject response) {
		    public void onSuccess(int statusCode, JSONObject response) {
			super.onSuccess(statusCode, response);
			try {
			    if (response.has("geotables")) {
				JSONArray jsonArray = response
					.getJSONArray("geotables");
				if (jsonArray.length() != 0) {
				    for (int i = 0; i < jsonArray.length(); i++) {
					JSONObject jsonObject = jsonArray
						.getJSONObject(i);
					if (jsonObject.has("name")) {
					    String name = jsonObject
						    .getString("name");
					    System.out.println("name------->"
						    + name);
					    if (name.equals("staff_" + imei)) {
						geoTableID = jsonObject
							.getInt("id");
						System.out.println("id------->"
							+ geoTableID);
						GpsLog.writeLogFile("查询web端表格：成功——id："
							+ geoTableID);
						break;
					    }
					}
				    }
				}
			    }
			    if (geoTableID == -1) {
				Toast.makeText(gpsService, "未在服务器上查询到表格!",
					Toast.LENGTH_LONG).show();
				GpsLog.writeLogFile("查询web端表格：未找到");
			    }
			} catch (JSONException e) {
			    System.out
				    .println("get tableid exception ___________");
			    e.printStackTrace();
			}
		    }

		    @Override
		    public void onFailure(Throwable e, JSONObject errorResponse) {
			System.out.println("failure");
			GpsLog.writeLogFile("查询web端表格：连接错误");
			super.onFailure(e, errorResponse);
		    }
		});

    }

    /**
     * Timer
     * 
     * @author luo 记录两次上传直接的间隔，如果超过一定分钟，则手动上传
     */
//    private  Runnable timerRunnable = new  Runnable() {
//	public void run() {
//	    if(!stopTimerThread) {
//		if ((System.currentTimeMillis() - lastTimer) > postMinute * 60 * 1000)
//		    handler.sendEmptyMessage(1);
//		handler.postDelayed(timerRunnable, 1*60*1000);
//	    }else {
//		handler.sendEmptyMessage(2);
//	    }
//	}
//    };

    private void creatNotification(String message) {
	creatNotification(message, null, -1);
    }

    private void creatNotification(String message, String secondMessage,
	    int action) {
	int Notification_ID_BASE = 110;
	NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
	// 新建状态栏通知
	Notification baseNF = new Notification();
	// 设置通知在状态栏显示的图标
	baseNF.icon = com.uniquestudio.R.drawable.ic_launcher;
	// 通知时在状态栏显示的内容
	baseNF.tickerText = message;
	baseNF.flags |= Notification.FLAG_AUTO_CANCEL;
	// 点击该通知时执行页面跳转
	Intent notificationIntent = null;
	if (action == -1)
	    notificationIntent = new Intent(gpsService,
		    LeftAndRightActivity.class);
	else
	    notificationIntent = new Intent(
		    Settings.ACTION_LOCATION_SOURCE_SETTINGS);
	PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
		notificationIntent, 0);

	SimpleDateFormat sDateFormat = new SimpleDateFormat("HH:mm:ss");
	String currentTime = sDateFormat.format(new java.util.Date());
	if (secondMessage == null)
	    secondMessage = currentTime;
	baseNF.setLatestEventInfo(gpsService, message, secondMessage,
		contentIntent);
	nm.notify(Notification_ID_BASE, baseNF);
    }

//    BroadcastReceiver netConnectReceiver = new BroadcastReceiver() {
//	State wifiState = null;
//	State mobileState = null;
//
//	@Override
//	public void onReceive(Context context, Intent intent) {
//	    if (NET_ACTION.equals(intent.getAction())) {
//		// 获取手机的连接服务管理器，这里是连接管理器类
//		ConnectivityManager cm = (ConnectivityManager) context
//			.getSystemService(Context.CONNECTIVITY_SERVICE);
//		wifiState = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
//			.getState();
//		mobileState = cm
//			.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
//			.getState();
//
//		if (wifiState != null && mobileState != null
//			&& State.CONNECTED != wifiState
//			&& State.CONNECTED == mobileState && !lastNetworkState) {
//		    lastNetworkState = true;
//		    updateLocationListener();
//		    httpGetTableId();
//		    GpsLog.writeLogFile("手机网络连接成功");
//		} else if (wifiState != null && mobileState != null
//			&& State.CONNECTED == wifiState
//			&& State.CONNECTED != mobileState && !lastNetworkState) {
//		    lastNetworkState = true;
//		    httpGetTableId();
//		    updateLocationListener();
//		    GpsLog.writeLogFile("无线网络连接成功");
//		} else if (wifiState != null && mobileState != null
//			&& State.CONNECTED != wifiState
//			&& State.CONNECTED != mobileState && lastNetworkState) {
//		    GpsLog.writeLogFile("手机没有任何网络");
//		    lastNetworkState = false;
//		}
//	    }
//	}
//    };

    private void setLocationClientOption(LocationClient client) {
	LocationClientOption option = new LocationClientOption();
	option.setOpenGps(true);
	option.setLocationMode(LocationMode.Hight_Accuracy);
	option.setPriority(LocationClientOption.GpsFirst);
	option.setProdName("uniquestudio");
	option.setCoorType("wgs84");//WGS-84  1   bd09ll  3
	option.setAddrType("all");  
	option.setScanSpan(15 * 1000);
	option.setNeedDeviceDirect(false);
	option.disableCache(true);
	client.setLocOption(option);
    }
    
    public class MyLocationListener implements BDLocationListener{

	@Override
	public void onReceiveLocation(BDLocation location) {
	    if (location == null) {
		GpsLog.writeLogFile("无法获取GPS");
	          return ;
	    }
	    if(isFirstGPS) {
		//第一次启动获取的GPS直接上传，不用检测时间与距离
		httpPostToUpdateMyLocation(location);
		isFirstGPS  = false;
		return;
	    }
	    //LocType      61 ： GPS定位结果     161： 表示网络定位结果
	      StringBuffer sb = new StringBuffer(256);
	      sb.append("time : ");
	      sb.append(location.getTime());
	      sb.append("\nerror code : ");
	      sb.append(location.getLocType());
	      sb.append("\nlatitude : ");
	      sb.append(location.getLatitude());
	      sb.append("\nlontitude : ");
	      sb.append(location.getLongitude());
//	      sb.append("\nradius : ");
//	      sb.append(location.getRadius());
	      if (location.getLocType() == BDLocation.TypeGpsLocation){
	           sb.append("\nspeed : ");
	           sb.append(location.getSpeed());
	           sb.append("\nsatellite : ");
	           sb.append(location.getSatelliteNumber());
	       } else if (location.getLocType() == BDLocation.TypeNetWorkLocation){
	           sb.append("\naddr : ");
	           sb.append(location.getAddrStr());
	       }
	 
//	     System.out.println(sb.toString());
	     //时间 若停止，则会上传上次获得的点。比较时间即可判断是否为上个点。
	    if(location.getLocType() == 61 || location.getLocType()==161) {
		if (lastPosTime.equals(location.getTime())) {
		    if (System.currentTimeMillis() - lastUpdateTime > postMinute * 60 * 1000) {
			System.out.println("超过规定时间没动");
			httpPostToUpdateMyLocation(location);
		    }else {
			System.out.println("当前未移动");
		    }
		}else if(getDistance(lastPostGPS, location) > minMeters ){
		    //移动距离满足条件，上传
		    System.out.println("移动范围大于最小距离");
		    httpPostToUpdateMyLocation(location);
		}else {
		    System.out.println("距离太近，避免显示太密集，舍弃");
		}
	    }else {
		GpsLog.writeLogFile("定位失败");
	    }
	    lastPosTime = location.getTime();
	}
	@Override
	public void onReceivePoi(BDLocation arg0) {}
    }
    /** 
     * 计算两点之间距离 
     * @param start 
     * @param end 
     * @return 米 
     */  
    public double getDistance(BDLocation start,BDLocation end){  
	if( start == null) {
	    return minMeters + 1;
	}
	
        double lat1 = (Math.PI/180)*start.getLatitude();  
        double lat2 = (Math.PI/180)*end.getLatitude();  
          
        double lon1 = (Math.PI/180)*start.getLongitude();  
        double lon2 = (Math.PI/180)*end.getLongitude();  
          
        //地球半径  
        double R = 6371;  
          
        //两点间距离 km，如果想要米的话，结果*1000就可以了  
        double d =  Math.acos(Math.sin(lat1)*Math.sin(lat2)+Math.cos(lat1)*Math.cos(lat2)*Math.cos(lon2-lon1))*R;  
        System.out.println("相距 ：" + d*1000 + "米");
        return d*1000;  
    }  
}
