package tests;

import development.Bank;
import development.Construction;
import development.DepositAccount;
import development.EconAgent;
import development.House;
import development.HouseSaleMarket;
import development.IMessage;
import development.ITriggerable;
import development.HousingMarket;
import development.Model;
import development.OOMarketBid;
import development.RentalMarket;
import development.Trigger;
import sim.engine.SimState;
import sim.engine.Steppable;
import utilities.ModelTime;

@SuppressWarnings("serial")
public class MarketTest extends Model {

	public static class HouseholdStub extends EconAgent {
		public HouseholdStub(Bank bank) {
			asDepositAccountOwner = new DepositAccount.Owner();
			bank.openAccount(asDepositAccountOwner);
			asMarketBidder = new OOMarketBid.Issuer(this);
			addTrait(asDepositAccountOwner);
			addTrait(asMarketBidder);
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
		
		OOMarketBid.Issuer 		asMarketBidder;
		DepositAccount.Owner 	asDepositAccountOwner;
		House					home;
	}
	
	public MarketTest(long seed) {
		super(seed);
		bank = new Bank();
		saleMarket = new HouseSaleMarket();
		rentalMarket = new RentalMarket();
		construction = new Construction(bank);
		Household1 = new HouseholdStub(bank);
		Household2 = new HouseholdStub(bank);
		Household3 = new HouseholdStub(bank);
	}

	public void start() {
		construction.buildHouse();
		System.out.println(saleMarket.offers.size());
		Household1.asMarketBidder.issue(50000000, 0, saleMarket.bids);
		Household2.asMarketBidder.issue(55000000, 0, saleMarket.bids);
	}
	
	public void finish() {
		System.out.println("Household1 "+(Household1.home != null));
		System.out.println("Household2 "+(Household2.home != null));
	}
		
    public static void main(String[] args) {
    	SimState.doLoop(MarketTest.class, args);
    }
    
    HouseholdStub 		Household1;
    HouseholdStub	 	Household2;
    HouseholdStub	 	Household3;    
}
