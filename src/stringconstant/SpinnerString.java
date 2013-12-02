package stringconstant;

public class SpinnerString {
    //
    public static String[] SCENE_VERIFY_ARRAY = { "/", "不合格", "合格", "功率因数小",
	    "电压异常", "停电", "无负荷", "负荷小" };

    public static String[] WORK_MODE_ARRAY = { "/", "三相三线正向有功", "三相三线正向无功",
	    "三相四线正向有功", "三相四线正向无功", "三相三线反向有功", "三相三线反向无功", "三相四线反向有功",
	    "三相四线反向无功", "单相正向有功", "单相正向无功" };

    public static String[] SEAL_ARRAY = { "/ ", "完整", "不完整" };

    public static String[] SECONDARY_LINE_CONCLUSION_ARRAY = { "/", "错误", "正确",
	    "未检查" };

    public static String[] CLOCK_MISTAKE_CONCLUSION = { "/", "不合格", "合格" };

    public static String[] CLOCK_DATE_CONCLUSION = { "/ ", "错误", "正确" };

    public static String[] TABLE_SEAL_ARRAY = { "/", "普通", "防盗", "印模" };
    
    public static String[] VOLT_PHASE_ARRAY = {"/ ","正序","反序"};
}
