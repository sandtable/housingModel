package development;

import sim.engine.SimState;
import tests.OwnerOccupierRenterTest;
import tests.OwnerOccupierRenterTest.HouseholdStub;
import utilities.ModelTime;

public class Model extends ModelBase {
	public Model(long seed) {
		super(seed,
				new CentralBank(),
				new Bank(),
				new HouseSaleMarket(),
				new RentalMarket(),
				new Construction(),
				new Firm()
		);
		households = new NodeGroup<Household>();
		this.root.addChild(households);
	}

	public void start() {
		super.start();
		
		root.get(Construction.class).buildHouse();
		households.add(new Household());
		households.add(new Household());
		
		Trigger.after(ModelTime.year()).schedule(new ITriggerable() {
			public void trigger() {kill();}});
	}
	
	public void finish() {
	}
		
    public static void main(String[] args) {
    	SimState.doLoop(Model.class, args);
    }
    
    NodeGroup<Household> households;
}
