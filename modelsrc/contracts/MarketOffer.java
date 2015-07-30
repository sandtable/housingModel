package contracts;

import development.House;
import development.HouseSaleMarket;
import development.HousingMarket;
import development.IMessage;
import development.IModelNode;
import development.HouseSaleMarket.IYieldPriceSupplier;
import development.HousingMarket.IQualityPriceSupplier;
import development.HousingMarket.Match;
import utilities.ModelTime;
import utilities.PriorityQueue2D;

public class MarketOffer extends Contract implements HousingMarket.IQualityPriceSupplier, HouseSaleMarket.IYieldPriceSupplier {
	public House 		house;
	public long 		initialListedPrice;
	public long			currentPrice;
	public ModelTime	tInitialListing; 	// time of initial list
	public HousingMarket.Match currentMatch;		// if non-null the house is currently 'under offer'
	HousingMarket.Offers<? extends MarketOffer>	market;
	/***********************************************
	 * Construct a new record.
	 * 
	 * @param h The house that is for sale.
	 * @param price The initial list price for the house.
	 ***********************************************/
	public MarketOffer(Issuer<? extends MarketOffer> issuer, HousingMarket market, House iHouse, long price) {
		super(issuer);
		house = iHouse;
		this.market = market.offers;
		setPrice(price);
		initialListedPrice = currentPrice;
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
	public double getExpectedGrossYield() {
		return(house.rentalMarket.expectedGrossYield(getQuality()));
	}
	
	@SuppressWarnings("unchecked")
	public Issuer<? extends MarketOffer> getIssuer() {
		return((Issuer<? extends MarketOffer>)issuer);
	}

	@Override
	public int getQuality() {
		return(house.quality);
	}
	
	public boolean isUnderOffer() {
		return(currentMatch != null);
	}
	
	public void reducePrice(long newPrice) {
		market.reducePrice(this,newPrice);
	}
	
	/**
	public HousingMarket.Match matchWith(MarketBid bid) {
		if(currentMatch != null) return(null);
		currentMatch = new HousingMarket.Match(this, bid);
		return(currentMatch);
	}
	**/
	
	public void unMatch() {
		currentMatch = null;
	}
	
	public static interface IIssuer extends Contract.IIssuer {
		void completeSale(MarketOffer offer, MarketBid bid);
	}
	
	public static abstract class Issuer<CONTRACT extends MarketOffer> extends Contract.Issuer<CONTRACT> implements IIssuer {
		DepositAccount.Owner payee;

		public Issuer(Class<CONTRACT> clazz) {
			super(clazz);
		}
		
		@Override
		public void start(IModelNode parent) {
			super.start(parent);
			payee = parent.get(DepositAccount.Owner.class);
			if(payee == null) System.out.println("A MarketOffer.Issuer needs to be a DepositAccount.Owner");
		}
		
//		public void issue(House house, long price, IMessage.IReceiver market) {
//			this.issue( new MarketOffer(this, house, price), market);
//		}
				
//		public IMessage.IReceiver getMarket(House h) {
//			return(h.saleMarket);
//		}
		abstract public void completeSale(MarketOffer offer, MarketBid bid);

	}
	
}
