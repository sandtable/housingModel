package testing;

public class OOMarketBid extends MarketBid {

	public OOMarketBid(long iPrice, int iMinQuality) {
		super(iPrice);
		minQuality = iMinQuality;
	}
	
	int minQuality;
}
