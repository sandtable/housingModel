package development;

import contracts.BTLMarketBid;
import contracts.MarketBid;
import contracts.MarketOffer;
import utilities.PriorityQueue2D;

public class HouseSaleMarket extends HousingMarket {

	@Override
	public HousingMarket.Offers newOffers() {
		return(this.new Offers());
	}

	@Override
	public HousingMarket.Bids newBids() {
		return(this.new Bids());
	}

	@Override
	public long referencePrice(int quality) {
		return(Data.HousingMarket.referenceSalePrice(quality));
	}

	public Match matchBid(BTLMarketBid bid) {
		return(setupMatch((MarketOffer)((HouseSaleMarket.Offers)offers).BTLqueue.peek(bid), bid));
	}
	

	public class Bids extends HousingMarket.Bids {	
		
		@Override
		public void start(IModelNode parent) {
			super.start(parent);
			if(parent instanceof EconAgent) {
				((EconAgent)parent).registerHandler(BTLMarketBid.class, this);
			}
		}
		
		@Override
		public boolean receive(IMessage contract) {
			if(contract instanceof BTLMarketBid) {
				BTLMarketBid bid = (BTLMarketBid)contract;
				Match match = HouseSaleMarket.this.matchBid(bid);
				if(match != null) {
					add((MarketBid)contract);
					return(true);
				}
			}
			return(super.receive(contract));
		}
	}

	

	public class Offers extends HousingMarket.Offers {
		public Offers() {
			BTLqueue = new PriorityQueue2D<>(new IYieldPriceSupplier.Comparator());
		}

		@Override
		public void add(MarketOffer offer) {
			super.add(offer);
			BTLqueue.add(offer);
		}

		@Override
		public boolean discard(Object offer) {
			if(super.discard(offer)) {
				return(BTLqueue.remove(offer));
			}
			return(false);
		}
		
		@Override
		public void reducePrice(MarketOffer offer, long newPrice) {
			if(newPrice != offer.currentPrice) {
				BTLqueue.remove(offer);
				super.reducePrice(offer, newPrice);
				BTLqueue.add(offer);
			}
		}

		PriorityQueue2D<HouseSaleMarket.IYieldPriceSupplier>	BTLqueue; // offers sorted by yeild
	}

	
	public static interface IYieldPriceSupplier {
		double getExpectedGrossYield();
		long getPrice();
		public static class Comparator implements PriorityQueue2D.XYComparator<IYieldPriceSupplier> {
			@Override
			public int XCompare(IYieldPriceSupplier arg0, IYieldPriceSupplier arg1) {
				return(Long.signum(arg0.getPrice() - arg1.getPrice()));
			}
			@Override
			public int YCompare(IYieldPriceSupplier arg0, IYieldPriceSupplier arg1) {
				return((int)Math.signum(arg0.getExpectedGrossYield() - arg1.getExpectedGrossYield()));
			}
		}
	}

}
