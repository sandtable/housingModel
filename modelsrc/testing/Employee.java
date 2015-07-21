package testing;

import utilities.ModelTime;

public class Employee extends Contract.Owner<LabourContract> implements LabourContract.IOwner {
	public Employee(Lifecycle iLifecycle, DepositAccount iBankAccount) {
		super(LabourContract.class);
		lifecycle = iLifecycle;
		bankAccount = iBankAccount;
	}

	@Override
	public ModelTime age() {
		if(lifecycle != null) {
			return(lifecycle.age());
		} else {
			return(ModelTime.years(25.0));
		}
	}

	@Override
	public DepositAccount account() {
		return(bankAccount);
	}
	
	public long monthlyIncome() {
		long income = 0;
		for(LabourContract incomeStream : this) {
			income += incomeStream.payment.amount();
		}
		return(income);
	}
	
	Lifecycle lifecycle;
	DepositAccount bankAccount;
}
