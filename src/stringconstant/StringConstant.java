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

    public static String rwPath = Environment.getExternalStorageDirectory()
	    + "/dbf/" + RW + ".dbf";
    public static String dnbxxPath = Environment.getExternalStorageDirectory()
	    + "/dbf/" + DNBXX + ".dbf";
    public static String gpsPath = Environment.getExternalStorageDirectory()
	    + "/dbf/" + GPS + ".dbf";
    public static String dnbxysjPath = Environment
	    .getExternalStorageDirectory() + "/dbf/" + DNBXYSJ + ".dbf";
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

    public static String[] RW_ITEM = { WCZT , ZC_ID ,CATEGORIES, BEGIN_DATE, PHONE , CONTACT };
    
    public static String[] DNBXX_ITEM = { METER_ID, CONS_NO , CONS_NAME, SUBS_NAME,
	    ELEC_ADDR,  LINE_NAME, MADE_NO, MODEL, VOLT_NAME, RATED,
	    RC_RATIO, MULTIRATE, DEMAND, CONST, MD_TYPE };

    public static String[] DNBXYSJ_ITEM = { METER_ID };
    
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
    
    // Message types sent from the BluetoothChatService Handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";
    // Intent request codes
    public static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
    public static final int REQUEST_CONNECT_DEVICE_INSECURE = 2;
    public static final int REQUEST_ENABLE_BT = 3;
    public static String EXTRA_DEVICE_ADDRESS = "MAC";
    //send message 
    /**
     *读文件  0xAA+0xAA+长度+”FLRD”+”e:\\pec\\4”+ CRCL+CRCH
     */
    public static final int TYPE_GETFI_FLRD = 0;
    /**
     * 应答读文件 0xAA+0xAA+长度+”FLSD”+”e:\\pec\\4”（与读文件时的文件名相同+ CRCL+CRCH；
     */
//    public static final int TYPE_ = 1;
    /**
     * 应答读文件名返回 0xAA+0xAA+长度+”YFLSDOK”+ CRCL+CRCH；
     */
    public static final int TYPE_SndFlNm_YFLSDOK = 2;
    /**
     * 发送一帧数据：0xAA+0xAA+长度+”FD”+帧序号低位+帧序号帧高位+数据+CRCL+CRCH；
     */
//    public static final int TYPE_ = 3;
    /**
     * 应答帧数据 0xAA+0xAA+长度+”YFD”+帧序号低位+帧序号高位+ “OK” +0xFF+0xFF;
     */
    public static final int TYPE_SndData_YFD = 4;
    /**
     * 文件结束命令：0xAA+0xAA+长度+”FLSE”+CRCL+CRCH；
     */
//    public static final int TYPE_ = 5;
    /**
     * 应答文件结束命令0xAA+0xAA+长度 + “FLRE” + CRCL+CRCH；
     */
    public static final int TYPE_SndFlEnd_FLRE = 6;
    
}
