package tests;

import sim.engine.SimState;
import development.EconAgent;
import development.Model;

@SuppressWarnings("serial")
public class EmptyTest extends Model {

	public static class HouseholdStub extends EconAgent {
		public HouseholdStub() {
		}
	}
	
	public EmptyTest(long seed) {
		super(seed);
	}

	public void start() {
	}
	
	public void finish() {
	}
		
    public static void main(String[] args) {
    	SimState.doLoop(EmptyTest.class, args);
    }
}
