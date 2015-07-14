package testing;

import utilities.ModelTime;
import housing.House;
import housing.Model;

public class MarketOffer extends Contract {
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
		tInitialListing = Model.timeNow();
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

	public int getQuality() {
		return(house.quality);
	}
	
//	public double doubleValue() {
//		return(currentPrice);
//	}

	public static interface IIssuer extends Contract.IIssuer {
		void completeSale(MarketOffer offer);
	}
	
	public House 		house;
	// public int			quality;
	public long 		initialListedPrice;
	public long			currentPrice;
	public ModelTime	tInitialListing; // time of initial listing
}
