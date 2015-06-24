package testing;

public class DepositAccount extends Contract<DepositAccount, DepositAccount.Issuer> {
	
	public DepositAccount(Issuer iIssuer, Contract.IOwner<DepositAccount> iOwner) {
		super(iIssuer, iOwner);
	}
	/***
	 * Agent module for deposit account issuer
	 * @author daniel
	 */
	
	private void transfer(DepositAccount toAC, long amount) {
		
	}
	
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
		public boolean issue(Contract.IOwner<DepositAccount> holder) {
			return(issue(new DepositAccount(this,holder)));
		}
		
	}


	long balance = 0; // balance in cents
}
