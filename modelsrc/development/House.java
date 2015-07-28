package development;

import contracts.Mortgage;
import contracts.TangibleAsset;
import contracts.RentalContract;


/************************************************
 * Class representing a house.
 * Use this to represent the intrinsic properties of the house.
 * 
 * @author daniel
 *
 ************************************************/
public class House extends TangibleAsset implements Comparable<House> {
	
	static public class Config {
		public static int N_QUALITY = 48; // number of quality bands		
	}
	
	public House(HouseSaleMarket saleMarket, RentalMarket rentalMarket) {
		id = ++id_pool;	
		lodger = null;
		quality = (int)(Model.root.random.nextDouble()*Config.N_QUALITY);
		this.saleMarket = saleMarket;
		this.rentalMarket = rentalMarket;
	}
	
	public static class Owner extends TangibleAsset.Owner {
		public boolean isHomeless() {
			return(size() == 0);
		}
	}
	
	public int 				quality;
	public RentalContract.Owner			lodger;
	public HouseSaleMarket	saleMarket;
	public RentalMarket		rentalMarket;
	
	
	public int				id;	
	static int 				id_pool = 0;
	
	@Override
	public int compareTo(House o) {
		return((int)Math.signum(id-o.id));
	}
	
}
