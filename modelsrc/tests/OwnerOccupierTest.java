package tests;

import sim.engine.SimState;
import utilities.ModelTime;
import contracts.DepositAccount;
import contracts.Mortgage;
import development.Bank;
import development.CentralBank;
import development.Construction;
import development.EconAgent;
import development.Employee;
import development.Firm;
import development.HouseSaleMarket;
import development.IModelNode;
import development.ITriggerable;
import development.ModelBase;
import development.OwnerOccupier;
import development.RentalMarket;
import development.Trigger;

@SuppressWarnings("serial")
public class OwnerOccupierTest extends ModelBase {

	public static class HouseholdStub extends EconAgent {
		public HouseholdStub() {
			super(
					new DepositAccount.Owner(),
					new OwnerOccupier(),
					new Mortgage.Borrower(),
					new Employee()
				);
		}
		
		@Override
		public void start(IModelNode parent) {
			parent.find(Bank.class).openAccount(this);
			super.start(parent);
			Trigger.after(ModelTime.day()).schedule(new ITriggerable() {
				public void trigger() {
					get(OwnerOccupier.class).bidOnHouseMarket();
				}
			});
		}
	}
	
	public OwnerOccupierTest(long seed) {
		super(seed,
				new CentralBank(),
				new Bank(),
				new HouseSaleMarket(),
				new RentalMarket(),
				new Construction(),
				new HouseholdStub(),
				new Firm()
		);
		household = root.get(HouseholdStub.class);
	}

	public void start() {
		super.start();
		
		root.get(Firm.class).employ(root.get(HouseholdStub.class));
		root.get(Construction.class).buildHouse();
		Trigger.after(ModelTime.year()).schedule(new ITriggerable() {
			public void trigger() {kill();}});
	}
	
	public void finish() {
	}
		
    public static void main(String[] args) {
    	SimState.doLoop(OwnerOccupierTest.class, args);
    }
    
    HouseholdStub 		household;
}
