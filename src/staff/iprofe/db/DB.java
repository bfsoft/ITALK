package staff.iprofe.db;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;

import android.util.Log;

interface iDB {
    int version = 1;
    String name = "ITALK.db";
    String[] tables = {
    	"friend_list",
    	"task_list"
    };
}

public class DB implements iDB {
	private HashMap<String , ArrayList<String>> map = new HashMap<String , ArrayList<String>>(); 
	
	private String tb;
	
	public DB(final String table)
	{
		
		this.tb = table;
		if (map.containsValue(table)) 
			return;
			
		Field[] fields;
		ArrayList<String> str = new ArrayList<String>();
		try {
			fields = Class.forName(getClass().getPackage().getName() +"."+ table).getFields();
			if (0 < fields.length) {
				for (Field field:fields) {
					try {
						str.add(field.getName() +" "+ field.get(field.getName()));
					} catch (IllegalArgumentException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			} 
			
			map.put(table, str);
				
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public ArrayList<String> getFieldList()
	{
		if (map.containsKey(tb))
		    return map.get(tb); 
		else 
			return new ArrayList<String>();
	}
	
	public static void log(final String msg)
	{
		Log.v("DB", msg);
	}
}

interface TABLE {
	String id = "integer primary key"; 
}

interface friend_list extends TABLE {
	String data = "varchar";
	String uid = "integer not null UNIQUE";
	String sort_key = "varchar";
}

interface task_list extends TABLE {
	String content = "varchar";
	String properties = "varchar";
}