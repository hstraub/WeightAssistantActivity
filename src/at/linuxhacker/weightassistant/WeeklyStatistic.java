package at.linuxhacker.weightassistant;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class WeeklyStatistic {
	List<MeasuringPoint> weeklyPoints = new ArrayList( );
	private String weekOfYear;
	double min = 0;
	double max = 0;
	double sum = 0;
	double average = 0;
	Date montag;
	
	WeeklyStatistic( String weekOfYear ) {
		this.weekOfYear = weekOfYear;
	}
		
	public void addMeasurePoint( MeasuringPoint point ) throws Exception {

		if ( this.weekOfYear.compareTo( point.getWeekOfYear( ) ) > 0 ) {
			System.out.println( "throw Exception in addMeasurePoint" );
			throw new Exception( "New Measure Point is out of this.weekOfYear" );
		}

		this.weeklyPoints.add( point );
		this.sum += point.getWeight( );
		this.average = this.sum / this.weeklyPoints.size( );
		if ( this.min == 0 || this.min > point.getWeight( ) ) {
			this.min = point.getWeight( );
		}
		if (this.max == 0 || this.max < point.getWeight( ) ) {
			this.max = point.getWeight( );
		}
	}
	
	String getWeekOfTheYear( ) {
		return this.weekOfYear;
	}
	
	String getWeekOfTheYearWithoutYear( ) {
		String test;
		test = this.weekOfYear.substring( 5 );
		return test;
	}
	
	MeasuringPoint findMeasuringPointForDayOfWeek( int dayOfWeek ) {
		MeasuringPoint point = null;
		int i;
		Calendar calendar = Calendar.getInstance( );
		
		for ( i = 0; i < this.weeklyPoints.size( ); i++ ) {
			point = this.weeklyPoints.get( i ); 
			calendar.setTime( point.getDate( ) );
			if ( calendar.get( Calendar.DAY_OF_WEEK ) == dayOfWeek ) {
				break;
			}
		}
		
		if ( i == this.weeklyPoints.size( ) ) {
			return null;
		} else {
		 return point;
		}
	}
}
