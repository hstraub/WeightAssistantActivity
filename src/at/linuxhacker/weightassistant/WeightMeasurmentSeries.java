package at.linuxhacker.weightassistant;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class WeightMeasurmentSeries {
	private DbHelper dbHelper;
	private SQLiteDatabase db;
	public List<MeasuringPoint> measurmentSeries;
	public List<WeekStatistic> weekStatisticList;
	
	WeightMeasurmentSeries( Context context ) {
		this.dbHelper = new DbHelper( context );
		this.db = dbHelper.getReadableDatabase( );		
	}
	
	void readAll( ) {
		this.measurmentSeries = new ArrayList<MeasuringPoint>( );
		this.weekStatisticList = new ArrayList<WeekStatistic>( );
		SimpleDateFormat dateFormat = new SimpleDateFormat( "yyyy-MM-dd" );
		Date myDate = null;
		
		try {
			Cursor cur = this.db.query( this.dbHelper.TABLE, 
					new String[] { this.dbHelper.C_ID, 
					this.dbHelper.C_DATETIME, this.dbHelper.C_GEWICHT },
					null, null, null, null, null );
			cur.moveToFirst( );
			int rowCount = cur.getCount( );
			for( int i = 0; i < rowCount; i++ ) {
				String tmp =  cur.getString( 1 );
				myDate = dateFormat.parse( tmp );
				MeasuringPoint point = new MeasuringPoint( myDate, cur.getDouble( 2 ) );
				this.measurmentSeries.add( point );
				this.addPointToWeekly( point );
				cur.moveToNext( );
			}
		} catch ( Exception e ) {
			Exception y;
			y = e;
		}
	}
	
	protected void addPointToWeekly( MeasuringPoint point ) {
		try {
			if ( this.weekStatisticList.size( ) == 0 ) {
				this.weekStatisticList.add( new WeekStatistic( point ) );
			} else {
				try {
					this.weekStatisticList.get( this.weekStatisticList.size( ) -1 ).addMeasurePoint( point );
				} catch ( Exception e ) {
					this.weekStatisticList.add( new WeekStatistic( point ) );
				}
			}
		} catch ( Exception e ) {
			// FIXME
		}
	}
}
