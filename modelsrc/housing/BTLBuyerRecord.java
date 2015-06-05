package housing;

public class BTLBuyerRecord extends HouseBuyerRecord {

	public BTLBuyerRecord(Household h, double p, double minGrossYield) {
		super(h, p, 0);
		grossYield = minGrossYield;
	}
	
	@Override
	public int compareTo(HouseBuyerRecord other) {
		double diff = ((BTLBuyerRecord)other).grossYield - grossYield;
		if(diff == 0.0) {
			diff = other.buyer.id - buyer.id;
		}
		return((int)Math.signum(diff));
	}
	
	double grossYield;

}
