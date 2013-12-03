package refreshablelist;

import java.util.List;
import java.util.Map;

public interface DataBaseService {
    
    public int fetchTableLength(String tableName);
    
    public boolean addMyData(String tableName ,String[] tableItems , Object[] params);   
    
    public boolean deleteMyData(Object[] params);  
      
    public boolean updateMyData(String tableName,String where , String  whereValue , String[] tableItems , Object[] params);
      
    //使用 Map<String, String> 做一个封装，比如说查询数据库的时候返回的单条记录  
    public List<Map<String, String>>  viewMyData(String tableName , String selectionWhere,
	    String[] selectionArgs);  
      
    //使用 List<Map<String, String>> 做一个封装，比如说查询数据库的时候返回的多条记录  
    public List<Map<String, String>> listMyDataMaps(String tableName , String[] selectionArgs);  
}
