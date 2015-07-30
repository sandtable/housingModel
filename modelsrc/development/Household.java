package development;

import utilities.ModelTime;
import contracts.DepositAccount;
import contracts.Mortgage;



public class Household extends EconAgent {
	public Household() {
		super(	new DepositAccount.Owner(),
				new Lifecycle(),
				new Employee(),
				new Consumer(),
				new OwnerOccupier(),
				new Renter(),
	//			new BTLInvestor(),
				new Mortgage.Borrower()
				);
	}

	@Override
	public void start(IModelNode parent) {
		super.start(parent);
		parent.mustFind(Bank.class).openAccount(mustGet(DepositAccount.Owner.class));
		parent.mustFind(Firm.class).employ(mustGet(Employee.class));
		// trigger that we're homeless
		Trigger.after(ModelTime.now()).schedule(new Message.EndOfContract(null), get(Renter.class));
	}	
}
