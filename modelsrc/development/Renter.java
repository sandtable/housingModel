package development;

import contracts.OOMarketBid;

public class Renter extends EconAgent {
	
	public Renter() {
		super(new OOMarketBid.Issuer());
	}
	
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

	House 				home;
}
