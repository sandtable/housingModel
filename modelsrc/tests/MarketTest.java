package tests;

import development.Bank;
import development.Construction;
import development.DepositAccount;
import development.EconAgent;
import development.House;
import development.HouseSaleMarket;
import development.IMessage;
import development.Model;
import development.OOMarketBid;
import sim.engine.SimState;

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
				return(true);
			}
			return(super.receive(message));
		}
		
		OOMarketBid.Issuer 		asMarketBidder;
		DepositAccount.Owner 	asDepositAccountOwner;
	}
	
	public MarketTest(long seed) {
		super(seed);
		bank = new Bank();
		houseSaleMarket = new HouseSaleMarket();
		construction = new Construction(bank);
		Household1 = new HouseholdStub(bank);
	}

	public void start() {
		construction.buildHouse();
		System.out.println(houseSaleMarket.offers.size());
		Household1.asMarketBidder.issue(new OOMarketBid(Household1.asMarketBidder, 50000000, 0), houseSaleMarket.bids);
	}
		
    public static void main(String[] args) {
    	SimState.doLoop(MarketTest.class, args);
    }
    
    HouseholdStub 		Household1;
    OOMarketBid.Issuer 	Household2;
    OOMarketBid.Issuer 	Household3;    
}
