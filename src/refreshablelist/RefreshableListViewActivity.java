package refreshablelist;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.linuxense.javadbf.DBFWriter;

import details.DetailActivity;

import refreshablelist.RefreshableListView.OnRefreshListener;
import static stringconstant.StringConstant.*;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
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

public class RefreshableListViewActivity extends Fragment {

    private List<Map<String, String>> mItems;
    private RefreshableListView mListView;

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
	mItems = getItems();
	mListView = (RefreshableListView) view.findViewById(R.id.listview);
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
	creatDataBase();
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
//	    mItems.add(result);
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
	    TextView not_find = (TextView)view.findViewById(R.id.item_not_find);
	    TextView savedMapTextView = (TextView) view
		    .findViewById(R.id.item_save_map);
	    TextView cate = (TextView) view.findViewById(R.id.item_categories);
	    TextView cont = (TextView) view.findViewById(R.id.item_contact);
	    TextView pho = (TextView) view.findViewById(R.id.item_phone);

	    Intent intent = new Intent();
	    if (not_find.getVisibility() == View.VISIBLE) {
		Toast.makeText(getActivity(), "未找到,请检查数据库..", Toast.LENGTH_SHORT).show();
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

//     //DBF文件的数据添加
//     public void addItem() throws Exception {
//     DBFWriter dbfwriter = new DBFWriter(new File(rwPath));
//     Object[] obj=new Object[3];
//     // obj[0]="442899999811111111";
//     // obj[1]="123";
//     // obj[2]="广州软件";
//     dbfwriter.addRecord(obj);
//     }

    private void creatDataBase() {
	ParseDbf2Map parseDbf2Map = new ParseDbf2Map();
	DataBaseService service = new MyData(getActivity());
	Object[] params = new Object[DNBXX_ITEM.length];
	// 创建table —— dnbxx
	List<Map<String, String>> dnbxxItems = parseDbf2Map
		.getListMapFromDbf(dnbxxPath);
	for (int i = 0; i < dnbxxItems.size(); i++) {
	    Map<String, String> map = dnbxxItems.get(i);
	    for (int y = 0; y < DNBXX_ITEM.length; y++) {
		params[y] = map.get(DNBXX_ITEM[y]);
	    }
	    boolean flag = service.addMyData(DNBXX, params);
	    Log.i("dnbxx add item", "--->" + flag);
	}
	// 创建table —— dnbxysj
	List<Map<String, String>> dnbxysjItems = parseDbf2Map
		.getListMapFromDbf(dnbxxPath);
	for (int i = 0; i < dnbxysjItems.size(); i++) {
	    Map<String, String> map = dnbxysjItems.get(i);
	    params[0] = map.get(METER_ID);
	    boolean flag = service.addMyData(DNBXX, params);
	    Log.i("dnbxysj add items", "--->" + flag);
	}

    }
}