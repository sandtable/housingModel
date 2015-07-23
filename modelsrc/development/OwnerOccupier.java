package development;

import utilities.ModelTime;

public class OwnerOccupier implements IAgentTrait, MarketBid.IIssuer, IMessage.IReceiver {
	public OwnerOccupier(HouseSaleMarket iSaleMarket, Employee iEmployeeTrait, DepositAccount ac) {
		saleMarket = iSaleMarket;
		employeeTrait = iEmployeeTrait;
		marketOfferIssuer = new MarketOffer.Issuer(ac);
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
		if(houseForSale == false && decideToSellHome()) {
			long price = desiredPurchasePrice(employeeTrait.monthlyIncome(), saleMarket.getHPIAppreciation());
			saleMarket.receive(new OOMarketBid(this, price, home.quality));
		}
	}
	
	public void die(IMessage.IReceiver beneficiary) {
		beneficiary.receive(home); 
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

	/***************************
	 * Decide on desired purchase price as a function of monthly income and current
	 *  of house price appreciation.
	 ****************************/
	public long desiredPurchasePrice(double monthlyIncome, double hpa) {
		final double A = 0.0;//0.48/12.0;			// sensitivity to house price appreciation
		final double EPSILON = 0.36;//0.36;//0.48;//0.365; // S.D. of noise
		final double SIGMA = 5.6*12.0*100.0;//5.6;	// scale
		return((long)(SIGMA*monthlyIncome*Math.exp(EPSILON*Model.root.random.nextGaussian())/(1.0 - A*hpa)));
	}
	@Override
	public boolean receive(House h) {
		home = h;
		return true;
	}

	@Override
	public boolean receive(DemandForPayment d) {
		// TODO Auto-generated method stub
		return false;
	}

	public double P_SELL = 1.0/(7.0*12.0);  // monthly probability of selling home
	
	OOMarketBid			activeBid = null;
	MarketOffer			activeOffer = null;
	House				home = null;
	Mortgage 			mortgage = null;
	boolean				houseForSale = false;
	ModelTime			lastIntrospectionTime;
	HouseSaleMarket		saleMarket;
	Employee			employeeTrait;
	MarketOffer.Issuer   marketOfferIssuer;
}
