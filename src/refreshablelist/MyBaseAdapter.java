package refreshablelist;

import static stringconstant.StringConstant.*;

import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import badgeview.BadgeView;
import baidumapsdk.demo.R;
import details.StringToMap;

public class MyBaseAdapter extends BaseAdapter {
    private int[] colors = new int[] { 0xffdcdcdc, 0xfff8f8ff };
    private Context mContext;
    private List<Map<String, String>> dataList;
    private Boolean isFirstNotFound = true;

    public MyBaseAdapter(Context context, List<Map<String, String>> dataList) {
	this.mContext = context;
	this.dataList = dataList;
	// parseDbf2Map = new ParseDbf2Map();
	DBOpenHelper helper = new DBOpenHelper(context);
	// 调用 getWritableDatabase()或者 getReadableDatabase()其中一个方法将数据库建立
	helper.getWritableDatabase();
    }

    @Override
    public int getCount() {
	return dataList.size();
    }

    @Override
    public Map<String, String> getItem(int position) {
	return dataList.get(position);
    }

    @Override
    public long getItemId(int position) {
	return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
	ViewHolder holder = null;
	if (convertView == null) {
	    holder = new ViewHolder();
	    convertView = LayoutInflater.from(mContext).inflate(
		    R.layout.refresh_list_item, null);
	    holder.refresh_item = (LinearLayout) convertView
		    .findViewById(R.id.refreshlist_item);
	    holder.categoriesZero = (LinearLayout) convertView
		    .findViewById(R.id.categories_00);
	    holder.categoriesOne = (LinearLayout) convertView
		    .findViewById(R.id.categories_01);
	    holder.categoriesTwo = (LinearLayout) convertView
		    .findViewById(R.id.categories_02);

	    holder.save_map = (TextView) convertView
		    .findViewById(R.id.item_save_map);
	    holder.not_find = (TextView) convertView
		    .findViewById(R.id.item_not_find);
	    holder.zc_id = (TextView) convertView.findViewById(R.id.item_zc_id);
	    holder.categories = (TextView) convertView
		    .findViewById(R.id.item_categories);
	    holder.begin_date = (TextView) convertView
		    .findViewById(R.id.item_begin_date);
	    holder.cons_name = (TextView) convertView
		    .findViewById(R.id.item_cons_name);
	    holder.elec_address = (TextView) convertView
		    .findViewById(R.id.item_address);
	    holder.contactAndPhone = (TextView) convertView
		    .findViewById(R.id.item_contact_and_phone);
	    holder.contact = (TextView) convertView
		    .findViewById(R.id.item_contact);
	    holder.phone = (TextView) convertView.findViewById(R.id.item_phone);
	    holder.substation = (TextView) convertView
		    .findViewById(R.id.item_substation);
	    holder.line_name = (TextView) convertView
		    .findViewById(R.id.item_line_name);
	    holder.made_number = (TextView) convertView
		    .findViewById(R.id.item_made_number);
	    holder.badgeView_complete = new BadgeView(mContext,
		    holder.categoriesZero);
	    holder.badgeView_categories = new BadgeView(mContext,
		    holder.categoriesZero);
	    // holder.badgeView_complete.setBadgeBackgroundColor(droidGreen);
	    // holder.badgeView_complete.setTextColor(Color.BLACK);
	    // 将holder绑定到convertView
	    convertView.setTag(holder);
	} else {
	    holder = (ViewHolder) convertView.getTag();
	}
	//　　根据RW.DBF中的ZC_ID索引dnbxx.dbf中的METER_ID查找对应的信息
	DataBaseService service = new MyData(mContext);
	List<Map<String, String>>  searchMaps = null;
	Map<String , String> searchMap = null;
	String[] seleStrings = new String[1];
	if (getItem(position).get(ZC_ID) != null) {
	    seleStrings[0] = getItem(position).get(ZC_ID).trim();
	    searchMaps = service.viewMyData(DNBXX, METER_ID,
		    seleStrings);
	} else {
	    seleStrings[0] = "";
	}
	// System.out.println("------查询单条记录--> " + searchMap.toString());

	if (searchMaps.size() != 0) {
	    searchMap = searchMaps.get(0);
	    holder.categoriesZero.setVisibility(View.VISIBLE);
	    holder.not_find.setVisibility(View.GONE);
	    // 向ViewHolder中填入的数据
	    holder.zc_id.setText((String) getItem(position).get(ZC_ID).trim());
	    holder.categories.setText((String) getItem(position)
		    .get(CATEGORIES).trim());
	    holder.begin_date.setText((String) getItem(position)
		    .get(BEGIN_DATE).trim());
	    holder.contact.setText((String) getItem(position).get(CONTACT)
		    .trim());
	    holder.phone.setText((String) getItem(position).get(PHONE).trim());
	    holder.contactAndPhone.setText(holder.contact.getText().toString() + "\t" + holder.phone.getText().toString());

	    holder.cons_name.setText(searchMap.get(CONS_NAME).trim());
	    holder.elec_address.setText(searchMap.get(ELEC_ADDR).trim());
	    holder.substation.setText(searchMap.get(SUBSTATION).trim());
	    holder.line_name.setText(searchMap.get(LINE_NAME).trim());
	    holder.made_number.setText(searchMap.get(MADE_NO).trim());
	    // 将被点击的任务的详细信息传递到任务信息界面以显示
	    searchMap.put(ZC_ID, getItem(position).get(ZC_ID.trim()));
	    searchMap.put(TASKID, getItem(position).get(TASKID));
	    String stringMap = StringToMap.MapToString(searchMap);
	    holder.save_map.setText(stringMap);

	    Boolean IsCategoriesOne = getItem(position).get(CATEGORIES).trim()
		    .equals("01");
	    int colorPos = IsCategoriesOne ? 1 : 0;
	    convertView.setBackgroundColor(colors[colorPos]);
	    holder.categoriesOne.setVisibility(IsCategoriesOne ? View.VISIBLE
		    : View.GONE);
	    holder.categoriesTwo.setVisibility(IsCategoriesOne ? View.GONE
		    : View.VISIBLE);

	    // 设置badgeView
	    if (getItem(position).get(WCZT).trim().equals("未完成")) {
		holder.badgeView_complete.setText("新");
		holder.badgeView_complete.show();
	    } else {
		holder.badgeView_complete.hide();
	    }
	    if (!IsCategoriesOne) {
		holder.badgeView_categories.setText("2");
		holder.badgeView_complete.setBadgeMargin(5, 50);
		holder.badgeView_categories.show();
	    } else {
		holder.badgeView_categories.hide();
	    }
	} else {
	    holder.save_map.setText("");
	    holder.categoriesZero.setVisibility(View.GONE);
	    holder.not_find.setVisibility(View.VISIBLE);
	    holder.badgeView_complete.hide();
	    holder.badgeView_categories.hide();
	    if (isFirstNotFound) {
		new AlertDialog.Builder(mContext)
			.setTitle("退出确认")
			.setMessage("无法打开电表信息数据库，请检查数据库是否存在!")
			.setPositiveButton("确定",
				new DialogInterface.OnClickListener() {
				    public void onClick(DialogInterface dialog,
					    int whichButton) {}
				}).show();
		isFirstNotFound = false;
	    }
	}
	return convertView;
    }

    /**
     * ViewHolder类用以储存item中控件的引用
     */
    final class ViewHolder {
	LinearLayout refresh_item;
	LinearLayout categoriesZero;
	LinearLayout categoriesOne;
	LinearLayout categoriesTwo;
	// ImageView image;
	TextView not_find;
	TextView zc_id;;
	TextView categories;
	TextView begin_date;
	// categories=01
	TextView cons_name;
	TextView elec_address;
	TextView contactAndPhone;
	TextView contact;
	TextView phone;
	// categories=02
	TextView substation;
	TextView line_name;
	TextView made_number;
	// save map
	TextView save_map;

	BadgeView badgeView_complete;
	BadgeView badgeView_categories;
    }
    // new int[] { R.id.item_zc_id, R.id.item_categories,R.id.item_begin_date,
    // R.id.item_cons_name,R.id.item_address,R.id.item_contact, R.id.item_phone
    // , //categories=01
    // R.id.item_subs_name,R.id.item_substation,R.id.item_line_name,//categories=02
    // R.id.item_made_number});
}