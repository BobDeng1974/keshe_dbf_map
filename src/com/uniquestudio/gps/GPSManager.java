package com.uniquestudio.gps;

import java.util.List;
import java.util.Map;

import static com.uniquestudio.stringconstant.StringConstant.*;

import com.uniquestudio.DBFRW.WriteDbfFile;
import com.uniquestudio.refreshablelist.DataBaseService;
import com.uniquestudio.refreshablelist.MyData;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.PendingIntent;
import android.app.PendingIntent.CanceledException;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.provider.Settings;
import android.util.Log;
import android.view.WindowManager;
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
    private int minMeters = 220;

    public GPSManager(Context context) {
	this.mContext = context;
	// 获取LocationManager服务
	this.locationManager = (LocationManager) mContext
		.getSystemService(Context.LOCATION_SERVICE);
	// 获取Location Provider
	getProvider();
	// 如果未设置位置源，打开GPS设置界面
	CheckCurrentNetWork();
    }

    public  void updateProvider() {
	this.getProvider();
	    GpsLog.writeLogFile("坐标来源："+this.provider);
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
    private void CheckCurrentNetWork() {
	if (isNetworkEnabled()) {
	    Toast.makeText(mContext, "获取GPS中..", Toast.LENGTH_SHORT).show();
	    return;
	}
	if(isFirstIn) {
	    isFirstIn = false;
	    OpenNetwork();
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
       else if(!isGPSEnabled() && !isNetworkEnabled()) {
	   this.provider = LocationManager.NETWORK_PROVIDER;
       }
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
	    latLongString = "获取的location为null";
	}
	Log.e("GPS----------->", latLongString);
	return;
    }
    
    /**	由GPS定位坐标获取地址
     * @return	转换后的地址
     */
    public Location WriteLocation(String cons_no) {
	Location location = this.getMyLastKnownLocation();
	if(location != null) {
	    //写入gps信息
	    DataBaseService dataBaseService = new MyData(mContext);
	    if(dataBaseService.viewMyData(GPS, CONS_NO,new String[]{cons_no}).size() == 0) {
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
	return location;
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
//	    Settings.Secure.setLocationProviderEnabled( mContext.getContentResolver(), LocationManager.GPS_PROVIDER, true);
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
    public boolean OpenNetwork() {
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

