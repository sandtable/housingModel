package testing;

public class DepositAccount extends Contract<DepositAccount> {
	
	/***
	 * Agent module for deposit account issuer
	 * @author daniel
	 */
	
	Issuer issuer() {
		return((Issuer)issuer);
	}
	
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
		public void issue(Contract.Owner<DepositAccount> holder) {
			issue(new DepositAccount(), holder);
		}
		
	}
	
	double balance = 0.0;

}
