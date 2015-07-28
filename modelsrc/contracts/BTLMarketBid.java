package contracts;

import development.HouseSaleMarket;
import development.IMessage;
import development.IModelNode;

public class BTLMarketBid extends MarketBid implements HouseSaleMarket.IYeildPriceSupplier {
	double minYield;

	public BTLMarketBid(IIssuer issuer, long iPrice, double iMinYield, IMessage.IReceiver market) {
		super(issuer, iPrice, market);
		minYield = iMinYield;
	}


	public static class Issuer extends MarketBid.Issuer {
		HouseSaleMarket saleMarket;
		
		@Override
		public void start(IModelNode parent) {
			saleMarket = parent.mustFind(HouseSaleMarket.class);
		}
		
		public boolean issue(long price, double yield) {
			return(issue(new BTLMarketBid(this, price, yield, saleMarket)));
		}		
	}

	@Override
	public double getYeild() {
		// TODO Auto-generated method stub
		return 0;
	}

}
