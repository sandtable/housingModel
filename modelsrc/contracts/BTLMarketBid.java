package contracts;

import development.HouseSaleMarket;
import development.IMessage;

public class BTLMarketBid extends MarketBid implements HouseSaleMarket.IYeildPriceSupplier {

	public BTLMarketBid(IIssuer issuer, long iPrice, double iMinYield, IMessage.IReceiver market) {
		super(issuer, iPrice, market);
		minYield = iMinYield;
	}

	double minYield;

	@Override
	public double getYeild() {
		// TODO Auto-generated method stub
		return 0;
	}

}
