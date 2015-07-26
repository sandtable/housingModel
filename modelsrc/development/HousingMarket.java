package development;

import housing.Model;
import contracts.BTLMarketBid;
import contracts.Contract;
import contracts.MarketBid;
import contracts.MarketOffer;
import contracts.OOMarketBid;
import utilities.ExponentialAverage;
import utilities.ModelTime;
import utilities.PriorityQueue2D;

public abstract class HousingMarket extends EconAgent {
	public static final double E = Math.exp(-1.0/200); // decay const for averaging days on market
	public static final double G = Math.exp(-1.0/8); // Decay const for averageListPrice averaging
	public static final double F = Math.exp(-1.0/12.0); // House Price Index appreciation decay const (in market clearings)

	public Bids 	bids;
	public Offers	offers;
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

	public Match matchBid(OOMarketBid bid) {
		return(setupMatch((MarketOffer)offers.OOqueue.peek(bid), bid));
	}

	Match setupMatch(MarketOffer matchedOffer, MarketBid bid) {
		if(matchedOffer == null) return(null);
		if(matchedOffer.isUnderOffer()) {
			matchedOffer.currentMatch.stop();
			HousingMarket.this.bids.discard(matchedOffer.currentMatch.bid);
			matchedOffer.unMatch();
		}
		Match match = matchedOffer.matchWith(bid);
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
	abstract public Offers newOffers();
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
		while(averageSalePrice[q].value() < price) ++q;
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
			if(parent instanceof EconAgent) {
				((EconAgent)parent).registerHandler(OOMarketBid.class, this);
			}
		}

		@Override
		public boolean receive(IMessage contract) {
			if(contract instanceof OOMarketBid) {
				OOMarketBid bid = (OOMarketBid)contract;
				Match match = HousingMarket.this.matchBid(bid);
				if(match != null) {
					add((MarketBid)contract);
					return(true);
				}
			}
			return(false); // Don't use super.receive as we're not expecting MarketBid type
		}
	}

	/////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////
	public class Offers extends Contract.Owner<MarketOffer> {
		public Offers() {
			super(MarketOffer.class);
			OOqueue = new PriorityQueue2D<>(new IQualityPriceSupplier.Comparator());
		}
	
		@Override
		public boolean discard(Object offer) {
			if(super.discard(offer)) {
				return(OOqueue.remove(offer));
			}
			return(false);
		}

		@Override
		public void add(MarketOffer offer) {
			super.add(offer);
			OOqueue.add(offer);
		}
		
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
	

}
