package contracts;

import contracts.OOMarketBid.IIssuer;
import development.HousingMarket;
import development.IMessage;
import development.IModelNode;

public class RentalMarketBid extends MarketBid implements HousingMarket.IQualityPriceSupplier {	
	int minQuality;

	public RentalMarketBid(IIssuer issuer, long iPrice, int iMinQuality, IMessage.IReceiver market) {
		super(issuer, iPrice, market);
		minQuality = iMinQuality;
	}

	public static class Issuer extends MarketBid.Issuer implements IIssuer {
		public boolean issue(long price, int quality, IMessage.IReceiver market) {
			return(issue(new RentalMarketBid(this, price, quality, market)));
		}
	}

	@Override
	public int getQuality() {
		return(minQuality);
	}



}
