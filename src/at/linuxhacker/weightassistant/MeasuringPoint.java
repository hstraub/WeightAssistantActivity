package at.linuxhacker.weightassistant;

import java.util.Date;

public class MeasuringPoint {
	private Date date;
	private double weight;
	
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
}
