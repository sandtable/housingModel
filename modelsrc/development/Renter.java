package development;

import housing.House;

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
	
	@Override
	public void completePurchase(MarketBid bid, MarketOffer offer,
			DepositAccount depositAccount) {
		// TODO Auto-generated method stub
		
	}

	OOMarketBid	rentalBid;
	House 				home;
}
