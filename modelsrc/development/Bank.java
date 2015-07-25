package development;

import contracts.DepositAccount;
import contracts.Mortgage;

public class Bank extends EconAgent implements Mortgage.Lender.IBehaviour {
//	public int    N_PAYMENTS = 12*25; // number of monthly repayments
	public double INITIAL_BASE_RATE = 0.5; // Bank base-rate

	public Bank() {
		super(	new DepositAccount.Issuer(),
				new DepositAccount.Owner(),
				new Mortgage.Lender()
				);
		depositAccounts = get(DepositAccount.Issuer.class);
		internalAccounts = get(DepositAccount.Owner.class);

		endowmentAccount = new DepositAccount(depositAccounts);
		depositAccounts.issue(endowmentAccount, internalAccounts);
		profitAccount = new DepositAccount(depositAccounts);
		depositAccounts.issue(profitAccount, internalAccounts);
		init();
	}

	public boolean openAccount(DepositAccount.Owner accountHolder) {
		return(depositAccounts.issue(accountHolder));
	}
	
	public boolean openAccount(EconAgent accountHolder) {
		return(depositAccounts.issue(accountHolder.get(DepositAccount.Owner.class)));
	}
	
		
	public void init() {
		baseRate = INITIAL_BASE_RATE;
		dDemand_dInterest = 10*1e10;
		setMortgageInterestRate(0.03);
//		mortgageLender.trigger();
	}
	
	/***
	 * Calculates the next months mortgage interest based on this months
	 * rate and the resulting demand.
	 * 
	 * Assumes a linear relationship between interest rate and demand,
	 * and aims to halve the difference between current demand
	 * and target supply
	 */
	public void recalcInterestRate(double thisMonthsSupply) {
//		supplyTarget = 100000 * Model.root.setOfHouseholds.size();
		supplyTarget = 100000;
		setMortgageInterestRate(getMortgageInterestRate() + 0.5*(thisMonthsSupply - supplyTarget)/dDemand_dInterest);
	}
	
	/******************************
	 * Get the interest rate on mortgages.
	 * @return The interest rate on mortgages.
	 *****************************/
	public double getMortgageInterestRate() {
		return(baseRate + interestSpread);
	}	

	/******************************
	 * Get the interest rate on mortgages.
	 * @return The interest rate on mortgages.
	 *****************************/
	public void setMortgageInterestRate(double rate) {
		interestSpread = rate - baseRate;
	}
	
	public double getBaseRate() {
		return baseRate;
	}

	public void setBaseRate(double baseRate) {
		this.baseRate = baseRate;
	}
	
//	public Mortgage.Lender	mortgageLender;	// all unpaid mortgage contracts supplied by the bank
	public double		interestSpread;	// current mortgage interest spread above base rate (monthly rate*12)
	public double		baseRate;
	// --- supply strategy stuff
	public long			supplyTarget; 	// target supply of mortgage lending (pence)
	public long			demand;			// monthly demand for mortgage loans (pence)
	public long			lastMonthsSupplyVal;
	public double		dDemand_dInterest; // rate of change of demand with interest rate (pounds)
//	public int			nLoans; 			// total number of non-BTL loans this step
	

	
	DepositAccount.Issuer		depositAccounts;
	DepositAccount.Owner		internalAccounts;
	public DepositAccount		endowmentAccount; // account from which we can endow people with cash
	public DepositAccount		profitAccount; // account from which we can endow people with cash
	//Contract.Owner<Mortgage>		 mortgages;
}
