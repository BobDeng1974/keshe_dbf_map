package details;

import java.util.List;
import java.util.Map;

import com.baidu.platform.comapi.map.l;

import refreshablelist.DataBaseService;
import refreshablelist.MyData;

import DBFRW.WriteDbfFile;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.widget.Toast;
import static stringconstant.StringConstant.*;

public class MyWriteDataTask extends
	AsyncTask<Map<String, Object>, Void, Boolean> {

    Activity mContext;
    DataBaseService mDataService;
    ProgressDialog mProgressDialog;
    ContentValues contentValues;

    public MyWriteDataTask(Activity context) {
	this.mContext = context;
    }

    @Override
    protected void onPreExecute() {
	// 弹出等待框
	mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
	mProgressDialog.setMessage("记录数据中..");
	mProgressDialog.show();
	super.onPreExecute();

    }

    @Override
    protected Boolean doInBackground(Map<String, Object>... params) {

	this.mDataService = new MyData(mContext);
	Map<String, Object> dataMap = params[0];
	// 剪切源文件到副本文件件

	// 先更新数据库，再用数据库数据生成dbf文件
	String zc_id = dataMap.get(ZC_ID).toString();
	// 删除已有信息
	mDataService.deleteMyData(DNBXYSJ, METER_ID, new String[] { zc_id });
	mDataService.deleteMyData(DNBXCSSXX, METER_ID, new String[] { zc_id });
	mDataService.deleteMyData(DNBDGLXX, METER_ID, new String[] { zc_id });
	mDataService.deleteMyData(JLFYZCXX, SEAL_ID, new String[] { zc_id });

	// 操作dnbxysj文件和dnbdglxx文件,因为这两个文件更改的方法是一样的
	contentValues = getContentValues(DNBXYSJ_ITEM, dataMap);
	if (!makeDnbxysjOrDnbdglxx(DNBXYSJ, DNBXYSJ_ITEM, contentValues))
	    return false;
	
	contentValues = getContentValues(DNBDGLXX_ITEM, dataMap);
	if (!makeDnbxysjOrDnbdglxx(DNBDGLXX, DNBDGLXX_ITEM, contentValues))
	    return false;

	// 操作dnbxcssxx文件
	if(!doDNBxcssxxDB(dataMap))
	    return false;
	return true;
    }

    private boolean doDNBxcssxxDB(Map<String, Object> dataMap) {
	boolean flag = false;
	ContentValues cv = new ContentValues();
	// 往里面先填充好除readType之外的参数
	for (int i = 0; i < DNBXCSSXX_ITEM.length; i++) {
	    String key = DNBXCSSXX_ITEM[i];
	    if (key.equals(READ_TYPE) || key.equals(THIS_READ))
		continue;
	    cv.put(key, dataMap.get(key).toString());
	}
	String[] read_type = new String[] { "11", "13", "14", "15", "21", "31",
		"41", "43", "44", "45", "51" };
	String[] this_read = new String[] { ACTIVE_TOTAL, ACTIVE_PEAK,
		ACTIVE_VALLEY, ACTIVE_AVERAGE, REACTIVE_TOTAL, MAX_NEED,
		ACTIVE_REVERSE_TOTAL, ACTIVE_REVERSE_PEAK,
		ACTIVE_REVERSE_VALLEY, ACTIVE_REVERSE_AVERAGE, REACTIVE_REVERSE };
	//有多少个read_type就添加多少条对应的this_read数据
	for (int j = 0; j < read_type.length; j++) {
	    cv.put(READ_TYPE, read_type[j]);
	    cv.put(THIS_READ, this_read[j]);
	    if (!mDataService.addMyData(DNBXCSSXX, cv)) {
		flag = false;
		break;
	    }
	    flag = true;
	}
	//创建dbf文件
	List<Map<String , String>> listMap = mDataService.listMyDataMaps(DNBXCSSXX, null);
	if(!WriteDbfFile.creatDbfFile(root+"/"+DNBXCSSXX+".dbf" , DNBXCSSXX_ITEM, listMap))
	    flag = false;
	return flag;
    }

    private ContentValues getContentValues(String[] items,
	    Map<String, Object> dataMap) {
	ContentValues cv = new ContentValues();
	// dnbxysj
	for (int i = 0; i < items.length; i++) {
	    String key = items[i];
	    cv.put(key, dataMap.get(key).toString());
	}
	return cv;
    }

    private boolean makeDnbxysjOrDnbdglxx(String tableName, String[] items,
	    ContentValues cv) {
	boolean flag = false;
	if (mDataService.addMyData(tableName, cv)) {
	    List<Map<String, String>> listMap = mDataService.listMyDataMaps(
		    tableName, null);
	    if (WriteDbfFile.creatDbfFile(root + "/" + tableName + ".dbf",
		    items, listMap))
		flag = true;
	}
	return flag;
    }

    @Override
    protected void onPostExecute(Boolean result) {
	// 关闭的等待框
	mProgressDialog.dismiss();
	new AlertDialog.Builder(mContext)
		.setTitle("任务成功")
		.setMessage("数据已成功写入")
		.setPositiveButton("返回任务列表",
			new DialogInterface.OnClickListener() {
			    @Override
			    public void onClick(DialogInterface dialog,
				    int which) {
				mContext.finish();
			    }
			}).show();
	super.onPostExecute(result);
    }
}
