package development;

import contracts.DemandForPayment;
import contracts.DepositAccount;
import contracts.Mortgage;
import contracts.OOMarketBid;
import contracts.SaleMarketOffer;
import contracts.TangibleAsset;
import contracts.TangibleAsset.IOwner;

public class OwnerOccupier extends EconAgent implements ITriggerable, TangibleAsset.IOwner {
	public double P_SELL = 1.0/(7.0*12.0);  // monthly probability of selling home
	public double DOWNPAYMENT_FRACTION; 	// Fraction of bank-balance household would like to spend on mortgage downpayments

	House				home = null;
	Mortgage			mortgage;
//	ModelTime			lastIntrospectionTime;
	HouseSaleMarket		saleMarket;
	int					qualityOfLife; // quality of last home
	Employee			employeeTrait;
	Renter				renter;
//	MarketOffer.Issuer  marketOfferIssuer;
	ModelRoot			root;
	Bank				bank;

	public OwnerOccupier() {
		super(	new SaleMarketOffer.Issuer(),
				new OOMarketBid.Issuer()
		);
		addDependency(TangibleAsset.IOwner.class, this);
		qualityOfLife = 0;
	}

	@Override
	public void start(IModelNode parent) {
		saleMarket = parent.mustFind(HouseSaleMarket.class);
		root = parent.mustFind(ModelRoot.class);
		bank = parent.mustFind(Bank.class);
		employeeTrait = parent.mustGet(Employee.class);
		addDependency(parent.mustGet(DepositAccount.Owner.class));
		addDependency(parent.mustGet(Mortgage.Borrower.class));
		renter = parent.get(Renter.class);
		DOWNPAYMENT_FRACTION = 0.1 + 0.0025*root.random.nextGaussian();
		Trigger.monthly().schedule(this);
		super.start(parent);
	}
	
	@Override
	public void trigger() {
		// Triggers once a month on introspection
		// Selling behaviour
		if(houseForSale()) {
			SaleMarketOffer offer = get(SaleMarketOffer.Issuer.class).first();
			if(!offer.isUnderOffer()) {
				offer.reducePrice(rethinkHouseSalePrice(offer.currentPrice));
			}
		} else if(isOwnerOccupier() && decideToSellHome()) {
			get(SaleMarketOffer.Issuer.class).putHouseForSale(home);
		}
	}

	@Override
	public boolean receive(IMessage message) {
		if(message instanceof House) {
			System.out.println("got house");
			home = (House)message;
			home.owner = this;
			qualityOfLife = home.quality;
			return(true);
		}
		else if(message instanceof DemandForPayment) {
			if(((DemandForPayment)message).contract instanceof OOMarketBid) {
				// do apply for mortgage
				System.out.println("Applying for mortgage");
				Bank bank = find(Bank.class);
				mortgage = bank.get(Mortgage.Lender.class).requestApproval(get(Mortgage.Borrower.class), 10000000, 1000000, true);
				System.out.println("Got Mortgage = "+mortgage.principal);
				bank.get(Mortgage.Lender.class).issue(mortgage, get(Mortgage.Borrower.class));
				if(parent() instanceof IMessage.IReceiver) {
					return(((IMessage.IReceiver)parent()).receive(message));
				}
				return(false);
			}
		}
		return(super.receive(message));
	}
	

	
	public int calcAffordableQualityOfHouse() {
		long price, maxMortgage;
		price = desiredPurchasePrice(employeeTrait.monthlyIncome(), saleMarket.housePriceAppreciation());
		maxMortgage = bank.get(Mortgage.Lender.class).getMaxMortgage(get(Mortgage.Borrower.class), true);
		if(maxMortgage > price) price = maxMortgage;
		return(saleMarket.qualityGivenPrice(price));
	}

	public void bidOnHouseMarket() {
		long price = desiredPurchasePrice(employeeTrait.monthlyIncome(), saleMarket.housePriceAppreciation());
		long maxMortgage = bank.get(Mortgage.Lender.class).getMaxMortgage(get(Mortgage.Borrower.class), true);
		if(maxMortgage > price) price = maxMortgage;
		get(OOMarketBid.Issuer.class).issue(price, qualityOfLife, saleMarket);				
	}
	
	
//	public void putHouseForSale() {
//		long minPrice = 0;
//		if(mortgage != null) {minPrice = -mortgage.balance;}
//		long price = initialSalePrice(saleMarket.getAverageSalePrice(home.quality), saleMarket.getAverageDaysOnMarket(), minPrice);
//		get(MarketOffer.Issuer.class).issue(home, price, saleMarket);		
//	}
	
	
	public boolean houseForSale() {
		return(get(SaleMarketOffer.Issuer.class).size() > 0);
	}

	public boolean isBidding() {
		return(get(OOMarketBid.Issuer.class).size() > 0);		
	}
	
	public boolean isOwnerOccupier() {
		return(home != null);
	}
	
	public boolean isRenting() {
		return(renter != null && renter.isRenting());
	}

//	public void die(IMessage.IReceiver beneficiary) {
//		beneficiary.receive(home); 
//	}
		
	
	////////////////////////////////////////////////////////
	// Behaviour
	////////////////////////////////////////////////////////

	/**
	 * @return Does an owner-occupier decide to sell house?
	 */
	public boolean decideToSellHome() {
		if(root.random.nextDouble() < P_SELL) return(true);
		return false;
	}
	

	public long downPayment(double bankBalance) {
		return(Math.round(bankBalance*DOWNPAYMENT_FRACTION));
	}

	/********************************************************
	 * Decide how much to drop the list-price of a house if
	 * it has been on the market for (another) month and hasn't
	 * sold. Calibrated against Zoopla dataset in Bank of England
	 * 
	 * @param sale The HouseSaleRecord of the house that is on the market.
	 ********************************************************/
	public long rethinkHouseSalePrice(long currentPrice) {
		return(Math.round(currentPrice *0.95));
		/*** BoE calibrated reprice
		if(rand.nextDouble() > 0.944) {
			double logReduction = 1.603+(rand.nextGaussian()*0.6173);
			return(sale.currentPrice * (1.0-Math.exp(logReduction)));
		}
		return(sale.currentPrice);
		***/
	}

	public boolean purchaseDecision(boolean ftb, long housePrice, long annualRent) {
		final double COST_OF_RENTING = 600; // Annual psychological cost of renting
		final double FTB_K = 1.0/600.0;//1.0/100000.0;//0.005 // Heterogeneity of sensitivity of desire to first-time-buy to cost
		double costOfHouse;
//			costOfHouse = housePrice*((1.0-HousingMarketTest.bank.config.THETA_FTB)*HousingMarketTest.bank.mortgageInterestRate() - HousingMarketTest.housingMarket.housePriceAppreciation());
		costOfHouse = housePrice*(bank.loanToValue(ftb,true)*bank.getMortgageInterestRate() - saleMarket.housePriceAppreciation());
		return(root.random.nextDouble() < 1.0/(1.0 + Math.exp(-FTB_K*(annualRent + COST_OF_RENTING - costOfHouse))));
	}


	/***************************
	 * Decide on desired purchase price as a function of monthly income and current
	 *  of house price appreciation.
	 ****************************/
	public long desiredPurchasePrice(double monthlyIncome, double hpa) {
		final double A = 0.0;//0.48/12.0;			// sensitivity to house price appreciation
		final double EPSILON = 0.36;//0.36;//0.48;//0.365; // S.D. of noise
		final double SIGMA = 5.6*12.0*100.0;//5.6;	// scale
		return((long)(SIGMA*monthlyIncome*Math.exp(EPSILON*root.random.nextGaussian())/(1.0 - A*hpa)));
	}

	/***
	 * 
	 * @return true when agent decides to move from owner occupation to rented accommodation
	 * N.B. this is conceptually distinct from the negation of the decision to become an
	 * owner-occupier from being a renter as the dedcision may be state dependent.
	 */
	public boolean decideToStartRenting() {
		return(calcAffordableQualityOfHouse() < qualityOfLife && renter.calcAffordableQualityOfHouse() >= qualityOfLife);
	}

	@Override
	public boolean give(TangibleAsset asset, IOwner recipient) {
		if(asset == home && recipient.receive(asset)) {
			home = null;
			if(decideToStartRenting()) {
				renter.bidOnRentalMarket();
			} else {				
				System.out.println("OO: Bidding on house market2");
				bidOnHouseMarket();
			}
			return(true);
		}
		return(false);
	}	
}
