package housing;

/***********************************************
 * Class that represents the market for houses for rent.
 * 
 * @author daniel
 *
 **********************************************/
public class HouseRentalMarket extends HousingMarket {
	
	public HouseRentalMarket() {
		averageGrossYield = new ExponentialAverage(Math.exp(-1.0/12.0), INIT_YIELD);
	}
	
	@Override
	public void init() {
		super.init();
		int i;
		for(i = 0; i<House.Config.N_QUALITY; ++i) {
			averageSalePrice[i].average *= INIT_YIELD/12.0; // assume 3% gross yield on house price
		}
	}
	
	@Override
	public void completeTransaction(HouseBuyerRecord purchase, HouseSaleRecord sale) {
		super.completeTransaction(purchase, sale);
		purchase.buyer.completeHouseRental(sale);
		sale.house.owner.completeHouseLet(sale.house);
		Collectors.rentalMarketStats.recordSale(purchase, sale);
		averageGrossYield.record(sale.currentPrice*12.0/Model.housingMarket.getAverageSalePrice(sale.house.quality));
	}

	/***
	 * 
	 * @return Average annual gross rental yield based on the mark-to-market valuation of properties
	 * partitioned by quality.
	 * 
	 */
	public double getAverageGrossYield() {
		return(averageGrossYield.value());
	}
	
	final static double INIT_YIELD = 0.03;
	
	ExponentialAverage averageGrossYield; // mark-to-market annual yeild
}
