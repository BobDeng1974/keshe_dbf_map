package GPS;

import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.app.PendingIntent;
import android.app.PendingIntent.CanceledException;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

public class GPSManager {
    /** Called when the activity is first created. */
    private LocationManager locationManager;
    private String provider;
    private Location location;
    private Address address;
    private Activity mActivity;

    public GPSManager(Activity activity) {
	this.mActivity = activity;
    }

    public List<Address> getGPS() {
	// 获取LocationManager服务
	locationManager = (LocationManager) mActivity
		.getSystemService(Context.LOCATION_SERVICE);
	// 获取Location Provider
	getProvider();
	// 如果未设置位置源，打开GPS设置界面
	openGPS();
	// 获取位置
	location = locationManager.getLastKnownLocation(provider);
	// 显示位置信息到文字标签
	return updateWithNewLocation(location);
	// // 注册监听器locationListener，第2、3个参数可以控制接收gps消息的频度以节省电力。第2个参数为毫秒，
	// // 表示调用listener的周期，第3个参数为米,表示位置移动指定距离后就调用listener
	// locationManager.requestLocationUpdates(provider, 2000, 10,
	// locationListener);
    }

    // 判断是否开启GPS，若未开启，打开GPS设置界面
    private void openGPS() {
	if (locationManager
		.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)
		|| locationManager
			.isProviderEnabled(android.location.LocationManager.NETWORK_PROVIDER)) {
	    Toast.makeText(mActivity, "位置源已设置..", Toast.LENGTH_SHORT).show();
	    return;
	}
	Toast.makeText(mActivity, "位置源未设置,请开启GPS！", Toast.LENGTH_SHORT).show();
	// 强制帮用户打开GPS
	locationManager.setTestProviderEnabled("gps", true);
	Intent GPSIntent = new Intent();
	GPSIntent.setClassName("com.android.settings",
		"com.android.settings.widget.SettingsAppWidgetProvider");
	GPSIntent.addCategory("android.intent.category.ALTERNATIVE");
	GPSIntent.setData(Uri.parse("custom:3"));
	try {
	    PendingIntent.getBroadcast(mActivity, 0, GPSIntent, 0).send();
	} catch (CanceledException e) {
	    e.printStackTrace();
	}
    }

    // 获取Location Provider
    private void getProvider() {
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

    // Gps消息监听器
    private final LocationListener locationListener = new LocationListener() {
	// 位置发生改变后调用
	public void onLocationChanged(Location location) {
	    updateWithNewLocation(location);
	}

	// provider被用户关闭后调用
	public void onProviderDisabled(String provider) {
	    updateWithNewLocation(null);
	}

	// provider被用户开启后调用
	public void onProviderEnabled(String provider) {
	}

	// provider状态变化时调用
	public void onStatusChanged(String provider, int status, Bundle extras) {
	}
    };

    // Gps监听器调用，处理位置信息
    private List<Address> updateWithNewLocation(Location location) {
	String latLongString;
	if (location != null) {
	    double lat = location.getLatitude();
	    double lng = location.getLongitude();
	    latLongString = "纬度:" + lat + "\n经度:" + lng;
	} else {
	    latLongString = "无法获取地理信息";
	}
	
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
		Geocoder gc = new Geocoder(mActivity, Locale.getDefault());
		result = gc.getFromLocation(location.getLatitude(),
			location.getLongitude(), 1);
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	}
//	    System.out.println("result size------->"+result.size());
	    return result;
	
    }
}