package development;

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
			BTLqueue = new PriorityQueue2D<>(new IYeildPriceSupplier.Comparator());
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

		PriorityQueue2D<HouseSaleMarket.IYeildPriceSupplier>	BTLqueue; // offers sorted by yeild
	}

	
	static interface IYeildPriceSupplier {
		double getYeild();
		long getPrice();
		public static class Comparator implements PriorityQueue2D.XYComparator<IYeildPriceSupplier> {
			@Override
			public int XCompare(IYeildPriceSupplier arg0, IYeildPriceSupplier arg1) {
				return(Long.signum(arg0.getPrice() - arg1.getPrice()));
			}
			@Override
			public int YCompare(IYeildPriceSupplier arg0, IYeildPriceSupplier arg1) {
				return((int)Math.signum(arg0.getYeild() - arg1.getYeild()));
			}
		}
	}

}
