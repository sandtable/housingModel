package testing;

import utilities.ModelTime;

public class Employee extends Contract.Owner<LabourContract> implements LabourContract.IOwner {
	public Employee(Lifecycle iLifecycle, Household iHousehold) {
		super(LabourContract.class);
		lifecycle = iLifecycle;
		household = iHousehold;
	}

	@Override
	public ModelTime age() {
		return(lifecycle.age());
	}

	@Override
	public DepositAccount account() {
		return(household.bankAccount());
	}
	Lifecycle lifecycle;
	Household household;
}
