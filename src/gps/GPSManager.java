package gps;

import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.app.PendingIntent;
import android.app.PendingIntent.CanceledException;
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
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

public class GPSManager {
    /** Called when the activity is first created. */
    private LocationManager locationManager;
    private String provider;
    private Address address;
    private Context mContext;

    public GPSManager(Context context) {
	this.mContext = context;
	// 获取LocationManager服务
	this.locationManager = (LocationManager) mContext
		.getSystemService(Context.LOCATION_SERVICE);
	// 获取Location Provider
	getProvider();
	// 如果未设置位置源，打开GPS设置界面
	openGPS();
	locationManager.addGpsStatusListener(new Listener() {
	    @Override
	    public void onGpsStatusChanged(int event) {
		switch (event) {
		case GpsStatus.GPS_EVENT_STARTED:
		    System.out.println("GpsStatus-------->GPS_EVENT_STARTED");
		    break;

		case GpsStatus.GPS_EVENT_STOPPED:
		    System.out.println("GpsStatus-------->GPS_EVENT_STOPPED");
		    break;

		case GpsStatus.GPS_EVENT_FIRST_FIX:
		    System.out.println("GpsStatus-------->GPS_EVENT_FIRST_FIX");
		    break;

		case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
		    break;
		}
	    }
	});
    }

    public void setRequestLocationUpdates(LocationListener myLocationListener) {
	// // 注册监听器locationListener，第2、3个参数可以控制接收gps消息的频度以节省电力。第2个参数为毫秒，
	// // 表示调用listener的周期，第3个参数为米,表示位置移动指定距离后就调用listener
	if (isGPSEnabled()) {
	    System.out.println("setRequestLocationUpdates------------------->GPS");
	    locationManager.requestLocationUpdates(
		    LocationManager.GPS_PROVIDER, 500, 0, myLocationListener);
	}
	else if (isNetworkEnabled()) {
	    System.out.println("setRequestLocationUpdates------------------->Network");
	    locationManager.requestLocationUpdates(
		    LocationManager.NETWORK_PROVIDER, 500, 0,
		    myLocationListener);
	}else {
	    Log.e("GPSManager", "noProviderIsEnable");
	}
    }

    public void removeLocationListener(LocationListener locationListener) {
	if (locationManager != null) {
	    System.out
		    .println("removeLocationListener------------------->true");
	    locationManager.removeUpdates(locationListener);
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
	if (locationManager != null && provider != null)
	    return locationManager.getLastKnownLocation(provider);
	else
	    return null;
    }

    // 判断是否开启GPS，若未开启，打开GPS设置界面
    private void openGPS() {
	if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
		|| locationManager
			.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
	    Toast.makeText(mContext, "获取GPS中..", Toast.LENGTH_SHORT).show();
	    return;
	}
	Toast.makeText(mContext, "位置源未设置,请开启GPS！", Toast.LENGTH_SHORT).show();
	// // 强制帮用户打开GPS
	// Intent settingsIntent = new Intent(
	// Settings.ACTION_LOCATION_SOURCE_SETTINGS);
	// mContext.startActivity(settingsIntent);
//	 locationManager.setTestProviderEnabled("gps", true);
	 Intent GPSIntent = new Intent();
	 GPSIntent.setClassName("com.android.settings",
	 "com.android.settings.widget.SettingsAppWidgetProvider");
	 GPSIntent.addCategory("android.intent.category.ALTERNATIVE");
	 GPSIntent.setData(Uri.parse("custom:3"));
	 try {
	 PendingIntent.getBroadcast(mContext, 0, GPSIntent, 0).send();
	 } catch (CanceledException e) {
	 e.printStackTrace();
	 }
    }

    // 获取Location Provider
    public void getProvider() {
	// 构建位置查询条件
	Criteria criteria = new Criteria();
	// 查询精度：高
	criteria.setAccuracy(Criteria.ACCURACY_FINE);
	// 是否查询海拨：否
	criteria.setAltitudeRequired(false);
	// 是否查询方位角:否
	criteria.setBearingRequired(false);
	// 是否允许付费：是
	criteria.setCostAllowed(true);
	// 电量要求：低
	criteria.setPowerRequirement(Criteria.POWER_LOW);
	// 返回最合适的符合条件的provider，第2个参数为true说明,如果只有一个provider是有效的,则返回当前provider
	provider = locationManager.getBestProvider(criteria, true);
	System.out.println("provider--------------->" + provider);
    }

    // Gps监听器调用，处理位置信息
    public void getNewLocation(Location location) {
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
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	}
	// System.out.println("result size------->"+result.size());
	return result;

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
	boolean enable = false;
	TelephonyManager telephonyManager = (TelephonyManager) mContext
		.getSystemService(Context.TELEPHONY_SERVICE);
	if (telephonyManager != null) {
	    if (telephonyManager.getNetworkType() != TelephonyManager.NETWORK_TYPE_UNKNOWN) {
		enable = true;
		    Log.e("GPSManager", "isTelephonyEnabled");
	    }
	}

	return enable;
    }

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
}