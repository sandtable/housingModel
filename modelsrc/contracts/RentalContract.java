package contracts;

import sim.engine.Stoppable;
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
	public Stoppable paymentTrigger;
	public Stoppable endOfAgreementTrigger;

	public RentalContract(IIssuer issuer, final House house, DepositAccount payerAC, DepositAccount payeeAC, long amount, int months) {
		super(issuer);
		this.house = house;
		agreement = new FixedPaymentAgreement(payerAC, payeeAC, amount);
		paymentTrigger = Trigger.monthly().until(Trigger.after(ModelTime.months(months-1))).schedule(agreement);
		endOfAgreementTrigger = Trigger.after(ModelTime.months(months-1)).schedule(new ITriggerable() {
			public void trigger() {ownerDiscarded(); house.lodger.endOfAgreement(RentalContract.this);}
		});
	}
	
	public long monthlyRent() {
		return(agreement.amount());
	}
	
	@Override
	public boolean ownerDiscarded() {
		if(super.ownerDiscarded()) {
			paymentTrigger.stop();
			endOfAgreementTrigger.stop();
			return(true);
		}
		System.out.println("Error: Can't complete discard of rental contract");
		return(false);
	}
	
	@Override
	public boolean issuerTerminated() {
		if(super.issuerTerminated()) {
			paymentTrigger.stop();
			endOfAgreementTrigger.stop();
			return(true);
		}
		System.out.println("Error: Can't complete termination of rental contract");
		return(false);		
	}
	
	public static class Owner extends Contract.Owner<RentalContract> {
		public Owner() {
			super(RentalContract.class);
		}
		
		public void endOfAgreement(RentalContract contract) {
			remove(contract);
			parent().receive(new Message.EndOfContract(contract));
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
		public boolean ownerDiscarded(Contract contract) {
			if(super.ownerDiscarded(contract)) {
				parent().receive(new Message.EndOfContract(contract));				
			}
			return(false);
		}
	}
	
	
}
