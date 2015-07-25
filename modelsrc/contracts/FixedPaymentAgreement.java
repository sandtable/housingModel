package contracts;


public class FixedPaymentAgreement extends PaymentAgreement {

	public FixedPaymentAgreement(DepositAccount iPayerAC, DepositAccount iPayeeAC, long Amount) {
		super(iPayerAC, iPayeeAC);
		payment = Amount;
	}

	@Override
	public long amount() {
		return payment;
	}

	public long payment;
}
