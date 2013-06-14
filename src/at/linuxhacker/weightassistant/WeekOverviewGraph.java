package at.linuxhacker.weightassistant;


import java.util.List;
import org.achartengine.ChartFactory;
import org.achartengine.chart.LineChart;
import org.achartengine.chart.PointStyle;
import org.achartengine.chart.RangeBarChart;
import org.achartengine.model.RangeCategorySeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYValueSeries;
import org.achartengine.renderer.SimpleSeriesRenderer;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;


public class WeekOverviewGraph {
	private XYMultipleSeriesRenderer renderer;
	private XYMultipleSeriesDataset dataset;
	private WeightMeasurmentSeries weightMeasurmentSeries;
	private double min;
	private double max;
	
	public Intent execute( Context context ) {
		String[] types = new String[] { RangeBarChart.TYPE, LineChart.TYPE };
		this.dataset = new XYMultipleSeriesDataset();
		
		this.determineMinMaxValues( );
		this.setRangeSeries( );
		this.setKwAverage( );
		this.setChartSettings( );

		return ChartFactory.getCombinedXYChartIntent( context, this.dataset,
				this.renderer, types, "Weight Assistant Wochenstatistik" );
	}
	
	private void determineMinMaxValues() {
		List<WeeklyStatistic> weeklyStatisticList = this.weightMeasurmentSeries.getWeeklyStatisticList( );
		int length = weeklyStatisticList.size( );
		
		for ( int i = 0; i < length; i++ ) {
			if ( this.min == 0 || this.min > weeklyStatisticList.get( i ).min ) {
				this.min = weeklyStatisticList.get( i ).min;
			}
			if ( this.max == 0 || this.max < weeklyStatisticList.get( i ).max ) {
				this.max = weeklyStatisticList.get( i ).max;
			}
		}		
	}

	public void setWeightMeasurmentSeries( WeightMeasurmentSeries weightMeasurmentSeries ) {
		this.weightMeasurmentSeries = weightMeasurmentSeries;
	}
	
	protected void setChartSettings( ) {
		this.renderer.setChartTitle( "Wochenstatistik" );
		this.renderer.setXTitle( "KW" );
		this.renderer.setYTitle( "Gewicht in kg" );
		this.renderer.setAxesColor( Color.BLACK);
		this.renderer.setLabelsColor( Color.BLACK );
		this.renderer.setBackgroundColor( Color.WHITE );
		this.renderer.setApplyBackgroundColor( true );
		this.renderer.setMarginsColor( 0xffeeeeee );
		renderer.setXLabelsColor( Color.BLACK );
		renderer.setYLabelsColor( 0, Color.BLACK );
		this.renderer.setGridColor( 0x77333333 );
		this.renderer.setShowGrid( true );
		this.renderer.setZoomButtonsVisible( true );

		this.renderer.setXLabels( 7 );
		this.renderer.setYLabels( 7 );
		this.renderer.setAxisTitleTextSize( 24 );
		this.renderer.setLabelsTextSize( 20 );
		this.renderer.setLegendTextSize( 23 );
		this.renderer.setPointSize(5f);
		// top, left, button, right
		this.renderer.setMargins(new int[] { 20, 40, 20, 30 });
	}
	
	protected XYMultipleSeriesRenderer buildBarRenderer(int[] colors) {
		XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer( );
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
	
	protected void setKwAverage( ) {
		List<WeeklyStatistic> weeklyStatisticList = this.weightMeasurmentSeries.getWeeklyStatisticList( );
		XYValueSeries series = new XYValueSeries( "Durchschnitt" );
		int length = weeklyStatisticList.size( );
		for ( int i = 0; i < length; i++ ) {
			series.add(
					i + 1,
					weeklyStatisticList.get( i ).average
					);
		}
		this.dataset.addSeries( series );
		XYSeriesRenderer avgRenderer = new XYSeriesRenderer( );
		avgRenderer.setColor( Color.YELLOW );
		avgRenderer.setPointStyle( PointStyle.CIRCLE );
		avgRenderer.setFillPoints( true );
		avgRenderer.setLineWidth( 3 );
		this.renderer.addSeriesRenderer( avgRenderer );
	}
	
	protected void setRangeSeries( ) {
		RangeCategorySeries series = new RangeCategorySeries( "KW Übersicht" );
		List<WeeklyStatistic> weeklyStatisticList = this.weightMeasurmentSeries.getWeeklyStatisticList( );
		int length = weeklyStatisticList.size( );
		for ( int i = 0; i < length; i++ ) {
			series.add( 
					weeklyStatisticList.get( i ).min,
					weeklyStatisticList.get( i ).max
					);
		}
		this.dataset.addSeries( series.toXYSeries( ) );
		int[] colors = new int[] { Color.BLUE };
		this.renderer = this.buildBarRenderer( colors );
		for ( int i = 0; i < length; i++ ) {
			String label = weeklyStatisticList.get( i ).getWeekOfTheYearWithoutYear( );
			this.renderer.addXTextLabel( i + 1 , label ); // Der Arsch startet mit 1 !!!
		}
	    SimpleSeriesRenderer r = renderer.getSeriesRendererAt(0);
	    r.setDisplayChartValues(true);
	    r.setChartValuesTextSize(20);
	    r.setChartValuesSpacing(3);
	    r.setGradientEnabled(true);
	    r.setGradientStart( this.min - 1, Color.GREEN);
	    r.setGradientStop( this.max + 1, Color.RED);
	}
}
