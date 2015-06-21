package testing;

import java.util.IdentityHashMap;

import sim.engine.SimState;
import sim.engine.Steppable;

public class Contract<OWNER extends Contract.Owner<?>,ISSUER extends Contract.Issuer<?>> extends IntangibleAsset<OWNER> implements ITriggerable {

	public Contract(OWNER iOwner, ISSUER iIssuer) {
		this(iOwner, iIssuer, onDemand());
	}
	
	public Contract(OWNER iOwner, ISSUER iIssuer, ITrigger when) {
		super(iOwner);
		issuer = iIssuer;
		trigger = when;
	}
	
	public void trigger() { 
		// default behaviour: do nothing
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
		static public class Issuer<CONTRACT extends Contract<? extends Owner<CONTRACT>,? extends Issuer<CONTRACT>>> {
		// honour contract
		public boolean honour(CONTRACT contract) {
			return(false);
		}
		
		// issue contract
		public void issue(CONTRACT newContract) {
			contracts.put(newContract, newContract.owner);
			newContract.owner.receive(newContract); 
		}
		
		IdentityHashMap<CONTRACT, Object> contracts = new IdentityHashMap<>();
	}
		
	/***
	 * Agent module for deposit account holder
	 * @author daniel
	 */
	static public class Owner<CONTRACT extends Contract<? extends Owner<CONTRACT>,? extends Issuer<CONTRACT>>> {
		public void receive(CONTRACT newContract) {
			contracts.put(newContract, newContract.issuer);
		}
		
		IdentityHashMap<CONTRACT, Object> contracts = new IdentityHashMap<>();
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

	
	ISSUER 		issuer;
	ITrigger	trigger;
}
