package testing;

import utilities.LongSupplier;

public class PeriodicPayment extends Contract<PeriodicPayment, PeriodicPayment.Issuer> {
	
	public PeriodicPayment(Issuer iIssuer, Contract.IOwner<PeriodicPayment> iOwner, DepositAccount iFrom, DepositAccount iTo, ITrigger when, LongSupplier iAmount) {
		super(iIssuer, iOwner, when);
		fromAC = iFrom;
		toAC = iTo;
		amount = iAmount;
	}
	
	private void exercise() {
		fromAC.issuer.transfer(contract.fromAC, contract.amount.getAsLong(), contract.toAC));
	}
	
	static public class Issuer extends Contract.Issuer<PeriodicPayment> {
		public boolean issue(Contract.IOwner<PeriodicPayment> owner, DepositAccount iFrom, DepositAccount iTo, ITrigger when, LongSupplier iAmount) {
			return(issue(new PeriodicPayment(this,owner,iFrom, iTo, when, iAmount)));
		}

		public boolean issue(Contract.IOwner<PeriodicPayment> iOwner, DepositAccount iFrom, DepositAccount iTo, ITrigger when, final long iAmount) {
			return(issue(iOwner, iFrom, iTo, when, new LongSupplier() {
				public long getAsLong() {return(iAmount);}
			}));
		}

		public boolean honour(PeriodicPayment contract) {
			return(contract.fromAC.issuer.transfer(contract.fromAC, contract.amount.getAsLong(), contract.toAC));
		}
	}
		
	LongSupplier amount;
	DepositAccount fromAC;
	DepositAccount toAC;
}
