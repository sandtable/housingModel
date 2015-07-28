package contracts;

import utilities.ModelTime;
import development.House;
import development.IMessage;
import development.IModelNode;
import development.ITriggerable;
import development.Message;
import development.Trigger;

public class RentalContract extends Contract {
	public FixedPaymentAgreement agreement;
	public House house;

	public RentalContract(IIssuer issuer, final House house, DepositAccount payerAC, DepositAccount payeeAC, long amount, int months) {
		super(issuer);
		this.house = house;
		agreement = new FixedPaymentAgreement(payerAC, payeeAC, amount);
		Trigger.monthly().until(Trigger.after(ModelTime.months(months-1))).schedule(agreement);
		Trigger.after(ModelTime.months(months-1)).schedule(new ITriggerable() {
			public void trigger() {terminate(); house.lodger.endOfAgreement(RentalContract.this);}
		});
	}
	
	public long monthlyRent() {
		return(agreement.amount());
	}
		
	
	public static class Owner extends Contract.Owner<RentalContract> {
		public Owner() {
			super(RentalContract.class);
		}
		
		public void endOfAgreement(RentalContract contract) {
			remove(contract);
			parent().receive(Message.endOfContract);
		}
	}

	public static class Issuer extends Contract.Issuer<RentalContract> implements IIssuer {
		DepositAccount.Owner payee;
		
		public Issuer() {
			super(RentalContract.class);
		}
		
		@Override
		public void start(IModelNode parent) {
			super.start(parent);
			parent.mustFind(DepositAccount.Owner.class);
		}
		
		public boolean issue(House house, long monthlyRent, IMessage.IReceiver lodger) {
			return(issue(new RentalContract(this, house, null, payee.defaultAccount(), monthlyRent, 12), lodger));
		}

		@Override
		public boolean terminate(Contract contract) {
			return(false);
		}
	}
	
	
}
