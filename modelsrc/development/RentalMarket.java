package development;

import contracts.Contract;
import contracts.RentalMarketOffer;
import utilities.ExponentialAverage;
import utilities.ModelTime;

public class RentalMarket extends HousingMarket {
	double 						bestExpectedYield;
	HouseSaleMarket 			houseSaleMarket;
	public ExponentialAverage 	daysOnMarket[];
	public double 				expectedGrossYield[];

	public RentalMarket() {
		expectedGrossYield = new double[House.Config.N_QUALITY];
		daysOnMarket = new ExponentialAverage[House.Config.N_QUALITY];
		for(int i = 0; i<House.Config.N_QUALITY; ++i) {
			daysOnMarket[i] = new ExponentialAverage(G, 0.0);
		}
	}
	
	@Override
	public void start(IModelNode parent) {
		super.start(parent);
		houseSaleMarket = parent.mustFind(HouseSaleMarket.class);
	}
	
	@Override
	public Bids newBids() {
		return this.new Bids();
	}

	@Override
	public Offers newOffers() {
		return this.new Offers();
	}

	@Override
	public long referencePrice(int quality) {
		return(Data.HousingMarket.referenceRentalPrice(quality));
	}
	
	/*** @return maximum over quality of expected gross yeild */
	public double bestExpectedYield() {
		return bestExpectedYield;
	}

	/*** @return maximum over quality of expected gross yeild */
	public double expectedGrossYield(int quality) {
		return expectedGrossYield[quality];
	}

	/***
	 * @param quality Quality of the house
	 * @return Expected fraction of time that the house will be occupied, based on
	 *         a 12 month rental contract and the average number of days on the rental
	 *         market of a house of this quality.
	 */
	public double expectedOccupancy(int quality) {
		return(12.0*30.0/(12.0*30.0 + daysOnMarket[quality].value()));
	}
	
	@Override
	public void recordTransaction(Match match) {
		super.recordTransaction(match);
		daysOnMarket[match.offer.getQuality()].record(ModelTime.now().minus(match.offer.tInitialListing).inDays());
	}
	
	@Override
	protected void doQuarterlyStats() {
		super.doQuarterlyStats();
		bestExpectedYield = 0.0;
		for(int q=0; q < House.Config.N_QUALITY; ++q) {
			expectedGrossYield[q] = getAverageSalePrice(q)*12.0*expectedOccupancy(q)/houseSaleMarket.getAverageSalePrice(q);
			if(expectedGrossYield[q] > bestExpectedYield) bestExpectedYield = expectedGrossYield[q];
		}
	}
	
	public class Offers extends HousingMarket.Offers<RentalMarketOffer> {
		public Offers() {
			super(RentalMarketOffer.class);
		}
	}

}
