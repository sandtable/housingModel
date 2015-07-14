package testing;

public class Firm extends EconAgent {
	public Firm() {
		depositAccounts = new DepositAccount.Owner();
		employerTrait = new Employer(this);
		addTrait(depositAccounts);
		addTrait(employerTrait);
	}
	
	public DepositAccount getPayrollAC() {
		return(depositAccounts.first());
	}
	
	public boolean employ(EconAgent newEmployee) {
		return(employerTrait.issue(newEmployee.getTrait(Employee.class)));
	}
	
	DepositAccount.Owner depositAccounts;
	Employer employerTrait;
}
