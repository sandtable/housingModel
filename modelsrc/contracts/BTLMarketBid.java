package contracts;

import development.HouseSaleMarket;

public class BTLMarketBid extends MarketBid implements HouseSaleMarket.IYeildPriceSupplier {

	public BTLMarketBid(IIssuer issuer, long iPrice, double iMinYield) {
		super(issuer, iPrice);
		minYield = iMinYield;
	}

	double minYield;

	@Override
	public double getYeild() {
		// TODO Auto-generated method stub
		return 0;
	}

}
