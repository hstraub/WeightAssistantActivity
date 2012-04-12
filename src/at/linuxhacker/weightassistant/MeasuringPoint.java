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
	private Calendar calendar;
	
	MeasuringPoint( Date timestamp, double weight ) {
		this.date = timestamp;
		this.weight = weight;
		this.calendar = Calendar.getInstance( );
		this.calendar.setFirstDayOfWeek( Calendar.MONDAY );
		this.calendar.setTime( this.date );
	}
	
	public Date getDate( ) {
		return this.date;
	}
	
	public double getWeight( ) {
		return this.weight;
	}
	
	String getWeekOfYear( ) {
		String weekOfYear;

		int week = calendar.get( Calendar.WEEK_OF_YEAR );
		int year = calendar.get( Calendar.YEAR );
		int doy = calendar.get( Calendar.DAY_OF_YEAR );
		
		if (week == 52 && doy < 6 ) {
			year--;
		}
		weekOfYear = "" + year + "/" + this.decFormat.format( week );
		return weekOfYear;
		
	}
	
	int getDayOfWeek( ) {
		return calendar.get( Calendar.DAY_OF_WEEK );
	}
}
