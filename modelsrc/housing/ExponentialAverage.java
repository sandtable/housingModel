package housing;

public class ExponentialAverage {
	public ExponentialAverage(double decayConst) {
		this(decayConst,0.0);
	}

	public ExponentialAverage(double decayConst, double initialAverage) {
		k = decayConst;
		oneminusk = 1.0-k;
		average = initialAverage;
	}
	
	public void record(double val) {
		average = average*k + val*oneminusk;
	}
	
	public double value() {
		return(average);
	}
	
	public double			average; // average = sum weight*recorded_value
	protected final double 	k; // weight = e^{kt} where t is age of data point (in number of newer data points)
	protected final double  oneminusk;
}
