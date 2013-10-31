package refreshablelist;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.linuxense.javadbf.DBFWriter;

import details.DetailActivity;

import refreshablelist.RefreshableListView.OnRefreshListener;
import static refreshablelist.StringConstant.*;
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
//	    HashMap<String, String> map = (HashMap<String, String>) parent
//		    .getItemAtPosition(position + 1);
	    TextView savedMapTextView = (TextView) view.findViewById(R.id.item_save_map);
	    Intent intent = new Intent();
	    intent.putExtra(MISSION_DETAIL, savedMapTextView.getText().toString());
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

    private void creatDataBase() {
	ParseDbf2Map parseDbf2Map = new ParseDbf2Map();
	DataBaseService service = new MyData(getActivity());
	Object[] params = new Object[DNBXX.length];
	List<Map<String, String>> testItems = parseDbf2Map
		.getListMapFromDbf(dnbxxPath);
	for (int i = 0; i < testItems.size(); i++) {
	    Map<String, String> map = testItems.get(i);
	    for (int y = 0; y < DNBXX.length; y++) {
		params[y] = map.get(DNBXX[y]);
	    }
	    boolean flag = service.addMyData(params);
	    Log.i("fuck", "--->" + flag);
	}

    }
}