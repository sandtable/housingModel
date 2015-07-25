package development;

import sim.engine.SimState;

public class Model extends SimState {

	public Model(long seed, IModelNode...agents) {
		super(seed);
		root = new ModelRoot(this, agents);
	}
	
	public void start() {
		root.start(null);
	}
		
	// --- addressable entities (null if not present)
//	public Bank				bank;
//	public Firm				firm;
//	public Government 		government;
//	public SetOfHouseholds	setOfHouseholds;
//	public HouseSaleMarket	saleMarket;
//	public RentalMarket		rentalMarket;
//	public Construction		construction;
//	public CentralBank		centralBank;

	static public ModelRoot root;
	private static final long serialVersionUID = 1714518191380607106L;
}
