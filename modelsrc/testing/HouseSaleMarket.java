package testing;

import testing.Market.Match;
import utilities.PriorityQueue2D;

public class HouseSaleMarket extends Market {
	static interface IYeildPriceSupplier {
		double getYeild();
		long getPrice();
	}
	
	public class Offers extends Market.Offers {
		public Match matchBid(BTLMarketBid bid) {
			return(setupMatch((MarketOffer)BTLqueue.poll(bid), bid));
		}

		PriorityQueue2D<HouseSaleMarket.IYeildPriceSupplier>	BTLqueue;
	}
}
