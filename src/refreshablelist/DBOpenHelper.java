package refreshablelist;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import android.content.Context;
import static stringconstant.StringConstant.*;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBOpenHelper extends SQLiteOpenHelper {

    private static String name = "MyDnb.db"; // 表示数据库的名称
    private static int version = 1; // 表示数据库的版本号
    private Context mContext;
    
    public DBOpenHelper(Context context) {
	super(context, name, null, version);
	this.mContext = context;
	System.out.println("DBOpenHelper构造函数");  
	// TODO Auto-generated constructor stub
    }

    // 当数据库创建的时候，是第一次被执行，完成对数据库的表的创建
    @Override
    public void onCreate(SQLiteDatabase db) {
	System.out.println("onCreate创建表");  
	// TODO Auto-generated method stub
	//获取rw的列名
	String rw_col_string = "";
	for (int i = 0; i < RW_ITEM.length; i++) {
	    rw_col_string = rw_col_string + ","+RW_ITEM[i] + " TEXT";
	}
	//获取dnbxx的列名
	String dnbxx_col_string = "";
	for (int i = 0; i < DNBXX_ITEM.length; i++) {
	    dnbxx_col_string = dnbxx_col_string + ","+DNBXX_ITEM[i] + " TEXT";
	}
	//获取dnvxysj的列名
	String dnbxysj_col_string = ",METER_ID char(16)";

//	System.out.println(myString);
	// SQLite 数据创建支持的数据类型： 整型数据，字符串类型，日期类型，二进制的数据类型
	// 数据库这边有一个特点，就是SQLite数据库中文本类型没有过多的约束，也就是可以把布尔类型的数据存储到文本类型中，这样也是可以的
	String sql_rw = "create table rw(id integer primary key autoincrement"+rw_col_string+")";
	String sql_dnb = "create table dnbxx(id integer primary key autoincrement"+dnbxx_col_string+")";
	String sql_dnbxysj = "create table dnbxysj(id integer primary key autoincrement"+dnbxysj_col_string+")";
	db.execSQL(sql_rw);
	db.execSQL(sql_dnb); // 完成数据库的创建
	db.execSQL(sql_dnbxysj);
//	creatDataBase();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	// TODO Auto-generated method stub
	System.out.println("onUpgrade删除表");  
    }
    
//    private void creatDataBase() {
//	creatDBTable(RW, RW_ITEM, rwPath);
//	creatDBTable(DNBXX, DNBXX_ITEM, dnbxxPath);
//	creatDBTable(DNBXYSJ, DNBXYSJ_ITEM, dnbxysjPath);
//    }
//
//    private void creatDBTable(String tableName , String[] tableItem , String filePath) {
//	ParseDbf2Map parseDbf2Map = new ParseDbf2Map();
//	DataBaseService service = new MyData(mContext);
//	String[] params = new String[tableItem.length];
//	// 创建table
//	List<Map<String, String>> Items = parseDbf2Map
//		.getListMapFromDbf(filePath);
//	for (int i = 0; i < Items.size(); i++) {
//	    Map<String, String> map = Items.get(i);
//	    for (int y = 0; y < tableItem.length; y++) {
//		params[y] = map.get(tableItem[y]);
//	    }
//	    boolean flag = service.addMyData(tableName, tableItem , params);
//	    Log.i(tableName + " add item", "--->" + flag);
//	}
//    }
}