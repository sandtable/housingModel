package testing;

public class BTLMarketBid extends MarketBid {

	public BTLMarketBid(IIssuer issuer, long iPrice, double iMinYield) {
		super(issuer, iPrice);
		minYield = iMinYield;
	}
	
	double minYield;

}
