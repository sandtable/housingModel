package development;


import java.util.TreeSet;

public class SetOfHouseholds extends TreeSet<Household> {
	

	public SetOfHouseholds() {
//		super(new Comparator<Household>() {
//			public int compare(Household h0, Household h1) {
//				return((int)Math.signum(h0.asLifecycle.birthday.raw() - h1.asLifecycle.birthday.raw()));
//			}
//		});
		// nextBirthEvent().schedule(this); // set birth going
	}
	
	/***
	 * Triggered at each household birth event
	 */
//	public void trigger() {
//		add(new Household());
//		nextBirthEvent().schedule(this);
//	}
	private static final long serialVersionUID = 6148851701951243398L;
}
