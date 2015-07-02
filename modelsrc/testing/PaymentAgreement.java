package testing;

public class PaymentAgreement {
	
	public PaymentAgreement(DepositAccountAgreement iPayerAC, DepositAccountAgreement iPayeeAC) {
		payerAC= iPayerAC;
		payeeAC = iPayeeAC;
//		amount = iAmount;
	}
	
	public void honour() throws Throwable {
		payerAC.transfer(payeeAC, amount());
	}
	
	public long amount() {
		return(0);
	}
	
//	LongSupplier 				amount;
	DepositAccountAgreement 	payerAC;
	DepositAccountAgreement 	payeeAC;


}
