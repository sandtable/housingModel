package development;

import contracts.SaleMarketOffer;
import contracts.TangibleAsset;
import contracts.DepositAccount;


public class Construction extends EconAgent implements ITriggerable {
	HouseSaleMarket houseSaleMarket;
	RentalMarket	rentalMarket;
	ModelRoot		root;
	public double 	housesPerHousehold; 	// target number of houses per household
	public int 		housingStock;			// total number of houses built
	
	
	public Construction() {
		super(new DepositAccount.Owner(),
				new SaleMarketOffer.Issuer(),
				new TangibleAsset.Owner());
		housesPerHousehold = 4100.0/5000.0;
		housingStock = 0; 
	}
	
	@Override
	public void start(IModelNode parent) {
		houseSaleMarket = parent.mustFind(HouseSaleMarket.class);
		rentalMarket = parent.mustFind(RentalMarket.class);
		root = parent.mustFind(ModelRoot.class);
		
		parent.get(Bank.class).openAccount(get(DepositAccount.Owner.class));
		Trigger.monthly().schedule(this); // TODO: deal with death of construction sector?
		super.start(parent);
	}
	
	public void init() {
		housingStock = 0;		
	}
	
	public void trigger() {
		int targetStock = (int)(mustFind(Households.class).size()*housesPerHousehold);
		int shortFall = targetStock - housingStock;
		while(shortFall > 0) {
			buildHouse();
			--shortFall;
		}
	}
	
	public void buildHouse() {
		House newBuild;
		long price;

//		System.out.println("Building a new house");
		newBuild = new House(houseSaleMarket, rentalMarket, root);
		get(TangibleAsset.Owner.class).receive(newBuild);
		++housingStock;
		price = Data.HousingMarket.referenceSalePrice(newBuild.quality);
		get(SaleMarketOffer.Issuer.class).issue(newBuild, price, houseSaleMarket);
	}
	
}
