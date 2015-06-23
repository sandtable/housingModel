package testing;

public class DepositAccount extends Contract<DepositAccount, DepositAccount.Issuer, DepositAccount.Owner> {
	
	public DepositAccount(Issuer iIssuer, Owner iOwner) {
		super(iIssuer, iOwner);
	}
	/***
	 * Agent module for deposit account issuer
	 * @author daniel
	 */
	
	static public class Issuer extends Contract.Issuer<DepositAccount> {
		// honour contract
		public boolean transfer(DepositAccount account, long amount, DepositAccount payee) {
			if(!contains(account)) return(false);
			if(account.balance < amount) return(false);
			account.balance -= amount;
			payee.balance += amount;
			return true;
		}
		
		// open new account / issue contract
		public boolean issue(DepositAccount.Owner holder) {
			return(issue(new DepositAccount(this,holder)));
		}
		
	}

	static public class Owner extends Contract.Owner<DepositAccount> {}

	long balance = 0; // balance in cents
}
