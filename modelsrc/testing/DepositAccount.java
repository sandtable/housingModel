package testing;

public class DepositAccount extends Contract<DepositAccount.Owner, DepositAccount.Issuer> {
	public DepositAccount(Owner depositHolder, Issuer bank) {
		super(depositHolder, bank, onDemand());
	}
	
	/***
	 * Agent module for deposit account issuer
	 * @author daniel
	 */
	static public class Issuer extends Contract.Issuer<DepositAccount> {
		// honour contract
		public boolean transfer(DepositAccount account, double amount, DepositAccount payee) {
//			if(!accounts.contains(account)) return false;
			//account.
			if(account.balance < amount) return false;
			account.balance -= amount;
			payee.balance += amount;
			return true;
		}
		
		// issue contract
		public void openAccount(Owner holder) {
			issue(new DepositAccount(holder, this));
		}
		
	}
	
	/***
	 * Agent module for deposit account holder
	 * @author daniel
	 */
	static public class Owner extends Contract.Owner<DepositAccount> {
	}

	double balance = 0.0;

}
