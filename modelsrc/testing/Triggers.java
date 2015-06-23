package testing;

import sim.engine.SimState;
import sim.engine.Steppable;
import utilities.ModelTime;

/***
 * Standard triggers
 * @author daniel
 *
 */
public class Triggers {
	static public abstract class Qualifiable implements ITrigger {
		public Qualifiable until(ITrigger end) {
			return(new Until(this, end));
		}
		
//		@Override
//		public void schedule(ITriggerable listener) {
//			baseTrigger.schedule(listener);
//		}

//		ITrigger baseTrigger;
	}
	
	static public class Until extends Qualifiable implements ITriggerable {
		public Until(ITrigger iBaseTrigger, ITrigger endTrigger) {
			baseTrigger = iBaseTrigger;
			activated = true;
			endTrigger.schedule(new ITriggerable() {
				public void trigger() {
					activated = false;
				}
			});
		}

		@Override
		public void trigger() {
			// TODO Auto-generated method stub
			if(activated) listener.trigger();
		}

		@Override
		public void schedule(ITriggerable iListener) {
			listener = iListener;
			baseTrigger.schedule(this);
		}
		
		ITriggerable 	listener;
		ITrigger		baseTrigger;
		boolean 		activated;
	}
	
	static public Qualifiable repeatingEvery(final ModelTime time) {
		return(new Qualifiable() {
			@SuppressWarnings("serial")
			@Override
			public void schedule(final ITriggerable contract) {
				housing.Model.globalSchedule.scheduleRepeating(time.raw(), new Steppable() {
					@Override
					public void step(SimState arg0) {
						contract.trigger();
					}
					
				});
			}
			
		});
	}
	static public ITrigger yearly() {return(repeatingEvery(ModelTime.year()));}
	static public ITrigger monthly() {return(repeatingEvery(ModelTime.month()));}
	static public ITrigger weekly() {return(repeatingEvery(ModelTime.week()));}
	static public ITrigger daily() {return(repeatingEvery(ModelTime.day()));}

	static public ITrigger timeIs(final ModelTime t) {
		return(new ITrigger() {
			@SuppressWarnings("serial")
			@Override
			public void schedule(final ITriggerable listener) {
				housing.Model.globalSchedule.scheduleOnce(t.raw(), new Steppable() {
					@Override
					public void step(SimState arg0) {
						listener.trigger();
					}
				});
			}
		});
	}
	
	static public ITrigger onDemand() {
		return(new ITrigger() {
			@Override
			public void schedule(ITriggerable contract) {
			}
		});
	}
}
