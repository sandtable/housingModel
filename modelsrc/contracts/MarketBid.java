package contracts;

import contracts.TangibleAsset.IOwner;
import utilities.ModelTime;
import development.HousingMarket;
import development.IMessage;
import development.IModelNode;
import development.ITriggerable;
import development.Trigger;
import development.IMessage.IReceiver;

public class MarketBid extends Contract {
	long price;
	IMessage.IReceiver market;
	public HousingMarket.Match currentMatch;		// if non-null this bid is currently an 'under offer' bid	
	
	public MarketBid(IIssuer issuer, long price, IMessage.IReceiver market) {
		super(issuer);
		this.price = price;
		this.market = market;
	}
	
	public long getPrice() {
		return(price);
	}
	
	public IIssuer getIssuer() {
		return((IIssuer)issuer);
	}
	
	
	public static class Issuer extends Contract.Issuer<MarketBid> implements IIssuer {
		
		public Issuer() {
			super(MarketBid.class);
		}
		

		@Override
		public boolean receive(IMessage d) {
			return(parent().receive(d));
		}

		public boolean issue(MarketBid bid) {
			if(super.issue(bid, bid.market)) {
				return(true);
			}
			reIssue(bid, ModelTime.week());
			return(false);
		}

		@Override
		public boolean ownerDiscarded(Contract contract) {
			if(super.ownerDiscarded(contract)) {
				reIssue(((MarketBid)contract), ModelTime.week());
				return(true);
			}
			return(false);
		}
		
		void reIssue(final MarketBid bid, ModelTime delay) {
			Trigger.after(delay).schedule(new ITriggerable() {
				public void trigger() {
					issue(bid);
				}
			});			
		}


	}
	
	
}
