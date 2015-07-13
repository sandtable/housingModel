package testing;

public class OwnerOccupier implements IAgentTrait, MarketBid.IIssuer {

	@Override
	public boolean receive(Message message) {
		// what messages could we receive?
		return false;
	}
	
	public void introspect() {
		
	}
	
	public void completeHousePurchase(housing.House house, DepositAccountAgreement depositAccount) {
		// successful bid
		home = house;
		// get mortgage
		// transfer activeBid.price to depositAccount
	}
	
	public boolean terminate(Contract contract) {
		if(contract instanceof MarketBid) {
			activeBid = null;
			return(true);
		} else if(contract instanceof MortgageAgreement) {
			mortgage = null;
			return(true);
		}
		return(false);
	}

	
	OOMarketBid			activeBid;
	housing.House		home;
	MortgageAgreement 	mortgage;
	
}
