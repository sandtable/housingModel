package testing;

import utilities.ModelTime;

public class MortgageAgreement extends InvestmentAccountAgreement {
	public MortgageAgreement(DepositAccountAgreement borrowerAC, DepositAccountAgreement lenderAC, double annualInterest, final long monthlyPayment) {
		super(new DepositAccountAgreement(),annualInterest, ModelTime.month());
		payoff = new PaymentAgreement(borrowerAC, lenderAC) {
			public long amount() {
				return(monthlyPayment);
			}
		};
	}
	
	public void trigger() {
		super.trigger();
		try {
			payoff.honour();
			if(balance == 0) {
				interestPaymentTrigger.stop();
			}
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	PaymentAgreement payoff;
}
