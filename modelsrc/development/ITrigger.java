package development;

import sim.engine.Stoppable;

/**
 * A Trigger represents a pattern of events to be added to the schedule
 * when the 'schedule()' function is called. When the scheduled event occurs,
 * the listener's trigger() function should be called.
 * 
 * @author daniel
 */
public interface ITrigger {
	public void schedule(ITriggerable listener);
	public void schedule(IMessage message, IMessage.IReceiver handler);
	public void stop();//in Stoppable interface
}
