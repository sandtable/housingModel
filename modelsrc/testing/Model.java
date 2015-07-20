package testing;

import sim.engine.SimState;
import utilities.ModelTime;

public class Model extends SimState implements ITriggerable {

	public Model(long seed) {
		super(seed);
		root = this;
		bank = new Bank();
		firm = new Firm(bank);
		government = new Government(bank, firm.getSalesAC());
		households = new SetOfHouseholds();
    	households.add(newHousehold());
    	households.add(newHousehold());
    	for(Household h : households) {
    		firm.employ(h);
    	}
	}

	public void start() {
    	test();
    	Trigger.monthly().schedule(this);
	}
	
	public void test() {
    	Household household1 = households.first();
    	Household household2 = households.last();

    	bank.endowmentAccount.transfer(household1.bankAccount(), 100);
    	bank.endowmentAccount.transfer(household2.bankAccount(), 55);
    	
    	System.out.println("Household 1 = "+household1.bankAccount().balance);
    	System.out.println("Household 2 = "+household2.bankAccount().balance);
    	System.out.println("Total Endowment = "+bank.endowmentAccount.balance);

	}

	@Override
	public void trigger() {
		System.out.println("Time = "+timeNow().raw());
    	System.out.println("Household 1 = "+households.first().bankAccount().balance);
    	System.out.println("Household 2 = "+households.last().bankAccount().balance);
	}

	
	public ModelTime timeNow() {
		double scheduleTime = schedule.getTime();
		if(scheduleTime < 0.0) scheduleTime = 0.0;
		return(new ModelTime(scheduleTime,ModelTime.Units.RAW));
	}
	
	public Household newHousehold() {
		return(new Household(bank, firm.getSalesAC()));
	}
	
	Bank			bank;
	Firm			firm;
	Government		government;	
	SetOfHouseholds	households;


	static public Model root;
	private static final long serialVersionUID = 1714518191380607106L;
}
