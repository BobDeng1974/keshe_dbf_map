package com.uniquestudio.spinneredittext;

import java.util.ArrayList;
import java.util.List;

import com.uniquestudio.R;

import android.content.Context;
import android.text.InputType;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnFocusChangeListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

public class SpinnerEditText extends LinearLayout{

    private Context mContext;
    private LayoutInflater layoutInflater;
    private TextView textView;
    private EditText editText1;
    private EditText editText2;
    private ImageButton button;
    private DropdownAdapter adapter;
    private List<String> names;
    private PopupWindow pop;
    private ListView listView;
    private int status = -1;  //1无效  2损坏

    public SpinnerEditText(Context context) {
	super(context);
	this.mContext = context;
    }

    public SpinnerEditText(Context context, AttributeSet attrs) {
	super(context, attrs);
	this.mContext = context;
	names = new ArrayList<String>();
	// TODO Auto-generated constructor stub
	String infService = Context.LAYOUT_INFLATER_SERVICE;
	layoutInflater = (LayoutInflater) getContext().getSystemService(
		infService);
	layoutInflater.inflate(R.layout.my_edit_text, this, true);
	getViews();
	adapter = new DropdownAdapter(context, getData());
	listView = new ListView(context);
	listView.setAdapter(adapter);
	listView.setOverScrollMode(View.OVER_SCROLL_NEVER);
	button.setOnClickListener(new OnClickListener() {

	    @Override
	    public void onClick(View v) {
		// TODO Auto-generated method stub
		// 构造方法写在onCreate方法体中会因为布局没有加载完毕而得不到宽高。
		if (null == pop) {
		    // 创建一个在输入框下方的popup
		    pop = new PopupWindow(listView, editText1.getWidth(), names
			    .size() * editText1.getHeight());
		    // 控制popupwindow点击屏幕其他地方消失
		    pop.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.base_tip_bg));
		    pop.setOutsideTouchable(true);
		    pop.showAsDropDown(editText1);

		} else {
		    if (pop.isShowing()) {
			pop.dismiss();
		    } else{
			pop.showAsDropDown(editText1);
		    }
		}
	    }
	});
    }

    public SpinnerEditText(Context context, AttributeSet attrs, int defStyle) {
	super(context, attrs, defStyle);
	// TODO Auto-generated constructor stub
	this.mContext = context;
    }

    public void init(String[] dataArray , String title , Boolean isValueVisible) {
	List<String> myItemData = new ArrayList<String>();
	for (String sp_item : dataArray) {
	    myItemData.add(sp_item);
	}
	this.init(myItemData, title, isValueVisible);
    }
    
    public void init(List<String > data , String title , Boolean isValueVisible) {
	this.setData(data);
	this.setTitle(title);
	if (!isValueVisible) {
	    this.setValueEditTextGone();
	}
    }
    
    private void getViews() {
	// TODO Auto-generated method stub
	textView = (TextView) findViewById(R.id.left_text);
	editText1 = (EditText) findViewById(R.id.edit_text_1);
	editText2 = (EditText) findViewById(R.id.edit_text_2);
	button = (ImageButton) findViewById(R.id.imgbtn);
    }
    
    public void setValueEditTextGone() {
	editText2.setVisibility(View.GONE);
    }

    public List<String> getData() {
	return names;
    }

    public void setData(List<String> itemData) {
	//此句为了刷新pop的高度
	names = itemData;
	adapter.refresh(itemData);
    }

    public void setTitle(String title) {
	textView.setText(title);
	invalidate();
    }
    
    public void setValue(String value) {
	editText2.setText(value);
    }
    
    public void setStatus(int sealInfoStatus) {
	this.status = sealInfoStatus;
    }

    public String getType() {
	return editText1.getText().toString();
    }
    
    public String getValue() {
	return editText2.getText().toString();
    }
    
    public int getStatus() {
	return this.status;
    }
    
    public void setOpenDialogListener(View.OnFocusChangeListener listener){  
	editText2.setOnFocusChangeListener(listener);
    }

    /** 用于显示popupWindow内容的适配器 */
    class DropdownAdapter extends BaseAdapter {

	private Context context;
	private List<String> list;
	private ImageButton close;

	public DropdownAdapter(Context context, List<String> list) {
	    this.context = context;
	    this.list = list;
	}
	public void refresh(List<String> itemData) {  
	        list = itemData;  
	        notifyDataSetChanged();  
	    }  
	
	public int getCount() {
	    return list.size();
	}

	public Object getItem(int position) {
	    return null;
	}

	public long getItemId(int position) {
	    return position;
	}

	public View getView(final int position, View convertView,
		ViewGroup parent) {
	    ViewHolder holder = null;
	    if (convertView == null) {
		holder = new ViewHolder();
		convertView = LayoutInflater.from(context).inflate(
			R.layout.edit_spinner_row, null);
		// close = (ImageButton)
		// convertView.findViewById(R.id.close_row);
		holder.content = (TextView) convertView
			.findViewById(R.id.text_row);

		convertView.setTag(holder);
	    } else {
		holder = (ViewHolder) convertView.getTag();
	    }
	    final String editContent = list.get(position);
	    holder.content.setText(editContent);
	    holder.content.setOnClickListener(new OnClickListener() {

		@Override
		public void onClick(View v) {
		    // TODO Auto-generated method stub
		    editText1.setText(editContent);
		    if(editContent.equals("/")) {
			editText2.setEnabled(false);
			editText2.setHint("不填");
		    }else {
			editText2.setEnabled(true);
			editText2.setHint("");
		    }
		    pop.dismiss();
		}
	    });
	    return convertView;
	}

	final class ViewHolder {
	    TextView content;
	}

    }
}
