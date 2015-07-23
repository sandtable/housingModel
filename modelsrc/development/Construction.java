package development;


public class Construction extends EconAgent implements IHouseOwner {

	public Construction(Bank bank) {
		super(new DepositAccount.Owner());
		bank.openAccount(getTrait(DepositAccount.Owner.class));
		bankAccount = getTrait(DepositAccount.Owner.class).first();
		marketOfferIssuer = new MarketOffer.Issuer(bankAccount);
		housesPerHousehold = 4100.0/5000.0;
		housingStock = 0; 
	}
	
	public void init() {
		housingStock = 0;		
	}
	
	public void step() {
		int targetStock = (int)(Model.root.setOfHouseholds.size()*housesPerHousehold);
		int shortFall = targetStock - housingStock;
		while(shortFall > 0) {
			buildHouse();
			--shortFall;
		}
	}
	
	public void buildHouse() {
		House newBuild;
		long price;

		newBuild = new House();
		newBuild.owner = this;
		++housingStock;
		price = Data.HousingMarket.referenceSalePrice(newBuild.quality);
		marketOfferIssuer.issue(newBuild, price);
	}
	
	@Override
	public boolean remove(House house) {
		return(true);
	}

//	@Override
//	public void completeSale(MarketOffer sale) {
//	}
	
	public double 	housesPerHousehold; 	// target number of houses per household
	public int 		housingStock;			// total number of houses built
	DepositAccount  bankAccount;
	MarketOffer.Issuer marketOfferIssuer;
}
