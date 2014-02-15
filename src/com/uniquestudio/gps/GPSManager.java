package com.uniquestudio.gps;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static com.uniquestudio.stringconstant.StringConstant.*;

import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.uniquestudio.DBFRW.WriteDbfFile;
import com.uniquestudio.details.DetailActivity;
import com.uniquestudio.refreshablelist.DataBaseService;
import com.uniquestudio.refreshablelist.MyData;
import com.uniquestudio.stringconstant.SpinnerString;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.PendingIntent;
import android.app.PendingIntent.CanceledException;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.GpsStatus;
import android.location.GpsStatus.Listener;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

public class GPSManager {
    /** Called when the activity is first created. */
    private LocationManager locationManager;
    private String provider = "";
    private Address address;
    private Context mContext;
    private Boolean isFirstIn = true;
    /**
     * 触发坐标上传的半径大小
     */
    private int minMeters = 120;

    public GPSManager(Context context) {
	this.mContext = context;
	// 获取LocationManager服务
	this.locationManager = (LocationManager) mContext
		.getSystemService(Context.LOCATION_SERVICE);
	// 获取Location Provider
	getProvider();
	// 如果未设置位置源，打开GPS设置界面
	openGPS();
    }

    public  void updateProvider() {
	this.getProvider();
    }
    
    public void setRequestLocationUpdates(LocationListener myLocationListener) {
	// // 注册监听器locationListener，第2、3个参数可以控制接收gps消息的频度以节省电力。第2个参数为毫秒，
	// // 表示调用listener的周期，第3个参数为米,表示位置移动指定距离后就调用listener
	if (!this.provider.equals("")) {
	    System.out.println("setRequestLocationUpdates------------------->"+this.provider);
	    this.locationManager.requestLocationUpdates(
		    this.provider, 0, minMeters, myLocationListener);
	} else {
	    Log.e("GPSManager", "noProviderIsEnable");
	}
    }

    public void removeLocationListener(LocationListener locationListener) {
	if (locationManager != null) {
	    System.out
		    .println("removeLocationListener------------------->true");
	    this.locationManager.removeUpdates(locationListener);
	}
    }

    /**
     * 获取定位获得的点转换得到的地址
     * 
     * @return
     */
    public List<Address> getAddresses(Location newLocation) {
	// 显示位置信息到文字标签
	return this.getNewAddresses(newLocation);
    }

    public Location getMyLastKnownLocation() {
	Location lastKnownLocation = null;
	if (this.locationManager != null && this.provider != null) {
	    lastKnownLocation = locationManager.getLastKnownLocation(this.provider);
	    System.out.println(provider);
	}
	if (lastKnownLocation == null) {
            lastKnownLocation =locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            System.out.println("当前方式(GPS或网络)无法获取GPS，切换成网络方式获取");
	}
	return lastKnownLocation;
    }

    // 判断是否开启GPS，若未开启，打开GPS设置界面
    private void openGPS() {
	if (isNetworkEnabled()) {
	    Toast.makeText(mContext, "获取GPS中..", Toast.LENGTH_SHORT).show();
	    return;
	}
	if(isFirstIn) {
	    isFirstIn = false;
	    CheckNetwork();
	}
    }

    // 获取Location Provider
    public void getProvider() {
	// 如果只是Use GPS satellites勾选，即指允许使用GPS定位
        if (isGPSEnabled() && !isNetworkEnabled())                 
            this.provider = LocationManager.GPS_PROVIDER;
       // 如果只是Use wireless networks勾选，即只允许使用网络定位。
        else if(!isGPSEnabled() && isNetworkEnabled())
            this.provider = LocationManager.NETWORK_PROVIDER;
        // 如果二者都勾选，优先使用GPS,因为GPS定位更精确。
       else if (isGPSEnabled() && isNetworkEnabled())
	   this.provider = LocationManager.GPS_PROVIDER;
        
	System.out.println("provider--------------->" + this.provider);
    }

    // Gps监听器调用，处理位置信息
    public void printNewLocation(Location location) {
	String latLongString;
	if (location != null) {
	    double lat = location.getLatitude();
	    double lng = location.getLongitude();
	    latLongString = "纬度:" + lat + "\n经度:" + lng;
	} else {
	    latLongString = "无法获取地理信息";
	}
	Log.e("GPS----------->", latLongString);
	return;
    }

    private List<Address> getNewAddresses(Location location) {
	return getAddressbyGeoPoint(location);
    }

    // 获取地址信息
    private List<Address> getAddressbyGeoPoint(Location location) {
	List<Address> result = null;
	// 先将Location转换为GeoPoint
	// GeoPoint gp=getGeoByLocation(location);
	try {
	    if (location != null) {
		// 获取Geocoder，通过Geocoder就可以拿到地址信息
		Geocoder gc = new Geocoder(mContext, Locale.getDefault());
		result = gc.getFromLocation(location.getLatitude(),
			location.getLongitude(), 1);
		System.out.println(result);
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	}
	// System.out.println("result size------->"+result.size());
	return result;

    }
    /**	由GPS定位坐标获取地址
     * @return	转换后的地址
     */
    public String getNowPosition(String cons_no) {
	String nowPosition = "";
	Location location = this.getMyLastKnownLocation();
	List<Address> gps = this.getAddresses(location);
	// System.out.println("GPS------------>" + gps);
	if(location != null) {
	    if (gps == null || gps.size() == 0)
		nowPosition = "获取GPS成功,解析地址失败";
	    else {
		Log.e("GPS--------->Address----->", gps.size()+"");
		Address address = gps.get(0);
		nowPosition = address.getAddressLine(0);
	    }
	    //写入gps信息
	    DataBaseService dataBaseService = new MyData(mContext);
	    if(dataBaseService.listMyDataMaps(GPS, null).size() == 0) {
		//无对应数据则新建
		ContentValues cv = new ContentValues();
		cv.put(CONS_NO, cons_no);
		cv.put(LATITUDE, location.getLatitude());
		cv.put(LONGITUDE, location.getLongitude());
		dataBaseService.addMyData(GPS, cv);
	    }else {
		//有对应数据则更新
		dataBaseService.updateMyData(GPS, CONS_NO, cons_no,
			new String[] {LATITUDE , LONGITUDE}, 
			new String[] {Double.toString(location.getLatitude()),Double.toString(location.getLongitude())});
	    }
	    List<Map<String, String>> listMap = dataBaseService.listMyDataMaps(GPS, null);
	    if(WriteDbfFile.creatDbfFile(gpsPath, GPS_ITEM, listMap))
		Toast.makeText(mContext, "成功写入GPS信息", Toast.LENGTH_LONG).show();
	} else
	    Toast.makeText(mContext, "无法获取,请检查网络状况..",Toast.LENGTH_LONG).show();
	return nowPosition;
    }
    
    public GeoPoint getGeoPoint(JSONObject jsonObject) {
        Double lon = new Double(0);
        Double lat = new Double(0);
        try {
            lon = ((JSONArray) jsonObject.get("results")).getJSONObject(0)
                    .getJSONObject("geometry").getJSONObject("location")
                    .getDouble("lng");
            lat = ((JSONArray) jsonObject.get("results")).getJSONObject(0)
                    .getJSONObject("geometry").getJSONObject("location")
                    .getDouble("lat");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new GeoPoint((int) (lat * 1E6), (int) (lon * 1E6));
    }
    /**
     * 判断GPS是否开启
     * 
     * @return
     */
    public boolean isGPSEnabled() {
	if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
	    Log.e("GPSManager", "isGPSEnabled");
	    return true;
	} else {
	    return false;
	}
    }

    /**
     * 判断Network是否开启(包括移动网络和wifi)
     * 
     * @return
     */
    public boolean isNetworkEnabled() {
	return (isWIFIEnabled() || isTelephonyEnabled());
    }

    /**
     * 判断移动网络是否开启
     * 
     * @return
     */
    public boolean isTelephonyEnabled() {
	try {
	    ConnectivityManager connectivity = (ConnectivityManager) mContext
		    .getSystemService(Context.CONNECTIVITY_SERVICE);
	    if (connectivity != null) {

		NetworkInfo info = connectivity.getActiveNetworkInfo();
		if (info != null && info.isConnected()) {

		    if (info.getState() == NetworkInfo.State.CONNECTED) {
			Log.e("GPSManager", "isTelephonyEnabled");
			return true;
		    }
		}
	    }
	} catch (Exception e) {
	    return false;
	}
	return false;
    }

    // return enable;
    // }

    /**
     * 判断wifi是否开启
     */
    public boolean isWIFIEnabled() {
	boolean enable = false;
	WifiManager wifiManager = (WifiManager) mContext
		.getSystemService(Context.WIFI_SERVICE);
	if (wifiManager.isWifiEnabled()) {
	    enable = true;
	    Log.e("GPSManager", "isWIFIEnabled");
	}
	return enable;
    }

    // 没有网络的时候跳转到设置界面
    public boolean CheckNetwork() {
	boolean flag = false;
	ConnectivityManager cwjManager = (ConnectivityManager) mContext
		.getSystemService(Context.CONNECTIVITY_SERVICE);
	if (cwjManager.getActiveNetworkInfo() != null)
	    flag = cwjManager.getActiveNetworkInfo().isAvailable();// 如果得到的network可用
	else {
	    Builder b = new AlertDialog.Builder(mContext).setTitle("没有可用的网络")
		    .setMessage("请开启GPRS或WIFI");
	    b.setPositiveButton("设置", new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog, int whichButton) {
		    Intent mIntent = new Intent();;
		    if( android.os.Build.VERSION.SDK_INT > 13 ){    
			mIntent.setAction(android.provider.Settings.ACTION_SETTINGS);}
		    else {
			mIntent.setAction(android.provider.Settings.ACTION_WIRELESS_SETTINGS);
			}
		    mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);  
		    mContext.startActivity(mIntent);
		}
	    }).setNeutralButton("取消", new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog, int whichButton) {
		    dialog.cancel();
		}
	    });
	    AlertDialog mDialog=b.create();  
	    mDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
	    mDialog.show();
	}
	return flag;
    }
}

//this.locationManager.addGpsStatusListener(new Listener() {
//@Override
//public void onGpsStatusChanged(int event) {
//	switch (event) {
//	case GpsStatus.GPS_EVENT_STARTED:
//	    System.out.println("GpsStatus-------->GPS_EVENT_STARTED");
//	    provider = LocationManager.GPS_PROVIDER;
//	    break;
//
//	case GpsStatus.GPS_EVENT_STOPPED:
//	    provider = LocationManager.NETWORK_PROVIDER;
//	    System.out.println("GpsStatus-------->GPS_EVENT_STOPPED");
//	    break;
//
//	case GpsStatus.GPS_EVENT_FIRST_FIX:
//	    System.out.println("GpsStatus-------->GPS_EVENT_FIRST_FIX");
//	    break;
//
//	case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
//	    break;
//	}
//}
//});