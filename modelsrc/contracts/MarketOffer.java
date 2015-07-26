package contracts;

import development.House;
import development.HouseSaleMarket;
import development.HousingMarket;
import development.IMessage;
import development.IModelNode;
import development.HouseSaleMarket.IYeildPriceSupplier;
import development.HousingMarket.IQualityPriceSupplier;
import development.HousingMarket.Match;
import utilities.ModelTime;
import utilities.PriorityQueue2D;

public class MarketOffer extends Contract implements HousingMarket.IQualityPriceSupplier, HouseSaleMarket.IYeildPriceSupplier {
	public House 		house;
	public long 		initialListedPrice;
	public long			currentPrice;
	public ModelTime	tInitialListing; 	// time of initial list
	public HousingMarket.Match currentMatch;		// if non-null the house is currently 'under offer'
	
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
		return(
				house.rentalMarket.getAverageSalePrice(getQuality())/
				getPrice()
				);
	}
	
	public IIssuer getIssuer() {
		return((IIssuer)issuer);
	}

	@Override
	public int getQuality() {
		return(house.quality);
	}
	
	public boolean isUnderOffer() {
		return(currentMatch != null);
	}
	
	public HousingMarket.Match matchWith(MarketBid bid) {
		if(currentMatch != null) return(null);
		currentMatch = new HousingMarket.Match(this, bid);
		return(currentMatch);
	}
	
	public void unMatch() {
		currentMatch = null;
	}
	
	public static interface IIssuer extends Contract.IIssuer {
		void completeSale(MarketOffer offer, MarketBid bid);
	}
	
	public static class Issuer extends Contract.Issuer<MarketOffer> implements IIssuer {
		DepositAccount.Owner payee;

		public Issuer() {
			super(MarketOffer.class);
		}
		
		@Override
		public void start(IModelNode parent) {
			super.start(parent);
			payee = parent.get(DepositAccount.Owner.class);
			if(payee == null) System.out.println("A MarketOffer.Issuer needs to be a DepositAccount.Owner");
		}
		
		public void issue(House house, long price) {
			this.issue(new MarketOffer(this, house, price), getMarket(house));
		}
		
		public boolean reducePrice(MarketOffer offer, long newPrice) {
			if(terminate(offer)) {
				offer.currentPrice = newPrice;
				this.issue(offer, getMarket(offer.house));
				return(true);
			}
			return(false);
		}
		
		public IMessage.IReceiver getMarket(House h) {
			return(h.saleMarket);
		}

		@Override
		public void completeSale(MarketOffer offer, MarketBid bid) {
			offer.house.owner.remove(offer.house);
			bid.getIssuer().receive(offer.house);
			bid.getIssuer().receive(new DemandForPayment(payee.defaultAccount(), offer.getPrice(), bid));
		}
	}
	
}
