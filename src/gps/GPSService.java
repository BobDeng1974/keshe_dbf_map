package gps;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

public class GPSService extends Service {
    private GPSService gpsService = null;
    private GPSManager gpsManager = null;
    private LocationListener locationListener = null;

    @Override
    public void onCreate() {
	super.onCreate();
	this.gpsService = this;
	System.out.println("GpsService---------->onCreat");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
	gpsManager = new GPSManager(gpsService);
	locationListener = new myLocationListener();
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
	    gpsManager.getNewLocation(location);
	}

	@Override
	public void onProviderDisabled(String arg0) {
	    System.out.println("GpsService---------->onProviderDisabled");
//	    gpsManager.getNewLocation(null);
	    gpsManager.removeLocationListener(locationListener);
	    gpsManager.setRequestLocationUpdates(locationListener);
	}

	@Override
	public void onProviderEnabled(String arg0) {
	    System.out.println("GpsService---------->onProviderEnabled");
	}

	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
	    System.out.println("GpsService---------->onStatusChanged");
	}
    }
}
