package testing;

public class Household extends EconAgent {
	public Household(Bank bank, DepositAccount consumptionAC) {
			DepositAccount.Owner depositAccounts = new DepositAccount.Owner();
			addTrait(depositAccounts);
			bank.openAccount(depositAccounts);
			Lifecycle life = new Lifecycle();
			Employee employee = new Employee(life, bankAccount());
			Consumer consumer = new Consumer(bankAccount(), consumptionAC, employee);
			addTrait(life);
			addTrait(employee);
			addTrait(consumer);
//			new Renter(),
//			new OwnerOccupier(),
//			new BuyToLetInvestor()
			
	}
	
	DepositAccount bankAccount() {
		return(getTrait(DepositAccount.Owner.class).first());
	}
}
