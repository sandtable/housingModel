package contracts;

import development.EconAgent;
import development.IMessage;
import development.IModelNode;


public class DepositAccount extends Contract {

	public DepositAccount(DepositAccount.IIssuer issuer) {
		super(issuer);
	}
	
	public void transfer(DepositAccount payee, long amount) {
	//	if(balance < amount) throw(new Throwable("Insufficient Funds"));
		balance -= amount;
		payee.balance += amount;
	}
	
	static public class Issuer extends Contract.Issuer<DepositAccount> {
		public Issuer() {super(DepositAccount.class);}
		public boolean issue(Owner owner) {
			return(issue(new DepositAccount(this), owner));
		}
	}

	static public class Owner extends Contract.Owner<DepositAccount> {
		public Owner() {super(DepositAccount.class);}
		public Owner(Class<? extends DepositAccount> class1) {
			super(class1);
		}
		
		@Override
		public void start(IModelNode parent) {
			super.start(parent);
			if(parent instanceof EconAgent && getClass() == DepositAccount.Owner.class) {
				((EconAgent) parent).registerHandler(DemandForPayment.class, this);
			}
		}
		
		@Override
		public boolean receive(IMessage message) {
			if(message instanceof DemandForPayment) {
				((DemandForPayment)message).pay(defaultAccount());
				return(true);
			}
			return(super.receive(message));
		}
		
		public DepositAccount defaultAccount() {
			return(first());
		}
		
	}

	public long balance = 0; // balance in cents
}
