package housing;

import housing.Person.Sex;
import housing.Person.Status;

import java.util.ArrayList;


public class Household {
	
	// Household Characteristics 
	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	// identifiers
	public final int HID;
	
	
	// socio-economic variables
	double householdIncome;
	public int householdMembers;

	// lists
	public ArrayList<Person> adults = new ArrayList<Person>();
	public ArrayList<Person> children = new ArrayList<Person>();
	
	
	// Class Characteristics
	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	public static int HouseholdCount;
	public static int HIDCount;
	public static int HouseholdMarriedCount;
	public static int HouseholdSingleCount;


// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
//			CONSTRUCTOR - CONSTRUCTOR - CONSTRUCTOR - CONSTRUCTOR - CONSTRUCTOR - CONSTRUCTOR
//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	
	// Constructor
	public Household(Person person) {
		// set household id and count number of households
		HID = HIDCount;
		HouseholdCount++;
		HIDCount++;
		adults.add(person);
	}

	// Constructor when marriage
	public Household(Person husband, Person wife) {
		// set household id and count number of households
		HID = HIDCount;
		HouseholdCount++;
		HIDCount++;
		adults.add(husband);
		adults.add(wife);
	}
	
	public void step() {
		// aggregate over income of members
		for(int i = 0; i<adults.size(); i++) {
			householdIncome =  householdIncome + adults.get(i).income;
		}
		economicDecisions();
	}

	
// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
//					METHODS - METHODS - METHODS - METHODS - METHODS - METHODS
//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	
	public void economicDecisions() {
		
	}
	
	public void handleDivorce() {
		adults.clear();
		children.clear();
		householdMembers = 0;
		Model.households_justremoved.add(this);
		HouseholdCount = HouseholdCount - 1;
	}
	
	public void handleMarriage() {
		adults.clear();
		children.clear();
		householdMembers = 0;
		Model.households_justremoved.add(this);
		HouseholdCount = HouseholdCount - 1;
	}
	
	public void returnToSingleHouseholdAfterDivorce(int pid) {
		adults.add(Model.personsAll.get(pid));
		if(adults.get(0).sex == Sex.FEMALE) {
			children.addAll(Model.personsAll.get(pid).children);
		}
	}
	
	public void handleDeath(Person person) {
		
		if(person.status == Status.DEPENDENTCHILD) {children.remove(person);}
		else {adults.remove(person);}

		householdMembers = adults.size() + children.size();
		if(householdMembers == 0) {
			Model.households_justremoved.add(this);
			HouseholdCount = HouseholdCount - 1;
		}

	}
	
	
	
	
	
}


/*	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	
	static public class Config {

		// --- Household structure in 2013. Source: ONS Families and households 2013
		public double POnePersonHousehold = 0.290; // Probability of a household being one person in 2013. Source: ONS families and households 2013
		public double PCouple = 0.562;
		public double PLoneParent = 0.105;
		public double PMultipleOccupation = 0.043; // multiple families and multiple unrelated adults
		

		public static double PFemale = 100.0/205.1; // Probability of being male given that you were born in the UK 2007-2011 (Source: Birth ratios in the UK, 2013, Dept of health)
		public double PFemaleGivenOnePersonHousehold = 0.537;
		public double PFemaleGivenLoneParent = 0.872;
		public double PFemaleGivenMultipleOccupation =
				(PFemale 
						- PFemaleGivenOnePersonHousehold * POnePersonHousehold 
						- PFemaleGivenLoneParent * PLoneParent
						- 0.5 * PCouple)/PMultipleOccupation; // N.B. Gay couples are roughly equal male/female Source: ONS Civil partnership statistics 2012

		public SampledFunction AgeGivenMaleOnePersonHousehold = new SampledFunction(new Double [][] {
				{16.0, 0.0},
				{25.0, 0.0296},
				{45.0, 0.2762},
				{65.0, 0.3684},
				{75.0, 0.1516},
				{100.0, 0.1742}
		});
		public SampledFunction AgeGivenFemaleOnePersonHousehold = new SampledFunction(new Double [][] {
				{16.0, 0.0},
				{25.0, 0.0214},
				{45.0, 0.1181},
				{65.0, 0.2753},
				{75.0, 0.2169},
				{100.0,0.3683}
		});
		public double [] PChildrenGivenCouple = {0.496 + 0.119, 0.163, 0.163, 0.059}; // number of dependent children given that the household is a copule. Source ONS Families and households 2013
		// N.B. the two figures for zero children are (no children) + (children but none dependent), 3 children represents 3-or-more 
		public double [] PChildrenGivenLoneParent = {0.307, 0.403, 0.213, 0.077}; // number of DEPENDENT children, starting from zero

		// --- getters and setters for MASON tweaking
		public static double getPFemale() {
			return PFemale;
		}
		public static void setPFemale(double pFemale) {
			PFemale = pFemale;
		}
	}
	
	public LifecycleHousehold() {
		
	}
	
	public void step() {
		for(Person p : people) {
			p.step();
		}		
	}
		
	ArrayList<Person> 		people = new ArrayList<Person>(); // ages in Months of people in the household
	ArrayList<Person> 		will = new ArrayList<Person>(); // beneficiaries on the last will and testament
	
	
}
*/