package AnalyseTxt;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import refreshablelist.RefreshableListViewActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;

public class AnalyseTxtUtil {
    
    public static HashMap<String,List<String>> getDataFromConfigTXT(String filePath){
	InputStream in;
	String line;
	InputStreamReader inputStreamReader = null;
	HashMap<String, List<String>> hmOne = new HashMap<String, List<String>>();
	if (!isDirExist(filePath)) {
	    List<String> defaultList  = new ArrayList<String>();
	    defaultList.add("/");
	    hmOne.put("计量柜旧封类型", defaultList);
	    hmOne.put("计量柜新封类型", defaultList);
	    hmOne.put("端子盒旧封类型", defaultList);
	    hmOne.put("端子盒新封类型", defaultList);
	    hmOne.put("表新封类型", defaultList);
	    hmOne.put("表旧封类型", defaultList);
	    hmOne.put("设备评级", defaultList);
	    hmOne.put("设备厂家", defaultList);
	    hmOne.put("测试人", defaultList);
	    hmOne.put("记录人", defaultList);
	    hmOne.put("校验人", defaultList);
	    hmOne.put("审核人", defaultList);
	    hmOne.put("常用备注", defaultList);
	    hmOne.put("串口配置", defaultList);
	    hmOne.put("结论", defaultList);
	    return hmOne;
	}
	try {
	    in = new FileInputStream(filePath);
	    inputStreamReader = new InputStreamReader(in,"gbk");
	    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
	    List<String>l1 = new ArrayList<String>();
	    while((line = bufferedReader.readLine()) != null) {
		if (line.startsWith("//")) 
		    continue;
		else if (line.startsWith("##")) {
		    String string = l1.get(0).substring(0, l1.get(0).length()-1);
		    List<String> list = l1.subList(1, l1.size());
		    hmOne.put(string, list);
		    l1 = new ArrayList<String>();
		}else if (!line.equals("")) {
		    l1.add(line);
		}
	    }
	} catch (IOException e1) {
	    e1.printStackTrace();
	}
	return hmOne;
}
    
    
    private  static  boolean isDirExist(String path) {
	// 如果不存在的话，则创建存储目录
	File mediaStorageDir = new File(path);
	if (!mediaStorageDir.exists()) {
	    if (!mediaStorageDir.mkdirs()) {
		Log.d("MyCameraApp", "failed to create directory");
	    }
	    return false;
	} else {
	    return true;
	}
    }
}

