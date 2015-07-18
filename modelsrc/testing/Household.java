package testing;


public class Household extends EconAgent {
	public Household(Bank bank, DepositAccount consumptionAC) {
			asDepositAccountOwner = new DepositAccount.Owner();
			addTrait(asDepositAccountOwner);
			bank.openAccount(asDepositAccountOwner);
			asLifecycle = new Lifecycle();
			asEmployee = new Employee(asLifecycle, bankAccount());
			asConsumer = new Consumer(bankAccount(), consumptionAC, asEmployee);
			addTrait(asLifecycle);
			addTrait(asEmployee);
			addTrait(asConsumer);
//			new Renter(),
//			new OwnerOccupier(),
//			new BuyToLetInvestor()
			
	}
	
	/////////////////////////////////////////////////////////
	// Inheritance behaviour
	/////////////////////////////////////////////////////////

	public void transferAllWealthTo(Household beneficiary) {
		bankAccount().transfer(beneficiary.bankAccount(), bankAccount().balance);
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
	public DepositAccount.Owner asDepositAccountOwner;
}
