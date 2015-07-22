package development;

public class Firm extends EconAgent {
	public Firm(Bank bank) {
		depositAccounts = new DepositAccount.Owner();
		employerTrait = new Employer(this);
		addTrait(depositAccounts);
		addTrait(employerTrait);
		bank.openAccount(depositAccounts);
	}
	
	public DepositAccount getPayrollAC() {
		return(depositAccounts.first());
	}

	public DepositAccount getSalesAC() {
		return(depositAccounts.first());
	}
	
	public boolean employ(EconAgent newEmployee) {
		return(employerTrait.issue(newEmployee.getTrait(Employee.class)));
	}
	
	DepositAccount.Owner depositAccounts;
	Employer employerTrait;
}
