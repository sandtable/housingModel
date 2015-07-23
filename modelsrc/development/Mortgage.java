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
	
	public static class Borrower extends Contract.Issuer<Mortgage> {
		public Borrower() {
			super(Mortgage.class);
		}
		
		public boolean isFirstTimeBuyer() {
			return false;
		}
		
		public long monthlyIncome() {
			return(0);
		}
		
		public long monthlyDisposableIncome() {
			return(0);
		}
		
		public long bankBalance() {
			return(0);
		}
	}
	
	public static class Lender extends Contract.Owner<Mortgage> {
		public Lender() {
			super(Mortgage.class);
		}
	}
	
	PaymentAgreement payoff;
}
