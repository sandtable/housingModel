package development;

public class Renter implements MarketBid.IIssuer, IAgentTrait, IMessage.IReceiver {
	
	@Override
	public boolean receive(IMessage message) {
		if(message instanceof IntrospectMessage) {
			introspect();
			return(true);
		}
		return false;
	}

	public void introspect() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public boolean terminate(Contract contract) {
		if(contract == rentalBid) {
			rentalBid = null;
			return(true);
		}
		return(false);
	}
	
	OOMarketBid	rentalBid;
	House 				home;
	@Override
	public boolean receive(House h) {
		home = h;
		return false;
	}

	@Override
	public boolean receive(DemandForPayment d) {
		// TODO Auto-generated method stub
		return false;
	}
}
