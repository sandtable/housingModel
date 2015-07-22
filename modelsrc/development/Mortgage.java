package development;

import utilities.ModelTime;

public class Mortgage extends InvestmentAccount {
	public Mortgage(DepositAccount borrowerAC, DepositAccount lenderAC, double annualInterest, final long monthlyPayment) {
		super(new DepositAccount(),annualInterest, ModelTime.month());
		payoff = new PaymentAgreement(borrowerAC, lenderAC) {
			public long amount() {
				return(monthlyPayment);
			}
		};
	}
	
	public void trigger() {
		super.trigger();
		payoff.trigger();
		if(balance == 0) {
			interestPaymentTrigger.stop();
		}
	}
	
	PaymentAgreement payoff;
}
