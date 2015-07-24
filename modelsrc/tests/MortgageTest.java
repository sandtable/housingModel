package tests;

import sim.engine.SimState;
import utilities.ModelTime;
import development.Bank;
import development.CentralBank;
import development.DepositAccount;
import development.EconAgent;
import development.ITriggerable;
import development.Model;
import development.Mortgage;
import development.Trigger;
import development.DepositAccount.Owner;

@SuppressWarnings("serial")
public class MortgageTest extends Model implements ITriggerable {

	public static class HouseholdStub extends EconAgent implements ITriggerable {
		public HouseholdStub(Bank bank) {
			depositAccountOwner = new DepositAccount.Owner();
			bank.openAccount(depositAccountOwner);
			borrower = new Mortgage.Borrower(depositAccountOwner.first());
			Trigger.monthly().schedule(this);
		}
		
		public void trigger() {
			System.out.println("Bank account ="+depositAccountOwner.first().balance);
		}
		
		Mortgage.Borrower borrower;
		DepositAccount.Owner depositAccountOwner;
	}
	
	public MortgageTest(long seed) {
		super(seed);
		centralBank = new CentralBank();
		bank = new Bank();
		household = new HouseholdStub(bank);
		Trigger.after(ModelTime.year()).schedule(this);
	}

	public void start() {
		Mortgage m = bank.mortgageLender.requestApproval(household.borrower, 10000000, 1000000, true);
		System.out.println("Got Mortgage = "+m.principal);
		bank.mortgageLender.issue(m, household.borrower);
	}
	
	public void trigger() {
		this.kill();
	}
	
	public void finish() {
	}
		
    public static void main(String[] args) {
    	SimState.doLoop(MortgageTest.class, args);
    }
    
    HouseholdStub household;
}
