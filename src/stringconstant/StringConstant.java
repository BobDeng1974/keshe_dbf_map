package stringconstant;

import static stringconstant.StringConstant.CONST;
import static stringconstant.StringConstant.CONS_NAME;
import static stringconstant.StringConstant.DEMAND;
import static stringconstant.StringConstant.ELEC_ADDR;
import static stringconstant.StringConstant.LINE_NAME;
import static stringconstant.StringConstant.MADE_NO;
import static stringconstant.StringConstant.MD_TYPE;
import static stringconstant.StringConstant.METER_ID;
import static stringconstant.StringConstant.MODEL;
import static stringconstant.StringConstant.MULTIRATE;
import static stringconstant.StringConstant.RATED;
import static stringconstant.StringConstant.RC_RATIO;
import static stringconstant.StringConstant.SUBSTATION;
import static stringconstant.StringConstant.SUBS_NAME;
import static stringconstant.StringConstant.VOLT_NAME;
import baidumapsdk.demo.R.string;
import android.os.Environment;

public class StringConstant {

    public static String DNBXX = "dnbxx";

    public static String RW = "rw";

    public static String DNBXYSJ = "dnbxysj";

    public static String GPS = "gps";
    
    public static String BZQJ = "bzqj";

    public static String root = Environment.getExternalStorageDirectory() + "/dbf/";
    
    public static String rwPath = root + RW + ".dbf";
    public static String dnbxxPath = root + DNBXX + ".dbf";
    public static String gpsPath = root + GPS + ".dbf";
    public static String dnbxysjPath = root + DNBXYSJ + ".dbf";
    public static String bzqjPath = root + BZQJ +".dbf";
    // rw.dbf
    public static String WCZT = "WCZT";
    public static String ZC_ID = "ZC_ID";
    public static String CATEGORIES = "CATEGORIES";
    public static String BEGIN_DATE = "BEGIN_DATE";
    public static String CONTACT = "CONTACT";
    public static String PHONE = "PHONE";
    // dnbxx.DBF
    public static String METER_ID = "METER_ID";// 若无则无法打开，即将退出软件
    public static String CONS_NO = "CONS_NO";
    public static String CONS_NAME = "CONS_NAME";
    public static String SUBS_NAME = "SUBS_NAME";
    public static String ELEC_ADDR = "ELEC_ADDR";
    public static String SUBSTATION = "SUBS_NAME";// 供电所
    public static String LINE_NAME = "LINE_NAME";
    public static String MADE_NO = "MADE_NO";
    public static String MODEL = "MODEL";
    public static String VOLT_NAME = "VOLT__NAME";//fuck!!!!!!!!!!
    public static String RATED = "RATED";
    public static String RC_RATIO = "RC_RATIO";
    public static String MULTIRATE = "MULTIRATE";
    public static String DEMAND = "DEMAND";
    public static String CONST = "CONST";
    public static String MD_TYPE = "MD_TYPE";
    //gps
    public static String LONGITUDE = "LONGITUDE";
    public static String LATITUDE = "LATITUDE";

    public static String[] RW_ITEM = { WCZT , ZC_ID ,CATEGORIES, BEGIN_DATE, PHONE , CONTACT };
    
    public static String[] DNBXX_ITEM = { METER_ID, CONS_NO , CONS_NAME, SUBS_NAME,
	    ELEC_ADDR,  LINE_NAME, MADE_NO, MODEL, VOLT_NAME, RATED,
	    RC_RATIO, MULTIRATE, DEMAND, CONST, MD_TYPE };

    public static String[] DNBXYSJ_ITEM = { METER_ID };
    
    public static String[] GPS_ITEM = { CONS_NO , LATITUDE , LONGITUDE };
    
    public static String[] MISSION_INFO_ITEM_01 = { CONS_NO , CONS_NAME, ELEC_ADDR,
	    CONTACT, PHONE, MODEL, VOLT_NAME, RATED, RC_RATIO, MULTIRATE,
	    DEMAND, CONST, MD_TYPE };
    public static String[] MISSION_INFO_ITEM_01_noCONTACTandPHONE = { CONS_NO , CONS_NAME, ELEC_ADDR,
	    MODEL, VOLT_NAME, RATED, RC_RATIO, MULTIRATE,
	    DEMAND, CONST, MD_TYPE };

    public static String[] MISSION_INFO_ITEM_01_CHINESE = { "户号" ,"户名", "地址", //"联系人","电话", 
	    "型号", "电压", "电流", "TA变比", "是否执行复费率", "是否用需量", "有功常数",
	    "电能计量装置分类" };

    public static String[] MISSION_INFO_ITEM_02 = { SUBS_NAME, VOLT_NAME,
	    LINE_NAME, RC_RATIO, MODEL };
    
    public static String[] MISSION_INFO_ITEM_02_CHINESE = { "变电所名称" ,"电压等级", "线路名称","TA变比","型号" };
    // intent
    public static String MISSION_DETAIL = "mission_detail";
    
}
