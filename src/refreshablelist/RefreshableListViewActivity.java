package refreshablelist;

import java.io.File;
import java.text.Format;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.baidu.platform.comapi.map.u;
import com.linuxense.javadbf.DBFWriter;

import details.DetailActivity;

import refreshablelist.RefreshableListView.OnRefreshListener;
import static stringconstant.StringConstant.*;
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
	isDirExist("dbf", "", false);
	mItems = getItems();
	mListView = (RefreshableListView) view.findViewById(R.id.listview);
	completeNumber = (TextView) view.findViewById(R.id.accomplish_number);
	creatDataBase();
	MyBaseAdapter myBaseAdapter = new MyBaseAdapter(getActivity(), mItems);
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
	ParseDbf2Map parseDbf2Map = new ParseDbf2Map();
	List<Map<String, String>> items = parseDbf2Map
		.getListMapFromDbf(rwPath);
	items = items.subList(1, items.size());
	return items;
    }

    // //DBF文件的数据添加
    // public void addItem() throws Exception {
    // DBFWriter dbfwriter = new DBFWriter(new File(rwPath));
    // Object[] obj=new Object[3];
    // // obj[0]="442899999811111111";
    // // obj[1]="123";
    // // obj[2]="广州软件";
    // dbfwriter.addRecord(obj);
    // }
    private void updateCompleteNumber() {
	DataBaseService service = new MyData(getActivity());
	String[] seleStrings = new String[] { "已完成" };
	List<Map<String, String>> searchMap = service.viewMyData(RW, WCZT,
		seleStrings);
	Log.e("updateCompleteNumber----->", searchMap + "");
	List<Map<String, String>> tableAll = service.listMyDataMaps(RW, null);
	int length = service.fetchTableLength(RW);
	// WCZT=未完成 ,
	completeNumber.setText(String.format("当前已完成 %d 条/共有 %d 条",
		searchMap.size(), tableAll.size() - 1));
    }

    private void creatDataBase() {
	creatDBTable(RW, RW_ITEM, rwPath);
	creatDBTable(DNBXX, DNBXX_ITEM, dnbxxPath);
	creatDBTable(DNBXYSJ, DNBXYSJ_ITEM, dnbxysjPath);
    }

    private void creatDBTable(String tableName, String[] tableItem,
	    String filePath) {
	ParseDbf2Map parseDbf2Map = new ParseDbf2Map();
	DataBaseService service = new MyData(getActivity());
	String[] params = new String[tableItem.length];
	// 创建table
	List<Map<String, String>> Items = parseDbf2Map
		.getListMapFromDbf(filePath);
	Items = Items.subList(1, Items.size());
	Log.e("creatDBTable--------->Paramitems------>", Items+"");
	for (int i = 0; i < Items.size(); i++) {
	    Map<String, String> map = Items.get(i);
	    for (int y = 0; y < tableItem.length; y++) {
		params[y] = map.get(tableItem[y]).trim();
	    }
	    boolean flag = service.addMyData(tableName, tableItem, params);
	    Log.i(tableName + " add item", "--->" + flag);
	}
    }
    /* 
     * 判断SD卡中目录或文件是否存在 
     */  
    public void isDirExist(String dir , String fileName , Boolean isFile){  
	String SDCardRoot = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator;  
	File file =  null;
	if (isFile) {
	    file = new File(SDCardRoot + dir + File.separator + fileName);  
	}else {
	    file = new File(SDCardRoot + dir + File.separator);  
	}
        if(!file.exists())  {
            new AlertDialog.Builder(getActivity())
	    .setTitle("警告")
	    .setMessage("未发现dbf文件,请检查SDCard!")
	    .setPositiveButton("确定",
		    new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog,
				int whichButton) {
		            getActivity().finish();			
		            }
		    }).show();
        }
//            file.mkdir();  //如果不存在则创建  
    }  
}