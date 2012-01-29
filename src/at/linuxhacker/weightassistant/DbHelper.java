package at.linuxhacker.weightassistant;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

public class DbHelper extends SQLiteOpenHelper {
	static String TAG = "DbHelper";
	static String DB_NAME = "gewicht.db";
	static int DB_VERSION = 1;
	static String TABLE = "gewicht";
	static String C_ID = BaseColumns._ID;
	static String C_DATETIME = "datum";
	static String C_GEWICHT = "gewicht";
	Context context;

	public DbHelper( Context context ) {
		super( context, DB_NAME, null, DB_VERSION );
		this.context = context;
	}
	@Override
	public void onCreate( SQLiteDatabase db ) {
		String sql = "create table " + TABLE + " ( " + C_ID +
				" integer primary key autoincrement, " +
				C_DATETIME + " text, " +
				C_GEWICHT + " double )";
		
		db.execSQL( sql );

	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		// TODO: Das fehlt noch

	}

}
