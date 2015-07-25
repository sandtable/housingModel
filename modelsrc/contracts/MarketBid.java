package contracts;

import development.IMessage;
import development.IMessage.IReceiver;

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
	
	public interface IIssuer extends Contract.IIssuer, IMessage.IReceiver {
	}
	
	long price;
	
}
