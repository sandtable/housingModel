package testing;

import utilities.LongSupplier;

public class PeriodicPayment extends Contract<PeriodicPayment, PeriodicPayment.Issuer, PeriodicPayment.Owner> {
	
	public PeriodicPayment(Issuer iIssuer, Owner iOwner, DepositAccount iFrom, DepositAccount iTo, ITrigger when, LongSupplier iAmount) {
		super(iIssuer, iOwner, when);
		from = iFrom;
		to = iTo;
		amount = iAmount;
	}

	@Override
	public void trigger() {
		owner.trigger(this);
	}
	
	static public class Issuer extends Contract.Issuer<PeriodicPayment> {
		public boolean issue(Owner owner, DepositAccount iFrom, DepositAccount iTo, ITrigger when, LongSupplier iAmount) {
			return(issue(new PeriodicPayment(this,owner,iFrom, iTo, when, iAmount)));
		}

		public boolean issue(Owner iOwner, DepositAccount iFrom, DepositAccount iTo, ITrigger when, final long iAmount) {
			return(issue(iOwner, iFrom, iTo, when, new LongSupplier() {
				public long getAsLong() {return(iAmount);}
			}));
		}

		public boolean honour(PeriodicPayment contract) {
			return(contract.from.issuer.transfer(contract.from, contract.amount.getAsLong(), contract.to));
		}
	}
	
	static public class Owner extends Contract.Owner<PeriodicPayment> {}
	
	LongSupplier amount;
	DepositAccount from;
	DepositAccount to;
}
