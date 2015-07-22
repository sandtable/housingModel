package development;

abstract public class PaymentAgreement implements ITriggerable {
	
	public PaymentAgreement(DepositAccount iPayerAC, DepositAccount iPayeeAC) {
		payerAC= iPayerAC;
		payeeAC = iPayeeAC;
//		amount = iAmount;
	}
	
	public void trigger() {
		payerAC.transfer(payeeAC, amount());
	}
	
	// Override this to return the amount of the payment
	abstract public long amount();
	
//	LongSupplier 				amount;
	DepositAccount 	payerAC;
	DepositAccount 	payeeAC;


}
