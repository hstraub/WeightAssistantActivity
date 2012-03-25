package at.linuxhacker.weightassistant;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.achartengine.ChartFactory;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.TimeSeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.Cursor;
import android.graphics.Color;

public class WeightOverviewGraph {
	
	private DbHelper dbHelper;
	private SQLiteDatabase db;
	private XYMultipleSeriesRenderer renderer;
	private XYMultipleSeriesDataset dataset;
	private List<MeasuringPoint> measurmentSeries;
	private List<Double> series3;
	private List<Double> series5;

	public Intent execute(Context context) {
		this.dbHelper = new DbHelper( context );
		this.db = dbHelper.getReadableDatabase( );
		this.dataset = new XYMultipleSeriesDataset();
		this.renderer = new XYMultipleSeriesRenderer( );

		this.readDB( );
		this.series3 = this.calcAverage( 3 );
		this.series5 = this.calcAverage( 5 );
		setChartSettings( );
		setSeries( );
		return ChartFactory.getTimeChartIntent(context, this.dataset,
				this.renderer, "yyyy-MM-dd");
	}
	
	protected void setChartSettings( ) {
		this.renderer.setChartTitle( "Mein Gewicht" );
		this.renderer.setXTitle( "Datum" );
		this.renderer.setYTitle( "Gewicht in kg" );
		this.renderer.setAxesColor( Color.GRAY );
		this.renderer.setLabelsColor( Color.LTGRAY );
		this.renderer.setYLabels(10);
		this.renderer.setAxisTitleTextSize( 24 );
		this.renderer.setChartTitleTextSize( 28 );
		this.renderer.setLabelsTextSize( 23 );
		this.renderer.setLegendTextSize( 23 );
		this.renderer.setPointSize(5f);
		// top, left, button, right
		this.renderer.setMargins(new int[] { 20, 40, 20, 30 });
	}
	
	protected void setSeries( ) {
		TimeSeries timeSeries = ( TimeSeries ) new TimeSeries( "Gewicht" );
		TimeSeries timeSeries3 = ( TimeSeries ) new TimeSeries( "3. Ordnung" );
		TimeSeries timeSeries5 = ( TimeSeries ) new TimeSeries( "5. Ordnung" );
		
		int length3 = this.series3.size( );
		int length5 = this.series5.size( );
		int maxLength = this.measurmentSeries.size( );
		for ( int i = 0; i < maxLength; i++ ) {
			int pos3 = length3 - maxLength + i;
			int pos5 = length5 - maxLength + i;
			if ( pos5 < 0 || pos3 < 0 ) {
				continue;
			}
			Date time = this.measurmentSeries.get( i ).getDate( );
			timeSeries.add( time, this.measurmentSeries.get( i ).getWeight( ) );
			timeSeries3.add( time, this.series3.get( pos3 ) );
			timeSeries5.add( time, this.series5.get( pos5 ) );
		}
		
		this.dataset.addSeries( timeSeries );
		XYSeriesRenderer r = new XYSeriesRenderer( );
		r.setColor( Color.CYAN );
		r.setPointStyle( PointStyle.TRIANGLE );
		this.renderer.addSeriesRenderer( r );
		
		this.dataset.addSeries( timeSeries3 );
		r = new XYSeriesRenderer( );
		r.setColor( Color.RED );
		r.setPointStyle( PointStyle.DIAMOND );
		this.renderer.addSeriesRenderer( r );		
		
		this.dataset.addSeries( timeSeries5 );
		r = new XYSeriesRenderer( );
		r.setColor( Color.YELLOW );
		r.setPointStyle( PointStyle.TRIANGLE );
		r.setLineWidth( 5 );
		this.renderer.addSeriesRenderer( r );		
	}
	
	protected List<Double> calcAverage( int order ) {
		List<Double> series = new ArrayList<Double>( );
		if ( this.measurmentSeries.size() < order ) {
			return series;
		}
		
		for ( int i = this.measurmentSeries.size( ) -1; i >= order -1; i-- ) {
			double average = this.measurmentSeries.get( i ).getWeight( );
			for ( int j = 1; j < order; j++ ) {
				average += this.measurmentSeries.get( i - j ).getWeight( );
			}
			average = average / order;
			series.add( average );
		}
		Collections.reverse( series );
		return series;
	}

	protected void readDB( ) {
		this.measurmentSeries = new ArrayList<MeasuringPoint>( );
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
				this.measurmentSeries.add( new MeasuringPoint( myDate, cur.getDouble( 2 ) ) );
				cur.moveToNext( );
			}
		} catch ( Exception e ) {
			Exception y;
			y = e;
		}
	}
}
