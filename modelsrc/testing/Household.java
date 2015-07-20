package testing;


public class Household extends EconAgent {
	public Household(Bank bank, DepositAccount consumptionAC) {
			asDepositAccountOwner = new DepositAccount.Owner();
			addTrait(asDepositAccountOwner);
			bank.openAccount(asDepositAccountOwner);
			asLifecycle = new Lifecycle(this);
			asEmployee = new Employee(asLifecycle, bankAccount());
			asConsumer = new Consumer(bankAccount(), consumptionAC, asEmployee);
			asOwnerOccupier = new OwnerOccupier();
			addTrait(asLifecycle);
			addTrait(asEmployee);
			addTrait(asConsumer);
			addTrait(asOwnerOccupier);
//			new Renter(),
//			new BuyToLetInvestor()
			
	}
	
	/////////////////////////////////////////////////////////
	// Inheritance behaviour
	/////////////////////////////////////////////////////////
	
	public void die() {
		bankAccount().transfer(Model.root.government.bankAccount(), bankAccount().balance);
		asDepositAccountOwner.discardAll();
		asEmployee.discardAll();
		asOwnerOccupier.die(Model.root.government);
		Model.root.households.remove(this);
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

	
	DepositAccount bankAccount() {
		return(getTrait(DepositAccount.Owner.class).first());
	}
	
	public Employee asEmployee;
	public Consumer asConsumer;
	public Lifecycle asLifecycle;
	public OwnerOccupier asOwnerOccupier;
	public DepositAccount.Owner asDepositAccountOwner;

}
