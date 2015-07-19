package utilities;

import testing.Model;

// import java.util.function.DoubleUnaryOperator; // not compatible with Java 1.7

/****
 * Represents an arbitrarily shaped, 1-dimensional Probability Density Function.
 * Supply a DoubleUnaryOperator class that returns the probability density for
 * a given value.
 * 
 * @author daniel
 *
 */
public class Pdf {

	/**
	 * @param ipdf functional class whose apply function returns the probability density at that point
	 * @param istart the value below which probability is assumed to be zero
	 * @param iend   the value above which probability is assumed to be zero
	 */
	public Pdf(double istart, double iend, DoubleUnaryOperator ipdf) {
		this(istart, iend, ipdf, DEFAULT_CDF_SAMPLES);
	}

	/**
	 * @param ipdf functional class whose apply function returns the probability density at that point
	 * @param istart the value below which probability is assumed to be zero
	 * @param iend   the value above which probability is assumed to be zero
	 * @param NSamples the number of samples of the PDF to take in order to build the CDF
	 */
	public Pdf(double istart, double iend, DoubleUnaryOperator ipdf, int NSamples) {
		pdf = ipdf;
		start = istart;
		end = iend;
		nSamples = NSamples;
		initInverseCDF();
	}
	
	/***
	 * Get probability density P(x)
	 * @param x 
	 * @return P(x)
	 */
	public double p(double x) {
		return(pdf.applyAsDouble(x));
	}
	
	
	private void initInverseCDF() {
		double cp;		// cumulative proability
		double targetcp;// target cumulative probability
		double x;		// x in P(x)
		int INTEGRATION_STEPS = 2048;
		double dcp_dx;
		int i;

		inverseCDF = new double[nSamples];
		dx = (end-start)/INTEGRATION_STEPS;
		x = start + dx/2.0;
		cp = 0.0;
		dcp_dx = 0.0;
		inverseCDF[0] = start;
		inverseCDF[nSamples-1] = end;
		for(i=1; i<(nSamples-1); ++i) {
			targetcp = i/(nSamples-1.0);
			while(cp < targetcp && x < end) {
				dcp_dx = p(x);
				cp += dcp_dx*dx;
				x += dx;
			}
			if(x < end) {
				x += (targetcp - cp)/dcp_dx;
				cp = targetcp;
			} else {
				x = end;
			}
			inverseCDF[i] = x;
		}
	}
	
	/***
	 * Sample from the PDF
	 * @return A random sample from the PDF
	 */
	public double nextDouble() {
		double uniform = Model.root.random.nextDouble(); // uniform random sample on [0:1)
		int i = (int)(uniform*(nSamples-1));
		double remainder = uniform*(nSamples-1.0) - i;
		return((1.0-remainder)*inverseCDF[i] + remainder*inverseCDF[i+1]);
	}
	
	DoubleUnaryOperator	pdf;				// function that gives the pdf
	public double		start;				// lowest value of x that has a non-zero probability
	public double		end;				// highest value of x that has a non-zero probability
	double []			inverseCDF;			// pre-computed equi-spaced points on the inverse CDF including 0 and 1
	double 				dx;					// dx between samples
	int					nSamples;			// number of sample	points on the CDF
	static final int	DEFAULT_CDF_SAMPLES = 100;
}
