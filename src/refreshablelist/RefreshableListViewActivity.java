package refreshablelist;

import gps.GPSService;

import java.io.File;
import java.text.Format;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.baidu.platform.comapi.map.u;
import com.linuxense.javadbf.DBFReader;
import com.linuxense.javadbf.DBFWriter;

import details.DetailActivity;

import refreshablelist.RefreshableListView.OnRefreshListener;
import static stringconstant.StringConstant.*;
import DBFRW.ParseDbf2Map;
import DBFRW.WriteDbfFile;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Path;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import baidumapsdk.demo.R;
import baidumapsdk.demo.R.string;

public class RefreshableListViewActivity extends Fragment {

    private List<Map<String, String>> mItems;
    private RefreshableListView mListView;
    private TextView completeNumber;

    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
	    Bundle savedInstanceState) {
	// TODO Auto-generated method stub
	return inflater.inflate(R.layout.refresh_list, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
	// TODO Auto-generated method stub
	super.onViewCreated(view, savedInstanceState);
	// 检查dbf文件夹是否存在。不存在则退出
	if (isDirExist(dbfPath)) {
	    mItems = getItems();
	    mListView = (RefreshableListView) view.findViewById(R.id.listview);
	    completeNumber = (TextView) view
		    .findViewById(R.id.accomplish_number);
	    MyBaseAdapter myBaseAdapter = new MyBaseAdapter(getActivity(),
		    mItems);
	    mListView.setAdapter(myBaseAdapter);
	    // Callback to refresh the list
	    mListView.setOnItemClickListener(new ItemClickListener());
	    mListView.setOnRefreshListener(new OnRefreshListener() {
		@Override
		public void onRefresh(RefreshableListView listView) {
		    // TODO Auto-generated method stub
		    new NewDataTask().execute();
		}
	    });
	    updateCompleteNumber();
	}
    }

    
    private class NewDataTask extends
	    AsyncTask<Void, Void, Map<String, String>> {

	@Override
	protected Map<String, String> doInBackground(Void... params) {
	    Map<String, String> map = new HashMap<String, String>();
	    // map.put("ZC_ID", "121212");
	    map.put("CONTACT", "李狗蛋");
	    try {
		Thread.sleep(2000);
	    } catch (InterruptedException e) {
	    }

	    return map;
	}

	@Override
	protected void onPostExecute(Map<String, String> result) {
	    // mItems.add(result);
	    // This should be called after refreshing finished
	    mListView.completeRefreshing();

	    super.onPostExecute(result);
	}
    }

    class ItemClickListener implements OnItemClickListener {
	@SuppressWarnings("unchecked")
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
		long id) {
	    // TODO Auto-generated method stub
	    // HashMap<String, String> map = (HashMap<String, String>) parent
	    // .getItemAtPosition(position + 1);
	    TextView not_find = (TextView) view
		    .findViewById(R.id.item_not_find);
	    TextView savedMapTextView = (TextView) view
		    .findViewById(R.id.item_save_map);
	    TextView cate = (TextView) view.findViewById(R.id.item_categories);
	    TextView cont = (TextView) view.findViewById(R.id.item_contact);
	    TextView pho = (TextView) view.findViewById(R.id.item_phone);

	    Intent intent = new Intent();
	    if (not_find.getVisibility() == View.VISIBLE) {
		Toast.makeText(getActivity(), "未找到,请检查数据库..",
			Toast.LENGTH_SHORT).show();
		return;
	    }
	    if (cate.getText().toString().equals("01")) {
		System.out.println("this is categories 01 in RefreshList");
		intent.putExtra(CONTACT, cont.getText().toString());
		intent.putExtra(PHONE, pho.getText().toString());
	    }
	    intent.putExtra(MISSION_DETAIL, savedMapTextView.getText()
		    .toString());
	    intent.setClass(getActivity(), DetailActivity.class);
	    startActivity(intent);
	}
    }

    /**
     * 设置ListView的数据
     * 
     * @return
     */
    private List<Map<String, String>> getItems() {
//	gps2m(30.510428, 114.426294, 30.507962, 114.42796);
	 
	DataBaseService service = new MyData(getActivity());
	List<Map<String, String>> items = service.listMyDataMaps(RW, null);
	
//	WriteDbfFile.creatDbfFile(root+"/rw.dbf", RW_ITEM, items);
	ParseDbf2Map parseDbf2Map = new ParseDbf2Map();
	List<Map<String, String>> list =
		parseDbf2Map.getListMapFromDbf(gpsPath);
//		parseDbf2Map.getListMapFromDbf(root +"/"+JLFYZCXX+".dbf");
	System.out.println(list);
	
	return items;
    }

    /**
     * 此函数用来更新 已完成数量/总数量
     */
    private void updateCompleteNumber() {
	DataBaseService service = new MyData(getActivity());
	String[] seleStrings = new String[] { "已完成" };
	List<Map<String, String>> searchMap = service.viewMyData(RW, WCZT,
		seleStrings);
	Log.e("updateCompleteNumber----->", searchMap + "");
	List<Map<String, String>> tableAll = service.listMyDataMaps(RW, null);
	// WCZT=未完成 ,
	completeNumber.setText(String.format("当前已完成 %d 条/共有 %d 条",
		searchMap.size(), tableAll.size() - 1));
    }
    /**判断SD卡中目录或文件是否存在
     * @param path
     * @return
     */
    public  boolean isDirExist(String path) {
	// 如果不存在的话，则创建存储目录
	File mediaStorageDir = new File(path);
	if (!mediaStorageDir.exists()) {
	    if (!mediaStorageDir.mkdirs()) {
		Log.d("MyCameraApp", "failed to create directory");
	    }
	    new AlertDialog.Builder(getActivity())
		    .setTitle("警告")
		    .setMessage("未发现dbf文件,请放到"+root+"下")
		    .setPositiveButton("确定",
			    new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,
					int whichButton) {
				    getActivity().finish();
				}
			    }).show();
	    return false;
	} else {
	    return true;
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