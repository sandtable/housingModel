package testing;

import java.util.HashMap;

import utilities.ModelTime;

public class Market extends EconAgent {
	public Market() {
		bids = this.new Bids();
		offers = this.new Offers(); // good way of initialising circular dependency...!
		addTrait(bids);
		addTrait(offers);
	}
		
	public class Bids extends Contract.Owner<MarketBid> {
		public Bids() {
			super(MarketBid.class);
		}
		
		@Override
		public boolean receive(IMessage contract) {
			if(super.receive(contract)) {
				// match bid with current offers
				// Market.this.offers blah blah
			}
			return(false);
		}
	}

	public class Offers extends Contract.Owner<MarketOffer> {
		public Offers() {
			super(MarketOffer.class);
		}
	}

	@SuppressWarnings("serial")
	static public class Match extends Trigger.OnceAfter {
		public Match() {
			super(gazumpTime);
		}
		
		MarketOffer offer;
		MarketBid	bid;
		static final ModelTime	gazumpTime = ModelTime.week();
	}
	
	Bids 	bids;
	Offers	offers;
	HashMap<MarketOffer,Match> 	offerMatches;
	HashMap<MarketBid,Match>	bidMatches;
	
}
