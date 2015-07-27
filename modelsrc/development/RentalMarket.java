package development;

public class RentalMarket extends HousingMarket {

	@Override
	public Bids newBids() {
		return this.new Bids();
	}

	@Override
	public Offers newOffers() {
		return this.new Offers();
	}

	@Override
	public long referencePrice(int quality) {
		return(Data.HousingMarket.referenceRentalPrice(quality));
	}

}
