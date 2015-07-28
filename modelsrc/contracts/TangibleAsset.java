package contracts;

import development.IMessage;
import development.IModelNode;
import development.NodeHashSet;

public class TangibleAsset implements IMessage {
	public TangibleAsset.IOwner owner;
	
	public interface IOwner extends IModelNode {
		boolean give(TangibleAsset asset, TangibleAsset.IOwner recipient);
	}
	
	public static class Owner extends NodeHashSet<TangibleAsset> implements TangibleAsset.IOwner {
		public Owner() {
			super(TangibleAsset.class);
		}
		
		@Override
		public boolean receive(IMessage asset) {
			if(asset instanceof TangibleAsset) {
				add((TangibleAsset)asset);
				((TangibleAsset) asset).owner = this;
				return(true);
			}
			return(super.receive(asset));
		}
		
		public boolean give(TangibleAsset asset, TangibleAsset.IOwner recipient) {
			if(asset.owner == this && recipient.receive(asset)) {
				remove(asset);
				return(true);
			}
			return(false);
		}
	}
}
