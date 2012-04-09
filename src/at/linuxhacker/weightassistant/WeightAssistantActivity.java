package at.linuxhacker.weightassistant;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;

public class WeightAssistantActivity extends Activity {
	private static final int ACTIVITY_ADD_ENTRY = 1;
	private static final int DIALOG_IMPORT_FILE_ID = 0;
	private static String C_CSV_DIRNAME = "weighassistant";
	private static String C_CSV_FILENAME = "weightassistant.csv";
	private WeightOverviewGraph weightOverviewGraph = new WeightOverviewGraph( );
	private WeekOverviewGraph weekOverviewGraph = new WeekOverviewGraph( );
	private WeightMeasurmentSeries weightMeasurmentSeries;
	private TextView size = null;
	private TextView targetWeight = null;
	private TextView thisWeek = null;
	private TextView lastWeek = null;
	int dataUpdated = 0;
	private TextView lastWeekHeader;
	private TextView thisWeekHeader;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        this.weightMeasurmentSeries = new WeightMeasurmentSeries( WeightAssistantActivity.this );
        this.weightMeasurmentSeries.readAll( );
        
        this.fillPersonalData( );
        
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
				// FIXME: das muss woanders hin
				//WeightAssistantActivity.this.dataUpdated = 1;
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
    
    private void fillPersonalData() {
    	SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences( this );
        this.size = ( TextView ) findViewById( R.id.myData_size );
        this.size.setText( prefs.getString( "size", "k.A" ) );
        this.size = ( TextView ) findViewById( R.id.myData_targetWeight );
        this.size.setText( prefs.getString( "prefTargetWeight", "k.A" ) );		
        this.lastWeek = ( TextView ) findViewById( R.id.myData_lastWeek );
        this.lastWeekHeader = ( TextView ) findViewById( R.id.myData_lastWeekHeader );
        this.thisWeek = ( TextView ) findViewById( R.id.myData_thisWeek );
        this.thisWeekHeader = ( TextView ) findViewById( R.id.myData_thisWeekHeader );
        List<WeeklyStatistic> weeklyStatistic = this.weightMeasurmentSeries.getWeeklyStatisticList( );
        
        int weeks = weeklyStatistic.size( ) - 1;
        if ( weeks >= 3 ) {
        	DecimalFormat format = new DecimalFormat( "0.00" );
        	this.lastWeekHeader.setText( weeklyStatistic.get( weeks - 1 ).getWeekOfTheYear( ) );
        	this.lastWeek.setText( format.format( weeklyStatistic.get( weeks - 1 ).average )
        			+ " / " +
        			format.format( weeklyStatistic.get( weeks - 1 ).average -
        					weeklyStatistic.get( weeks - 2 ).average )
        			);
        	this.thisWeekHeader.setText( weeklyStatistic.get( weeks ).getWeekOfTheYear( ) );
        	this.thisWeek.setText( format.format( weeklyStatistic.get( weeks ).average )
        			+ " / " +
        			format.format( weeklyStatistic.get( weeks ).average -
        					weeklyStatistic.get( weeks -1  ).average )
        			);
        }
    }

	protected void onActivityResult( int requestCode, int resultCode, Intent data ) {
    	if ( requestCode == WeightAssistantActivity.ACTIVITY_ADD_ENTRY ) {
    		if ( resultCode == RESULT_OK ) {
    			this.dataUpdated = 1;
    			this.csvExport( );
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
    	String directoryname = Environment.getExternalStorageDirectory( ) + File.separator + this.C_CSV_DIRNAME;
    	File directory = new File( directoryname );
    	String[] filenames = directory.list( );
    	String filename = directoryname + File.separator + filenames[filenames.length - 1];	
    	
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
    	this.dataUpdated = 1;
    	Toast toast = Toast.makeText( this, "Import von " + i + " Record von File: "
    			+ filename, Toast.LENGTH_LONG );
    	toast.show( );
    }
    public void csvExport( ) {
    	int i = 0;
    	Date now = new Date( );
    	SimpleDateFormat format = new SimpleDateFormat( "yyyy-MM-dd_HH:mm" );
    	String filenamePrefix = new String( format.format( now ) );
    	
    	String directoryname = Environment.getExternalStorageDirectory( ) + File.separator + this.C_CSV_DIRNAME;
    	File directory = new File( directoryname );
    	directory.mkdirs( );
    	String filename = directoryname + File.separator + filenamePrefix + "-" + this.C_CSV_FILENAME;
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater( );
		inflater.inflate( R.menu.menu, menu);
		return true;
		// TODO Auto-generated method stub
		//return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch ( item.getItemId( ) ) {
		case R.id.itemPrefs:
			startActivity( new Intent( this, PrefsActivity.class ) );
			break;
		}
		return true;
	}
}