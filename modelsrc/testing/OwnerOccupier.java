package testing;

public class OwnerOccupier implements IAgentTrait, MarketBid.IIssuer, MarketOffer.IIssuer {

	@Override
	public boolean receive(Message message) {
		// what messages could we receive?
		return false;
	}
	
	public void introspect() {
		
	}
	
	public void completePurchase(MarketBid bid, MarketOffer offer, DepositAccount depositAccount) {
		// successful bid
		home = offer.house;
		// get mortgage
		// transfer activeBid.price to depositAccount
	}
	
	public void completeSale(MarketOffer offer) {
		if(offer != activeOffer) {
			System.out.println("Strange, completing house sale on offer I didn't make");
		}
		if(home == offer.house) {
			home = null;
			// bid on market
		}
	}
	
	public boolean terminate(Contract contract) {
		if(contract == activeBid) {
			activeBid = null;
			return(true);
		} else if(contract == activeOffer) {
			activeOffer = null;
			return(true);
		} else if(contract == mortgage) {
			mortgage = null;
			return(true);
		}
		return(false);
	}

	
	OccupierMarketBid			activeBid = null;
	MarketOffer			activeOffer = null;
	housing.House		home = null;
	Mortgage 	mortgage = null;
}
