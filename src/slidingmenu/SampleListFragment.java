package slidingmenu;

import java.util.ArrayList;

import baidumapsdk.demo.R;
import android.content.Context;
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
	if(iChangeFragment != null){
		iChangeFragment.changeFragment(position);
	}
    }
}
