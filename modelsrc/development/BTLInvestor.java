package development;


import sim.engine.Stoppable;
import contracts.BTLMarketBid;
import contracts.Contract;
import contracts.Mortgage;
import contracts.RentalMarketOffer;
import contracts.SaleMarketOffer;
import contracts.TangibleAsset;
import contracts.MarketOffer;
import contracts.RentalContract;

public class BTLInvestor extends ModelLeaf implements ITriggerable {
	int						desiredBTLProperties;
	HouseSaleMarket			saleMarket;
	RentalMarket			rentalMarket;
	Employee				employee;
	ModelRoot				root;
	Bank					bank;
	Stoppable				introspectionTrigger;

	public BTLInvestor() {
		super(	new RentalContract.Issuer(),
				new BTLMarketBid.Issuer(),
				new RentalMarketOffer.Issuer(),
				new SaleMarketOffer.Issuer(),
				new TangibleAsset.Owner());
	}
	
	@Override
	public void start(IModelNode parent) {
		super.start(parent);
		root = parent.mustFind(ModelRoot.class);
		saleMarket = root.mustFind(HouseSaleMarket.class);
		rentalMarket = root.mustFind(RentalMarket.class);
		employee = parent.mustGet(Employee.class);
		bank = parent.mustFind(Bank.class);
		addDependency(parent.mustGet(Mortgage.Borrower.class));
		if(employee.getIncomePercentile() > 0.5 && root.random.nextDouble() < Data.HousingMarket.P_INVESTOR*2.0) {
			desiredBTLProperties = (int)Data.HousingMarket.buyToLetDistribution.inverseCumulativeProbability(root.random.nextDouble());
		} else {
			desiredBTLProperties = 0;
		}
		introspectionTrigger = Trigger.monthly().schedule(this);
	}
	
	@Override
	public boolean receive(IMessage message) {
		if(message instanceof Message.EndOfContract) {
			Contract c = ((Message.EndOfContract)message).contract;
			if(c instanceof RentalContract) {
				RentalContract rc = (RentalContract)c;
				if(decideToSellInvestmentProperty(rc)) {
					get(SaleMarketOffer.Issuer.class).putHouseForSale(rc.house);
				} else {
					putHouseOnRentalMarket(((RentalContract)c).house);
				}
				return(true);
			}
		}
		else if(message instanceof House) {
			putHouseOnRentalMarket((House)message);
			// don't return, let house go to tangible asset owner
		}
		return(super.receive(message));
	}
	
	@Override
	public void trigger() {
		// do monthly introspection
				
		// buy properties if below portfolio target
		// minimum gross yield is based on comparing leveraged yield with alternative of 2%
		if(numberOfBTLProperties() < desiredBTLProperties && !isCurrentlyBidding()) {
			final double alternativeYield = 0.02;
			final double ltv = bank.loanToValue(false,false);
			final double interest = bank.getMortgageInterestRate();
			final double costs = 0.01;
			final double minYield = alternativeYield*(1.0-ltv) + ltv*interest + costs;
			final long price = bank.getMaxMortgage(parent(), false);
			get(BTLMarketBid.Issuer.class).issue(price, minYield);
		}
		
		// reprice current offers
		for(RentalMarketOffer offer : mustGet(RentalMarketOffer.Issuer.class)) {
			offer.reducePrice(rethinkBuyToLetRent(offer.currentPrice));
		}
	}
	
	@Override
	public void die() {
		introspectionTrigger.stop();
		super.die();
	}

	void putHouseOnRentalMarket(House house) {
		long price = buyToLetRent(rentalMarket.getAverageSalePrice(house.quality), rentalMarket.getAverageDaysOnMarket());
		get(RentalMarketOffer.Issuer.class).issue(house,price);		
	}
	
	int numberOfBTLProperties() {
		return get(TangibleAsset.Owner.class).size();
	}
	
	boolean isCurrentlyBidding() {
		return get(BTLMarketBid.Issuer.class).size() > 0;
	}
	
//	public double realisedGrossYeild(RentalContract rental) {
//		long houseValue = saleMarket.getAverageSalePrice(rental.house.quality);
//		return(rental.monthlyRent()*12.0/houseValue);
//	}

	
	/**
	 * The decision to sell is based on gross yeild, rather than leveraged yeild
	 * as the two are related linearly as: 
	 * Y_leveraged = (Y_gross - LTV*INTEREST)/(1-LTV)
	 * @return Does an investor decide to sell a buy-to-let property? */
	public boolean decideToSellInvestmentProperty(RentalContract rental) {
//		double performance = Math.min(realisedGrossYeild(rental) / rentalMarket.bestYeild(), 1.0);
//		double probability = 1.0/(1.0 + Math.exp(15.0*(performance - 0.6)));
//		return(probability > root.random.nextDouble());
		return false;
	}
	

	/**
	 * How much rent does an investor decide to charge on a buy-to-let house? 
	 * @param pbar average rent for house of this quality
	 * @param d average days on market
	 */
	public long buyToLetRent(double pbar, double d) {
		final double C = 0.02;//0.095;	// initial markup from average price
		final double D = 0.0;//0.024;//0.01;//0.001;		// Size of Days-on-market effect
		final double E = 0.05; //0.05;	// SD of noise
		double exponent = C + Math.log(pbar) - D*Math.log((d + 1.0)/31.0) + E*root.random.nextGaussian();
//		return(Math.max(Math.exp(exponent), mortgagePayment));
		return(Math.round(100.0*Math.exp(exponent)));
//		return(mortgagePayment*(1.0+RENT_PROFIT_MARGIN));
	}

	public long rethinkBuyToLetRent(long currentPrice) {
		if(root.random.nextDouble() > 0.944) {
			double logReduction = 1.603+(root.random.nextGaussian()*0.6173);
			return(Math.round(currentPrice * (1.0-Math.exp(logReduction))));
		}
		return(currentPrice);
	}

	/********************************************************
	 * Decide whether to buy a house as a buy-to-let investment
	 ********************************************************/
	/***
	public boolean decideToBuyBuyToLet(House h, Household me, double price) {
		// --- give preference to cheaper properties
		if(Model.rand.nextDouble() < (h.quality*1.0/House.Config.N_QUALITY)-0.5) return(false);
		if(price <= Model.bank.getMaxMortgage(me, false)) {
			MortgageApproval mortgage;
			mortgage = Model.bank.requestApproval(me, price, 0.0, false); // maximise leverege with min downpayment
			return(buyToLetPurchaseDecision(price, mortgage.monthlyPayment, mortgage.downPayment));

		}
//		System.out.println("BTL refused mortgage on "+price+" can get "+Model.bank.getMaxMortgage(me, false));
		return(false);
	}
	***/
	/**
	 * @param price The asking price of the house
	 * @param monthlyPayment The monthly payment on a mortgage for this house
	 * @param downPayment The minimum downpayment on a mortgage for this house
	 * @return will the investor decide to buy this house?
	 */
	/***
	public boolean buyToLetPurchaseDecision(double price, double monthlyPayment, double downPayment) {
		double yield;
		yield = (monthlyPayment*12*RENT_PROFIT_MARGIN + Model.housingMarket.housePriceAppreciation()*price)/
				downPayment;
		if(Model.rand.nextDouble() < 1.0/(1.0 + Math.exp( - yield*4.0))) {
//			System.out.println("BTL: bought");
			return(true);
		}
//		System.out.println("BTL: didn't buy");
		return(false);
	}
***/

	public boolean isPropertyInvestor() {
		return(desiredBTLProperties > 0);
	}

	/***
	@Override
	public double buyToLetMaxInvestment(Household me) {
		return(Model.bank.getMaxMortgage(me, false));
	}
***/

	/***
	 * If we're below our desired investment portfolio size, desired yield
	 * is average yield on the market plus noise. Otherwise, swap if we
	 * find a house with better yield than worst yield in current portfolio.
	 */
	/***
	@Override
	public double buyToLetDesiredYield(Household me) {
		if(me.housePayments.size()+1 < desiredBTLProperties) {
			return(Model.rentalMarket.getAverageGrossYield()*(Model.rand.nextGaussian()*0.25+1.0));
		}		
		double worstYield = 1.0;
		double yield;
		for(House h : me.housePayments.keySet()) {
			if(h != me.home && h.owner == me) {
				yield = me.rentCollectedFrom(h)/Model.housingMarket.getAverageSalePrice(h.quality);
				if(yield < worstYield) worstYield = yield;
			}
		}
		return(worstYield);
	}
	***/
}
