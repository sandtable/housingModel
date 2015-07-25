package development;

import contracts.DepositAccount;
import contracts.MarketOffer;


public class Construction extends EconAgent implements IHouseOwner {

	public Construction() {
		super(new DepositAccount.Owner(),
				new MarketOffer.Issuer());
		housesPerHousehold = 4100.0/5000.0;
		housingStock = 0; 
	}
	
	@Override
	public void start(IModelNode parent) {
		parent.get(Bank.class).openAccount(get(DepositAccount.Owner.class));
		super.start(parent);
	}
	
	public void init() {
		housingStock = 0;		
	}
	
	public void step() {
		int targetStock = (int)(get(NodeGroup.class).size()*housesPerHousehold);
		// TODO: work out how to refer to different NodeGroups
		int shortFall = targetStock - housingStock;
		while(shortFall > 0) {
			buildHouse();
			--shortFall;
		}
	}
	
	public void buildHouse() {
		House newBuild;
		long price;

		newBuild = new House(parent.find(HouseSaleMarket.class), parent.find(RentalMarket.class));
		newBuild.owner = this;
		++housingStock;
		price = Data.HousingMarket.referenceSalePrice(newBuild.quality);
		get(MarketOffer.Issuer.class).issue(newBuild, price);
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
//	MarketOffer.Issuer marketOfferIssuer;
}
