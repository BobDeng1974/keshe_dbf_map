package refreshablelist;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import baidumapsdk.demo.R.string;

import static stringconstant.StringConstant.*;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class MyData implements DataBaseService {

    private DBOpenHelper helper = null;

    public MyData(Context context) {
	helper = new DBOpenHelper(context);
    }

    /*
     * (non-Javadoc)
     * 
     * @see refreshablelist.DataBaseService#addMyData(java.lang.Object[])
     */
    @Override
    public boolean addMyData(String tableName, String[] tableItems,
	    Object[] params) {
	ContentValues cv = new ContentValues();
	for (int i = 0; i < tableItems.length; i++) {
	    cv.put(tableItems[i], params[i].toString());
	}
	Boolean flag = addMyData(tableName, cv);
	return flag;
    }

    public boolean addMyData(String tableName,ContentValues cv) {
	boolean flag = false;
	SQLiteDatabase database = null;
	try {
	    database = helper.getWritableDatabase(); // 实现对数据库写的操作
	    database.insert(tableName, null, cv);
	    flag = true;
	} catch (Exception e) {
	    e.printStackTrace();
	} finally {
	    if (database != null) {
		database.close();
	    }
	}
	return flag;
    }
    @Override
    public boolean deleteMyData(String tableName ,String whereArgs , Object[] params) {
	// TODO Auto-generated method stub
	boolean flag = false;
	SQLiteDatabase database = null;
	try {
	    String sql = "delete from "+tableName+" where "+whereArgs+" = ? ";
	    database = helper.getWritableDatabase();
	    database.execSQL(sql, params);
	    flag = true;
	} catch (Exception e) {
	    e.printStackTrace();
	} finally {
	    if (database != null) {
		database.close();
	    }
	}
	return flag;
    }

    @Override
    public boolean updateMyData(String tableName, String where,
	    String whereValue, String[] tableItems, Object[] params) {
	// TODO Auto-generated method stub
	boolean flag = false;
	SQLiteDatabase database = null;
	ContentValues cv = new ContentValues();
	String whereis = where + " = ?";
	String[] whereValues = { whereValue };
	for (int i = 0; i < tableItems.length; i++) {
	    cv.put(tableItems[i], params[i].toString());
	}
	try {
	    database = helper.getWritableDatabase();
	    database.update(tableName, cv, whereis, whereValues);
	    flag = true;
	} catch (Exception e) {
	    e.printStackTrace();
	} finally {
	    if (database != null) {
		database.close();
	    }
	}
	return flag;
    }

    /* (non-Javadoc)
     * @see refreshablelist.DataBaseService#viewMyData(java.lang.String, java.lang.String, java.lang.String[])
     * 根据指定的表和查询条件返回查询到的数据（多条）
     */
    @Override
    public List<Map<String, String>> viewMyData(String tableName,
	    String selectionWhere, String[] selectionArgs) {
	// TODO Auto-generated method stub
	List<Map<String, String>> list = new ArrayList<Map<String, String>>();
	Cursor cursor = null;
	SQLiteDatabase database = null;
	database = helper.getReadableDatabase(); // 查询读取数据，查询结果使用Map来存储
	// 声明一个游标，这个是行查询的操作，支持原生SQL语句的查询
	try {
	    cursor = database.query(tableName, null, selectionWhere + "=?",
		    selectionArgs, null, null, null);
	    int colums = cursor.getColumnCount();// 获得数据库的列的个数
	    // cursor.moveToNext() 移动到下一条记录
	    while (cursor.moveToNext()) {
		Map<String, String> map = new HashMap<String, String>();
		for (int i = 0; i < colums; i++) {
		    String cols_name = cursor.getColumnName(i); // 提取列的名称
		    String cols_value = cursor.getString(cursor
			    .getColumnIndex(cols_name)); // 根据列的名称提取列的值
		    // 数据库中有些记录是允许有空值的,所以这边需要做一个处理
		    if (cols_value == null) {
			cols_value = "";
		    }
		    map.put(cols_name, cols_value);
		}
		list.add(map);
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	} finally {
	    cursor.close();
	    if (database != null) {
		database.close();
	    }
	}
	return list;
    }

    /* (non-Javadoc)
     * @see refreshablelist.DataBaseService#listMyDataMaps(java.lang.String, java.lang.String[])
     * 获取指定表中的所有条数据
     */
    @Override
    public List<Map<String, String>> listMyDataMaps(String tableName,
	    String[] selectionArgs) {
	// TODO Auto-generated method stub
	List<Map<String, String>> list = new ArrayList<Map<String, String>>();
	SQLiteDatabase database = null;
	Cursor cursor = null;
	try {
	    database = helper.getReadableDatabase();
	    cursor = database.query(tableName, null, null, null, null, null,
		    null);
	    int colums = cursor.getColumnCount();
	    while (cursor.moveToNext()) {
		Map<String, String> map = new HashMap<String, String>();
		for (int i = 0; i < colums; i++) {
		    String cols_name = cursor.getColumnName(i);
		    String cols_value = cursor.getString(cursor
			    .getColumnIndex(cols_name));
		    if (cols_name == null) {
			cols_value = "";
		    }
		    map.put(cols_name, cols_value);
		}
		list.add(map);
	    }
	} catch (Exception e) {
	    // TODO: handle exception
	} finally {
	    if (database != null) {
		database.close();
	    }
	}
	return list;
    }


}
