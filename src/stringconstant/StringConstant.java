package stringconstant;

import android.os.Environment;

public class StringConstant {


    public static String DNBXX = "dnbxx";

    public static String RW = "rw";

    public static String DNBXYSJ = "dnbxysj";

    public static String GPS = "gps";
    
    public static String BZQJ = "bzqj";
    
    public static String DNBXCSSXX = "dnbxcssxx";
    
    public static String DNBDGLXX = "dnbdglxx";
    
    public static String JLFYZCXX = "jlfyzcxx";
    
    public static String PREFS_NAME = "uniquestudio.mydata";

    public static final String DEF_AK = "3323e0313fc29a0263ba3f50fc28791c";//默认AK
    
    public static String root = Environment.getExternalStorageDirectory()
		.getAbsolutePath() + "/Android/data/uniquestudio.Electric";
    public static String dbfPath = root + "/dbf/";
    
    public static String rwPath = dbfPath + RW + ".dbf";
    public static String dnbxxPath = dbfPath + DNBXX + ".dbf";
    public static String gpsPath = dbfPath + GPS + ".dbf";
    public static String dnbxysjPath = dbfPath + DNBXYSJ + ".dbf";
    public static String bzqjPath = dbfPath + BZQJ +".dbf";
    public static String dnbxcssxxPath = dbfPath + DNBXCSSXX +".dbf";
    public static String dnbdglxxPath = dbfPath + DNBDGLXX + ".dbf";
    public static String jlfyzcxxPath = dbfPath + JLFYZCXX + ".dbf";
    // rw.dbf
    public static String TASKID = "TASKID";
    public static String ZC_ID = "ZC_ID";
    public static String RWLX = "RWLX";
    public static String ACTIVITY = "ACTIVITY";
    public static String BEGIN_DATE = "BEGIN_DATE";
    public static String USER = "USER";
    public static String USER_NAME ="USER_NAME";
    public static String DEPT ="DEPT";
    public static String DEPT_NAME = "DEPT_NAME";
    public static String CONTACT = "CONTACT";
    public static String PHONE = "PHONE";
    public static String WCZT = "WCZT";
    public static String CATEGORIES = "CATEGORIES";
    
    // dnbxx.DBF
    public static String METER_ID = "METER_ID";// 若无则无法打开，即将退出软件//电能表标识
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
    public static String WIRING = "WIRING";
    
    //dnbxysj
    public static String APP_NO = "APP_NO";//申请编号
    //METER_ID
    public static String WORK_MODE ="WORK_MODE";//工作方式★必须传入 1，三相三线正向有功 10单相正向无功
    public static String TEST_CIRCLE_QRY = "TC_QRY";//测试圈数
    public static String TESTER_NO = "TESTER_NO";//现场校验人员
    public static String TEST_DATE = "TEST_DATE";//现场校验日期  需要精确到时分秒 ★必须传入
    public static String CHECKER_NO = "CHECKER_NO";//核验人员  
    public static String MD_NO = "MD_NO";//现场校验仪编号
    public static String TEMP = "TEMP"; //温度
    public static String HUMIDITY = "HUMIDITY";
    public static String PHASE_CODE = "PHASE_CODE";//电压相序(正序、反序)？
    public static String UA = "Ua";//Uab或Ua (V)
    public static String UB = "Ub";
    public static String UC = "Uc";
    public static String IA = "Ia";
    public static String IB = "Ib";
    public static String IC = "Ic";
    public static String I1 = "I1";//A相位角
    public static String I2 = "I2";
    public static String I3 = "I3";
    public static String U12_U1 = "U12_U1";
    public static String U2 = "U2";
    public static String U32_U3 = "U32_U3";
    public static String PF = "PF";//功率因数
    public static String ERR1 = "ERR1";//误差1(%)
    public static String ERR2 = "ERR2";//误差2(%)
    public static String INT_C_ERR = "INT_C_ERR";//化整误差(%)：需要给规则。
    public static String AVG_ERR = "AVG_ERR";//平均误差(%)，1+2/2
    public static String A_PF = "A_PF";//A相功率
    public static String B_PF = "B_PF";
    public static String C_PF = "C_PF";
    public static String SND_PF = "SND_PF";//二次总有功功率
    public static String SND_RPF = "SND_RPF";//二次总无功功率
    public static String SIX_CIRCLE = "SIX_CIRCLE";
    //现场校验结论, 0：不合格，1：合格，9：负荷小，8：无负荷，7：停电。6：电压异常，5：功率因数小 ★必须传入
    public static String TEST_RSLT = "TEST_RSLT";
    public static String REMARK = "REMARK";//现场校验说明
    public static String SND_CONC = "SND__CONC";//二次线结论代码如下：0：错误，1：正确， 9：未检查
    public static String SCR = "SCR";//上传人  PDA不填，营销系统使用
    public static String SCSJ = "SCSJ";//数据上传时间（YYYY-MM-DD HH：MM：SS）
    public static String WC_FLAG = "WC_FLAG";
    
    //电能表现场检验示数DNBXCSSXX  删除以前的记录信息。然后在添加新信息
    //APP_NO 
    //METER_ID
    public static String READ_TYPE = "READ_TYPE";//传入代码（readTypeCode）；★必须传入；
    public static String THIS_READ = "THIS_READ";//本次示数★必须传入
    //SCR
    //SCSJ
    
    public static String ACTIVE_TOTAL = "active_total";//11有功（总）
    public static String ACTIVE_PEAK = "active_peak";//13有功（峰）
    public static String ACTIVE_VALLEY = "active_valley";//14有功（谷）
    public static String ACTIVE_AVERAGE = "active_average";//15有功（平）
    public static String REACTIVE_TOTAL = "reactive_total";//21无功(总)
    public static String MAX_NEED = "max_need";//31最大需量
    public static String ACTIVE_REVERSE_TOTAL = "active_reverse_total";//41有功反向（总）
    public static String ACTIVE_REVERSE_PEAK = "active_reverse_peak";//43有功反向（峰）
    public static String ACTIVE_REVERSE_VALLEY = "active_reverse_valley";//44有功反向（谷）
    public static String ACTIVE_REVERSE_AVERAGE = "active_reverse_average";//45有功反向（平）
    public static String REACTIVE_REVERSE = "reactive_reverse";//51无功反向（总）
    //电能表校验多功能数据(dnbdglxx.dbf)修改以前的记录信息
    //APP_NO
    //METER_ID
    public static String TIME_ERR = "TIME_ERR";//“现场情况中”的“时钟误差值(分)”
    public static String T_ERR_CONC = "T_ERR_CONC";//“现场情况中”的“时钟误差结论”   0 ：不合格 1：合格
    public static String METET_DATE = "METET_DATE";//“现场情况中”的“电表日期”   年-月-日
    public static String DATE_CONC = "DATE__CONC";//“现场情况中”的“电表日期”   0错误1正确
    public static String TS_CODE = "TS_CODE";//时段结论   0错误1正确   
    public static String LOAD_CONC = "LOAD_CONC";//负荷曲线结   0错误1正确  
    public static String ACCESSCONC = "ACCESSCONC";//访问权限设置 0错误1正确   
    public static String C_ERR_CONC = "C_ERR_CONC";//组合误差结论  1正确:“电能表止码”的“组合误差”<0.1   0错误:
    public static String PERIODCONC = "PERIODCONC";//冻结日结论 
    public static String DE_PERIOD = "DE_PERIOD";//需量冻结值  
    public static String DE_CYCLE = "DE_CYCLE";//需量周期
    public static String SEAL_FLAG = "SEAL_FLAG";//“结果确认”中的“封印”封印完整结论
    //SCR
    //SCSJ

    
    
    //计量封印装拆信息(jlfyzcxx.dbf)  除以前的记录信息。然后在添加新信息
    public static String SEAL_TYPE = "SEAL_TYPE";//封印类别，01普通封印、02防盗封印、03印模
    public static String SEAL_ID = "SEAL_ID";//“封口信息”中的封印的编号
    public static String EQUIPCATEG = "EQUIPCATEG";//固定填写01。
    public static String EQUIP_ID = "EQUIP_ID";//填写任务编号。
    public static String HANDLEFLAG = "HANDLEFLAG";//旧封填写02，新封填写01。
    public static String HANDLEDATE = "HANDLEDATE";//封印的施启封处理的时间
    public static String HANDLEDEPT = "HANDLEDEPT";//PDA不需要填写
    public static String HANDLER_NO = "HANDLER_NO";//“结果确认”中的“校验人”
    public static String SEAL_LOC = "SEAL_LOC";//不填
    public static String VALID_FLAG = "VALID_FLAG";//在填写“封口信息”的时候会选择有效标志，01有效、02无效 要填
    public static String INTACTFLAG = "INTACTFLAG";//在填写“封口信息”的时候会选好标志，01完好、02损坏 要填
    public static String CHG_REASON = "CHG_REASON";//固定填写“电能表校验”
    //SCR
    //SCSJ

    //gps
    //CONS_NO
    public static String CP_NO = "CP_NO";
    public static String MP_NO = "MP_NO";
    public static String MP_ADDR = "MP_ADDR";
    //METER_ID
    //MADE_NO
    public static String T_FACTOR = "T_FACTOR";
    public static String TAL_ADDR   = "TAL_ADDR";
    //ELEC_ADDR
    public static String TEL ="TEL";
    public static String LONGITUDE = "LONGITUDE";
    public static String LATITUDE = "LATITUDE";
    public static String UPBZ = "UPBZ";
    //SCR
    //SCSJ

    public static String[] RW_ITEM = { TASKID, ZC_ID ,RWLX, ACTIVITY, BEGIN_DATE , USER, USER_NAME,
	DEPT,DEPT_NAME,CONTACT,PHONE,WCZT,CATEGORIES};
    
    public static String[] DNBXX_ITEM = { METER_ID, CONS_NO , CONS_NAME, SUBS_NAME,
	    ELEC_ADDR,  LINE_NAME, MADE_NO, MODEL, VOLT_NAME, RATED,
	    RC_RATIO, MULTIRATE, DEMAND, CONST, MD_TYPE ,WIRING};

    public static String[] DNBXYSJ_ITEM = { APP_NO,METER_ID ,WORK_MODE,TEST_CIRCLE_QRY,TESTER_NO,
	TEST_DATE,CHECKER_NO,MD_NO,TEMP,HUMIDITY,PHASE_CODE,UA,UB,UC,IA,IB,IC,I1,I2,I3,U12_U1,U2,
	U32_U3,PF,ERR1,ERR2,INT_C_ERR,AVG_ERR,A_PF,B_PF,C_PF,SND_PF,SND_RPF,SIX_CIRCLE,TEST_RSLT,
	REMARK,SND_CONC,SCR,SCSJ,WC_FLAG};
    
    public static String[] DNBXCSSXX_ITEM = {APP_NO,METER_ID,READ_TYPE,THIS_READ,SCR,SCSJ};
    
    public static String[] DNBDGLXX_ITEM = {APP_NO,METER_ID,TIME_ERR,T_ERR_CONC,METET_DATE,DATE_CONC,
	TS_CODE,LOAD_CONC,ACCESSCONC,C_ERR_CONC,PERIODCONC,DE_PERIOD,DE_CYCLE,SEAL_FLAG,
	SCR,SCSJ};
    
    public static String[] JLFYZCXX_ITEM = {SEAL_TYPE,SEAL_ID,EQUIPCATEG,EQUIP_ID,HANDLEFLAG,HANDLEDATE,
	HANDLEDEPT,HANDLER_NO,SEAL_LOC,VALID_FLAG,INTACTFLAG,CHG_REASON,SCR,SCSJ};
    
    public static String[] GPS_ITEM = { CONS_NO , CP_NO,MP_NO,MP_ADDR,METER_ID,MADE_NO,T_FACTOR,
	TAL_ADDR,ELEC_ADDR,TEL,LATITUDE , LONGITUDE,UPBZ,SCR,SCSJ };
    
    public static String[] BZQJ_ITEM = {MADE_NO};
    
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
    
    public static String[] PecDataItem = {UA,IA,UB,IB,UC,IC,A_PF,B_PF,C_PF,"Qa","Qb","Qc",I1,I2,I3,
	SND_PF,SND_RPF,U12_U1,U2,"Iac","Cos","Fre",U32_U3,ERR1,ERR2};
    
    public static String[] WIRING_03 = {"温度：","湿度：","Uan：","Ubn：","Ucn：","Ia：","Ib：","Ic：",
	"Uan^Ia：","Ubn^Ib：","Ucn^Ic：","A相功率：","B相功率：","C相功率：","功率因素：","二次总有功功率：",
	"电表误差：","电表误差：","二次总无功功率："};
    
    public static String[] WIRING_OTHER = {"温度：","湿度：","Uab：","Uac：","Ucb：","Ia：","Ib：","Ic：","Uab^Ia：",
	"Uac^Ib：","Ucb^Ic：","A相功率：","B相功率：","C相功率：","功率因素：","二次总有功功率：",
	"电表误差：","电表误差：","二次总无功功率："};
    
    // intent
    public static String MISSION_DETAIL = "mission_detail";
    
}
