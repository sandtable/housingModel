package contracts;

import development.ITriggerable;

abstract public class PaymentAgreement implements ITriggerable {
	
	public PaymentAgreement(DepositAccount iPayerAC, DepositAccount iPayeeAC) {
		payerAC= iPayerAC;
		payeeAC = iPayeeAC;
	}
	
	public void trigger() {
		payerAC.transfer(payeeAC, amount());
	}
	
	// Override this to return the amount of the payment
	abstract public long amount();
	
	DepositAccount 	payerAC;
	DepositAccount 	payeeAC;


}
