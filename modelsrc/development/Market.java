package development;

import java.util.HashMap;

import utilities.ModelTime;
import utilities.PriorityQueue2D;

public abstract class Market extends EconAgent {
	public Market() {
		bids = newBids();
		offers = newOffers(); // good way of initialising circular dependency...!
		addTrait(bids);
		addTrait(offers); 
	}
	
	public double hpa() {
		return(0.0);
	}

	@Override
	public boolean receive(IMessage message) {
		if(message instanceof Match) {
			// a match has completed its wait
			Match match = (Match)message;
			match.offer.getIssuer().completeSale(match.offer);
			match.bid.getIssuer().completePurchase(match.bid, match.offer);
			return(true);
		}
		return(super.receive(message));
	}
	
	// --- override these to make sub-classed bids and offers traits
	abstract public Bids newBids();
	abstract public Offers newOffers();
	
	public class Bids extends Contract.Owner<MarketBid> {
		public Bids() {
			super(MarketBid.class);
		}
		
		@Override
		public boolean receive(IMessage contract) {
			if(contract instanceof OOMarketBid) {
				OOMarketBid bid = (OOMarketBid)contract;
				Match match = Market.this.offers.matchBid(bid);
				if(match != null) {
					add((MarketBid)contract);
					match.schedule(match, Market.this);
					return(true);
				}
			}
			return(false);
		}
//		HashMap<MarketBid,Match>	bidMatches;
	}

	public class Offers extends Contract.Owner<MarketOffer> {
		public Offers() {
			super(MarketOffer.class);
			OOqueue = new PriorityQueue2D<>(new IQualityPriceSupplier.Comparator());
		}
		
		@Override
		public boolean receive(IMessage message) {
			if(message instanceof MarketOffer) {
				MarketOffer offer = (MarketOffer)message;
				OOqueue.add(offer);
	//		} else if(message instanceof Match) {
				// a match has completed its wait
	//			doTransaction((Match)message);
			}
			return(super.receive(message));
		}
	
		public Match matchBid(OOMarketBid bid) {
			MarketOffer matchedOffer = (MarketOffer)OOqueue.poll(bid);
			if(matchedOffer == null) {
				return(null);
			}
			return(setupMatch(matchedOffer, bid));
		}

		Match setupMatch(MarketOffer matchedOffer, MarketBid bid) {
			if(matchedOffer.isUnderOffer()) {
				matchedOffer.currentMatch.stop();
				Market.this.bids.discard(matchedOffer.currentMatch.bid);
				matchedOffer.unMatch();
			}
			Match match = matchedOffer.matchWith(bid);
			match.schedule(match, this);
			return(match);
		}
		
//		public void doTransaction(Match match) {
//			
//		}
		
		PriorityQueue2D<IQualityPriceSupplier>	OOqueue;
	}

	public interface IQualityPriceSupplier {
		int getQuality();
		long getPrice();
		public static class Comparator implements PriorityQueue2D.XYComparator<IQualityPriceSupplier> {
			@Override
			public int XCompare(IQualityPriceSupplier arg0,
					IQualityPriceSupplier arg1) {
				return(Long.signum(arg0.getPrice() - arg1.getPrice()));
			}
			@Override
			public int YCompare(IQualityPriceSupplier arg0,
					IQualityPriceSupplier arg1) {
				return(Long.signum(arg0.getQuality() - arg1.getQuality()));
			}
		}
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
	
	public Bids 	bids;
	public Offers	offers;
}
