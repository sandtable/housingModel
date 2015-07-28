package contracts;

import contracts.TangibleAsset.IOwner;
import utilities.ModelTime;
import development.IMessage;
import development.IModelNode;
import development.ITriggerable;
import development.Trigger;
import development.IMessage.IReceiver;

public class MarketBid extends Contract {
	long price;
	IMessage.IReceiver market;
	
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
	
	public interface IIssuer extends Contract.IIssuer, IMessage.IReceiver {
		TangibleAsset.IOwner assetOwner();
	}
	
	public static class Issuer extends Contract.Issuer<MarketBid> implements IIssuer {
		TangibleAsset.IOwner assetOwner;
		
		public Issuer() {
			super(MarketBid.class);
		}
		
		@Override
		public void start(IModelNode parent) {
			assetOwner = parent.mustGet(TangibleAsset.IOwner.class);
			super.start(parent);
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
		public boolean terminate(Contract contract) {
			if(super.terminate(contract)) {
				reIssue(((MarketBid)contract), ModelTime.week());				
			}
			return(false);
		}
		
		void reIssue(final MarketBid bid, ModelTime delay) {
			Trigger.after(delay).schedule(new ITriggerable() {
				public void trigger() {issue(bid);}
			});			
		}

		@Override
		public TangibleAsset.IOwner assetOwner() {
			return assetOwner;
		}

	}
	
	
}
