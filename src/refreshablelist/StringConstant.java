package refreshablelist;

import static refreshablelist.StringConstant.CONST;
import static refreshablelist.StringConstant.CONS_NAME;
import static refreshablelist.StringConstant.DEMAND;
import static refreshablelist.StringConstant.ELEC_ADDR;
import static refreshablelist.StringConstant.LINE_NAME;
import static refreshablelist.StringConstant.MADE_NO;
import static refreshablelist.StringConstant.MD_TYPE;
import static refreshablelist.StringConstant.METER_ID;
import static refreshablelist.StringConstant.MODEL;
import static refreshablelist.StringConstant.MULTIRATE;
import static refreshablelist.StringConstant.RATED;
import static refreshablelist.StringConstant.RC_RATIO;
import static refreshablelist.StringConstant.SUBSTATION;
import static refreshablelist.StringConstant.SUBS_NAME;
import static refreshablelist.StringConstant.VOLT_NAME;
import android.os.Environment;

public class  StringConstant {
//	new String[] { "ZC_ID","CATEGORIES","BEGIN_DATE",
//	"CONS_NAME","ELEC_ADDR", "CONTACT", "PHONE",//如果categories=01时
//	"SUBS_NAME","SUBSTATION","LINE_NUMBER",//categories=02时
//	"MODE_ON" },
    public static String rwPath =Environment.getExternalStorageDirectory() + "/rw.dbf";
    public static String dnbxxPath = Environment.getExternalStorageDirectory() + "/dnbxx.DBF";
    //rw.dbf
    public static String ZC_ID = "ZC_ID";
    public static String CATEGORIES = "CATEGORIES";
    public static String BEGIN_DATE = "BEGIN_DATE";
    public static String CONTACT = "CONTACT";
    public static String PHONE = "PHONE";
    //dnbxx.DBF
    public static String METER_ID = "METER_ID";
    public static String CONS_NAME = "CONS_NAME";
    public static String SUBS_NAME = "SUBS_NAME";
    public static String ELEC_ADDR = "ELEC_ADDR";
    public static String SUBSTATION = "SUBSTATION";//供电所
    public static String LINE_NAME = "LINE_NAME";
    public static String MADE_NO = "MADE_NO";
    public static String MODEL = "MODEL";
    public static String VOLT_NAME = "VOLT_NAME";
    public static String RATED = "RATED";
    public static String RC_RATIO = "RC_RATIO";
    public static String MULTIRATE = "MULTIRATE";
    public static String DEMAND = "DEMAND";
    public static String CONST = "CONST";
    public static String MD_TYPE = "MD_TYPE";
    
    public static String[] DNBXX = { METER_ID, CONS_NAME, SUBS_NAME, ELEC_ADDR,
	    SUBSTATION, LINE_NAME, MADE_NO, MODEL, VOLT_NAME, RATED, RC_RATIO,
	    MULTIRATE, DEMAND, CONST, MD_TYPE };
    
    //intent
    public static String MISSION_DETAIL = "mission_detail";
}
