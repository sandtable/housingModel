package contracts;

import development.House;
import development.IModelNode;
import development.RentalMarket;

public class RentalMarketOffer extends MarketOffer {
	public RentalMarketOffer(Issuer issuer, House house, long price) {
		super(issuer, house.rentalMarket, house, price);
	}
	
	public static class Issuer extends MarketOffer.Issuer<RentalMarketOffer> {
		RentalContract.Issuer rentalContractIssuer;
		
		public Issuer() {
			super(RentalMarketOffer.class);
		}
		
		public boolean issue(House house, long price) {
			return(issue(new RentalMarketOffer(this, house, price), house.rentalMarket));
		}
		
		@Override
		public void start(IModelNode parent) {
			rentalContractIssuer = parent.mustGet(RentalContract.Issuer.class);
			super.start(parent);
		}
		
		@Override
		public void completeSale(MarketOffer offer, MarketBid bid) {
			bid.getIssuer().receive(offer.house);
			rentalContractIssuer.issue(offer.house, offer.currentPrice, bid.getIssuer());
		}

	}

}
