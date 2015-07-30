package development;

import housing.Model;
import contracts.BTLMarketBid;
import contracts.Contract;
import contracts.MarketBid;
import contracts.MarketOffer;
import contracts.OOMarketBid;
import contracts.RentalMarketBid;
import utilities.ExponentialAverage;
import utilities.ModelTime;
import utilities.PriorityQueue2D;

public abstract class HousingMarket extends EconAgent {
	public static final double E = Math.exp(-1.0/200); // decay const for averaging days on market
	public static final double G = Math.exp(-1.0/8); // Decay const for averageListPrice averaging
	public static final double F = Math.exp(-1.0/12.0); // House Price Index appreciation decay const (in market clearings)

	public Bids 	bids;
	public Offers<? extends MarketOffer>			offers;
	public ExponentialAverage averageDaysOnMarket;
	protected ExponentialAverage averageSalePrice[];
	public double HPIAppreciation;
	public double housePriceIndex;
	public double lastHousePriceIndex;

	public HousingMarket() {
		bids = newBids();
		offers = newOffers(); // good way of initialising circular dependency...!
		addChild(bids);
		addChild(offers);
		
		averageSalePrice = new ExponentialAverage[House.Config.N_QUALITY];
		for(int i = 0; i<House.Config.N_QUALITY; ++i) {
			averageSalePrice[i] = new ExponentialAverage(G, referencePrice(i));
		}
		housePriceIndex = 1.0;
		lastHousePriceIndex = 1.0;
		HPIAppreciation = 0.0;
		averageDaysOnMarket = new ExponentialAverage(E, 30);
	}

	@Override
	public void start(IModelNode parent) {
		super.start(parent);
		final HousingMarket me = this;
		Trigger.quarterly().schedule(new ITriggerable() {
			public void trigger() {me.doQuarterlyStats();}
		});		
	}

	@Override
	public boolean receive(IMessage message) {
		if(message instanceof Match) {
			// a match has completed its wait
			Match match = (Match)message;
			match.offer.getIssuer().completeSale(match.offer, match.bid);
			offers.discard(match.offer);
			bids.discard(match.bid);
			recordTransaction(match);
			return(true);
		}
		return(super.receive(message));
	}

	public Match matchBid(RentalMarketBid bid) {
		return(setupMatch(offers.peek(bid), bid));
	}

	Match setupMatch(MarketOffer matchedOffer, MarketBid bid) {
		if(matchedOffer == null) return(null);
		if(matchedOffer.isUnderOffer()) {
			matchedOffer.currentMatch.stop();
			System.out.println("Bids :"+bids.data);
			System.out.println("Offers :"+offers.data);
			System.out.println("Old Match: "+matchedOffer+":"+offers.contains(matchedOffer) +
					" "+matchedOffer.currentMatch.bid+":"+bids.contains(matchedOffer.currentMatch.bid));
			System.out.println("offer in ooqueue:"+offers.OOqueue.contains(matchedOffer));
//			System.out.println("in btlqueue:"+offers.BTLqueue.contains(matchedOffer));
			System.out.println("Offers size:"+this.offers.size()+" OOqueue size:"+this.offers.OOqueue.size());
			HousingMarket.this.bids.discard(matchedOffer.currentMatch.bid);
			matchedOffer.unMatch();
		}
//		Match match = matchedOffer.matchWith(bid);
		Match match = new HousingMarket.Match(matchedOffer, bid);

		match.schedule(match, this);
		return(match);
	}
	
	/**********************************************
	 * Record statistics on a completed transaction
	 * 
	 * @param match the matched bid and offer
	 **********************************************/
	public void recordTransaction(Match match) {
		averageDaysOnMarket.record(ModelTime.now().minus(match.offer.tInitialListing).inDays());
		averageSalePrice[match.offer.getQuality()].record(match.offer.getPrice());
	}

	// --- override these to make sub-classed bids and offers traits
	abstract public Bids newBids();
	abstract public Offers<? extends MarketOffer> newOffers();
	abstract public long referencePrice(int quality);

	///////////////////////////////////////////////////////////////
	// market stats
	///////////////////////////////////////////////////////////////
	
	/***
	 * @return Annual HPI appreciation
	 */
	public double housePriceAppreciation() {
		return HPIAppreciation;
	}
	
	public double housePriceIndex() {
		return housePriceIndex;
	}

	public long getAverageSalePrice(int quality) {
		return((long)averageSalePrice[quality].value());
	}
	
	public int getAverageDaysOnMarket() {
		return((int)averageDaysOnMarket.value());
	}
	
	/***
	 * @param price
	 * @return 	 The quality of house you would expect to get
	 * for the given price
	 */
	public int qualityGivenPrice(long price) {
		int q=0;
		while(q < House.Config.N_QUALITY && averageSalePrice[q].value() < price) ++q;
		if(q>0) --q;
		return(q);
	}
	
	protected void doQuarterlyStats() {
		HPIAppreciation = F*HPIAppreciation - 4.0*(1.0-F)*housePriceIndex;
		housePriceIndex = 0.0;
		for(ExponentialAverage price : averageSalePrice) {
			housePriceIndex += price.value(); // TODO: assumes equal distribution of houses over qualities
		}
		housePriceIndex /= House.Config.N_QUALITY*Data.HousingMarket.HPI_MEAN;
		HPIAppreciation += 4.0*(1.0-F)*housePriceIndex;

	}

	///////////////////////////////////////////////////////////////
	// Owner/Issuer
	///////////////////////////////////////////////////////////////
	public class Bids extends Contract.Owner<MarketBid> {
		public Bids() {
			super(MarketBid.class);
		}

		@Override
		public void start(IModelNode parent) {
			super.start(parent);
		}

		@Override
		public boolean discard(Contract contract) {
			if(super.discard(contract)) {
				Match match = ((MarketBid)contract).currentMatch;
				if(match != null) {
					match.offer.currentMatch = null;
					match.stop();
				}
				return(true);
			}
			return(false);
		}
		
		@Override
		public boolean receive(IMessage contract) {
			if(contract instanceof RentalMarketBid) { // RentalMarketBids and OOMarketBids
				RentalMarketBid bid = (RentalMarketBid)contract;
				Match match = HousingMarket.this.matchBid(bid);
				if(match != null) {
					add(bid);
					return(true);
				}
			}
			return(false); // Don't use super.receive as we're not expecting MarketBid type
		}
	}

	/////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////
	public class Offers<CONTRACT extends MarketOffer> extends Contract.Owner<CONTRACT> {
		public Offers(Class<CONTRACT> clazz) {
			super(clazz);
			OOqueue = new PriorityQueue2D<>(new IQualityPriceSupplier.Comparator());
		}
	
		@Override
		public boolean discard(Contract offer) {
			if(super.discard(offer)) {
				Match match = ((MarketBid)offer).currentMatch;
				if(match != null) {
					HousingMarket.this.bids.discard(match.bid);
				}
				if(!OOqueue.contains(offer)) System.out.println("Error: Offer is not in OOqueue");
				OOqueue.remove(offer);
				if(OOqueue.contains(offer)) System.out.println("Error: deletion failure");
				return(true);
			}
			return(false);
		}

		@Override
		public void add(CONTRACT offer) {
			super.add(offer);
			OOqueue.add(offer);
		}
		
		public void reducePrice(MarketOffer offer, long newPrice) {
			if(newPrice != offer.currentPrice) {
				OOqueue.remove(offer);
				offer.currentPrice = newPrice;
				OOqueue.add(offer);
			}
		}
		
		public MarketOffer peek(IQualityPriceSupplier xMax) {
			return((MarketOffer)OOqueue.peek(xMax));
		}
		
		PriorityQueue2D<IQualityPriceSupplier>	OOqueue;
	}

//	public interface IOffers extends Contract.IOwner {
//		MarketOffer peek(IQualityPriceSupplier xMax);
//		void reducePrice(MarketOffer offer, long newPrice);
//	}
	
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
			if(offer.currentMatch != null) System.out.println("Error: Setting up a match on an already matched MarketOffer");
			offer.currentMatch = this;
			if(bid.currentMatch != null) System.out.println("Error: Setting up a match on an already matched MarketBid");
			bid.currentMatch = this;
		}
		
		MarketOffer offer;
		MarketBid 	bid;
		static final ModelTime	gazumpTime = ModelTime.week();
	}
	

}
