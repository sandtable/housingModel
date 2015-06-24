package testing;

import sim.engine.SimState;
import sim.engine.Steppable;
import sim.engine.Stoppable;
import utilities.ModelTime;

/***
 * Standard triggers
 * @author daniel
 *
 */
public abstract class Trigger implements ITrigger {
			
	static public class RepeatingTrigger extends Trigger {
		public RepeatingTrigger(ModelTime t) {
			period = new ModelTime(t);
		}
		
		@SuppressWarnings("serial")
		@Override
		public void schedule(final ITriggerable listener) {
			stopper = housing.Model.globalSchedule.scheduleRepeating(housing.Model.modelTime().raw(),
					new Steppable() {
						@Override
						public void step(SimState arg0) {
							listener.trigger();
						}					
					},
					period.raw());
		}
		
		public void stop() {
			stopper.stop();
		}

		/**
		 * @param end
		 * @return A trigger that triggers when this triggers and 'end' hasn't triggered
		 * since scheduling
		 */
		public Trigger until(ITrigger end) {
			return(new Until(this, end));
		}
		/**
		 * @param duration
		 * @return A trigger that triggers when this triggers and time is less
		 * than or equal to the time at scheduling + duration
		 */
		public Trigger continuingFor(ModelTime duration) {
			return(until(after(duration)));
		}			
		
		Stoppable	 		stopper;
		final ModelTime		period;
	}
	
	static public class Until extends Trigger implements ITriggerable {
		public Until(RepeatingTrigger iBaseTrigger, ITrigger iEndTrigger) {
			baseTrigger = iBaseTrigger;
			endTrigger = iEndTrigger;
		}

		@Override
		public void trigger() {
			listener.trigger();
		}

		@Override
		public void schedule(ITriggerable iListener) {
			endTrigger.schedule(new ITriggerable() {
				public void trigger() {
					baseTrigger.stop();
				}
			});
			listener = iListener;
			baseTrigger.schedule(this);
		}
		
		ITriggerable 			listener;
		RepeatingTrigger		baseTrigger;
		ITrigger				endTrigger;
	}
	
	static public RepeatingTrigger repeatingEvery(ModelTime time) {
		return(new RepeatingTrigger(time));
	}
	
	static public Trigger timeIs(ModelTime time) {
		final ModelTime t = new ModelTime(time.raw());
		return(new Trigger() {
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

	static public Trigger after(ModelTime time) {
		final ModelTime t = new ModelTime(Math.nextUp(time.raw()));
		return(new Trigger() {
			@SuppressWarnings("serial")
			@Override
			public void schedule(final ITriggerable listener) {
				housing.Model.globalSchedule.scheduleOnceIn(t.raw(), new Steppable() {
					@Override
					public void step(SimState arg0) {
						listener.trigger();
					}
				});
			}
		});
	}

	static public RepeatingTrigger yearly() {return(repeatingEvery(ModelTime.year()));}
	static public RepeatingTrigger monthly() {return(repeatingEvery(ModelTime.month()));}
	static public RepeatingTrigger weekly() {return(repeatingEvery(ModelTime.week()));}
	static public RepeatingTrigger daily() {return(repeatingEvery(ModelTime.day()));}

	
	static public ITrigger onDemand() {
		return(new Trigger() {
			@Override
			public void schedule(ITriggerable contract) {
			}
		});
	}
	
	void test() {
	}
}
