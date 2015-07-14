package testing;

public class Household extends EconAgent {
	public Household() {
			super(new DepositAccount.Owner()
				  );
			Lifecycle life = new Lifecycle();
			addTrait(life);
			addTrait(new Employee(life, this));
//			new Renter(),
//			new OwnerOccupier(),
//			new BuyToLetInvestor()
	}
	
	DepositAccount bankAccount() {
		return(getTrait(DepositAccount.Owner.class).first());
	}
}
