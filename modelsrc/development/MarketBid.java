package development;

public class MarketBid extends Contract {
	public MarketBid(IIssuer issuer, long iPrice) {
		super(issuer);
		price = iPrice;
	}
	
	public long getPrice() {
		return(price);
	}
	
	public IIssuer getIssuer() {
		return((IIssuer)issuer);
	}
	
	public interface IIssuer extends Contract.IIssuer {
//		void completePurchase(MarketBid bid, MarketOffer offer);
		boolean receive(House h);
		boolean receive(DemandForPayment d);
	}
	
	long price;
	
}
