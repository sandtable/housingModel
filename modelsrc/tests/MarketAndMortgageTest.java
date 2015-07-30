package tests;

import contracts.DemandForPayment;
import contracts.DepositAccount;
import contracts.MarketBid;
import contracts.Mortgage;
import contracts.OOMarketBid;
import development.Bank;
import development.CentralBank;
import development.Construction;
import development.EconAgent;
import development.House;
import development.HouseSaleMarket;
import development.IMessage;
import development.IModelNode;
import development.ITriggerable;
import development.HousingMarket;
import development.ModelBase;
import development.NodeGroup;
import development.RentalMarket;
import development.Trigger;
import sim.engine.SimState;
import sim.engine.Steppable;
import utilities.ModelTime;

@SuppressWarnings("serial")
public class MarketAndMortgageTest extends ModelBase {

	public static class HouseholdStub extends EconAgent {
		public HouseholdStub() {
			super(
					new DepositAccount.Owner(),
					new OOMarketBid.Issuer(),
					new Mortgage.Borrower()
				);
//			registerHandler(House.class, this);
		}
		
		@Override
		public boolean receive(IMessage message) {
			if(message instanceof House) {
				System.out.println("got house");
				home = (House)message;
				return(true);
			} else if(message instanceof DemandForPayment) {
				if(((DemandForPayment)message).contract instanceof OOMarketBid) {
					System.out.println("Applying for mortgage");
					Bank bank = find(Bank.class);
					Mortgage m = bank.get(Mortgage.Lender.class).requestApproval(get(Mortgage.Borrower.class), 10000000, 1000000, true);
					System.out.println("Got Mortgage = "+m.principal);
					bank.get(Mortgage.Lender.class).issue(m, get(Mortgage.Borrower.class));
				}
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
				new CentralBank(),
				new Bank(),
				new HouseSaleMarket(),
				new RentalMarket(),
				new Construction(),
				new HouseholdStub()
		);
		household = root.get(HouseholdStub.class);
	}

	public void start() {
		super.start();
		root.get(Construction.class).buildHouse();
		System.out.println("Offers = "+root.get(HouseSaleMarket.class).offers.size());
		household.get(OOMarketBid.Issuer.class).issue(50000000, 0, root.get(HouseSaleMarket.class).bids);
		System.out.println("Bids = "+root.get(HouseSaleMarket.class).bids.size());		
		Trigger.after(ModelTime.year()).schedule(new ITriggerable() {
			public void trigger() {kill();}});
	}
	
	public void finish() {
		System.out.println("household.home = "+(household.home != null));
	}
		
    public static void main(String[] args) {
    	SimState.doLoop(MarketAndMortgageTest.class, args);
    }
    
    HouseholdStub 		household;
}
