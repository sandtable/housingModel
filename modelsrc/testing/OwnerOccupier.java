package testing;

import utilities.ModelTime;

public class OwnerOccupier implements IAgentTrait, MarketBid.IIssuer, MarketOffer.IIssuer, IMessage.IReceiver {

	@Override
	public boolean receive(IMessage message) {
		if(message instanceof IntrospectMessage) {
			introspect();
			return(true);
		}
		return false;
	}
	
	public void introspect() {
		
	}
	
	public void die(IMessage.IReceiver beneficiary) {
		beneficiary.receive(home); 
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

	////////////////////////////////////////////////////////
	// Behaviour
	////////////////////////////////////////////////////////
	
	public boolean decideToSellHome() {
		if(Model.root.random.nextDouble() < P_SELL) return(true);
		return false;
	}

	public double P_SELL = 1.0/(7.0*12.0);  // monthly probability of selling home
	
	OOMarketBid			activeBid = null;
	MarketOffer			activeOffer = null;
	House				home = null;
	Mortgage 			mortgage = null;
	boolean				houseForSale = false;
	ModelTime			lastIntrospectionTime;
}
