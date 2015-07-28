package contracts;

import java.util.HashMap;


import development.EconAgent;
import development.HousingMarket;
import development.IMessage;
import development.HousingMarket.IQualityPriceSupplier;
import development.HousingMarket.Match;
import development.IMessage.IReceiver;
import utilities.PriorityQueue2D;

public class OOMarketBid extends MarketBid implements HousingMarket.IQualityPriceSupplier {	
	int minQuality;

	public OOMarketBid(IIssuer issuer, long iPrice, int iMinQuality, IMessage.IReceiver market) {
		super(issuer, iPrice, market);
		minQuality = iMinQuality;
	}
	
	public static class Issuer extends MarketBid.Issuer {
		public boolean issue(long price, int quality, IMessage.IReceiver market) {
			return(issue(new OOMarketBid(this, price, quality, market)));
		}		
	}

	@Override
	public int getQuality() {
		return(minQuality);
	}

}
