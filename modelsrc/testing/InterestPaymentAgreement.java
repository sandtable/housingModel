package testing;

import housing.Model;
import utilities.LongSupplier;
import utilities.ModelTime;

public class InterestPaymentAgreement extends PaymentAgreement {
	public InterestPaymentAgreement(DepositAccountAgreement sourceAC, DepositAccountAgreement payeeAC, final DepositAccountAgreement interestBearingAC, double iAnnualInterest) {
		this(sourceAC,payeeAC, new LongSupplier() {
			@Override
			public long getAsLong() {
				return(interestBearingAC.balance);
			}
			
		}, iAnnualInterest);
	}
	
	public InterestPaymentAgreement(DepositAccountAgreement sourceAC, DepositAccountAgreement payeeAC, LongSupplier iBalance, double iAnnualInterest) {
		super(sourceAC,payeeAC);
		annualInterestRate = iAnnualInterest;
		unpaidInterest = 0.0;
		balance = iBalance;
		unpaidInterestTimestamp = Model.modelTime();
	}
	
	@Override
	public long amount() {
		return((long)unpaidInterest);
	}
	
	public void honour() throws Throwable {
		updateUnpaidInterest();
		super.honour();
		unpaidInterest -= amount();
	}
	
	public void updateUnpaidInterest() {
		ModelTime newTimestamp = Model.modelTime();
		unpaidInterest += balance.getAsLong()*annualInterestRate*(newTimestamp.inYears() - unpaidInterestTimestamp.inYears());
		unpaidInterestTimestamp = newTimestamp;
	}

	LongSupplier  balance;
//	PeriodicPayment interestPayment;
	double annualInterestRate;
	double unpaidInterest;
	ModelTime unpaidInterestTimestamp;

}
