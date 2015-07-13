package testing;

public class Household extends EconAgent {
	public Household() {
		super(
			new Renter(),
			new OwnerOccupier(),
			new BuyToLetInvestor()
				);
	}
	
	public boolean receive(DepositAccount a) {
		return(depositAccountHolderTrait.receive(a));
	}
	
	Contract.Owner<DepositAccount> depositAccountHolderTrait;
}
