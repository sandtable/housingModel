package development;

public class MarketBid extends Contract {
	public MarketBid(IIssuer issuer, long iPrice) {
		super(issuer);
		price = iPrice;
	}
	
	public interface IIssuer extends Contract.IIssuer {
		void completePurchase(MarketBid bid, MarketOffer offer, DepositAccount depositAccount);
	}

	public long getPrice() {
		return(price);
	}
	
	long price;
	
}
