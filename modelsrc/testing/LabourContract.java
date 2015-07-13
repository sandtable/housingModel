package testing;

public class LabourContract extends Contract {
	public LabourContract(final IIssuer issuer, DepositAccount issuerAC, final IOwner payee, DepositAccount payeeAC) {
		super(issuer);
		payment = new PaymentAgreement(issuerAC, payeeAC) {
			public long amount() {
				return(issuer.monthlyWage(payee.age()));
			}
		};
		Trigger.monthly().schedule(payment);
	}
	
	static public interface IOwner extends Contract.IOwner {
		public int age();
		public DepositAccount account();
	}
	
	static public interface IIssuer extends Contract.IIssuer {
		public long monthlyWage(int age);
		public DepositAccount account();
	}
	
	PaymentAgreement	payment;
	IOwner				payee;
}
