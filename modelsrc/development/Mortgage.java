package development;

import utilities.ModelTime;

public class Mortgage extends InvestmentAccount {
	
	static final int N_PAYMENTS = 12*25;	// number of payments
		
	/***
	 * @param borrowerAC		account from which to take monthly payments
	 * @param lenderAC			account to which interest payments will be made
	 * @param annualInterest
	 * @param principal
	 * @param downpayment
	 */
	public Mortgage(Mortgage.IIssuer issuer, DepositAccount borrowerAC, DepositAccount lenderAC, double annualInterest, long principal, long downpayment) {
		super(issuer, lenderAC, annualInterest, ModelTime.month());
		this.principal = principal;
		this.downpayment = downpayment;
		this.borrowerAC = borrowerAC;
		long monthlyPayment = Math.round(principal*monthlyPaymentFactor(annualInterest));
		payoff = new FixedPaymentAgreement(borrowerAC, this, monthlyPayment);
	}
	
	public void trigger() {
		super.trigger(); // add interest
		if(this.balance >= payoff.amount() - N_PAYMENTS) {
			payoff.payment = this.balance; // deal with roundoff error
		}
		payoff.trigger();
		nPayments--;
		if(nPayments == 0) {
			interestPaymentTrigger.stop();
		}
	}
	
	public void activate() {
		this.transfer(borrowerAC, principal);
		super.activate();
	}
	
	public long getMonthlyPayment() {
		return(payoff.amount());
	}
		
	/*******************************
	 * Get the monthly payment on a mortgage as a fraction of the mortgage principle.
	 * @return The monthly payment fraction.
	 *******************************/
	static public double monthlyPaymentFactor(double annualInterestRate) {
		double r = annualInterestRate/12.0;
		return(r/(1.0 - Math.pow(1.0+r, -N_PAYMENTS)));
	}

	///////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////
	public static class Borrower extends DepositAccount.Owner {
		public Borrower(DepositAccount bankAccount) {
			super(Mortgage.class);
			this.bankAccount = bankAccount;
		}
		
		public boolean isFirstTimeBuyer() {
			return false;
		}
		
		public long monthlyIncome() {
			return(150000);
		}
		
		public long monthlyDisposableIncome() {
			return(100000);
		}
		
		public long bankBalance() {
			return(2000000);
		}
		
		public DepositAccount getBankAccount() {
			return bankAccount;
		}
		
		DepositAccount bankAccount;
	}
	
	//////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////
	public static class Lender extends DepositAccount.Issuer implements ITriggerable {
		public double MAX_OO_LTV = 1.0;		// maximum LTV bank will give to owner-occupier when not regulated	
		public double MAX_BTL_LTV = 0.6;	// maximum LTV bank will give to BTL when not regulated
		public double MAX_OO_LTI = 6.5;		// maximum LTI bank will give to owner-occupier when not regulated	
		public double MAX_BTL_LTI = 10.0;	// maximum LTI bank will give to BTL when not regulated

		public Lender(IBehaviour behaviour, DepositAccount interestAC) {
			this.behaviour = behaviour;
			this.interestAC = interestAC;
			nLoans = 0;
			nOverLTICapLoans = 0;
			nOverLTVCapLoans = 0;
			supplyVal = 0;
			Trigger.monthly().schedule(this);
		}
		
		/***
		 *  Resets all the various monthly diagnostic measures ready for the next month
		 */
		public void trigger() {
			behaviour.recalcInterestRate(supplyVal);
			System.out.println("nLoans = "+nLoans);
			nLoans = 0;
			nOverLTICapLoans = 0;
			nOverLTVCapLoans = 0;
			supplyVal = 0;
		}

		public void issue(Mortgage mortgage, IMessage.IReceiver recipient) {
			if(recipient.receive(mortgage)) {
				mortgage.activate();
				supplyVal += mortgage.principal;
				++nLoans;
				if(!mortgage.isBuyToLet) {
					if(mortgage.LTI > Model.root.centralBank.loanToIncomeRegulation(mortgage)) {
						++nOverLTICapLoans;
					}
					if(mortgage.principal/(mortgage.principal + mortgage.downpayment) > Model.root.centralBank.loanToValueRegulation(mortgage)) {
						++nOverLTVCapLoans;
					}
				}
			}
			super.issue(mortgage, recipient);
		}
		
		/********
		 * Use this to request a mortgage approval but not actually sign a mortgage contract.
		 * This is useful if you want to inspect the details of the mortgage contract before
		 * deciding whether to actually go ahead and sign.
		 * 
		 * @param h 			The household that is requesting the approval.
		 * @param housePrice 	The price of the house that 'h' wants to buy
		 * @param isHome 		does 'h' plan to live in the house?
		 * @return A MortgageApproval object, or NULL if the mortgage is declined
		 */
		public Mortgage requestApproval(Mortgage.Borrower h, long housePrice, long desiredDownPayment, boolean isHome) {
			long ltv_principal, lti_principal;
			long principal, downPayment;

			// --- calculate maximum allowable principal
			principal = Math.round(Math.max(0.0,h.monthlyDisposableIncome())/monthlyPaymentFactor());

			ltv_principal = Math.round(housePrice*loanToValue(h, isHome));
			principal = Math.min(principal, ltv_principal);

			lti_principal = Math.round(h.monthlyIncome()*12.0 * loanToIncome(h,isHome));
			principal = Math.min(principal, lti_principal);

			downPayment = housePrice - principal;
			
			if(h.bankBalance() < downPayment) {
				System.out.println("Failed down-payment constraint: bank balance = "+h.bankBalance()+" Downpayment = "+downPayment);
				System.out.println("isHome = "+isHome+" isFirstTimeBuyer = "+h.isFirstTimeBuyer());
				downPayment = h.bankBalance();
//				return(null);
			}
			// --- allow larger downpayments
			if(desiredDownPayment < 0) desiredDownPayment = 0;
			if(desiredDownPayment > h.bankBalance()) desiredDownPayment = h.bankBalance();
			if(desiredDownPayment > housePrice) desiredDownPayment = housePrice;
			if(desiredDownPayment > downPayment) {
				downPayment = desiredDownPayment;
				principal = housePrice - desiredDownPayment;
			}
			
			Mortgage approval = new Mortgage(this, h.getBankAccount(), interestAC, behaviour.getMortgageInterestRate(), principal, downPayment);
			approval.isBuyToLet = !isHome;
			approval.isFirstTimeBuyer = h.isFirstTimeBuyer();
			return(approval);
		}
		
		/*******************************
		 * Get the monthly payment on a mortgage as a fraction of the mortgage principle.
		 * @return The monthly payment fraction.
		 *******************************/
		public double monthlyPaymentFactor() {
			double r = behaviour.getMortgageInterestRate()/12.0;
			return(r/(1.0 - Math.pow(1.0+r, -N_PAYMENTS)));
		}
		
		/**********************************************
		 * Get the Loan-To-Value ratio applicable to a given household.
		 * 
		 * @param h The houshold that is applying for the mortgage
		 * @param isHome true if 'h' plans to live in the house
		 * @return The loan-to-value ratio applicable to the given household.
		 *********************************************/
		public double loanToValue(Mortgage.Borrower h, boolean isHome) {
			double limit;
			if(isHome) {
				limit = MAX_OO_LTV;
			} else {
				limit = MAX_BTL_LTV;
			}
			if((nOverLTVCapLoans+1.0)/(nLoans + 1.0) > Model.root.centralBank.proportionOverLTVLimit) {
				limit = Math.min(limit, Model.root.centralBank.loanToValueRegulation(isHome,h.isFirstTimeBuyer()));
			}
			return(limit);
		}

		public double loanToIncome(Mortgage.Borrower h, boolean isHome) {
			double limit;
			if(isHome) {
				limit = MAX_OO_LTI;
			} else {
				limit = MAX_BTL_LTI;
			}
			if((nOverLTICapLoans+1.0)/(nLoans + 1.0) > Model.root.centralBank.proportionOverLTILimit) {
				limit = Math.min(limit, Model.root.centralBank.loanToIncomeRegulation(isHome,h.isFirstTimeBuyer()));
			}
			return(limit);
		}
		
		/*****************************************
		 * Find the maximum mortgage that this mortgage-lender will approve
		 * to a household.
		 * 
		 * @param h The household who is applying for the mortgage
		 * @param isHome true if 'h' plans to live in the house
		 * @return The maximum value of house that this mortgage-lender is willing
		 * to approve a mortgage for.
		 ****************************************/
		public double getMaxMortgage(Mortgage.Borrower h, boolean isHome) {
			double ltv_max; // loan to value constraint
			double pdi_max; // disposable income constraint
			double lti_max; // loan to income constraint

			pdi_max = h.bankBalance() + Math.max(0.0,h.monthlyDisposableIncome())/monthlyPaymentFactor();
			
			ltv_max = h.bankBalance()/(1.0 - loanToValue(h, isHome));
			pdi_max = Math.min(pdi_max, ltv_max);

			lti_max = h.monthlyIncome()*12.0* loanToIncome(h,isHome)/loanToValue(h,isHome);
			pdi_max = Math.min(pdi_max, lti_max);
			
			pdi_max = Math.floor(pdi_max*100.0)/100.0; // round down to nearest penny
			return(pdi_max);
		}

		
		public int			nOverLTICapLoans; 	// number of (non-BTL) loans above LTI cap this month
		public int			nOverLTVCapLoans;	// number of (non-BTL) loans above LTV cap this month
		public int			nLoans; 			// total number of non-BTL loans this month
		public long			supplyVal;			// monthly supply of mortgage loans (pence)
		IBehaviour			behaviour;
		DepositAccount		interestAC;
		
		public interface IBehaviour {
			double getMortgageInterestRate();
			void recalcInterestRate(double thisMonthsSupply);
		}
	}
	
	public int 				nPayments = N_PAYMENTS;
	public long				principal;
	public long				downpayment;
	// --- statistics information
	public boolean 			isBuyToLet;
	public boolean			isFirstTimeBuyer;
	public double			LTI;
	public DepositAccount	borrowerAC;

	FixedPaymentAgreement 	payoff;
}
