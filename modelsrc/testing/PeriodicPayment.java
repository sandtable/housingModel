package testing;

public class PeriodicPayment extends Contract<PeriodicPayment, PeriodicPayment.Owner, PeriodicPayment.Issuer> {
	
	public PeriodicPayment(double amount, DepositAccount from, DepositAccount to, ITrigger when) {
		trigger = when;
	}
	
	@Override
	public void trigger() {
		owner.trigger(this);
	}
	
	static public class Issuer extends Contract.Issuer<PeriodicPayment> {
		public boolean honour(PeriodicPayment contract) {
			return(contract.from.issuer.transfer(contract.from, contract.amount, contract.to));
		}
	}
	
	static public class Owner extends Contract.Owner<PeriodicPayment> {}
	
	double amount;
	DepositAccount from;
	DepositAccount to;
}
