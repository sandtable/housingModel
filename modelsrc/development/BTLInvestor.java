package development;


import contracts.BTLMarketBid;
import contracts.SaleMarketOffer;
import contracts.TangibleAsset;
import contracts.MarketOffer;
import contracts.RentalContract;

public class BTLInvestor extends ModelLeaf implements ITriggerable {
	int						desiredPortfolioSize;
	HouseSaleMarket			saleMarket;
	RentalMarket			rentalMarket;
	ModelRoot				root;

	public BTLInvestor() {
		super(	new RentalContract.Issuer(),
				new BTLMarketBid.Issuer(),
				new SaleMarketOffer.Issuer(),
				new TangibleAsset.Owner());
	}
	
	@Override
	public void start(IModelNode parent) {
		super.start(parent);
		root = parent.mustFind(ModelRoot.class);
		saleMarket = root.mustFind(HouseSaleMarket.class);
		rentalMarket = root.mustFind(RentalMarket.class);
		Trigger.monthly().schedule(this);
	}
	
	@Override
	public void trigger() {
		// do monthly introspection
		
		// sell off badly performing houses
		for(RentalContract rental : get(RentalContract.Issuer.class)) {
			if(decideToSellInvestmentProperty(rental)) {
				get(SaleMarketOffer.Issuer.class).putHouseForSale(rental.house);
			}
		}
	}
	
	public double realisedGrossYeild(RentalContract rental) {
		long houseValue = saleMarket.getAverageSalePrice(rental.house.quality);
		return(rental.monthlyRent()*12.0/houseValue);
	}

	
	/**
	 * @return Does an investor decide to sell a buy-to-let property
	 */
	public boolean decideToSellInvestmentProperty(RentalContract rental) {
		double performance = Math.min(realisedGrossYeild(rental) / rentalMarket.bestYeild(), 1.0);
		double probability = 1.0/(1.0 + Math.exp(15.0*(performance - 0.6)));
		return(probability > root.random.nextDouble());
	}
	

	/**
	 * How much rent does an investor decide to charge on a buy-to-let house? 
	 * @param pbar average rent for house of this quality
	 * @param d average days on market
	 */
	/***
	@Override
	public double buyToLetRent(double pbar, double d, double mortgagePayment) {
		final double C = 0.02;//0.095;	// initial markup from average price
		final double D = 0.0;//0.024;//0.01;//0.001;		// Size of Days-on-market effect
		final double E = 0.05; //0.05;	// SD of noise
		double exponent = C + Math.log(pbar) - D*Math.log((d + 1.0)/31.0) + E*Model.rand.nextGaussian();
//		return(Math.max(Math.exp(exponent), mortgagePayment));
		return(Math.exp(exponent));
//		return(mortgagePayment*(1.0+RENT_PROFIT_MARGIN));
	}
***/
	/***
	public double rethinkBuyToLetRent(HouseSaleRecord sale) {
		if(rand.nextDouble() > 0.944) {
			double logReduction = 1.603+(rand.nextGaussian()*0.6173);
			return(sale.currentPrice * (1.0-Math.exp(logReduction)));
		}
		return(sale.currentPrice);
	}
	***/

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
		
	/***
	public boolean isPropertyInvestor() {
		return(desiredBTLProperties > 0);
	}

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
