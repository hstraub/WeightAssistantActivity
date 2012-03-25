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
	private WeightMeasurmentSeries weightMeasurmentSeries;
	
	public Intent execute( Context context ) {
		this.dbHelper = new DbHelper( context );
		this.db = dbHelper.getReadableDatabase( );
		this.dataset = new XYMultipleSeriesDataset();

		this.setRangeSeries( );
		this.setChartSettings( );

		return ChartFactory.getRangeBarChartIntent(context, this.dataset,
				this.renderer, Type.DEFAULT, "Test" );
	}
	
	public void setWeightMeasurmentSeries( WeightMeasurmentSeries weightMeasurmentSeries ) {
		this.weightMeasurmentSeries = weightMeasurmentSeries;
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
		int length = this.weightMeasurmentSeries.weekStatisticList.size( );
		for ( int i = 0; i < length; i++ ) {
			timeSeries.add( 
					this.weightMeasurmentSeries.weekStatisticList.get( i ).weekPoints.get( 0 ).getDate(),
					this.weightMeasurmentSeries.weekStatisticList.get( i ).average
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
		int length = this.weightMeasurmentSeries.weekStatisticList.size( );
		for ( int i = 0; i < length; i++ ) {
			series.add( 
					this.weightMeasurmentSeries.weekStatisticList.get( i ).min,
					this.weightMeasurmentSeries.weekStatisticList.get( i ).max
					);
		}
		this.dataset.addSeries( series.toXYSeries( ) );
		int[] colors = new int[] { Color.CYAN };
		this.renderer = this.buildBarRenderer( colors );
		for ( int i = 0; i < length; i++ ) {
			this.renderer.addXTextLabel( i, "" + this.weightMeasurmentSeries.weekStatisticList.get( i ).weekOfYear );
		}
	    SimpleSeriesRenderer r = renderer.getSeriesRendererAt(0);
	    r.setDisplayChartValues(true);
	    r.setChartValuesTextSize(20);
	    r.setChartValuesSpacing(3);
	    r.setGradientEnabled(true);
	    r.setGradientStart(90, Color.GREEN);
	    r.setGradientStop(99, Color.RED);
	}
}
