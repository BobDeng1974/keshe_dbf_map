package slidingmenu;

import static stringconstant.StringConstant.BZQJ;
import static stringconstant.StringConstant.BZQJ_ITEM;
import static stringconstant.StringConstant.DNBDGLXX;
import static stringconstant.StringConstant.DNBDGLXX_ITEM;
import static stringconstant.StringConstant.DNBXCSSXX;
import static stringconstant.StringConstant.DNBXCSSXX_ITEM;
import static stringconstant.StringConstant.DNBXX;
import static stringconstant.StringConstant.DNBXX_ITEM;
import static stringconstant.StringConstant.DNBXYSJ;
import static stringconstant.StringConstant.DNBXYSJ_ITEM;
import static stringconstant.StringConstant.GPS;
import static stringconstant.StringConstant.GPS_ITEM;
import static stringconstant.StringConstant.JLFYZCXX;
import static stringconstant.StringConstant.JLFYZCXX_ITEM;
import static stringconstant.StringConstant.RW;
import static stringconstant.StringConstant.RW_ITEM;
import static stringconstant.StringConstant.bzqjPath;
import static stringconstant.StringConstant.dnbdglxxPath;
import static stringconstant.StringConstant.dnbxcssxxPath;
import static stringconstant.StringConstant.dnbxxPath;
import static stringconstant.StringConstant.dnbxysjPath;
import static stringconstant.StringConstant.gpsPath;
import static stringconstant.StringConstant.jlfyzcxxPath;
import static stringconstant.StringConstant.rwPath;

import gps.GPSService;

import java.util.ArrayList;

import refreshablelist.DBOpenHelper;
import refreshablelist.RefreshableListView;

import baidumapsdk.demo.R;
import android.app.Activity;
import android.app.ActivityManager;
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
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

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
		if(iChangeFragment != null)
			iChangeFragment.changeFragment(position);
	    break;
	case 4:
	    new MyUpdateDBTask(getActivity()).execute();
	    break;
	case 5:
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
		String message = flag ? "请重新进入应用" : "写入时出错,请重试";
		String button = flag ? "确定" : "确定";
		new AlertDialog.Builder(mContext)
		.setTitle("更新成功")
		.setMessage(message)
		.setPositiveButton(button,
			new DialogInterface.OnClickListener() {
			    @Override
			    public void onClick(DialogInterface dialog,
				    int which) {
				if(flag)
				    mContext.finish();
			    }
			}).show();
	    super.onPostExecute(result);
	}
    }
}
