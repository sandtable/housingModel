package testing;

public class DepositAccountAgreement extends Contract {

	public DepositAccountAgreement() {
		super(tmpBank);
	}
	
	public void transfer(DepositAccountAgreement payee, long amount) {
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
