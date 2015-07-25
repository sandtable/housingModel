package tests;

import contracts.DepositAccount;
import contracts.OOMarketBid;
import development.Bank;
import development.Construction;
import development.EconAgent;
import development.House;
import development.HouseSaleMarket;
import development.IMessage;
import development.IModelNode;
import development.ITriggerable;
import development.HousingMarket;
import development.Model;
import development.NodeGroup;
import development.RentalMarket;
import development.Trigger;
import sim.engine.SimState;
import sim.engine.Steppable;
import utilities.ModelTime;

@SuppressWarnings("serial")
public class MarketAndMortgageTest extends Model {

	public static class HouseholdStub extends EconAgent {
		public HouseholdStub() {
			super(
					new DepositAccount.Owner(),
					new OOMarketBid.Issuer()
				);
			registerHandler(House.class, this);			
		}
		
		@Override
		public boolean receive(IMessage message) {
			if(message instanceof House) {
				System.out.println("got house");
				home = (House)message;
				return(true);
			}
			return(super.receive(message));
		}

		@Override
		public void start(IModelNode parent) {
			parent.find(Bank.class).openAccount(this);
			super.start(parent);
		}

		OOMarketBid.Issuer 		asMarketBidder;
		DepositAccount.Owner 	asDepositAccountOwner;
		House					home;
	}
	
	public MarketAndMortgageTest(long seed) {
		super(seed,
				new Bank(),
				new HouseSaleMarket(),
				new RentalMarket(),
				new Construction(),
				new NodeGroup<HouseholdStub>()
		);
		NodeGroup<HouseholdStub> households = root.get(NodeGroup.class);
		Household1 = new HouseholdStub();
		Household2 = new HouseholdStub();
		Household3 = new HouseholdStub();
		households.add(Household1);
		households.add(Household2);
		households.add(Household3);
	}

	public void start() {
		super.start();
		root.get(Construction.class).buildHouse();
		System.out.println("Offers = "+root.get(HouseSaleMarket.class).offers.size());
		Household1.get(OOMarketBid.Issuer.class).issue(50000000, 0, root.get(HouseSaleMarket.class).bids);
		Household2.get(OOMarketBid.Issuer.class).issue(55000000, 0, root.get(HouseSaleMarket.class).bids);
		System.out.println("Bids = "+root.get(HouseSaleMarket.class).bids.size());		
		Trigger.after(ModelTime.year()).schedule(new ITriggerable() {
			public void trigger() {kill();}});
	}
	
	public void finish() {
		System.out.println("Household1 "+(Household1.home != null));
		System.out.println("Household2 "+(Household2.home != null));
	}
		
    public static void main(String[] args) {
    	SimState.doLoop(MarketAndMortgageTest.class, args);
    }
    
    HouseholdStub 		Household1;
    HouseholdStub	 	Household2;
    HouseholdStub	 	Household3;    
}
