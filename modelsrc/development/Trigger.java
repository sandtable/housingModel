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
		public void schedule(ITriggerable iListener) {
			active = true;
			listener = iListener;
			sendToSchedule();
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
		
		boolean 		active;
		ITriggerable 	listener;
	}
	
	@SuppressWarnings("serial")
	static public class Once extends StoppableSteppableTrigger {
		public Once(ModelTime triggerTime) {
			time = triggerTime.raw();
		}
		@Override
		public void sendToSchedule() {
			Model.root.schedule.scheduleOnce(time, this);
		}
		double time;
	}

	@SuppressWarnings("serial")
	static public class OnceAfter extends StoppableSteppableTrigger {
		public OnceAfter(ModelTime triggerDelay) {
			delay = Math.nextUp(triggerDelay.raw());
		}
		@Override
		public void sendToSchedule() {
			Model.root.schedule.scheduleOnceIn(delay, this);
		}
		double delay;
	}

	@SuppressWarnings("serial")
	static public class Repeating extends StoppableSteppableTrigger {
		public Repeating(ModelTime t) {
			period = new ModelTime(t);
		}
		
		public void sendToSchedule() {
			stopper = Model.root.schedule.scheduleRepeating(ModelTime.now().raw(),this,period.raw());
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
		public void schedule(ITriggerable listener) {
			scheduleEndTrigger();
			baseTrigger.schedule(listener);
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
	
	static public Repeating repeatingEvery(ModelTime time) 	{return(new Repeating(time));}
	static public Once 		timeIs(ModelTime time) 			{return(new Once(time));}
	static public OnceAfter after(ModelTime time) 			{return(new OnceAfter(time));}
	static public Repeating yearly() 						{return(repeatingEvery(ModelTime.year()));}
	static public Repeating monthly() 						{return(repeatingEvery(ModelTime.month()));}
	static public Repeating weekly() 						{return(repeatingEvery(ModelTime.week()));}
	static public Repeating daily() 						{return(repeatingEvery(ModelTime.day()));}
	
	static public ITrigger onDemand() {
		return(new ITrigger() {
			@Override
			public void schedule(ITriggerable contract) {
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
