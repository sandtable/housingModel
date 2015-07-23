package tests;

import java.util.Collection;
import java.util.HashSet;
import java.util.TreeSet;

import sim.engine.SimState;
import utilities.ModelTime;
import development.EconAgent;
import development.IMessage;
import development.ITriggerable;
import development.Lifecycle;
import development.Message;
import development.Model;
import development.Trigger;

@SuppressWarnings("serial")
public class LifecycleTest extends Model implements ITriggerable {

	public static class HouseholdStub extends EconAgent {
		public HouseholdStub(Collection<HouseholdStub> set) {
			lifecycle = new Lifecycle(this);
			parent = set;
		}
		
		@Override
		public boolean receive(IMessage message) {
			if(message == Message.die) {
				System.out.println("Death");
				parent.remove(this);
				return(true);
			}
			return(super.receive(message));
		}
		
		Lifecycle  lifecycle;
		Collection<HouseholdStub> parent;
	}
	
	public LifecycleTest(long seed) {
		super(seed);
		households = new HashSet<>();
	}

	public void start() {
		new Lifecycle.BirthTrigger().schedule(this);
	}
	
	public void finish() {
	}
	
	@Override
	public void trigger() {
		households.add(new HouseholdStub(households));
		System.out.println("Birth!! "+households.size());		
	}
		
    public static void main(String[] args) {
    	SimState.doLoop(LifecycleTest.class, args);
    }
    
    
    HashSet<HouseholdStub> households;
}
