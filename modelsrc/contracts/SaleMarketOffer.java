package contracts;

import development.House;
import development.IMessage;
import development.IModelNode;
import development.ModelRoot;

public class SaleMarketOffer extends MarketOffer {
	public SaleMarketOffer(IIssuer issuer, House house, long price) {
		super(issuer, house, price);
	}

	public static class Issuer extends MarketOffer.Issuer<SaleMarketOffer> {		
		ModelRoot root;
		
		public Issuer() {
			super(SaleMarketOffer.class);
		}
		
		@Override
		public void start(IModelNode parent) {
			super.start(parent);
			root = parent.mustFind(ModelRoot.class);
		}
		
		public void issue(House house, long price, IMessage.IReceiver market) {
			this.issue( new SaleMarketOffer(this, house, price), market);
		}

		@Override
		public void completeSale(MarketOffer offer, MarketBid bid) {
			offer.house.owner.give(offer.house, ((OOMarketBid.IIssuer)bid.issuer).assetOwner());
			bid.getIssuer().receive(new DemandForPayment(payee.defaultAccount(), offer.getPrice(), bid));
		}

		
		public void putHouseForSale(House house) {
			long minPrice = 0;
//			if(house.mortgage != null) {minPrice = -house.mortgage.balance;}
			// TODO: minPrice should be the price at which you decide you'd rather keep the house
			long price = initialSalePrice(house.saleMarket.getAverageSalePrice(house.quality), house.saleMarket.getAverageDaysOnMarket(), minPrice);
			issue(house, price, house.saleMarket);		
		}

		///////////////////////////////////////////////////////////////////////////////
		// Behaviour
		///////////////////////////////////////////////////////////////////////////////
		/********************************
		 * @param pbar average sale price of houses of the same quality
		 * @param d average number of days on the market before sale
		 * @param principal amount of principal left on any mortgage on this house
		 * @return initial sale price of a house 
		 ********************************/
		public long initialSalePrice(double pbar, double d, double principal) {
			final double C = 0.02;//0.095;	// initial markup from average price (more like 0.2 from BoE calibration)
			final double D = 0.00;//0.024;//0.01;//0.001;		// Size of Days-on-market effect
			final double E = 0.05; //0.05;	// SD of noise
			double exponent = C + Math.log(pbar) - D*Math.log((d + 1.0)/31.0) + E*root.random.nextGaussian();
			return(Math.round(Math.max(100*Math.exp(exponent), principal)));
		}


	}
}
