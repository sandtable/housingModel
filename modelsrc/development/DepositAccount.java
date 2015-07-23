package development;


public class DepositAccount extends Contract {

	public DepositAccount() {
		super(tmpBank);
	}

	public DepositAccount(DepositAccount.Issuer issuer) {
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
		@Override
		public boolean receive(IMessage message) {
			if(message instanceof Message.Die) {
				for(DepositAccount ac : this) {
					ac.transfer(Model.root.government.bankAccount(), ac.balance);
				}
				return(true);
			} else if(message instanceof DemandForPayment) {
				((DemandForPayment)message).pay(first());
				return(true);
			}
			return(super.receive(message));
		}
		
	}

//	Object issuer; // for systemic risk

	public long balance = 0; // balance in cents
	
	static Contract.IIssuer tmpBank = new Contract.IIssuer() {
		public boolean terminate(Contract o) {return(true);}
	};
}
