package com.uniquestudio.slidingmenu;

import static com.uniquestudio.stringconstant.StringConstant.*;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.uniquestudio.R;
import com.uniquestudio.gps.GPSService;
import com.uniquestudio.refreshablelist.DBOpenHelper;

public class SampleListFragment extends Fragment  implements OnItemClickListener{
	private ListView mListView;
	private  FragmentManager mFragmentManager;
	public SampleListFragment() {
	    
	}
	public SampleListFragment(FragmentManager fragmentManager){
		mFragmentManager = fragmentManager;
	}
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
	    Bundle savedInstanceState) {
	
	View view = inflater.inflate(R.layout.list, null);
	mListView = (ListView) view.findViewById(R.id.menu_frag_list);
	ArrayList<String> mItems = new ArrayList<String>();
	mItems.add("任务列表");
	mItems.add("DBF文件夹");
	mItems.add("更改AK值");
	mItems.add("查看设备号");
	mItems.add("更新任务");
	mItems.add("完全退出");
	SampleAdapter adapter = new SampleAdapter(getActivity());
	for (int i = 0; i < mItems.size(); i++) {
	    adapter.add(new SampleItem(mItems.get(i),
		    android.R.drawable.ic_menu_search));
	    mListView.setAdapter(adapter);
	}
	mListView.setOnItemClickListener(this);
	
	return view;
    }

    public void onActivityCreated(Bundle savedInstanceState) {
	super.onActivityCreated(savedInstanceState);
    }

    private class SampleItem {
	public String tag;
	public int iconRes;

	public SampleItem(String tag, int iconRes) {
	    this.tag = tag;
	    this.iconRes = iconRes;
	}
    }

    public class SampleAdapter extends ArrayAdapter<SampleItem> {

	public SampleAdapter(Context context) {
	    super(context, 0);
	}

	public View getView(int position, View convertView, ViewGroup parent) {
	    if (convertView == null) {
		convertView = LayoutInflater.from(getContext()).inflate(
			R.layout.row, null);
	    }
	    ImageView icon = (ImageView) convertView
		    .findViewById(R.id.row_icon);
	    icon.setImageResource(getItem(position).iconRes);
	    TextView title = (TextView) convertView
		    .findViewById(R.id.row_title);
	    title.setText(getItem(position).tag);

	    return convertView;
	}

    }
    
	private IChangeFragment iChangeFragment;
	
	public void setChangeFragmentListener(IChangeFragment listener){
		iChangeFragment = listener;
	}
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
	switch (position) {
	case 0:
	case 1:
	case 2:
	case 3:
	    //切换界面的方法接口
		if(iChangeFragment != null)
			iChangeFragment.changeFragment(position);
	    break;
	case 4:
	    new MyUpdateDBTask(getActivity()).execute();
	    break;
	case 5:
	    //完全退出，杀掉传坐标后台进程
	    Intent stopServiceIntent = new Intent(getActivity(), GPSService.class);
	    getActivity().stopService(stopServiceIntent);
	    
	    Intent intent = new Intent(Intent.ACTION_MAIN);
	    intent.addCategory(Intent.CATEGORY_HOME);
	    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	    startActivity(intent);
	    android.os.Process.killProcess(android.os.Process.myPid());
	    break;
	default:
	    break;
	}
    }
    
    class MyUpdateDBTask extends AsyncTask<Void, Void, Boolean>{
	    ProgressDialog mPd;
	    Activity mContext;
	    
	    public MyUpdateDBTask(Activity context) {
		this.mContext = context;
	    }
	@Override
	protected void onPreExecute() {
		// 弹出等待框
		mPd = new ProgressDialog(mContext);
		mPd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		mPd.setMessage("任务更新中..");
		mPd.setCancelable(false);
		mPd.show();
	    super.onPreExecute();
	}
	
	@Override
	protected Boolean doInBackground(Void... arg0) {
	    //清除数据
	    DBOpenHelper dbOpenHelper = new DBOpenHelper(getActivity());
	    	SQLiteDatabase db = dbOpenHelper.getWritableDatabase(); 
		DBOpenHelper.CreatTableSQL(RW_ITEM, RW,rwPath,db);
		DBOpenHelper.CreatTableSQL(DNBXX_ITEM, DNBXX,dnbxxPath,db);
		DBOpenHelper.CreatTableSQL(DNBXYSJ_ITEM, DNBXYSJ,dnbxysjPath,db);
		DBOpenHelper.CreatTableSQL(DNBXCSSXX_ITEM, DNBXCSSXX, dnbxcssxxPath,db);
		DBOpenHelper.CreatTableSQL(DNBDGLXX_ITEM, DNBDGLXX, dnbdglxxPath,db);
		DBOpenHelper.CreatTableSQL(JLFYZCXX_ITEM, JLFYZCXX,jlfyzcxxPath, db);
		DBOpenHelper.CreatTableSQL(GPS_ITEM, GPS,gpsPath,db);
		DBOpenHelper.CreatTableSQL(BZQJ_ITEM, BZQJ,bzqjPath,db);
	    return true;
	}
	
	@Override
	protected void onPostExecute(Boolean result) {
		// 关闭的等待框
		mPd.dismiss();
		final boolean flag = result;
		String message = flag ? "更新成功" : "更新时出错,请重试";
		String button = flag ? "确定" : "确定";
		new AlertDialog.Builder(mContext)
		.setTitle("更新成功")
		.setMessage(message)
		.setPositiveButton(button,
			new DialogInterface.OnClickListener() {
			    @Override
			    public void onClick(DialogInterface dialog,
				    int which) {
				if(flag) {
				  //发送广播事件通知更新  
				    Intent intentToRefresh = new Intent("com.unique.refresh");  
				    mContext.sendBroadcast(intentToRefresh);
				}
			    }
			}).show();
	    super.onPostExecute(result);
	}
    }
}
