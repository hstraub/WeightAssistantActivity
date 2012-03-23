package at.linuxhacker.weightassistant;

import java.util.Formatter;
import java.util.Locale;

import android.app.Activity;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class AddEntry extends Activity {
    private Toast datum;
    private Button buttonSave;
    private TextView kg;
    private DatePicker datePicker;
    private TimePicker timePicker;
    private String dateIso;
    private double gewicht;
    DbHelper dbHelper;
    SQLiteDatabase db;
    
   
	public void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.addentry );
      
        this.timePicker = ( TimePicker ) findViewById( R.id.entrytimePicker1 );
        this.timePicker.setIs24HourView( true );
        this.datePicker = ( DatePicker ) findViewById( R.id.entrydatePicker1 );
        this.dateIso = "";
        
        this.dbHelper = new DbHelper( this );
        this.db = this.dbHelper.getWritableDatabase( );
        
        buttonSave = ( Button ) findViewById( R.id.buttonSave );
        buttonSave.setEnabled( false );
        buttonSave.setOnClickListener( new View.OnClickListener() {		
			@Override
			public void onClick(View v) {
				StringBuilder sb = new StringBuilder( );
				Formatter f = new Formatter( sb, Locale.GERMAN );
				f.format( "%4d-%02d-%02d %02d:%02d:00", 
						AddEntry.this.datePicker.getYear( ),
						AddEntry.this.datePicker.getMonth( ) + 1,
						AddEntry.this.datePicker.getDayOfMonth( ),
						AddEntry.this.timePicker.getCurrentHour( ),
						AddEntry.this.timePicker.getCurrentMinute( )
						);
				AddEntry.this.dateIso = sb.toString( );
				try {
					AddEntry.this.gewicht = Double.parseDouble( AddEntry.this.kg.getText( ).toString( ) );
				} catch ( NumberFormatException e ) {
					return;
				}
				ContentValues values = new ContentValues( );
				values.put( DbHelper.C_DATETIME, AddEntry.this.dateIso );
				values.put( DbHelper.C_GEWICHT, AddEntry.this.gewicht );
				AddEntry.this.db.insertOrThrow( dbHelper.TABLE, null, values );
				AddEntry.this.db.close( );

    			AddEntry.this.datum = Toast.makeText( AddEntry.this,
    					sb,
    					Toast.LENGTH_LONG );
    			AddEntry.this.datum.show( );
    			AddEntry.this.finish( );
    			
			}
		});
        
        kg = ( TextView ) findViewById( R.id.editWeight );
        kg.addTextChangedListener( new TextWatcher( ) {
        	@Override
        	public void onTextChanged( CharSequence s, int start, int before, int count ) {
        		String inputValue =  AddEntry.this.kg.getText( ).toString( );
        		//double value = Double.parseDouble( AddEntry.this.kg.getText( ).toString( ) );
        		if ( inputValue.length( ) == 0 ) {
        			AddEntry.this.buttonSave.setEnabled( false );
        			return;
        		}
        		double value = Double.parseDouble( inputValue );
        		if ( value != 0 ) {
        			AddEntry.this.buttonSave.setEnabled( true );
        		}
        	}
        	
        	@Override
        	public void beforeTextChanged( CharSequence s, int start, int count, int after ) {
        		
        	}
        	
        	@Override
        	public void afterTextChanged( Editable s ) {
        		
        	}
        	
        } );
	}    	

}
