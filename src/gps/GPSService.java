package gps;

import java.util.HashMap;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import httpclient.LocationHttpClient;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class GPSService extends Service {
    private GPSService gpsService = null;
    private GPSManager gpsManager = null;
    private LocationListener locationListener = null;
    private HashMap<String, String> paramMap = null;
    private RequestParams requestParams = null;

    @Override
    public void onCreate() {
	super.onCreate();
	this.gpsService = this;
	System.out.println("GpsService---------->onCreat");
	paramMap = new HashMap<String, String>();
	// 创建表传递的参数
	// paramMap.put("ak", "DD1580bc446609f4dcfb2d20728b681a");
	// paramMap.put("name", "staff_15527597559");
	// paramMap.put("geotype", "1");
	// paramMap.put("is_published", "1");

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
	gpsManager = new GPSManager(gpsService);
	locationListener = new myLocationListener();
	gpsManager.setRequestLocationUpdates(locationListener);
	System.out.println("GpsService---------->onStart");
	// //创建表table
	// LocationHttpClient.post("geodata/v2/geotable/create", requestParams,
	// new JsonHttpResponseHandler() {
	// @Override
	// public void onSuccess(JSONArray response) {
	// super.onSuccess(response);
	// JSONObject jsonObject;
	// try {
	// jsonObject = response.getJSONObject(0);
	// Log.e("json-response------------>", jsonObject.getString("id"));
	// } catch (JSONException e) {
	// e.printStackTrace();
	// }
	// }
	// });
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
	    gpsManager.getNewLocation(location);
	    paramMap.put("ak", "DD1580bc446609f4dcfb2d20728b681a");
	    paramMap.put("geotable_id", "45290");
	    paramMap.put("time", System.currentTimeMillis() + "");
	    paramMap.put("coord_type", "1");
	    if (location != null) {
		paramMap.put("longitude", location.getLongitude() + "");
		paramMap.put("latitude", location.getLatitude() + "");
	    }
	    requestParams = new RequestParams(paramMap);
	    Log.e("json----------->", requestParams+"");
	    LocationHttpClient.post("geodata/v2/poi/create", requestParams,
		    new JsonHttpResponseHandler() {
			/* (non-Javadoc)
			 * @see com.loopj.android.http.JsonHttpResponseHandler#onFailure(java.lang.Throwable, org.json.JSONObject)
			 */
			@Override
			public void onFailure(Throwable e,
				JSONObject errorResponse) {
			    Log.e("json-------------->", "onFailure"+"-->"+errorResponse);
			    super.onFailure(e, errorResponse);
			}

			@Override
		        public void onSuccess(int statusCode, Header[] headers,
		                org.json.JSONObject response) {
			    if (statusCode == 200) {
				Log.e("json-------------->", "onSuccess" + "-->"
					+ response);
				Toast.makeText(gpsService, "成功上传一个坐标..", Toast.LENGTH_SHORT).show();
			    }
			}
			
		    });
	    
	}

	@Override
	public void onProviderDisabled(String arg0) {
	    System.out.println("GpsService---------->onProviderDisabled");
	    Toast.makeText(gpsService, "获GPS失败，请打开网络连接..", Toast.LENGTH_LONG)
		    .show();
	}

	@Override
	public void onProviderEnabled(String arg0) {
	    System.out.println("GpsService---------->onProviderEnabled");
	    // gpsManager.removeLocationListener(locationListener);
	    // gpsManager.setRequestLocationUpdates(locationListener);
	}

	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
	    System.out.println("GpsService---------->onStatusChanged");
	}
    }
}
