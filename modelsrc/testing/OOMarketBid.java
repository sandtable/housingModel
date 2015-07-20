package testing;

public class OOMarketBid extends MarketBid {

	public OOMarketBid(IIssuer issuer, long iPrice, int iMinQuality) {
		super(issuer, iPrice);
		minQuality = iMinQuality;
	}
	
	int minQuality;
}
