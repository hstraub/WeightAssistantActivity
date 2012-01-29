package at.linuxhacker.weightassistant;

import android.app.ListActivity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class DisplayData extends ListActivity {
	
	private static final int DIALOG_ID = 100;
	private static final String fields[] = { DbHelper.C_DATETIME,
		DbHelper.C_GEWICHT };
	private CursorAdapter dataSource;
	
	@Override
	public void onCreate( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );
		DbHelper helper = new DbHelper( this );
		SQLiteDatabase db = helper.getReadableDatabase( );
		Cursor data = db.query( DbHelper.TABLE, fields, null, null, null, null, null );
		
		this.dataSource = new SimpleCursorAdapter( this, R.layout.displaydata, data,
				fields,
				new int[] { R.id.displayDataDate, R.id.displayDataGewicht } );
		
		ListView view = getListView( );
		view.setHeaderDividersEnabled( true );
		view.addHeaderView( getLayoutInflater( )
				.inflate( R.layout.displaydata, null ) );
		setListAdapter( dataSource );
	}
	
	/*
	@Override
	public boolean onCreateOptionsMenu( Menu menu ) {
		menu.add( 0, DIALOG_ID, 1, R.string.addItem );
		return true;
	}
	
	@Override
	public boolean onMenuItemSelected( int freatureId, MenuItem item ) {
		if ( item.getItemId( ) == DIALOG_ID ) {
			showDialog( DIALOG_ID );
		}
		return true;
	}
	*/

}
