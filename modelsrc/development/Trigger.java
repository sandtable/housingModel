package development;

import development.IMessage.IReceiver;
import sim.engine.SimState;
import sim.engine.Steppable;
import sim.engine.Stoppable;
import utilities.ModelTime;

/***
 * Standard triggers
 * @author daniel
 *
 */
public class Trigger {
	
	@SuppressWarnings("serial")
	static abstract public class StoppableSteppableTrigger implements Stoppable, Steppable, ITrigger {
		public StoppableSteppableTrigger() {
			active = false;
			listener = null;
		}
		
		@Override
		public void step(SimState arg0) {
			if(active) listener.trigger();
		}
		
		@Override
		public void stop() {
			active = false;
		}

		@Override
		public StoppableSteppableTrigger schedule(ITriggerable iListener) {
			active = true;
			listener = iListener;
			sendToSchedule();
			return(this);
		}
		
		@Override
		public void schedule(final IMessage message, final IMessage.IReceiver handler) {
			schedule(new ITriggerable() {
				public void trigger() {
					handler.receive(message);
				}
			});
		}

		abstract public void sendToSchedule(); // actually put this object on the schedule
		
		public void scheduleOnce(double time) {
			ModelBase.root.schedule.scheduleOnce(time, this);			
		}
		
		public Stoppable scheduleRepeating(double startTime, double period) {
			return(ModelBase.root.schedule.scheduleRepeating(startTime, this, period));	
		}

		public void scheduleOnceIn(double delay) {
			ModelBase.root.schedule.scheduleOnce(ModelTime.now().raw()+delay, this);	
		}

		boolean 		active;
		ITriggerable 	listener;
	}
	
	@SuppressWarnings("serial")
	static public class Once extends StoppableSteppableTrigger {
		public Once(ModelTime triggerTime) {
			this.triggerTime = triggerTime.raw();
		}
		@Override
		public void sendToSchedule() {
			scheduleOnce(triggerTime);
		}
		double triggerTime;
	}

	@SuppressWarnings("serial")
	static public class OnceAfter extends StoppableSteppableTrigger {
		public OnceAfter(ModelTime triggerDelay) {
			delay = triggerDelay.raw();
		}
		@Override
		public void sendToSchedule() {
			scheduleOnceIn(delay);
		}
		double delay;
	}

	@SuppressWarnings("serial")
	static public class Repeating extends StoppableSteppableTrigger {
		public Repeating(ModelTime t) {
			period = new ModelTime(t);
		}
		
		public void sendToSchedule() {
			stopper = ModelBase.root.schedule.scheduleRepeating(ModelTime.now().raw(),this,period.raw());
		}
		
		@Override
		public void stop() {
			super.stop();
			if(stopper != null) stopper.stop();
		}

		/**
		 * @param end
		 * @return A trigger that triggers when this triggers and 'end' hasn't triggered
		 * since scheduling
		 */
		public ITrigger until(ITrigger end) {
			return(new Until(this, end));
		}
		/**
		 * @param duration
		 * @return A trigger that triggers when this triggers and time is less
		 * than or equal to the time at scheduling + duration
		 */
		public ITrigger continuingFor(ModelTime duration) {
			return(until(after(duration)));
		}			
		
		Stoppable	 		stopper;
		final ModelTime		period;
	}
	
	static public class Until implements ITrigger {
		public Until(Repeating iBaseTrigger, ITrigger iEndTrigger) {
			baseTrigger = iBaseTrigger;
			endTrigger = iEndTrigger;
		}

		@Override
		public StoppableSteppableTrigger schedule(ITriggerable listener) {
			scheduleEndTrigger();
			baseTrigger.schedule(listener);
			return(baseTrigger);
		}

		@Override
		public void schedule(IMessage message, IReceiver handler) {
			scheduleEndTrigger();
			baseTrigger.schedule(message, handler);
		}
		
		@Override
		public void stop() {
			baseTrigger.stop();
			endTrigger.stop();
		}
		
		void scheduleEndTrigger() {
			endTrigger.schedule(new ITriggerable() {
				public void trigger() {
					baseTrigger.stop();
				}
			});			
		}
		
		Repeating		baseTrigger;
		ITrigger		endTrigger;
	}
	
	/***
	 * Implements a trigger that triggers as a Poisson process with a given
	 * rate parameter r. i.e. probability of going time t between triggers
	 * is given by P(t) = e^-rt
	 * @author daniel
	 *
	 */
	@SuppressWarnings("serial")
	static public class PoissonProcess extends OnceAfter {
		public PoissonProcess(ModelTime meanInterval) {
			super(new ModelTime(0.0,ModelTime.Units.RAW));
			this.meanInterval = meanInterval;
			delay = nextDelay();
		}
		
		@Override
		public void step(SimState arg0) {
			if(active) {
				listener.trigger();
				delay = nextDelay();
				sendToSchedule();
			}
		}
		
		/** return: a random delay between events with exponential distribution */
		double nextDelay() {
			return(-Math.log(1.0 - ModelBase.root.random.nextDouble())*meanInterval.raw());
		}
		
		ModelTime meanInterval;
	}

	static public Repeating repeatingEvery(ModelTime time) 	{return(new Repeating(time));}
	static public Once 		timeIs(ModelTime time) 			{return(new Once(time));}
	static public OnceAfter after(ModelTime time) 			{return(new OnceAfter(time));}
	static public Repeating yearly() 						{return(repeatingEvery(ModelTime.year()));}
	static public Repeating quarterly()						{return(repeatingEvery(ModelTime.quarter()));}
	static public Repeating monthly() 						{return(repeatingEvery(ModelTime.month()));}
	static public Repeating weekly() 						{return(repeatingEvery(ModelTime.week()));}
	static public Repeating daily() 						{return(repeatingEvery(ModelTime.day()));}
	static public PoissonProcess   poisson(ModelTime meanInterval)		{return(new PoissonProcess(meanInterval));}
	static public ITrigger onDemand() {
		return(new ITrigger() {
			@Override
			public StoppableSteppableTrigger schedule(ITriggerable contract) {
				return(null);
			}
			@Override
			public void stop() {
			}
			@Override
			public void schedule(IMessage message, IReceiver handler) {
			}
		});
	}
}
