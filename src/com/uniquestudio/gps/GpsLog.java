package com.uniquestudio.gps;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.uniquestudio.stringconstant.StringConstant;

public class GpsLog 
{
    public static String logFileName;    
    public static FileOutputStream fos;
    private static final int MEMORY_LOG_FILE_MAX_SIZE = 2 * 1024 * 1024;           //内存中日志文件最大值，2M 
    
    private static String getLogFileName()
    {
        if(logFileName == null)
        {
            String root = StringConstant.root;
            String fileName = "GpsLog"+ ".txt";
            logFileName = root + File.separator + fileName;
        }
        return logFileName;
    }
    
    private static String getLogPathName()
    {
	return StringConstant.root;
    }
    
    public synchronized static void writeLogFile(String message)
    {        
	
        try
        {
            if (fos == null)
            {
//                System.gc();
                
                // 创建日志目录
                File fileLogPath = new File(getLogPathName());
                if (!fileLogPath.exists())
                {
                    if(!fileLogPath.mkdirs()) {
                	System.out.println("无法创建GPS日志目录");
                	return;
                    }
                }

                // 创建日志文件
                File fileLog = new File(getLogFileName());
                if (!fileLog.exists())
                {
                    fileLog.createNewFile();
                }else {
                    if(fileLog.length() > MEMORY_LOG_FILE_MAX_SIZE) {
                	if(fileLog.delete())
                	    fileLog.createNewFile();
                    }
		}

                // 构建FileOutputStream
                fos = new FileOutputStream(fileLog, true);
            }
          String format = "yyyyMMdd-HH:mm:ss"; 
          message = new SimpleDateFormat(format).format(new Date()) +"\t"+ message;
            message = message + "\r\n";
            fos.write(message.getBytes());
        } catch (IOException e)
        {
		e.printStackTrace();
        }
    }
    
    public static void closeLogStream()
    {
        try
        {
            if(fos != null)
            {
                fos.close();
            }
        } catch (IOException e)
        {
        }
    }

}