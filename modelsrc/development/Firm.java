package development;

import contracts.DepositAccount;

public class Firm extends EconAgent {
	public Firm() {
		super(	new DepositAccount.Owner(),
				new Employer()
		);
	}
	
	public void start(EconAgent parent) {
		parent.get(Bank.class).openAccount(get(DepositAccount.Owner.class));		
	}

	public DepositAccount getPayrollAC() {
		return(get(DepositAccount.Owner.class).defaultAccount());
	}

	public DepositAccount getSalesAC() {
		return(get(DepositAccount.Owner.class).defaultAccount());
	}
	
	public boolean employ(Employee newEmployee) {
		return(get(Employer.class).issue(newEmployee));
	}
}
