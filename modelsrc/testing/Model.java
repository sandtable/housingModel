package testing;

import java.util.ArrayList;

import sim.engine.SimState;
import utilities.ModelTime;

public class Model extends SimState implements ITriggerable {

	public Model(long seed) {
		super(seed);
		root = this;
		init();
	}
	
	public void init() {
		bank = new Bank();
		firm = new Firm(bank);
		households = new ArrayList<>(2);
    	households.add(new Household(bank, firm.getPayrollAC()));
    	households.add(new Household(bank, firm.getPayrollAC()));
	}

	public void start() {
    	Household household1 = households.get(0);
    	Household household2 = households.get(1);

    	test();
    	
    	firm.employ(household1);
    	firm.employ(household2);

    	Trigger.monthly().schedule(this);
	}
	
	public void test() {
    	Household household1 = households.get(0);
    	Household household2 = households.get(1);

    	bank.endowmentAccount.transfer(household1.bankAccount(), 100);
    	bank.endowmentAccount.transfer(household2.bankAccount(), 55);
    	
    	System.out.println("Household 1 = "+household1.bankAccount().balance);
    	System.out.println("Household 2 = "+household2.bankAccount().balance);
    	System.out.println("Total Endowment = "+bank.endowmentAccount.balance);

	}

	@Override
	public void trigger() {
		System.out.println("Time = "+timeNow().raw());
    	System.out.println("Household 1 = "+households.get(0).bankAccount().balance);
    	System.out.println("Household 2 = "+households.get(1).bankAccount().balance);
	}

	
	public ModelTime timeNow() {
		double scheduleTime = schedule.getTime();
		if(scheduleTime < 0.0) scheduleTime = 0.0;
		return(new ModelTime(scheduleTime,ModelTime.Units.RAW));
	}
	
	Bank					bank;
	Firm					firm;
	ArrayList<Household> 	households;


	static public Model root;
	private static final long serialVersionUID = 1714518191380607106L;
}
