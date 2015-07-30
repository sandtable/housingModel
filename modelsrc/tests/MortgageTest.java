package tests;

import contracts.DepositAccount;
import contracts.Mortgage;
import contracts.DepositAccount.Owner;
import sim.engine.SimState;
import utilities.ModelTime;
import development.Bank;
import development.CentralBank;
import development.EconAgent;
import development.IModelNode;
import development.ITriggerable;
import development.ModelBase;
import development.Trigger;

@SuppressWarnings("serial")
public class MortgageTest extends ModelBase {

	public static class HouseholdStub extends EconAgent implements ITriggerable {
		public HouseholdStub() {
			super(
					new DepositAccount.Owner(),
					new Mortgage.Borrower()
					);
			borrower = get(Mortgage.Borrower.class);
			depositAccountOwner = get(DepositAccount.Owner.class);
		}

		@Override
		public void start(IModelNode parent) {
			parent.find(Bank.class).openAccount(this);
			Trigger.monthly().schedule(this);
			super.start(parent);
		}
		
		public void trigger() {
			System.out.println("Bank account ="+depositAccountOwner.first().balance);
		}
		
		Mortgage.Borrower borrower;
		DepositAccount.Owner depositAccountOwner;
	}
	
	public MortgageTest(long seed) {
		super(seed, 
				new CentralBank(),
				new Bank(),
				new HouseholdStub());

	}

	public void start() {
		super.start();
		Bank bank = root.get(Bank.class);
		HouseholdStub household = root.get(HouseholdStub.class);
		Mortgage m = bank.get(Mortgage.Lender.class).requestApproval(household.borrower, 10000000, 1000000, true);
		System.out.println("Got Mortgage = "+m.principal);
		bank.get(Mortgage.Lender.class).issue(m, household.borrower);
		Trigger.after(ModelTime.year()).schedule(new ITriggerable() {
			public void trigger() {kill();}});
	}
	
	public void finish() {
	}
		
    public static void main(String[] args) {
    	SimState.doLoop(MortgageTest.class, args);
    }
}
