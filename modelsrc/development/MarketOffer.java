package development;

import utilities.ModelTime;
import utilities.PriorityQueue2D;

public class MarketOffer extends Contract implements Market.IQualityPriceSupplier, HouseSaleMarket.IYeildPriceSupplier {
	/***********************************************
	 * Construct a new record.
	 * 
	 * @param h The house that is for sale.
	 * @param price The initial list price for the house.
	 ***********************************************/
	public MarketOffer(IIssuer issuer, House iHouse, long price) {
		super(issuer);
		house = iHouse;
		setPrice(price);
		initialListedPrice = currentPrice;
	//	quality = house.quality;
		tInitialListing = ModelTime.now();
//		payinAC = iPayinAC;
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
	
	IIssuer getIssuer() {
		return((IIssuer)issuer);
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
		void completeSale(MarketOffer offer, MarketBid.IIssuer recipient);
	}
	
	public static class Issuer extends Contract.Issuer<MarketOffer> implements IIssuer {
		public Issuer(DepositAccount iPayeeAC) {
			super(MarketOffer.class);
			payeeAC = iPayeeAC;
		}
		
		public void issue(House house, long price) {
			Model.root.houseSaleMarket.receive(new MarketOffer(this, house, price));
		}

		@Override
		public void completeSale(MarketOffer offer, MarketBid.IIssuer recipient) {
			offer.house.owner.remove(offer.house);
			recipient.receive(offer.house);
			recipient.receive(new DemandForPayment(payeeAC, offer.getPrice(), offer));
		}
		
		DepositAccount payeeAC;
	}
	
	public House 		house;
	// public int			quality;
	public long 		initialListedPrice;
	public long			currentPrice;
	public ModelTime	tInitialListing; 	// time of initial list
//	public MarketBid	currentBid;			// if non-null the house is currently 'under offer'
	Market.Match 		currentMatch;			// if non-null the house is currently 'under offer'
	//DepositAccount		payinAC;
}
