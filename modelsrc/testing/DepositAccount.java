package testing;

public class DepositAccount extends Contract {

	public DepositAccount() {
		super(tmpBank);
	}
	
	public void transfer(DepositAccount payee, long amount) {
	//	if(balance < amount) throw(new Throwable("Insufficient Funds"));
		balance -= amount;
		payee.balance += amount;
	}
	
//	Object issuer; // for systemic risk

	long balance = 0; // balance in cents
	
	static Contract.IIssuer tmpBank = new Contract.IIssuer() {
		public boolean terminate(Contract o) {return(true);}
	};
}
