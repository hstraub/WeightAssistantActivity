package at.linuxhacker.weightassistant;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.achartengine.ChartFactory;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.TimeSeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
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

	public Intent execute(Context context) {
		this.dbHelper = new DbHelper( context );
		this.db = dbHelper.getReadableDatabase( );
		this.dataset = new XYMultipleSeriesDataset();
		this.renderer = new XYMultipleSeriesRenderer( );

		this.readDB( );
		setChartSettings( );
		setFirstSerie( );
		setSecondSerie( );
		setThirdSerie( );
		//setMinMaxChartSettings( );
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
	
	protected void setFirstSerie( ) {
		XYSeriesRenderer r = new XYSeriesRenderer( );
		r.setColor( Color.CYAN );
		r.setPointStyle( PointStyle.TRIANGLE );
		this.renderer.addSeriesRenderer( r );		
	}
	
	protected void setSecondSerie( ) {
		TimeSeries series = ( TimeSeries ) this.dataset.getSeriesAt( 0 );
		TimeSeries series3 = new TimeSeries( "3. Ordnung" );
		series3.add( series.getX( 0 ), series.getY( 0 ) );
		int length = series.getItemCount( );
		for ( int i = 1; i < length - 1; i++ ) {
			double y = series.getY( i -1 ) + series.getY( i ) + series.getY( i + 1 );
			y = y / 3;
			series3.add( series.getX( i ), y );
		}
		series3.add(series.getX( length -1  ), series.getY( length -1 ) );
		this.dataset.addSeries( series3 );
		XYSeriesRenderer r = new XYSeriesRenderer( );
		r.setColor( Color.RED );
		r.setPointStyle( PointStyle.DIAMOND );
		this.renderer.addSeriesRenderer( r );
	}

	protected void setThirdSerie( ) {
		TimeSeries series = ( TimeSeries ) this.dataset.getSeriesAt( 0 );
		TimeSeries series5 = new TimeSeries( "5. Ordnung" );
		series5.add( series.getX( 0 ), series.getY( 0 ) );
		series5.add( series.getX( 1 ), series.getY( 1 ) );
		int length = series.getItemCount( );
		for ( int i = 2; i < length - 2; i++ ) {
			double y = series.getY( i - 2 ) + series.getY( i - 1 ) + series.getY( i )
					+ series.getY( i + 1 ) + series.getY( i + 2 );
			y = y / 5;
			series5.add( series.getX( i ), y );
		}
		series5.add(series.getX( length -2  ), series.getY( length -2 ) );
		series5.add(series.getX( length -1  ), series.getY( length -1 ) );
		this.dataset.addSeries( series5 );
		XYSeriesRenderer r = new XYSeriesRenderer( );
		r.setColor( Color.YELLOW );
		r.setPointStyle( PointStyle.TRIANGLE );
		r.setLineWidth( 5 );
		this.renderer.addSeriesRenderer( r );
	}

	protected void readDB( ) {
		SimpleDateFormat dateFormat = new SimpleDateFormat( "yyyy-MM-dd" );
		Date myDate = null;
		TimeSeries series = new TimeSeries( new String( "Gewicht" ) );
		
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
				//myDate = dateFormat.parse( cur.getString( 1 ) );
				series.add( myDate, cur.getDouble( 2 ) );
				cur.moveToNext( );
			}
		} catch ( Exception e ) {
			Exception y;
			y = e;
		}
		this.dataset.addSeries( series );
	}
	
	// Ab da nur mehr obselete Methoden
	protected void setMinMaxChartSettings( ) {
		// Ist eigentlich gar nicht notwendig, in dieser Form
		TimeSeries series = ( TimeSeries ) this.dataset.getSeriesAt( 0 );

		this.renderer.setXAxisMin( series.getMinX( ) );
		this.renderer.setXAxisMax( series.getMaxX( ) );
		this.renderer.setYAxisMin( series.getMinY( ) );
		this.renderer.setYAxisMax( series.getMaxY( ) );		
	}

}
