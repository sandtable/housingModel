package testing;

import sim.engine.SimState;
import sim.engine.Steppable;
import utilities.IdentityHashSet;

public class Contract<CONTRACT extends Contract<CONTRACT>> extends IntangibleAsset<Contract.Owner<CONTRACT>> implements ITriggerable {

	public Contract() {
		this(null, null, onDemand());
	}

	public Contract(Contract.Owner<CONTRACT> iOwner, Contract.Issuer<CONTRACT> iIssuer, ITrigger when) {
		super(iOwner);
		issuer = iIssuer;
		trigger = when;
	}
	
	public void trigger() { 
		owner.trigger((CONTRACT)this);
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
		static public class Issuer<CONTRACT extends Contract<CONTRACT>> extends IdentityHashSet<CONTRACT> {
		// honour contract
		public boolean honour(CONTRACT contract) {
			if(contract.issuer != this) return(false);
			return(true);
		}
		
		// issue contract
		public void issue(CONTRACT newContract, Owner<CONTRACT> owner) {
			newContract.issuer = this;
			add(newContract);
			owner.receive(newContract); 
		}		
	}
		
	/***
	 * Agent module for deposit account holder
	 * @author daniel
	 */
	static public class Owner<CONTRACT extends Contract<CONTRACT>> extends IdentityHashSet<CONTRACT> {
		public void receive(CONTRACT newContract) {
			newContract.owner = this;
			add(newContract);
		}
		
		public boolean destroy(CONTRACT contract) {
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

	
	Contract.Issuer<CONTRACT> 		issuer;
	ITrigger				trigger;
}
