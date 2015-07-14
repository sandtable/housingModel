package testing;

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
	
	public void issueDepositAccounts(Iterable<Household> households) {
		for(Household household : households) {
			depositAccounts.issue(household.getTrait(DepositAccount.Owner.class));
		}
	}
	
	DepositAccount.Issuer		depositAccounts;
	DepositAccount.Owner		internalAccounts;
	DepositAccount				endowmentAccount; // account from which we can endow people with cash
	//Contract.Owner<Mortgage>		 mortgages;
}
