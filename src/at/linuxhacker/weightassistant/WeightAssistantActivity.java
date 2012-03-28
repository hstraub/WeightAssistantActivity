package at.linuxhacker.weightassistant;

import java.io.FileReader;
import java.io.FileWriter;
import java.util.Arrays;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;

public class WeightAssistantActivity extends Activity {
	private static final int ACTIVITY_ADD_ENTRY = 1;
	private static String C_CSV_FILENAME = "/weightassistant.csv";
	private WeightOverviewGraph weightOverviewGraph = new WeightOverviewGraph( );
	private WeekOverviewGraph weekOverviewGraph = new WeekOverviewGraph( );
	private WeightMeasurmentSeries weightMeasurmentSeries;
	int dataUpdated = 0;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        this.weightMeasurmentSeries = new WeightMeasurmentSeries( WeightAssistantActivity.this );
        this.weightMeasurmentSeries.readAll( );
        
        Button buttonNewEntry = ( Button ) findViewById( R.id.buttonAddEntry );
        buttonNewEntry.setOnClickListener( new View.OnClickListener( ) {
			@Override
			public void onClick( View view ) {
				/* alt
	        	startActivity( new Intent( WeightAssistantActivity.this,
	        			AddEntry.class ) );
	        	*/
	        	startActivityForResult( new Intent( WeightAssistantActivity.this,
	        			AddEntry.class ), WeightAssistantActivity.ACTIVITY_ADD_ENTRY );
			}
		} );
        
        Button buttonDisplayData = ( Button ) findViewById( R.id.buttonDisplayData );
        buttonDisplayData.setOnClickListener( new View.OnClickListener( ) {
			@Override
			public void onClick(View v) {
				startActivity( new Intent( WeightAssistantActivity.this,
						DisplayData.class ) );	
			}
		} );
        
        Button buttonImport = ( Button ) findViewById( R.id.buttonImport );
        buttonImport.setOnClickListener( new View.OnClickListener( ) {
        	@Override
			public void onClick(View v) {
				WeightAssistantActivity.this.csvImport( );
				WeightAssistantActivity.this.dataUpdated = 1;
			}
		} );

        Button buttonExport = ( Button ) findViewById( R.id.buttonExport );
        buttonExport.setOnClickListener( new View.OnClickListener( ) {
        	@Override
			public void onClick(View v) {
				WeightAssistantActivity.this.csvExport( );
				
			}
		} );

        Button buttonSimpleGraph = ( Button ) findViewById( R.id.buttonSimpleGraph );
        buttonSimpleGraph.setOnClickListener( new View.OnClickListener( ) {
        	@Override
        	public void onClick(View v) {
        		Intent intent = null;
        		
        		WeightAssistantActivity.this.checkAndUpdateData( );
        		weightOverviewGraph.setWeightMeasurmentSeries( weightMeasurmentSeries );
        		intent = weightOverviewGraph.execute( WeightAssistantActivity.this );
        		startActivity( intent );

        	}
        } );

        Button buttonWeekOverviewGraph = ( Button ) findViewById( R.id.buttonWeekGraph );
        buttonWeekOverviewGraph.setOnClickListener( new View.OnClickListener( ) {
        	@Override
        	public void onClick(View v) {
        		Intent intent = null;
        		
        		WeightAssistantActivity.this.checkAndUpdateData( );
        		weekOverviewGraph.setWeightMeasurmentSeries( weightMeasurmentSeries );
        		intent = weekOverviewGraph.execute( WeightAssistantActivity.this );
        		startActivity( intent );

        	}
        } );
        
    }
    
    protected void onActivityResult( int requestCode, int resultCode, Intent data ) {
    	if ( requestCode == WeightAssistantActivity.ACTIVITY_ADD_ENTRY ) {
    		if ( resultCode == RESULT_OK ) {
    			this.dataUpdated = 1;
    		}
    	}
    }
    
    protected void checkAndUpdateData( ) {
    	if ( this.dataUpdated == 1 ) {
    		this.weightMeasurmentSeries.readAll( );
    		this.dataUpdated = 0;
    	}
    }
    
    public void csvImport( ) {
    	DbHelper dbHelper;
    	SQLiteDatabase db;
    	int i = 0;
    	String filename = Environment.getExternalStorageDirectory( ) 
				+ C_CSV_FILENAME;
    	
    	dbHelper = new DbHelper( this );
    	db = dbHelper.getWritableDatabase( );
    	db.delete(DbHelper.TABLE, "", null);    	
    	try {
	    	CSVReader reader = new CSVReader(
	    			new FileReader( filename ) );
	    	String [] nextLine;
	    	while( ( nextLine = reader.readNext( ) ) != null ) {
	    		ContentValues values = new ContentValues( );
	    		values.put( DbHelper.C_DATETIME, nextLine[0] );
	    		values.put( DbHelper.C_GEWICHT, nextLine[1] );
	    		db.insertOrThrow( DbHelper.TABLE, null, values );
	    		i++;
	    	}
    	} catch ( Exception e ){
    		Toast toast = Toast.makeText( this, "Fehler: " + e.getMessage( ), Toast.LENGTH_LONG );
    		toast.show( );
        }
    	db.close( );
    	Toast toast = Toast.makeText( this, "Import von " + i + " Record von File: "
    			+ filename, Toast.LENGTH_LONG );
    	toast.show( );
    }
    
    public void csvExport( ) {
    	int i = 0;
    	String filename = Environment.getExternalStorageDirectory( ) 
				+ C_CSV_FILENAME;
    	CSVWriter writer;
    	DbHelper dbHelper = new DbHelper( this );
    	SQLiteDatabase db = dbHelper.getReadableDatabase( );
    	Cursor cursor = db.query( DbHelper.TABLE,
    			null, null, null, null, null, null );
    	cursor.moveToFirst( );
    	
    	try {
    		writer = new CSVWriter(
    				new FileWriter( filename ) );
    		while( cursor.isAfterLast( ) == false ) {
    			String[] values = { cursor.getString( 1 ), cursor.getString( 2 ) };
	    		writer.writeNext( values );
	    		cursor.moveToNext( );
	    		i++;
    		}
    		writer.close( ); 
    	} catch ( Exception e ) {
    		Toast toast = Toast.makeText( this, "Fehler: " + e.getMessage( ), Toast.LENGTH_LONG );
    		toast.show( );
    	}
    	Toast toast = Toast.makeText( this, "Export von " + i + " Records ins File: "
    			+ filename, Toast.LENGTH_LONG );
    	toast.show( );
    }
}