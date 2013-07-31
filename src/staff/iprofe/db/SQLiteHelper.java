package staff.iprofe.db;

import java.util.ArrayList;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLiteHelper  extends SQLiteOpenHelper {

	public SQLiteHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, DB.version);
		DB.log("INIT DB");
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		DB.log("create table");
		for (String tb: DB.tables) 
			tableFactory(tb, (new DB(tb)).getFieldList(), db);
	}

	//delete table
	protected void onDelete(SQLiteDatabase db)
	{
		DB.log("create table");
		for (String tb: DB.tables) 
			db.execSQL("DROP TABLE IF EXISTS " + tb);
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		DB.log("Upgrading from version " + oldVersion + " to " + newVersion);
		
		onDelete(db);
		onCreate(db);
	}

	//new table factory
	protected void tableFactory(final String table, ArrayList<String> list, SQLiteDatabase db)
	{
		StringBuffer sb = new StringBuffer();
		int len=list.size(),i;
		for (i=0;i<len;i++) {
			sb.append(list.get(i));
			if (i < (len-1)) 
				sb.append(",");
		}
		
		StringBuffer sql = new StringBuffer();
		sql.append("create table if not exists ");
		sql.append(table);
		sql.append("(");
		sql.append(sb.toString());
		sql.append(")");
		
		db.execSQL(sql.toString());
	}

}
