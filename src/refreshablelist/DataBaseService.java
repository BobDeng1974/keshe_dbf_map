package refreshablelist;

import java.util.List;
import java.util.Map;

public interface DataBaseService {
    
    public boolean addMyData(Object[] params);   
    
    public boolean deleteMyData(Object[] params);  
      
    public boolean updateMyData(Object[] params);  
      
    //使用 Map<String, String> 做一个封装，比如说查询数据库的时候返回的单条记录  
    public Map<String, String> viewMyData(String selectionWhere ,String[] selectionArgs);  
      
    //使用 List<Map<String, String>> 做一个封装，比如说查询数据库的时候返回的多条记录  
    public List<Map<String, String>> listMyDataMaps(String[] selectionArgs);  
}
