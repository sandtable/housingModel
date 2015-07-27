package development;

import java.util.HashSet;

import contracts.MarketBid.IIssuer;
import contracts.RentalContract;

public class BuyToLetInvestor extends ModelLeaf {
	int							desiredPortfolioSize;

	public BuyToLetInvestor() {
		super(new RentalContract.Issuer());
	}

	House myHouse;
}
