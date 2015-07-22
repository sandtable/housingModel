package development;

public class Bank extends EconAgent {
	public Bank() {
		super(	new DepositAccount.Issuer(),
				new DepositAccount.Owner());
		depositAccounts = getTrait(DepositAccount.Issuer.class);
		internalAccounts = getTrait(DepositAccount.Owner.class);
		endowmentAccount = new DepositAccount(depositAccounts);
		depositAccounts.issue(endowmentAccount, internalAccounts);
		
		//mortgages = new Contract.Owner<>(MortgageAgreement.class);
	}
		
	public boolean openAccount(DepositAccount.Owner accountHolder) {
		return(depositAccounts.issue(accountHolder));
	}
	
	DepositAccount.Issuer		depositAccounts;
	DepositAccount.Owner		internalAccounts;
	public DepositAccount		endowmentAccount; // account from which we can endow people with cash
	//Contract.Owner<Mortgage>		 mortgages;
}
