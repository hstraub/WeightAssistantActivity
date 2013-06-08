package at.linuxhacker.weightassistant;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WeightMeasurmentSerieStatistics {
	private List<MeasuringPoint> originalMeasurmentSeries;
	private List<MeasuringPoint> measurmentSeries;
	//private List<List<Double>> measurmentSeriesDerivatives;
	private int[] calcOrders;
	private List<List<Double>> calcSeries;
	//private List<List<Double>> derivativeSeries;

	
	WeightMeasurmentSerieStatistics( List<MeasuringPoint> measurmentSeries, int[] calcOrders ) {
		this.originalMeasurmentSeries = measurmentSeries;
		this.calcOrders = calcOrders;
	}
	
	int sizeOfStatisticSeries( ) {
		return this.measurmentSeries.size( );
	}
	
	MeasuringPoint getMeasuringPoint( int position ) {
		return this.measurmentSeries.get( position );
	}
	
	double getAverageFor( int position, int order ) {
		return this.calcSeries.get( order ).get( position );
	}
	
	void calcAverageSeries( ) {
		int pos, seriesNr, minListLength = -1;
		this.measurmentSeries = new ArrayList<MeasuringPoint> ();
		this.calcSeries = ( List ) new ArrayList<List<Double>>( );
		List<List<Double>> calculatedSeries = ( List ) new ArrayList<List<Double>>( );

		for ( seriesNr = 0; seriesNr < this.calcOrders.length; seriesNr ++) {
			calculatedSeries.add( this.calcAverage( this.calcOrders[seriesNr] ) );
			if ( minListLength == -1 || minListLength > calculatedSeries.get( seriesNr ).size( ) ) {
				minListLength = calculatedSeries.get( seriesNr ).size( );
			}
		}

		for( pos = this.originalMeasurmentSeries.size( ) - minListLength;
				pos < this.originalMeasurmentSeries.size( );
				pos++ ) {
			this.measurmentSeries.add( this.originalMeasurmentSeries.get( pos ) );
		}
		
		for ( seriesNr = 0; seriesNr < this.calcOrders.length; seriesNr ++ ) {
			this.calcSeries.add( new ArrayList<Double>( ) );
			for( pos = calculatedSeries.get( seriesNr ).size( ) - minListLength;
					pos < calculatedSeries.get( seriesNr ).size( );
					pos++ ) {
				this.calcSeries.get( seriesNr ).add( calculatedSeries.get( seriesNr ).get( pos ) );
			}
		}

		// FIXME: remove the next line
		pos = 50;
	}

	private List<Double> calcAverage( int order ) {
		List<Double> series = new ArrayList<Double>( );
		if ( this.originalMeasurmentSeries.size() < order ) {
			return series;
		}
		
		for ( int i = this.originalMeasurmentSeries.size( ) -1; i >= order -1; i-- ) {
			double average = this.originalMeasurmentSeries.get( i ).getWeight( );
			for ( int j = 1; j < order; j++ ) {
				average += this.originalMeasurmentSeries.get( i - j ).getWeight( );
			}
			average = average / order;
			series.add( average );
		}
		Collections.reverse( series );
		return series;
	}
	
}
