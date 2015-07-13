package testing;

public class MarketBid extends Contract {
	public MarketBid(long iPrice) {
		price = iPrice;
	}
	
	public interface IIssuer extends Contract.IIssuer {
		void completeHousePurchase(housing.House house, DepositAccountAgreement depositAccount);
	}
		
	long price;
	
}
