package development;

import sim.engine.SimState;
import tests.OwnerOccupierRenterTest;
import tests.OwnerOccupierRenterTest.HouseholdStub;
import utilities.ModelTime;

public class Model extends ModelBase {
	private static final long serialVersionUID = -6104859347518541540L;
	Households households;

    public Model(long seed) {
		super(seed,
				new CentralBank(),
				new Bank(),
				new HouseSaleMarket(),
				new RentalMarket(),
				new Construction(),
				new Firm(),
				new Households(),
				new HouseholdStats()
		);
		households = root.get(Households.class);
	}

	public void start() {
		super.start();
		
//		root.get(Construction.class).buildHouse();
//		root.get(Construction.class).buildHouse();
//		households.add(new Household());
//		households.add(new Household());
	
		new Lifecycle.BirthTrigger().schedule(new ITriggerable() {
			public void trigger() {
				households.add(new Household());
				System.out.println("Birth!! "+households.size());
			}
		});
		
		Trigger.after(ModelTime.year()).schedule(new ITriggerable() {
			public void trigger() {
				System.out.println("END: Killing");
				kill();
			}
		});
		
		Trigger.monthly().schedule(new ITriggerable() {
			public void trigger() {
				System.out.println("Month is: "+Model.this.root.timeNow().inMonths());
			}
		});
	}
	
	public void finish() {
	}
		
    public static void main(String[] args) {
    	SimState.doLoop(Model.class, args);
    }
}
