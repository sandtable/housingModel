package contracts;

import java.util.HashMap;


import development.EconAgent;
import development.HousingMarket;
import development.IMessage;
import development.IModelNode;
import development.HousingMarket.IQualityPriceSupplier;
import development.HousingMarket.Match;
import development.IMessage.IReceiver;
import utilities.PriorityQueue2D;

public class OOMarketBid extends RentalMarketBid {	
	public OOMarketBid(IIssuer issuer, long iPrice, int iMinQuality, IMessage.IReceiver market) {
		super(issuer, iPrice, iMinQuality, market);
	}

	public interface IIssuer extends Contract.IIssuer {
		TangibleAsset.IOwner assetOwner();
	}

	public static class Issuer extends MarketBid.Issuer implements IIssuer {
		TangibleAsset.IOwner assetOwner;

		@Override
		public void start(IModelNode parent) {
			assetOwner = parent.mustGet(TangibleAsset.IOwner.class);
			super.start(parent);
		}

		public boolean issue(long price, int quality, IMessage.IReceiver market) {
			return(issue(new OOMarketBid(this, price, quality, market)));
		}

		@Override
		public TangibleAsset.IOwner assetOwner() {
			return assetOwner;
		}

	}
}
