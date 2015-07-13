package testing;

public class OccupierMarketBid extends MarketBid {

	public OccupierMarketBid(IIssuer issuer, long iPrice, int iMinQuality) {
		super(issuer, iPrice);
		minQuality = iMinQuality;
	}
	
	int minQuality;
}
