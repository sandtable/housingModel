package testing;

import utilities.ModelTime;
import utilities.PriorityQueue2D;

public class MarketOffer extends Contract implements Market.IQualityPriceSupplier, HouseSaleMarket.IYeildPriceSupplier {
	/***********************************************
	 * Construct a new record.
	 * 
	 * @param h The house that is for sale.
	 * @param p The initial list price for the house.
	 ***********************************************/
	public MarketOffer(IIssuer issuer, House h, long p) {
		super(issuer);
		house = h;
		setPrice(p);
		initialListedPrice = currentPrice;
	//	quality = house.quality;
		tInitialListing = ModelTime.now();
	}
	
	/***********************************************
	 * Set the list price to a given value,
	 * rounded to the nearest penny.
	 * 
	 * @param p The list-price.
	 **********************************************/
	public void setPrice(long p) {
		currentPrice = p;
	}

	@Override
	public long getPrice() {
		return(currentPrice);
	}

	@Override
	public double getYeild() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getQuality() {
		return(house.quality);
	}
	
	public boolean isUnderOffer() {
		return(currentMatch != null);
	}
	
	public Market.Match matchWith(MarketBid bid) {
		if(currentMatch != null) return(null);
		currentMatch = new Market.Match(this, bid);
		return(currentMatch);
	}
	
	public void unMatch() {
		currentMatch = null;
	}
	
	public static interface IIssuer extends Contract.IIssuer {
		void completeSale(MarketOffer offer);
	}
	
	public House 		house;
	// public int			quality;
	public long 		initialListedPrice;
	public long			currentPrice;
	public ModelTime	tInitialListing; 	// time of initial list
//	public MarketBid	currentBid;			// if non-null the house is currently 'under offer'
	Market.Match 		currentMatch;			// if non-null the house is currently 'under offer'
}
