package testing;

import housing.Model;
import utilities.ModelTime;

public class LabourContract extends Contract {
	public LabourContract(final IIssuer issuer, final IOwner payee) {
		super(issuer);
		percentile = Model.rand.nextDouble();
		payment = new PaymentAgreement(issuer.account(), payee.account()) {
			public long amount() {
				return(issuer.monthlyWage(payee.age(), percentile));
			}
		};
		Trigger.monthly().schedule(payment);
	}
	
	
	static public interface IOwner extends Message.IReceiver, IAgentTrait {
		public ModelTime age();
		public DepositAccount account();
	}
	
	static public interface IIssuer extends Contract.IIssuer {
		public long monthlyWage(ModelTime age, double percentile);
		public DepositAccount account();
	}
	
	PaymentAgreement	payment;
	double				percentile; // wage percentile: proxy for skill of worker?
	IOwner				payee;
}
