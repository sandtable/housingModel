package development;

import java.util.HashSet;
import development.MarketBid.IIssuer;
import utilities.IdentityHashSet;

public class BuyToLetInvestor implements IAgentTrait, MarketBid.IIssuer, MarketOffer.IIssuer {

	public BuyToLetInvestor() {
		mortgages = new IdentityHashSet<>();
		activeBid = null;
		activeOffer = null;
	}

	@Override
	public boolean terminate(Contract contract) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void completeSale(MarketOffer offer, IIssuer recipient) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean receive(House h) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean receive(DemandForPayment d) {
		// TODO Auto-generated method stub
		return false;
	}

	int							desiredPortfolioSize;
	IdentityHashSet<Mortgage> 	mortgages;
	HashSet<House>				houses;
	MarketBid					activeBid;
	MarketOffer					activeOffer;
}
