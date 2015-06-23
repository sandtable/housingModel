package testing;

import housing.Model;
import utilities.LongSupplier;
import utilities.ModelTime;

public class DepositAccountWithInterest extends DepositAccount {
	public DepositAccountWithInterest(Issuer iIssuer, Owner iOwner, PeriodicPayment.Issuer interestIssuer, PeriodicPayment.Owner interestOwner, DepositAccount iFrom, ITrigger when, final double annualInterest) {
		super(iIssuer, iOwner);
		annualInterestRate = annualInterest;
		unpaidInterest = 0.0;
		interestPayment = new PeriodicPayment(interestIssuer, interestOwner, iFrom, this, when, new LongSupplier() {
			public long getAsLong() {
				updateUnpaidInterest();
				long payment = (long)unpaidInterest;
				unpaidInterest = 0.0;
				return(payment);
			}
		});
	}
	
	private void updateUnpaidInterest() {
		ModelTime newTimestamp = Model.modelTime();
		unpaidInterest += balance*annualInterestRate*(newTimestamp.inYears() - unpaidInterestTimestamp.inYears());
		unpaidInterestTimestamp = newTimestamp;		
	}

	////////////////////////////////////////////////////////////////////////////
	static public class Issuer extends DepositAccount.Issuer {
		public boolean issue(Owner holder, double interest) {
			return(false);
		}
		
		@Override
		public boolean transfer(DepositAccount account, long amount, DepositAccount payee) {
			DepositAccountWithInterest.class.cast(account).updateUnpaidInterest();
			return(super.transfer(account,amount,payee));
		}
	}
	
	PeriodicPayment interestPayment;
	double annualInterestRate;
	double unpaidInterest;
	ModelTime unpaidInterestTimestamp;
}
