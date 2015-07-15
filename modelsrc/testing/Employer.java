package testing;

import utilities.ModelTime;

public class Employer extends Contract.Issuer<LabourContract> implements LabourContract.IIssuer {
	public Employer(Firm iParent) {
		super(LabourContract.class);
		parent = iParent;
	}

	public boolean issue(Employee newEmployee) {
		return(issue(new LabourContract(this, newEmployee), newEmployee));
	}
	
	@Override
	public long monthlyWage(ModelTime age, double percentile) {
		return(150000);
	}

	@Override
	public DepositAccount account() {
		return(parent.getPayrollAC());
	}
	
	Firm parent;
}
