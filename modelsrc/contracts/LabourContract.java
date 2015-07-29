package contracts;

import utilities.ModelTime;
import development.IMessage;
import development.IModelNode;
import development.Trigger;

public class LabourContract extends Contract {
	public LabourContract(final IIssuer issuer, final IOwner payee) {
		super(issuer);
		payment = new PaymentAgreement(issuer.account(), payee.account()) {
			public long amount() {
				return(issuer.monthlyWage(payee.age(), payee.getIncomePercentile()));
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
		ModelTime age();
		DepositAccount account();
		double getIncomePercentile(); 
	}
	
	static public interface IIssuer extends Contract.IIssuer {
		public long monthlyWage(ModelTime age, double percentile);
		public DepositAccount account();
	}
	
	
	public PaymentAgreement	payment;
	IOwner				payee;
	Trigger.Repeating	trigger;
}
