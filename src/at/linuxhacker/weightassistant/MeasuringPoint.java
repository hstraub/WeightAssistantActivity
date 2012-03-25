package at.linuxhacker.weightassistant;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MeasuringPoint {
	private Date date;
	private double weight;
	private SimpleDateFormat yearFormat = new SimpleDateFormat( "yyyy");
	private DecimalFormat decFormat = new DecimalFormat( "00" );
	
	MeasuringPoint( Date timestamp, double weight ) {
		this.date = timestamp;
		this.weight = weight;
	}
	
	public Date getDate( ) {
		return this.date;
	}
	
	public double getWeight( ) {
		return this.weight;
	}
	
	String getWeekOfYear( ) {
		String weekOfYear;
		Calendar calendar = Calendar.getInstance( );
		calendar.setFirstDayOfWeek( Calendar.MONDAY );
		calendar.setTime( this.date );
		int week = calendar.get( Calendar.WEEK_OF_YEAR );
		int year = calendar.get( Calendar.YEAR );
		int doy = calendar.get( Calendar.DAY_OF_YEAR );
		if (week == 52 && doy < 6 ) {
			year--;
		}
		String tmp = date.toString( );
		//weekOfYear = this.yearFormat.format( this.date ) + "/" + this.decFormat.format( week );
		weekOfYear = "" + year + "/" + this.decFormat.format( week );
		System.out.println( "getWeekOfYear: " + weekOfYear );
		return weekOfYear;
	}
}
