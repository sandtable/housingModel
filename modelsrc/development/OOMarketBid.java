package development;

import utilities.PriorityQueue2D;

public class OOMarketBid extends MarketBid implements PriorityQueue2D.Comparable<OOMarketBid>, Market.IQualityPriceSupplier {	
	public OOMarketBid(IIssuer issuer, long iPrice, int iMinQuality) {
		super(issuer, iPrice);
		minQuality = iMinQuality;
	}
	
	int minQuality;

	@Override
	public int XCompareTo(OOMarketBid other) {
		return(Long.signum(price - other.price));
	}

	@Override
	public int YCompareTo(OOMarketBid other) {
		return(Integer.signum(minQuality - other.minQuality));
	}

	@Override
	public int getQuality() {
		return(minQuality);
	}
}
