package tests;

import sim.engine.SimState;
import utilities.ModelTime;
import development.EconAgent;
import development.ITriggerable;
import development.ModelBase;
import development.Trigger;

@SuppressWarnings("serial")
public class EmptyTest extends ModelBase {

	public static class HouseholdStub extends EconAgent {
		public HouseholdStub() {
		}
	}
	
	public EmptyTest(long seed) {
		super(seed);
	}

	public void start() {
		Trigger.after(ModelTime.year()).schedule(new ITriggerable() {public void trigger() {kill();}});
	}
	
	public void finish() {
	}
		
    public static void main(String[] args) {
    	SimState.doLoop(EmptyTest.class, args);
    }
}
