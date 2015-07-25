package development;

import contracts.DepositAccount;



public class Household extends EconAgent {
	public Household() {
		super(	new DepositAccount.Owner(),
				new Lifecycle(),
				new Employee(),
				new Consumer(),
				new OwnerOccupier()
				);
	}

	@Override
	public void start(IModelNode parent) {
		parent.find(Bank.class).openAccount(parent.get(DepositAccount.Owner.class));
	}
	
	/////////////////////////////////////////////////////////
	// Inheritance behaviour
	/////////////////////////////////////////////////////////
	
	public void die() {
//		bankAccount().transfer(Model.root.government.bankAccount(), bankAccount().balance);
//		asDepositAccountOwner.discardAll();
//		asEmployee.discardAll();
//		asOwnerOccupier.die(Model.root.government);
//		Model.root.setOfHouseholds.remove(this);
		// TODO: transfer houses and terminate mortgages (should use bank account to pay off mortgages)
/***		
		for(House h : housePayments.keySet()) {
			if(home == h) {
				h.resident = null;
				home = null;
			}
			if(h.owner == this) {
				if(rentalMarket.isOnMarket(h)) rentalMarket.removeOffer(h);
				if(houseMarket.isOnMarket(h)) houseMarket.removeOffer(h);
				beneficiary.inheritHouse(h);
			} else {
				h.owner.endOfLettingAgreement(h);
			}
		}
		housePayments.clear();
		beneficiary.bankBalance += bankBalance;
		***/
	}

	
//	public DepositAccount bankAccount() {
//		return(getTrait(DepositAccount.Owner.class).first());
//	}
	
//	public Employee asEmployee;
//	public Consumer asConsumer;
//	public Lifecycle asLifecycle;
//	public OwnerOccupier asOwnerOccupier;
//	public DepositAccount.Owner asDepositAccountOwner;

}
