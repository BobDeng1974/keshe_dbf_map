package baidumapsdk.demo;

import java.util.List;

import slidingmenu.LeftAndRightActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.cloud.BoundSearchInfo;
import com.baidu.mapapi.cloud.CloudListener;
import com.baidu.mapapi.cloud.CloudManager;
import com.baidu.mapapi.cloud.CloudPoiInfo;
import com.baidu.mapapi.cloud.CloudSearchResult;
import com.baidu.mapapi.cloud.DetailSearchResult;
import com.baidu.mapapi.cloud.NearbySearchInfo;
import com.baidu.mapapi.map.ItemizedOverlay;
import com.baidu.mapapi.map.LocationData;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationOverlay;
import com.baidu.mapapi.map.OverlayItem;
import com.baidu.mapapi.map.MyLocationOverlay.LocationMode;
import com.baidu.mapapi.map.PopupClickListener;
import com.baidu.mapapi.map.PopupOverlay;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import slidingmenu.*;

/**
 * 此demo用来展示如何结合定位SDK实现定位，并使用MyLocationOverlay绘制定位位置 同时展示如何使用自定义图标绘制并点击时弹出泡泡
 * 
 */
public class LocationOverlayDemo extends Activity implements CloudListener {
    private enum E_BUTTON_TYPE {
        LOC, COMPASS, FOLLOW
    }

    private E_BUTTON_TYPE mCurBtnType;

    // 定位相关
    LocationClient mLocClient;

    LocationData locData = null;

    public MyLocationListenner myListener = new MyLocationListenner();

    // 定位图层
    locationOverlay myLocationOverlay = null;

    // 地图相关，使用继承MapView的MyLocationMapView目的是重写touch事件实现泡泡处理
    // 如果不处理touch事件，则无需继承，直接使用MapView即可
    MapView mMapView = null; // 地图View

    private MapController mMapController = null;

    // UI相关
    OnCheckedChangeListener radioButtonListener = null;

    Button requestLocButton = null;

    boolean isRequest = false;// 是否手动触发请求定位

    boolean isFirstLoc = true;// 是否首次定位

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 初始化application
        DemoApplication app = (DemoApplication) this.getApplication();
        if (app.mBMapManager == null) {
            app.mBMapManager = new BMapManager(this);
            app.mBMapManager.init(DemoApplication.strKey,
                    new DemoApplication.MyGeneralListener());
        }
        setContentView(R.layout.activity_locationoverlay);
        CloudManager.getInstance().init(LocationOverlayDemo.this);

        mapViewInit();
        locationClientInit();
        setListener();

    }

    // =========================================== Init
    // ===================================================

    private void mapViewInit() {
        // TODO Auto-generated method stub
        CharSequence titleLable = "Map";
        setTitle(titleLable);
        requestLocButton = (Button) findViewById(R.id.button1);
        mCurBtnType = E_BUTTON_TYPE.LOC;
        // 地图初始化
        mMapView = (MapView) findViewById(R.id.bmapView);
        mMapController = mMapView.getController();
        mMapView.getController().setZoom(14);
        mMapView.getController().enableClick(true);
        mMapView.setBuiltInZoomControls(true);
    }

    public void locationClientInit() {
        // TODO Auto-generated method stub
        // 定位初始化
        mLocClient = new LocationClient(this);
        locData = new LocationData();
        mLocClient.registerLocationListener(myListener);
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);// 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setScanSpan(1000);
        mLocClient.setLocOption(option);
        mLocClient.start();

        // 定位图层初始化
        myLocationOverlay = new locationOverlay(mMapView);
        // 设置定位数据
        myLocationOverlay.setData(locData);
        // 添加定位图层
        mMapView.getOverlays().add(myLocationOverlay);
        myLocationOverlay.enableCompass();
        // 修改定位数据后刷新图层生效
        mMapView.refresh();
    }

    private void setListener() {
        // TODO Auto-generated method stub
        OnClickListener btnClickListener = new OnClickListener() {
            public void onClick(View v) {
                // 手动定位请求
                requestLocClick();
            }
        };
        requestLocButton.setOnClickListener(btnClickListener);

        RadioGroup group = (RadioGroup) this.findViewById(R.id.radioGroup);
        radioButtonListener = new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.rect_search) {
                    NearbySearchInfo info = new NearbySearchInfo();
                    info.ak = "DD1580bc446609f4dcfb2d20728b681a";
                    info.geoTableId = 36466;// 32038;
                    // info.filter = "time:20130801,20130810";
                    String locationString = locData.longitude + ","
                            + locData.latitude;
                    info.location = locationString;
                    Log.e("fuck", locationString);
                    info.radius = 30000;
                    CloudManager.getInstance().nearbySearch(info);
                }
                if (checkedId == R.id.near_search) {
                    BoundSearchInfo info = new BoundSearchInfo();
                    info.ak = "DD1580bc446609f4dcfb2d20728b681a";
                    info.geoTableId = 36466;
                    String boundString = getBoundString(locData, 0.0025);
                    info.bound = boundString;
                    CloudManager.getInstance().boundSearch(info);
                }
            }
        };
        group.setOnCheckedChangeListener(radioButtonListener);
    }

    /**
     * @param locationData
     * @param range
     *            矩形搜索区域的范围
     * @return boundstring 生成的矩形区域字符串
     */
    private String getBoundString(LocationData locationData, double range) {
        double left = locationData.longitude - range;
        double right = locationData.longitude + range;
        double top = locationData.latitude - range;
        double bottom = locationData.latitude + range;
        String boundStirng = left + "," + bottom + "," + right + "," + top;
        return boundStirng;
    }

    /**
     * 手动触发一次定位请求
     */
    public void requestLocClick() {
        isRequest = true;
        mLocClient.requestLocation();
        Toast.makeText(LocationOverlayDemo.this, "正在定位……", Toast.LENGTH_SHORT)
                .show();
    }

    // =========================================== Init
    // ===================================================
    // ========================================= Location
    // =================================================

    /**
     * 定位SDK监听函数
     */
    public class MyLocationListenner implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            if (location == null)
                return;

            locData.latitude = location.getLatitude();
            locData.longitude = location.getLongitude();
            // 如果不显示定位精度圈，将accuracy赋值为0即可
            locData.accuracy = location.getRadius();
            locData.direction = location.getDerect();
            // 更新定位数据
            myLocationOverlay.setData(locData);
            // 更新图层数据执行刷新后生效
            mMapView.refresh();
            // 是手动触发请求或首次定位时，移动到定位点
            if (isRequest || isFirstLoc) {
                // 移动地图到定位点
                Log.d("LocationOverlay", "receive location, animate to it");
                mMapController.animateTo(new GeoPoint(
                        (int) (locData.latitude * 1e6),
                        (int) (locData.longitude * 1e6)));
                isRequest = false;
            }
            // 首次定位完成
            isFirstLoc = false;
        }

        public void onReceivePoi(BDLocation poiLocation) {
            if (poiLocation == null) {
                return;
            }
        }
    }

    // 继承MyLocationOverlay重写dispatchTap实现点击处理
    public class locationOverlay extends MyLocationOverlay {

        public locationOverlay(MapView mapView) {
            super(mapView);
            // TODO Auto-generated constructor stub
        }

        @Override
        protected boolean dispatchTap() {
            // TODO Auto-generated method stub
            // 处理点击事件,弹出泡泡
            Toast.makeText(getApplicationContext(), "我的位置", Toast.LENGTH_SHORT)
                    .show();
            return true;
        }

    }

    @Override
    protected void onPause() {
        mMapView.onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        mMapView.onResume();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        // 退出时销毁定位
        if (mLocClient != null)
            mLocClient.stop();
        mMapView.destroy();
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mMapView.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    // ========================================= Location
    // =================================================
    // ======================================== CloudSearch
    // ===============================================
    @Override
    public void onGetDetailSearchResult(DetailSearchResult arg0, int arg1) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onGetSearchResult(CloudSearchResult result, int error) {
        // TODO Auto-generated method stub
        if (result != null && result.poiList != null
                && result.poiList.size() > 0) {
            CloudOverlay poiOverlay = new CloudOverlay(this, mMapView);
            poiOverlay.setData(result.poiList);
            // mMapView.getOverlays().clear();
            mMapView.getOverlays().add(poiOverlay);
            mMapView.refresh();
            mMapView.getController().animateTo(
                    new GeoPoint((int) (result.poiList.get(0).latitude * 1e6),
                            (int) (result.poiList.get(0).longitude * 1e6)));
        }
    }

    // ======================================== CloudSearch
    // ===============================================
}

class CloudOverlay extends ItemizedOverlay {

    List<CloudPoiInfo> mLbsPoints;

    Activity mContext;

    public CloudOverlay(Activity context, MapView mMapView) {
        super(null, mMapView);
        mContext = context;
    }

    public void setData(List<CloudPoiInfo> lbsPoints) {
        if (lbsPoints != null) {
            mLbsPoints = lbsPoints;
        }
        for (CloudPoiInfo rec : mLbsPoints) {
            GeoPoint pt = new GeoPoint((int) (rec.latitude * 1e6),
                    (int) (rec.longitude * 1e6));
            OverlayItem item = new OverlayItem(pt, rec.title, rec.address);
            Drawable marker1 = this.mContext.getResources().getDrawable(
                    R.drawable.icon_marka);
            item.setMarker(marker1);
            addItem(item);
        }
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        // TODO Auto-generated method stub
        return super.clone();
    }

    @Override
    protected boolean onTap(int arg0) {
        CloudPoiInfo item = mLbsPoints.get(arg0);
        Toast.makeText(mContext, item.title, Toast.LENGTH_SHORT).show();
        
        Intent intent  = new Intent(mContext , LeftAndRightActivity.class);
        mContext.startActivity(intent);
        return super.onTap(arg0);
    }

}
