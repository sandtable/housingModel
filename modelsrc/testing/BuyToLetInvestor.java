package testing;

import java.util.HashSet;

import utilities.IdentityHashSet;
import housing.House;

public class BuyToLetInvestor implements IAgentTrait, MarketBid.IIssuer, MarketOffer.IIssuer {

	public BuyToLetInvestor() {
		mortgages = new IdentityHashSet<>();
		activeBid = null;
		activeOffer = null;
	}
	
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
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void completeSale(MarketOffer offer) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void completePurchase(MarketBid bid, MarketOffer offer,
			DepositAccountAgreement depositAccount) {
		// TODO Auto-generated method stub
		
	}

	int									desiredPortfolioSize;
	IdentityHashSet<MortgageAgreement> 	mortgages;
	HashSet<House>						houses;
	MarketBid							activeBid;
	MarketOffer							activeOffer;
}
