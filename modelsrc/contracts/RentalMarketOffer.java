package contracts;

import development.House;
import development.IModelNode;

public class RentalMarketOffer extends MarketOffer {
	public RentalMarketOffer(IIssuer issuer, House house, long price) {
		super(issuer, house, price);
	}
	
	public static class Issuer extends MarketOffer.Issuer<RentalMarketOffer> {
		RentalContract.Issuer rentalContractIssuer;
		
		public Issuer() {
			super(RentalMarketOffer.class);
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
