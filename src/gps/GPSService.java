package gps;

import java.util.HashMap;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import stringconstant.StringConstant;

import baidumapsdk.demo.R.id;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import httpclient.LocationHttpClient;
import android.R.string;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

public class GPSService extends Service {
    private String myAK;
    private String imei = "-1";
    private int geoTableID = -1;
    private long lastTimer = 0;
    private GPSService gpsService = null;
    private GPSManager gpsManager = null;
    private LocationListener locationListener = null;
    private HashMap<String, String> paramMap = null;
    private RequestParams requestParams = null;
    private int timerMinute = 2;//最久2分钟上传一次
    
    Handler handler = new Handler() {
	@Override
	public void handleMessage(Message msg) {
	    super.handleMessage(msg);
	    switch (msg.what) {
	    case 1:
		httpPostToUpdateMyLocation(null);
		break;
	    case 2:
		gpsManager.updateProvider();
		gpsManager.removeLocationListener(locationListener);
		gpsManager.setRequestLocationUpdates(locationListener);
		break;
	    default:
		break;
	    }
	}
    };

    @Override
    public void onCreate() {
	super.onCreate();
	this.gpsService = this;
	System.out.println("GpsService---------->onCreat");
	locationListener = new myLocationListener();
	new Thread(new TimerThread()).start();
	//获取AK值
	SharedPreferences sharedPreferences = gpsService.getSharedPreferences(StringConstant.PREFS_NAME, Context.MODE_PRIVATE);
	myAK = sharedPreferences.getString("AK", StringConstant.DEF_AK);
	//获取手机设备号
	TelephonyManager telephonyManager = (TelephonyManager) this
		.getSystemService(Context.TELEPHONY_SERVICE);
	imei = telephonyManager.getDeviceId();
	System.out.println(imei);
	
	paramMap = new HashMap<String, String>();
	// 创建表传递的参数
	paramMap.put("ak", myAK);
	paramMap.put("name", "staff_" + imei);
	requestParams = new RequestParams(paramMap);
	LocationHttpClient.get("geodata/v2/geotable/list", requestParams,
		new JsonHttpResponseHandler() {
		    @Override
		    public void onSuccess(int statusCode, JSONObject response) {
			super.onSuccess(statusCode, response);
			try {
			    JSONArray jsonArray = response
				    .getJSONArray("geotables");
			    if (jsonArray.length() != 0) {
				for (int i = 0; i < jsonArray.length(); i++) {
				    JSONObject jsonObject = jsonArray
					    .getJSONObject(i);
				    String name = jsonObject.getString("name");
					System.out.println("name------->"+name);
				    if (name.equals("staff_"+imei)) {
					geoTableID = (Integer) jsonObject
						.get("id");
					System.out.println("id------->"+geoTableID);
					break;
				    }
				}
			    }
			} catch (JSONException e) {
			    e.printStackTrace();
			}
			if (geoTableID == -1)
			    Toast.makeText(gpsService, "未在服务器上查询到表格!",
				    Toast.LENGTH_LONG).show();
		    }

		    @Override
		    public void onFailure(Throwable e, JSONObject errorResponse) {
			super.onFailure(e, errorResponse);
			System.out.println("failure");
		    }
		});

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
	gpsManager = new GPSManager(gpsService);
	gpsManager.setRequestLocationUpdates(locationListener);
	System.out.println("GpsService---------->onStart");
	return super.onStartCommand(intent, flags, startId);
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
	if (gpsManager != null && locationListener != null)
	    gpsManager.removeLocationListener(locationListener);
	super.onDestroy();
    }

    class myLocationListener implements LocationListener {
	@Override
	public void onLocationChanged(Location location) {
	    System.out.println("GpsService---------->onLocationChanged");
	    gpsManager.printNewLocation(location);
	    httpPostToUpdateMyLocation(location);
	}

	@Override
	public void onProviderDisabled(String arg0) {
		handler.sendEmptyMessage(2);
	    System.out.println("GpsService---------->onProviderDisabled");
	    Toast.makeText(gpsService, "获GPS失败，请打开网络连接..", Toast.LENGTH_LONG)
		    .show();
	}

	@Override
	public void onProviderEnabled(String arg0) {
	    gpsManager.updateProvider();

	    System.out.println("GpsService---------->onProviderEnabled");
	    // gpsManager.removeLocationListener(locationListener);
	    // gpsManager.setRequestLocationUpdates(locationListener);
	}

	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
	    System.out.println("GpsService---------->onStatusChanged");
	}
    }

    public void httpPostToUpdateMyLocation(Location location) {
	 // 如果查詢的到对应的表
	    if (geoTableID != -1) {
		Log.e("httpPostToUpdateMyLocation", "上传");
		paramMap = new HashMap<String, String>();
		paramMap.put("ak", myAK);
		paramMap.put("geotable_id", geoTableID+"");
		paramMap.put("time", System.currentTimeMillis()+"");
		paramMap.put("coord_type", 1+"");
		paramMap.put("title", null);
		if (location == null) {
		    location = gpsManager.getMyLastKnownLocation();
		}
		paramMap.put("longitude", location.getLongitude() + "");
		paramMap.put("latitude", location.getLatitude() + "");
		requestParams = new RequestParams(paramMap);
		System.out.println(paramMap);
		LocationHttpClient.post("geodata/v2/poi/create", requestParams,
			new JsonHttpResponseHandler() {
			    @Override
			    public void onFailure(Throwable e,
				    JSONObject errorResponse) {
				Log.e("json-------------->", "onFailure"
					+ "-->" + errorResponse);
				Toast.makeText(gpsService, "坐标上传error..请检查网络.",
					Toast.LENGTH_SHORT).show();
				super.onFailure(e, errorResponse);
			    }
			    @Override
			    public void onSuccess(int statusCode,
				    Header[] headers,
				    org.json.JSONObject response) {
				if (statusCode == 200) {
				    Log.e("json-------------->", "onSuccess"+ "-->" + response);
				    //检查是否返回的是上传成功
				    if (response.has("message")) {
					String message = "";
					try {
					    message = response.getString("message");
					} catch (JSONException e) {
					    e.printStackTrace();
					}
					if (message.equals("成功")) {
					    Toast.makeText(gpsService, "成功上传一个坐标..",
						    Toast.LENGTH_SHORT).show();
					}else {
					    
					}
				    }
				}
				lastTimer = System.currentTimeMillis();
			    }

			});
	    }
    }

    /**Timer
     * @author luo
     *记录两次上传直接的间隔，如果超过5分钟，则手动上传
     */
    public class TimerThread implements Runnable{
	@Override
	public void run() {
	    while(true) {
		try {
		    Thread.sleep(2*60*1000);
		    if((System.currentTimeMillis() - lastTimer) > timerMinute*60*1000)
			handler.sendEmptyMessage(1);
		    Log.e("TimerThread---->", lastTimer+"");
		} catch (InterruptedException e) {
		    e.printStackTrace();
		}
	    }
	}
    }
    
	private final double EARTH_RADIUS = 6378137.0; 
	/**测量两点间的距离
	 * @return 米为单位
	 */
	private double gps2m(double lat_a, double lng_a, double lat_b, double lng_b) {
	       double radLat1 = (lat_a * Math.PI / 180.0);
	       double radLat2 = (lat_b * Math.PI / 180.0);
	       double a = radLat1 - radLat2;
	       double b = (lng_a - lng_b) * Math.PI / 180.0;
	       double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2)
	              + Math.cos(radLat1) * Math.cos(radLat2)
	              * Math.pow(Math.sin(b / 2), 2)));
	       s = s * EARTH_RADIUS;
	       s = Math.round(s * 10000) / 10000;
	       System.out.println("距离--->"+s);
	       return s;
	    }
}
