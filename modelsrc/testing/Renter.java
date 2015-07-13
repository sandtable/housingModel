package testing;

import housing.House;

public class Renter implements MarketBid.IIssuer, IAgentTrait {
	
	@Override
	public boolean receive(Message message) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
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
			DepositAccountAgreement depositAccount) {
		// TODO Auto-generated method stub
		
	}

	OccupierMarketBid	rentalBid;
	House 				home;
}
