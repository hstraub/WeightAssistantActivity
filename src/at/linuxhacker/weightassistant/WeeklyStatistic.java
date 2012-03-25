package at.linuxhacker.weightassistant;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class WeeklyStatistic {
	List<MeasuringPoint> weeklyPoints = new ArrayList( );
	int weekOfYear = -1;
	double min = 0;
	double max = 0;
	double sum = 0;
	double average = 0;
	Date montag;
	
	WeeklyStatistic( MeasuringPoint point ) throws Exception {
		this.addMeasurePoint( point );
	}
		
	public void addMeasurePoint( MeasuringPoint point ) throws Exception {
		Calendar calendar = Calendar.getInstance( );
		calendar.setTime( point.getDate( ) );
		int week = calendar.get( Calendar.WEEK_OF_YEAR );
		
		if ( this.weeklyPoints.size( ) > 0 ) {
			if ( this.weekOfYear != -1 && week != this.weekOfYear ) {
				throw new Exception( "New Measure Point is out of this.weekOfYear" );
			}
			
		}
		if( this.weekOfYear == -1 ) {
			this.weekOfYear = week;
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
}
