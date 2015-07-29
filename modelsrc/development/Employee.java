package development;

import contracts.Contract;
import contracts.DepositAccount;
import contracts.LabourContract;
import utilities.ModelTime;

public class Employee extends Contract.Owner<LabourContract> implements LabourContract.IOwner {
	public double incomePercentile; // proxy for innate ability to earn
	
	public Employee() {
		super(LabourContract.class);
	}
	
	@Override
	public void start(IModelNode parent) {
		incomePercentile = parent.mustFind(ModelRoot.class).random.nextDouble();
		super.start(parent);
	}

	@Override
	public ModelTime age() {
		Lifecycle lifecycle = parent().get(Lifecycle.class);
		if(lifecycle != null) {
			return(lifecycle.age());
		} else {
			return(ModelTime.years(25.0));
		}
	}

	@Override
	public DepositAccount account() {
		return(parent().get(DepositAccount.Owner.class).defaultAccount());
	}
	
	public double getIncomePercentile() {
		return incomePercentile;
	}
	
	public long monthlyIncome() {
		long income = 0;
		for(LabourContract incomeStream : this) {
			income += incomeStream.payment.amount();
		}
		return(income);
	}
	
}
