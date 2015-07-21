package testing;

import java.util.HashMap;

import utilities.ModelTime;
import utilities.PriorityQueue2D;

public class Market extends EconAgent {
	static public interface IQualityPriceSupplier {
		int getQuality();
		long getPrice();
	}
	

	public Market() {
		bids = this.new Bids();
		offers = this.new Offers(); // good way of initialising circular dependency...!
		addTrait(bids);
		addTrait(offers);
	}
	
	public double hpa() {
		return(0.0);
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
		HashMap<MarketBid,Match>	bidMatches;
	}

	public class Offers extends Contract.Owner<MarketOffer> {
		public Offers() {
			super(MarketOffer.class);
		}
		
		@Override
		public boolean receive(IMessage message) {
			if(message instanceof MarketOffer) {
				MarketOffer offer = (MarketOffer)message;
				OOqueue.add(offer);
			} else if(message instanceof Match) {
				// a match has completed its wait
				doTransaction((Match)message);
			}
			return(super.receive(message));
		}
	
		public Match matchBid(OOMarketBid bid) {
			return(setupMatch((MarketOffer)OOqueue.poll(bid), bid));
		}

		Match setupMatch(MarketOffer matchedOffer, MarketBid bid) {
			if(matchedOffer.isUnderOffer()) {
				matchedOffer.currentMatch.stop();
				Market.this.bids.remove(matchedOffer.currentMatch.bid);
				matchedOffer.unMatch();
			}
			Match match = matchedOffer.matchWith(bid);
			match.schedule(match, this);
			return(match);
		}
		
		public void doTransaction(Match match) {
			
		}
		
		PriorityQueue2D<Market.IQualityPriceSupplier>	OOqueue;
	}

	
	
	@SuppressWarnings("serial")
	static public class Match extends Trigger.OnceAfter implements IMessage {
		public Match(MarketOffer iOffer, MarketBid iBid) {
			super(gazumpTime);
			offer = iOffer;
			bid = iBid;
		}
		
		MarketOffer offer;
		MarketBid 	bid;
		static final ModelTime	gazumpTime = ModelTime.week();
	}
	
	Bids 	bids;
	Offers	offers;
}
