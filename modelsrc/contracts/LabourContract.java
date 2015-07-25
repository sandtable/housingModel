package contracts;

import development.IMessage;
import development.IModelNode;
import development.Model;
import development.Trigger;
import development.IMessage.IReceiver;
import development.Trigger.Repeating;
import utilities.ModelTime;

public class LabourContract extends Contract {
	public LabourContract(final IIssuer issuer, final IOwner payee) {
		super(issuer);
		percentile = Model.root.random.nextDouble();
		payment = new PaymentAgreement(issuer.account(), payee.account()) {
			public long amount() {
				return(issuer.monthlyWage(payee.age(), percentile));
			}
		};
		trigger = Trigger.monthly();
		trigger.schedule(payment);
	}
	
	@Override
	public boolean terminate() {
		if(super.terminate()) {
			trigger.stop();
			return(true);
		}
		return(false);
	}
	
	
	static public interface IOwner extends IMessage.IReceiver, IModelNode {
		public ModelTime age();
		public DepositAccount account();
	}
	
	static public interface IIssuer extends Contract.IIssuer {
		public long monthlyWage(ModelTime age, double percentile);
		public DepositAccount account();
	}
	
	
	public PaymentAgreement	payment;
	double				percentile; // wage percentile: proxy for skill of worker?
	IOwner				payee;
	Trigger.Repeating	trigger;
}
