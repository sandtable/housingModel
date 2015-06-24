package testing;

public class DepositAccountAgreement {

	public void transfer(DepositAccountAgreement payee, long amount) {
	//	if(balance < amount) throw(new Throwable("Insufficient Funds"));
		balance -= amount;
		payee.balance += amount;
	}

	long balance = 0; // balance in cents

}
