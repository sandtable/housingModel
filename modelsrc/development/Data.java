package development;

import org.apache.commons.math3.distribution.LogNormalDistribution;


public class Data {
	public static class HousingMarket {
		public static final double HPI_LOG_MEDIAN = Math.log(195000); // Median price from ONS: 2013 housse price index data tables table 34
		public static final double HPI_SHAPE = 0.555; // shape parameter for lognormal dist. ONS: 2013 house price index data tables table 34
		public static final double HPI_MEAN = Math.exp(HPI_LOG_MEDIAN + HPI_SHAPE*HPI_SHAPE/2.0);
		public static LogNormalDistribution listPriceDistribution = new LogNormalDistribution(HPI_LOG_MEDIAN, HPI_SHAPE);

		static public long referencePrice(int q) {
			return((long)(100*listPriceDistribution.inverseCumulativeProbability((q+0.5)/House.Config.N_QUALITY) * 0.9));
		}
	}
}
