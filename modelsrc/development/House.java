package development;


/************************************************
 * Class representing a house.
 * Use this to represent the intrinsic properties of the house.
 * 
 * @author daniel
 *
 ************************************************/
public class House implements Comparable<House>, IAsset {
	
	static public class Config {
		public static int N_QUALITY = 48; // number of quality bands		
	}
	
	public House(HouseSaleMarket saleMarket, RentalMarket rentalMarket) {
		id = ++id_pool;	
		resident = null;
		owner = null;
		quality = (int)(Model.root.random.nextDouble()*Config.N_QUALITY);
		this.saleMarket = saleMarket;
		this.rentalMarket = rentalMarket;
	}
	
	public int 				quality;
	public IHouseOwner  	owner;
	public Household		resident;
	public HouseSaleMarket	saleMarket;
	public RentalMarket		rentalMarket;
	
	public int				id;	
	static int 				id_pool = 0;
	
	@Override
	public int compareTo(House o) {
		return((int)Math.signum(id-o.id));
	}
	
}
