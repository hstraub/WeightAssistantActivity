package at.linuxhacker.weightassistant;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.achartengine.ChartFactory;
import org.achartengine.chart.BarChart.Type;
import org.achartengine.model.RangeCategorySeries;
import org.achartengine.model.TimeSeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.SimpleSeriesRenderer;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;

public class WeekOverviewGraph {
	private DbHelper dbHelper;
	private SQLiteDatabase db;
	private XYMultipleSeriesRenderer renderer;
	private XYMultipleSeriesDataset dataset;
	private List<WeekStatistic> weekStatisticList;
	
	public Intent execute( Context context ) {
		this.dbHelper = new DbHelper( context );
		this.db = dbHelper.getReadableDatabase( );
		this.dataset = new XYMultipleSeriesDataset();
		//this.renderer = new XYMultipleSeriesRenderer( );

		this.readDB( );
		this.setRangeSeries( );
		this.setChartSettings( );

		return ChartFactory.getRangeBarChartIntent(context, this.dataset,
				this.renderer, Type.DEFAULT, "Test" );
	}
	
	protected void setChartSettings( ) {
		this.renderer.setChartTitle( "KW Übersicht" );
		this.renderer.setXTitle( "KW" );
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
		TimeSeries timeSeries = ( TimeSeries ) new TimeSeries( "KW" );
		int length = this.weekStatisticList.size( );
		for ( int i = 0; i < length; i++ ) {
			timeSeries.add( 
					this.weekStatisticList.get( i ).weekPoints.get( 0 ).getDate(),
					this.weekStatisticList.get( i ).average
					);
		}
		
		this.dataset.addSeries( timeSeries );
	}

	protected XYMultipleSeriesRenderer buildBarRenderer(int[] colors) {
		XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
		renderer.setAxisTitleTextSize(16);
		renderer.setChartTitleTextSize(20);
		renderer.setLabelsTextSize(15);
		renderer.setLegendTextSize(15);
		int length = colors.length;
		for (int i = 0; i < length; i++) {
			SimpleSeriesRenderer r = new SimpleSeriesRenderer();
			r.setColor(colors[i]);
			renderer.addSeriesRenderer(r);
		}
		return renderer;
	}	
	
	protected void setRangeSeries( ) {
		RangeCategorySeries series = new RangeCategorySeries( "KW Übersicht" );
		int length = this.weekStatisticList.size( );
		for ( int i = 0; i < length; i++ ) {
			series.add( 
					this.weekStatisticList.get( i ).min,
					this.weekStatisticList.get( i ).max
					);
		}
		this.dataset.addSeries( series.toXYSeries( ) );
		int[] colors = new int[] { Color.CYAN };
		this.renderer = this.buildBarRenderer( colors );
		for ( int i = 0; i < length; i++ ) {
			this.renderer.addXTextLabel( i, "" + this.weekStatisticList.get( i ).weekOfYear );
		}
	    SimpleSeriesRenderer r = renderer.getSeriesRendererAt(0);
	    r.setDisplayChartValues(true);
	    r.setChartValuesTextSize(20);
	    r.setChartValuesSpacing(3);
	    r.setGradientEnabled(true);
	    r.setGradientStart(90, Color.GREEN);
	    r.setGradientStop(99, Color.RED);
	}
	
	protected void readDB( ) {
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
				if ( this.weekStatisticList.size( ) == 0 ) {
					this.weekStatisticList.add( new WeekStatistic( point ) );
				} else {
					try {
						this.weekStatisticList.get( this.weekStatisticList.size( ) -1 ).addMeasurePoint( point );
					} catch ( Exception e ) {
						this.weekStatisticList.add( new WeekStatistic( point ) );
					}
				}
				cur.moveToNext( );
			}
		} catch ( Exception e ) {
			Exception y;
			y = e;
		}
	}		
}
