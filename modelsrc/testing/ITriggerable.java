package testing;
/***
 * A class should implement this interface if it wants to receive
 * calls from the schedule. Temporal patterns of calls can be
 * composed from the static functions in the Trigger class,
 * or you can roll your own by implementing the ITrigger interface
 * 
 * @author daniel
 *
 */
public interface ITriggerable {
	public void trigger();
}
