package development;

import contracts.RentalMarketOffer;

public class RentalMarket extends HousingMarket {
	double bestYeild;
	HouseSaleMarket houseSaleMarket;
	
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
	
	/***
	 * @return maximum over quality of average rent/average sale price
	 */
	public double bestYeild() {
		return bestYeild;
	}
	
	@Override
	protected void doQuarterlyStats() {
		super.doQuarterlyStats();
		bestYeild = 0.0;
		double yeild;
		for(int q=0; q < House.Config.N_QUALITY; ++q) {
			yeild = getAverageSalePrice(q)*12.0/houseSaleMarket.getAverageSalePrice(q);
			if(yeild > bestYeild) bestYeild = yeild;
		}
	}
	
	public class Offers extends HousingMarket.Offers<RentalMarketOffer> {
		public Offers() {
			super(RentalMarketOffer.class);
		}
	}

}
