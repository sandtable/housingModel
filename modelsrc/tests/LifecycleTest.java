package tests;

import java.util.Collection;
import java.util.HashSet;
import java.util.TreeSet;

import sim.engine.SimState;
import utilities.ModelTime;
import development.EconAgent;
import development.IMessage;
import development.IModelNode;
import development.ITriggerable;
import development.Lifecycle;
import development.Message;
import development.Model;
import development.NodeGroup;
import development.Trigger;

@SuppressWarnings("serial")
public class LifecycleTest extends Model implements ITriggerable {

	public static class HouseholdStub extends EconAgent {
		public HouseholdStub() {
			super(new Lifecycle());
		}
		
		public void start(IModelNode parent) {
			super.start(parent);
		}
		
		@Override
		public void die() {
			System.out.println("Death");
			super.die();
		}
		
		Lifecycle  lifecycle;
		Collection<HouseholdStub> households;
	}
	
	public LifecycleTest(long seed) {
		super(seed, new NodeGroup<HouseholdStub>());
	}

	public void start() {
		super.start();
		households = root.get(NodeGroup.class);
		new Lifecycle.BirthTrigger().schedule(this);
	}
	
	public void finish() {
	}
	
	@Override
	public void trigger() {
		households.add(new HouseholdStub());
		System.out.println("Birth!! "+households.size());		
	}
		
    public static void main(String[] args) {
    	SimState.doLoop(LifecycleTest.class, args);
    }

    NodeGroup<HouseholdStub> households;
}
