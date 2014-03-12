package com.uniquestudio.refreshablelist;


import static com.uniquestudio.stringconstant.StringConstant.CONTACT;
import static com.uniquestudio.stringconstant.StringConstant.MISSION_DETAIL;
import static com.uniquestudio.stringconstant.StringConstant.PHONE;
import static com.uniquestudio.stringconstant.StringConstant.RW;
import static com.uniquestudio.stringconstant.StringConstant.WCZT;
import static com.uniquestudio.stringconstant.StringConstant.dbfPath;
import static com.uniquestudio.stringconstant.StringConstant.dnbxxPath;
import static com.uniquestudio.stringconstant.StringConstant.rwPath;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.uniquestudio.R;
import com.uniquestudio.details.DetailActivity;
import com.uniquestudio.refreshablelist.RefreshableListView.OnRefreshListener;

public class RefreshableListViewActivity extends Fragment {

    private List<Map<String, String>> mItems;
    private RefreshableListView mListView;
    private TextView completeNumber;
    private MyBaseAdapter myBaseAdapter;
    private ProgressDialog progressDialog;
    private boolean receiverIsRegisite = false;
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
	mListView = (RefreshableListView) view.findViewById(R.id.listview);
	completeNumber = (TextView) view.findViewById(R.id.accomplish_number);
	mItems = new ArrayList<Map<String,String>>();
	// 检查dbf文件夹是否存在。不存在则退出
	if (isDBFExist()) {
	    mItems = getItems();
	    myBaseAdapter = new MyBaseAdapter(getActivity(),
		    mItems);
	    mListView.setAdapter(myBaseAdapter);
	    updateCompleteNumber();
	    // Callback to refresh the list
	    mListView.setOnItemClickListener(new ItemClickListener());
	    mListView.setOnRefreshListener(new OnRefreshListener() {
		@Override
		public void onRefresh(RefreshableListView listView) {
		    // TODO Auto-generated method stub
		    new NewDataTask().execute();
		}
	    });
	    //注册广播
	    IntentFilter filter = new IntentFilter();         
	    filter.addAction("com.unique.refresh");  
	    //此处表示该接收器会刷新列表的广播请求
	    getActivity().registerReceiver(refreshReceiver, filter); 
	    
	}
    }
    
    
    /**
     * @author luo
     *下拉刷新的异步更新数据处理
     */
    private class NewDataTask extends
	    AsyncTask<Void, Void, Map<String, String>> {

	@Override
	protected Map<String, String> doInBackground(Void... params) {
	    Map<String, String> map = new HashMap<String, String>();
	    try {
		Thread.sleep(1000);
	    } catch (InterruptedException e) {
	    }

	    return map;
	}

	@Override
	protected void onPostExecute(Map<String, String> result) {
	    // mItems.add(result);
	    // This should be called after refreshing finished
	    myBaseAdapter.notifyDataSetChanged();
	    mListView.completeRefreshing();

	    super.onPostExecute(result);
	}
    }

    /**
     * @author luo
     *任务列表的点击响应
     */
    class ItemClickListener implements OnItemClickListener {
	@SuppressWarnings("unchecked")
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
		long id) {
		progressDialog = new ProgressDialog(getActivity());
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progressDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);  
		progressDialog.show();
		
		
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

    
    @Override
    public void onResume() {
	if(mListView != null) {
	    System.out.println("refreshlistview onresume");
	    if(mItems != null || mItems.size() != 0) {
		mItems.clear();
		mItems.addAll(getItems());
	    }
	    if(myBaseAdapter != null)
		myBaseAdapter.notifyDataSetChanged();
	}
	if(progressDialog != null) {
	    if(progressDialog.isShowing())
		progressDialog.dismiss();
	}
	super.onResume();
    }

    @Override
    public void onDestroy() {
	if(refreshReceiver != null && receiverIsRegisite)
	    getActivity().unregisterReceiver(refreshReceiver);
	System.out.println("refreshableListActivity destroy");
	super.onDestroy();
    }
    
    /**
     * 设置ListView的数据
     * @return
     */
    private List<Map<String, String>> getItems() {
	DataBaseService service = new MyData(getActivity());
	List<Map<String, String>> items = service.listMyDataMaps(RW, null);
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
		searchMap.size(), tableAll.size() ));
    }
    
    
    /**判断SD卡中目录或文件是否存在
     * @param path
     * @return
     */
    public  boolean isDBFExist() {
	// 如果不存在的话，则创建存储目录
	File mediaStorageDir = new File(dbfPath);
	if (mediaStorageDir.exists()) {
	    File rwDBFFile = new File(rwPath);
	    File dnbxxDBFFile = new File(dnbxxPath);
	    if(rwDBFFile.exists() && dnbxxDBFFile.exists())
		return true;
	} else if (!mediaStorageDir.mkdirs()) {
		Log.d("MyCameraApp", "failed to create directory");
	}
	    new AlertDialog.Builder(getActivity())
	    .setTitle("警告")
	    .setMessage("未发现dbf文件,请放到"+dbfPath+"下")
	    .setPositiveButton("确定",
		    new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog,
				int whichButton) {
//			    getActivity().finish();
			}
		    }).show();
		return false;
    }
    
    /**
     * @author luo
     *接收广播,刷新任务列表
     */
    
    private BroadcastReceiver refreshReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            System.out.println("i need refresh listView");
	    mItems.clear();
	    mItems.addAll(getItems());
	    if(myBaseAdapter != null)
	  	 myBaseAdapter.notifyDataSetChanged();
	    receiverIsRegisite = true;
        }
    };
    
}