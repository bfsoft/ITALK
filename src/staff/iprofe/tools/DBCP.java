package staff.iprofe.tools;

import staff.iprofe.db.DB;
import staff.iprofe.db.SQLiteHelper;
import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

public class DBCP extends ContentProvider {
	private static final UriMatcher matcher; 
	private SQLiteHelper sqlLite;
	private SQLiteDatabase db;
	
	private String FRIEND_TABLE;
	private String TASK_TABLE;
	
	private static final String AUTHORITY = "staff.iprofe.tools.DBCP";
	
	private static final int FRIEND_ALL = 0;  
    private static final int FRIEND_ONE = 1;
    
    private static final int TASK_ALL = 2;
    private static final int TASK_ONE = 3;
	
	public static final String FRIEND_CONTENT_TYPE = "vnd.android.cursor.dir/vnd.scott.friend";  
    public static final String FRIEND_CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.scott.friend";

    public static final String TASK_CONTENT_TYPE = "vnd.android.cursor.dir/vnd.scott.task";  
    public static final String TASK_CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.scott.task";
    
	public final static Uri CONTENT_URI_FRIEND = Uri.parse("content://" + AUTHORITY + "/friend_list");
	public final static Uri CONTENT_URI_TASK = Uri.parse("content://" + AUTHORITY + "/task_list"); 
      
    static {  
        matcher = new UriMatcher(UriMatcher.NO_MATCH);  
          
        matcher.addURI(AUTHORITY, "friend_list", FRIEND_ALL);   //匹配记录集合  
        matcher.addURI(AUTHORITY, "friend_list/#", FRIEND_ONE); //匹配单条记录  
        
        matcher.addURI(AUTHORITY, "task_list", TASK_ALL);   //匹配记录集合  
        matcher.addURI(AUTHORITY, "task_list/#", TASK_ONE); //匹配单条记录  
    } 
	
	@Override
	public int delete(Uri arg0, String arg1, String[] arg2) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getType(Uri uri) {
		// TODO Auto-generated method stub
		int match = matcher.match(uri);  
        switch (match) {  
        case FRIEND_ALL:  
            return FRIEND_CONTENT_TYPE;  
        case FRIEND_ONE:  
            return FRIEND_CONTENT_ITEM_TYPE;  
        case TASK_ALL:
        	return TASK_CONTENT_TYPE;
        case TASK_ONE:
        	return TASK_CONTENT_ITEM_TYPE;
        default:
            throw new IllegalArgumentException("Unknown URI: " + uri);  
        }
        
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		// TODO Auto-generated method stub
		
		db = sqlLite.getReadableDatabase();
		int match = matcher.match(uri);  
		switch (match) {  
	        case FRIEND_ALL:  
	            break;  
	        case FRIEND_ONE:  
	            long uid = ContentUris.parseId(uri);  
	            
	            String[] columns = new String[]{"id"};
	            String selection = "uid = ?";
	            String[] selectionArgs = new String[]{String.valueOf(uid)};
	            
	            Cursor c = db.query(FRIEND_TABLE, columns, selection, selectionArgs, null, null, null);
	            db = sqlLite.getWritableDatabase();
	            if (1 == c.getCount()) {
	            	
	            	db.update(FRIEND_TABLE, values, selection, selectionArgs);
	            } else {
	            	long rowId = db.insert(FRIEND_TABLE, null, values);  
	                if (rowId > 0) {
	                    return ContentUris.withAppendedId(uri, rowId);  
	                }
	            }
	            break;  
	        case TASK_ALL:
	        	break;
	        case TASK_ONE:
	        	break;
	        default:  
	            throw new IllegalArgumentException("Unknown URI: " + uri);  
	     }   
        
		 return null;
	}

	@Override
	public boolean onCreate() {
		// TODO Auto-generated method stub
		sqlLite = new SQLiteHelper(getContext(), DB.name, null, DB.version);
		this.FRIEND_TABLE = DB.tables[0];
		this.TASK_TABLE = DB.tables[1];
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		// TODO Auto-generated method stub
		
		 db = sqlLite.getReadableDatabase();  
	     int match = matcher.match(uri);  
	     switch (match) {  
	        case FRIEND_ALL:  
	            break;  
	        case FRIEND_ONE:  
	            long uid = ContentUris.parseId(uri);  
	            selection = "uid = ?";  
	            selectionArgs = new String[]{String.valueOf(uid)};  
	            break;  
	        case TASK_ALL:
	        	break;
	        case TASK_ONE:
	        	break;
	        default:  
	            throw new IllegalArgumentException("Unknown URI: " + uri);  
	     }  
	     
	     return db.query(FRIEND_TABLE, projection, selection, selectionArgs, null, null, sortOrder);
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		// TODO Auto-generated method stub
		return 0;
	}

}
