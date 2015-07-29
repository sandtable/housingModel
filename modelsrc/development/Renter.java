package development;

import housing.Model;
import contracts.OOMarketBid;
import contracts.RentalContract;

public class Renter extends EconAgent {
	OwnerOccupier	meOwnerOccupier;
	Employee		meEmployee;
	RentalMarket	rentalMarket;
	House			home;
	int				qualityOfLife;
	
	public Renter() {
		super(new OOMarketBid.Issuer(),
		 	  new RentalContract.Owner());
		qualityOfLife = 0;
	}
	
	@Override
	public void start(IModelNode parent) {
		super.start(parent);
		meOwnerOccupier = parent.get(OwnerOccupier.class);
		meEmployee = parent.mustGet(Employee.class);
		rentalMarket = parent.mustFind(RentalMarket.class);
	}
	
	@Override
	public boolean receive(IMessage message) {
		if(message instanceof Message.EndOfContract) { // end of rental contract
			home.lodger = null;
			home = null;
			if(decideToBecomeOwnerOccupier()) {
				meOwnerOccupier.bidOnHouseMarket();
			} else {
				bidOnRentalMarket();
			}
			return(true);
		}
		else if(message instanceof House) {
			System.out.println("got house");
			home = (House)message;
			home.lodger = get(RentalContract.Owner.class);
			qualityOfLife = home.quality;
			return(true);
		}
		return false;
	}

	public void introspect() {
		// TODO Auto-generated method stub
	}

	public void bidOnRentalMarket() {
		get(OOMarketBid.Issuer.class).issue(desiredRent(meEmployee.monthlyIncome()), qualityOfLife, rentalMarket);
	}
	
	public int calcAffordableQualityOfHouse() {
		long rent = desiredRent(meEmployee.monthlyIncome());
		return(rentalMarket.qualityGivenPrice(rent));
	}
	
	public boolean decideToBecomeOwnerOccupier() {
		return(meOwnerOccupier.calcAffordableQualityOfHouse() >= calcAffordableQualityOfHouse());
	}

	/********************************************************
	 * Decide how much to bid on the rental market
	 * Source: Zoopla rental prices 2008-2009 (at Bank of England)
	 ********************************************************/
	public long desiredRent(long monthlyIncome) {
//		return(monthlyIncome * 0.33);
		double annualIncome = monthlyIncome*12.0; // TODO: this should be net annual income, not gross
		double rent;
		if(annualIncome < 12000.0) {
			rent = 386.0;
		} else {
			rent = 11.72*Math.pow(annualIncome, 0.372);
		}
		rent *= Math.exp(Model.rand.nextGaussian()*0.0826);
		return(Math.round(rent));
	}

	
	public boolean isRenting() {
		return(home != null);
	}

}
