package development;

import sim.engine.SimState;
import utilities.ModelTime;

public class Model extends SimState {

	public Model(long seed) {
		super(seed);
		root = this;
	}
	
	public ModelTime timeNow() {
		double scheduleTime = schedule.getTime();
		if(scheduleTime < 0.0) scheduleTime = 0.0;
		return(new ModelTime(scheduleTime,ModelTime.Units.RAW));
	}
	
	// --- addressable entities (null if not present)
	public Bank				bank;
	public Firm				firm;
	public Government 		government;
	public SetOfHouseholds	setOfHouseholds;
	public HouseSaleMarket	saleMarket;
	public RentalMarket		rentalMarket;
	public Construction		construction;

	static public Model root;
	private static final long serialVersionUID = 1714518191380607106L;
}
