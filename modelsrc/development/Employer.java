package development;

import contracts.Contract;
import contracts.DepositAccount;
import contracts.LabourContract;
import utilities.ModelTime;

public class Employer extends Contract.Issuer<LabourContract> implements LabourContract.IIssuer {
	public Employer() {
		super(LabourContract.class);
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
		return(parent().get(DepositAccount.Owner.class).defaultAccount());
	}
}
