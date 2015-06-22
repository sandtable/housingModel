package testing;

public class DepositAccount extends Contract<DepositAccount, DepositAccount.Owner, DepositAccount.Issuer> {
	
	/***
	 * Agent module for deposit account issuer
	 * @author daniel
	 */
	
	static public class Issuer extends Contract.Issuer<DepositAccount> {
		// honour contract
		public boolean transfer(DepositAccount account, double amount, DepositAccount payee) {
			if(!contains(account)) return(false);
			if(account.balance < amount) return(false);
			account.balance -= amount;
			payee.balance += amount;
			return true;
		}
		
		// open new account / issue contract
		public void issue(DepositAccount.Owner holder) {
			issue(new DepositAccount());
		}
		
	}

	static public class Owner extends Contract.Owner<DepositAccount> {}

	double balance = 0.0;

}
