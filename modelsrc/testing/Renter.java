package testing;

import housing.House;

public class Renter implements MarketBid.IIssuer, IAgentTrait, Message.IReceiver {
	
	@Override
	public boolean receive(Message message) {
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

	OccupierMarketBid	rentalBid;
	House 				home;
}
