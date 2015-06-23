package testing;

import utilities.IdentityHashSet;

public class Contract<CONTRACT, ISSUER extends Contract.IIssuer<CONTRACT>, OWNER extends Contract.IOwner<CONTRACT>> extends IntangibleAsset<OWNER> implements ITriggerable {

	public Contract(ISSUER iIssuer, OWNER iOwner) {
		this(iIssuer, iOwner, Triggers.onDemand());
	}

	public Contract(ISSUER iIssuer, OWNER iOwner, ITrigger when) {
		super(iOwner);
		issuer = iIssuer;
		trigger = when;
	}
	
	public void trigger() { 
//		owner.trigger((CONTRACT)this);
	}
	
	public void schedule() {
		trigger.schedule(this);
	}
		
	/***
	public Class<? extends IAssetHolder> ownerType() {
		return(IAssetHolder.class);
	}
	***/
	/***
	 * Agent module for deposit account issuer
	 * @author daniel
	 */
//	static public class Issuer<	OWNER extends Owner<OWNER,ISSUER,CONTRACT>,
//								ISSUER extends Issuer<OWNER,ISSUER,CONTRACT>,
//								CONTRACT extends Contract<OWNER,ISSUER>> {
	static public class Issuer<CONTRACT extends Contract<CONTRACT,?,?>> extends IdentityHashSet<CONTRACT> implements IIssuer<CONTRACT> {
		// honour contract
		public boolean honour(CONTRACT contract) {
			if(contract.issuer != this) return(false);
			return(true);
		}
		
		// issue contract
		public boolean issue(CONTRACT newContract) {
			if(newContract.issuer != this) return(false);
			add(newContract);
			boolean accepted = newContract.owner.receive(newContract);
			if(accepted) return(true);
			remove(newContract);
			return(false);
		}
		
	}
	
	/***
	 * Agent module for deposit account holder
	 * @author daniel
	 */
	static public class Owner<CONTRACT extends Contract<CONTRACT,?,?>> extends IdentityHashSet<CONTRACT> implements IOwner<CONTRACT> {
		public boolean receive(CONTRACT newContract) {
			if(newContract.owner != this) return(false);
			add(newContract);
			return(true);
		}
		
		public boolean discard(CONTRACT contract) {
			if(contract.owner != this) return(false);
			remove(contract);
			contract.issuer.remove(contract);
			return(true);
		}
		
		// receive this when a contract I own is triggered
		public void trigger(CONTRACT contract) {
			contract.issuer.honour(contract); // default behaviour...exercise the contract
		}
	}
	
	static public interface IIssuer<CONTRACT> {
//		boolean issue(CONTRACT newContract);
		boolean honour(CONTRACT contract);
		boolean remove(CONTRACT contract);
	}
//	static public interface IContract {
//		void trigger();
//		IOwner owner();
//		IIssuer issuer();
//	}
	static public interface IOwner<CONTRACT> {
		boolean receive(CONTRACT newContract);
	//	boolean discard(CONTRACT contract);
		void trigger(CONTRACT contract);
	}
	
	ISSUER	 		issuer;
	ITrigger		trigger;
}
