package testing;

import sim.engine.SimState;
import sim.engine.Steppable;
import utilities.IdentityHashSet;

public class Contract<CONTRACT, OWNER extends Contract.IOwner<CONTRACT>, ISSUER extends Contract.IIssuer<CONTRACT>> extends IntangibleAsset<OWNER> implements ITriggerable {

	public Contract() {
		this(null, null, onDemand());
	}

	public Contract(OWNER iOwner, ISSUER iIssuer, ITrigger when) {
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
		public void issue(CONTRACT newContract) {
//			newContract.issuer = this;
			add(newContract);
			newContract.owner.receive(newContract); 
		}		
	}
	
	/***
	 * Agent module for deposit account holder
	 * @author daniel
	 */
	static public class Owner<CONTRACT extends Contract<CONTRACT,?,?>> extends IdentityHashSet<CONTRACT> implements IOwner<CONTRACT> {
		public void receive(CONTRACT newContract) {
//			newContract.owner = this;
			add(newContract);
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
//		void issue(CONTRACT newContract, IOwner owner);
		boolean honour(CONTRACT contract);
		boolean remove(CONTRACT contract);
	}
//	static public interface IContract {
//		void trigger();
//		IOwner owner();
//		IIssuer issuer();
//	}
	static public interface IOwner<CONTRACT> {
		void receive(CONTRACT newContract);
		boolean discard(CONTRACT contract);
		void trigger(CONTRACT contract);
	}
	
	//////////////////////////////////////////////////////////
	// Standard triggers
	//////////////////////////////////////////////////////////
	static public ITrigger repeatingEvery(final double time) {
		return(new ITrigger() {
			@SuppressWarnings("serial")
			@Override
			public void schedule(final ITriggerable contract) {
				housing.Model.globalSchedule.scheduleRepeating(time, new Steppable() {
					@Override
					public void step(SimState arg0) {
						contract.trigger();
					}
					
				});
			}
			
		});
	}
	static public ITrigger yearly() {return(repeatingEvery(360.0));}
	static public ITrigger monthly() {return(repeatingEvery(30.0));}
	static public ITrigger weekly() {return(repeatingEvery(7.0));}
	static public ITrigger daily() {return(repeatingEvery(1.0));}
	
	static public ITrigger onDemand() {
		return(new ITrigger() {
			@Override
			public void schedule(ITriggerable contract) {
			}
		});
	}

	
	ISSUER	 		issuer;
	ITrigger		trigger;
}
