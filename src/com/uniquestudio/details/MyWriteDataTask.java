package com.uniquestudio.details;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.uniquestudio.DBFRW.WriteDbfFile;
import com.uniquestudio.refreshablelist.DataBaseService;
import com.uniquestudio.refreshablelist.MyData;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.widget.Toast;
import static com.uniquestudio.stringconstant.StringConstant.*;

public class MyWriteDataTask extends
	AsyncTask<HashMap<String, Object>, Void, Boolean> {

    Activity mContext;
    DataBaseService mDataService;
    ProgressDialog mProgressDialog;
    ContentValues contentValues;
    
    String creatDnbxysjPath =dnbxysjPath;// root+"/"+DNBXYSJ +".dbf";
    String creatDnbxcssxxPath = dnbxcssxxPath;//root+"/"+DNBXCSSXX +".dbf";
    String creatDnbdglxxPath =  dnbdglxxPath;//root+"/"+DNBDGLXX +".dbf";
    String creatJlfyzcxxPath =  jlfyzcxxPath;//root+"/"+JLFYZCXX +".dbf";
    
    public MyWriteDataTask(Activity context) {
	this.mContext = context;
    }

    @Override
    protected void onPreExecute() {
	// 弹出等待框
	mProgressDialog = new ProgressDialog(mContext);
	mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
	mProgressDialog.setMessage("记录数据中..");
	mProgressDialog.show();
	super.onPreExecute();

    }

    @Override
    protected Boolean doInBackground(HashMap<String, Object>... params) {

	this.mDataService = new MyData(mContext);
	HashMap<String, Object> dataMap = params[0];
	// 剪切源文件到副本文件件

	// 先更新数据库，再用数据库数据生成dbf文件
	String zc_id = getValue(dataMap, ZC_ID);
	System.out.println("zc_id ="+zc_id);
	// 删除已有信息
	mDataService.deleteMyData(DNBXYSJ, METER_ID, new String[] { zc_id });
	mDataService.deleteMyData(DNBXCSSXX, METER_ID, new String[] { zc_id });
	mDataService.deleteMyData(DNBDGLXX, METER_ID, new String[] { zc_id });
	mDataService.deleteMyData(JLFYZCXX, EQUIP_ID, new String[] { zc_id });

	// 操作dnbxysj文件和dnbdglxx文件,因为这两个文件更改的方法是一样的
	contentValues = getContentValues(DNBXYSJ_ITEM, dataMap);
	if (!makeDnbxysjOrDnbdglxx(DNBXYSJ,creatDnbxysjPath, DNBXYSJ_ITEM, contentValues))
	    return false;
	
	contentValues = getContentValues(DNBDGLXX_ITEM, dataMap);
	if (!makeDnbxysjOrDnbdglxx(DNBDGLXX,creatDnbdglxxPath, DNBDGLXX_ITEM, contentValues))
	    return false;

	// 操作dnbxcssxx文件
	if(!doDNBxcssxxDB(dataMap))
	    return false;
	
	//操作jlfyzcxx文件 表封的
	if(!doJlfyzcxxDB(dataMap))
	    return false;
	mDataService.updateMyData(RW, ZC_ID, zc_id, new String[] {WCZT},new String[] {"已完成"});
	List<Map<String, String>> listMap = mDataService.listMyDataMaps(RW, null);
	if(!WriteDbfFile.creatDbfFile(rwPath, RW_ITEM, listMap))
	    return false;
	return true;
    }

    @Override
    protected void onPostExecute(Boolean result) {
	// 关闭的等待框
	mProgressDialog.dismiss();
	final boolean flag = result;
	String message = flag ? "数据已成功写入" : "写入时出错,请重试";
	String button = flag ? "返回任务列表" : "确定";
	new AlertDialog.Builder(mContext)
		.setTitle("任务结束")
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
    
    
    /** 获得更新数据库的数据对象 contentValues
     * @param items
     * @param dataMap
     * @return
     */
    private ContentValues getContentValues(String[] items,
	    HashMap<String, Object> dataMap) {
	ContentValues cv = new ContentValues();
	// dnbxysj
	for (int i = 0; i < items.length; i++) {
	    String key = items[i];
	    cv.put(key, getValue(dataMap, key));
	}
	return cv;
    }
    
    /**操作 此两个数据库中的表，并创建dbf文件
     * @param tableName
     * @param items
     * @param cv
     * @return
     */
    private boolean makeDnbxysjOrDnbdglxx(String tableName, String path , String[] items,
	    ContentValues cv) {
	boolean flag = false;
	if (mDataService.addMyData(tableName, cv)) {
	    List<Map<String, String>> listMap = mDataService.listMyDataMaps(
		    tableName, null);
	    if (WriteDbfFile.creatDbfFile(path,
		    items, listMap))
		flag = true;
	}
	return flag;
    }
    
    /**操作dnbxcssxx文件  更新数据库及创建dbf文件
     * @param dataMap
     * @return
     */
    private boolean doDNBxcssxxDB(HashMap<String, Object> dataMap) {
	boolean flag = true;
	ContentValues cv = new ContentValues();
	// 往里面先填充好除readType之外的参数
	for (int i = 0; i < DNBXCSSXX_ITEM.length; i++) {
	    String key = DNBXCSSXX_ITEM[i];
	    if (key.equals(READ_TYPE) || key.equals(THIS_READ))
		continue;
	    cv.put(key, getValue(dataMap, key));
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
	    cv.put(THIS_READ, getValue(dataMap,this_read[j]));
	    if (!mDataService.addMyData(DNBXCSSXX, cv)) {
		flag = false;
		break;
	    }
	    flag = true;
	}
	//创建dbf文件
	List<Map<String , String>> listMap = mDataService.listMyDataMaps(DNBXCSSXX, null);
	if(!WriteDbfFile.creatDbfFile(creatDnbxcssxxPath , DNBXCSSXX_ITEM, listMap))
	    flag = false;
	return flag;
    }
    
    /**操作Jlfyzcxx文件
     * @param dataMap
     * @return
     */
    private boolean doJlfyzcxxDB(HashMap<String, Object> dataMap) {
	boolean flag = false;
	ContentValues cv= new ContentValues();
	for (int i = 0; i < JLFYZCXX_ITEM.length; i++) {
	    String key = JLFYZCXX_ITEM[i];
	    if (key.equals(SEAL_TYPE) || key.equals(SEAL_ID) || key.equals(HANDLEFLAG) || key.equals(VALID_FLAG) ||key.equals(INTACTFLAG))
		continue;
	    cv.put(key, getValue(dataMap, key));
	}
	//先添加旧表封数据
	cv.put(HANDLEFLAG, "02");//旧封02
	if(dataMap.containsKey("old" + SEAL_TYPE)) {
	    if(!addNewOrOldSeal("old", "", cv, dataMap))
		return false;
	}
	//若有旧表封有两个
	if(dataMap.containsKey("old"+SEAL_TYPE +"2")) {
	    if(!addNewOrOldSeal("old", "2", cv, dataMap))
		return false;
	}
	//添加新封
	cv.put(HANDLEFLAG, "01");
	if(dataMap.containsKey("new"+SEAL_TYPE)) {
	    if(!addNewOrOldSeal("new", "", cv, dataMap))
		return false;
	}
	//若有新表封有两个
	if(dataMap.containsKey("new"+SEAL_TYPE +"2")) {
	    if(!addNewOrOldSeal("new", "2", cv, dataMap))
		return false;
	}
	
	//创建dbf文件
	List<Map<String , String>> listMap = mDataService.listMyDataMaps(JLFYZCXX, null);
	if(WriteDbfFile.creatDbfFile(creatJlfyzcxxPath , JLFYZCXX_ITEM, listMap))
	    flag = true;
	return flag;
    }
    
    public boolean addNewOrOldSeal(String oldOrNew , String nullOrSecond , ContentValues cv , HashMap<String, Object> dataMap) {
	    cv.put(SEAL_TYPE, getValue(dataMap,oldOrNew+SEAL_TYPE+nullOrSecond));
	    cv.put(SEAL_ID,getValue(dataMap,oldOrNew+SEAL_ID+nullOrSecond));
	    cv.put(VALID_FLAG,getValue(dataMap,oldOrNew+VALID_FLAG+nullOrSecond) );
	    cv.put(INTACTFLAG, getValue(dataMap,oldOrNew+INTACTFLAG+nullOrSecond));
	    if(!mDataService.addMyData(JLFYZCXX, cv))
		return false;
	return true;
    }
    public String getValue(HashMap<String, Object> hashMap , String key) {
	String value;
	if(hashMap.containsKey(key))
	    value = hashMap.get(key).toString();
	else
	    value = "";
	return value;
    }
}
