package development;

import java.util.HashMap;

import development.Market.Match;
import utilities.PriorityQueue2D;

public class OOMarketBid extends MarketBid implements Market.IQualityPriceSupplier {	
	public OOMarketBid(IIssuer issuer, long iPrice, int iMinQuality) {
		super(issuer, iPrice);
		minQuality = iMinQuality;
	}
	
	public static class Issuer extends Contract.Issuer<MarketBid> implements IIssuer {
		public Issuer(EconAgent iMe) {
			super(MarketBid.class);
			me = iMe;
		}

//		@Override
//		public void completePurchase(MarketBid bid, MarketOffer offer) {
//			DepositAccount payoutAC = me.getTrait(DepositAccount.Owner.class).first();
//			payoutAC.transfer(offer.payinAC, offer.getPrice());
//		}
		
		public boolean receive(DemandForPayment d) {
			return(me.receive(d));
		}
		
		public boolean receive(House h) {
			return(me.receive(h));
		}
		
		EconAgent me;
	}

	@Override
	public int getQuality() {
		return(minQuality);
	}

	int minQuality;
}
