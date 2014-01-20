package DBFRW;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

import com.linuxense.javadbf.DBFException;
import com.linuxense.javadbf.DBFField;
import com.linuxense.javadbf.DBFWriter;

public class WriteDbfFile {

    public static boolean creatDbfFile(String dbfName, // 生成的dbf文件名 加文件路径
	    String[] strutName, // 列名
//	    byte[] strutType,// 列类型
//	    int[] strutLenght,// 列类型长度char（16） char（254）
	    List<Map<String, String>> data// 数据
    ) {
	boolean flag = false;
	OutputStream fos = null;
	Object[][] objects = null; //排好顺序的数据
	int fieldCount = strutName.length;
	int dataCount = data.size();
	//按顺序格式化data
	if(dataCount != 0) {
	    objects = new Object[dataCount][fieldCount];
	    for (int row = 0; row < dataCount; row++) {
		Map<String, String> map = data.get(row);
		for (int  column= 0;  column < fieldCount; column++) {
		    objects[row][column] = map.get(strutName[column]);
		}
	    }
	}
	
	try {
	    DBFField[] fields = new DBFField[fieldCount];
	    // 填充列名类型
	    for (int i = 0; i < fieldCount; i++) {
		fields[i] = new DBFField();
		fields[i].setName(strutName[i]);
		fields[i].setDataType(DBFField.FIELD_TYPE_C);//默认全是字符串型
		fields[i].setFieldLength(16);//默认全是16   部分应处理为254
	    }
	    DBFWriter writer = new DBFWriter();
	    writer.setCharactersetName("GBK");
	    writer.setFields(fields);
	    //填充数据
	    for (int j = 0; j < dataCount; j++) {
		writer.addRecord(objects[j]);
	    }		
	    fos = new FileOutputStream(dbfName);
	    writer.write(fos);
	    flag = true;
 	} catch (Exception e) {
	    e.printStackTrace();
	}
	finally {
	    try {
		fos.close();
	    } catch (IOException e) {}
	}
	System.out.println("creatDbfFile "+dbfName+"-->" + flag);
	return flag;
    }
}
