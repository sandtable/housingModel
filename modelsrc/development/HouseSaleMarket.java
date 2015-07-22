package development;

import java.util.HashMap;

import development.Market.Match;
import utilities.PriorityQueue2D;

public class HouseSaleMarket extends Market {

	@Override
	public Market.Offers newOffers() {
		return(this.new Offers());
	}

	@Override
	public Market.Bids newBids() {
		return(this.new Bids());
	}

	static interface IYeildPriceSupplier {
		double getYeild();
		long getPrice();
	}

	public class OOBids extends Contract.Owner<OOMarketBid> {
		public OOBids() {
			super(OOMarketBid.class);
		}
		
		@Override
		public boolean receive(IMessage contract) {
			if(super.receive(contract)) {
				// match bid with current offers
				// Market.this.offers blah blah
			}
			return(false);
		}
		HashMap<MarketBid,Match>	bidMatches;
	}

	

	public class Offers extends Market.Offers {
		public Match matchBid(BTLMarketBid bid) {
			return(setupMatch((MarketOffer)BTLqueue.poll(bid), bid));
		}

		PriorityQueue2D<HouseSaleMarket.IYeildPriceSupplier>	BTLqueue;
	}
	
	public OOBids ooBids;
}
