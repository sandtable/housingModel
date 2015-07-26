package development;

import contracts.DepositAccount;

public class Firm extends EconAgent {
	public Firm() {
		super(	new DepositAccount.Owner(),
				new Employer()
		);
	}
	
	@Override
	public void start(IModelNode parent) {
		parent.mustGet(Bank.class).openAccount(get(DepositAccount.Owner.class));
		super.start(parent);
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

	public boolean employ(IModelNode newEmployee) {
		Employee e = newEmployee.get(Employee.class);
		if(e == null) return(false);
		return(get(Employer.class).issue(e));
	}

}
