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
	public ArrayList<Person> adults 			= new ArrayList<Person>();
	public ArrayList<Person> dependentChildren  = new ArrayList<Person>();
	
	
	// Class Characteristics
	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	 /** Number of active households **/
	public static int HouseholdCount = 0;
	
	/** Number of all households who have ever existed. **/
	public static int HIDCount = 0;
	

// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
//			CONSTRUCTOR - CONSTRUCTOR - CONSTRUCTOR - CONSTRUCTOR - CONSTRUCTOR - CONSTRUCTOR
//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	
	// Constructor for single households
	/**
	 * This method constructs a new household with one adult member. The household receives a unique and invariant household ID (HID) and subsequently 
	 * both the household counter as well as the HID counter are incremented by one. The person is added to the adults list and his current hid and
	 * unique singleHID are set equal to household ID. (The singleHID will not be changed after this). If the person constituting the household is
	 * female, her dependent children are added to the household's list of dependent children.
	 * Finally, the household is added to the list of active households (Model.households)
	 * @param person Adult member of the household
	 */
	public Household(Person person) {
		// set household id and count number of households
		HID = HIDCount;
		HouseholdCount++;
		HIDCount++;
		adults.add(person);
		adults.get(0).singleHID = HID;
		adults.get(0).hid = HID;
		
		if(person.sex == Sex.FEMALE) bringDependentChildren(person);
		Model.households.add(this);

	}

	// Constructor for marriage households 
	/**
	 * This constructer is called in the marriage process and creates a new household with two adult members (husband and wife).
	 * The household's unique HID becomes the adult members' current hid. Their singleHID remains unchanged for the case of death of the partner or divorce.
	 * The dependent children of the wife are added to the household's list of dependent children. 
	 * Finally, the household is added to the list of active households (Model.households)
	 * @param husband Male PA of married couple that creates the household
	 * @param wife Female PA of married couple that creates the household
	 */
	public Household(Person husband, Person wife) {
		// set household id and count number of households
		HID = HIDCount;
		HouseholdCount++;
		HIDCount++;
		adults.add(husband);
		adults.add(wife);
		adults.get(0).hid = HID;
		adults.get(1).hid = HID;
		
		bringDependentChildren(wife);
		bringDependentChildrenFather(husband);
		Model.households.add(this);
	}
	
	

	
// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
//					METHODS - METHODS - METHODS - METHODS - METHODS - METHODS
//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	
	/**
	 * This method executes the different actions/methods a household takes each period.
	 */
	public void step() {

	}

	/**
	 * This method turns an active household into a passive one. That is, removes the household from Model.households, 
	 * and removes all members from the adults and dependentChildren lists. In addition, the household counter is reduced by one.
	 */
	public void makePassive() {
		adults.clear();
		dependentChildren.clear();
		//System.out.println("exists? " + Model.households.indexOf(this));
		//System.out.println("exists? " + Model.householdsAll.indexOf(this));
		Model.households.remove(this);
		HouseholdCount = HouseholdCount - 1;
	}
	
	/**
	 * This method activates the single household of a PA who has been married until this period. 
	 * The PA is added to the adult list and the dependent children of females are added to the household's list of dependent children.
	 * The household is added to the Model.household list of active households and the counter is increased by one.
	 * @param person Person agent whose marriage just ended
	 */
	public void returnToSingleHousehold(Person person) {
		adults.add(person);
		person.hid = HID;
		if(person.hid != person.singleHID) {System.out.println("ERROR: hid != singleHID");}
		if(adults.get(0).sex == Sex.FEMALE)	bringDependentChildren(person);
		if(adults.get(0).sex == Sex.MALE)	bringDependentChildrenFather(person);
		Model.households.add(this);
		HouseholdCount++;
	}
	
	/**
	 * This method adjusts all necessary household variables and lists when the PA person dies.
	 * The PA is removed from the relevant list and if the number of adults is zero, the makePassive() method is called.
	 * If the household is made passive while there are still dependent children living in the household, they currently do not
	 * receive a new household. They show back up when they turn 18 and get their own single household. This needs work...
	 * @param person Person agent who just died
	 */
	public void handleDeath(Person person) {

		if(person.status == Status.DEPENDENTCHILD) {
			dependentChildren.remove(person);
		}
		else {
			makePassive();
		}
	}
	
	public void checkCounter() {	
		if(Model.households.size() != Household.HouseholdCount) {
			System.out.println("Problem in household");
			Model.households.get(1000000);
		}
	}
	
	/**
	 * This method adds the dependent children of the PA mother to the mother's (new) household. They are added to the household's dependent
	 * children list and their hid is adjusted.
	 * @param mother
	 */
	public void bringDependentChildren(Person mother) {
		dependentChildren.addAll(mother.dependentChildren);
		for(Person p : dependentChildren) {
			p.hid = HID;
		}
	}
	
	public void bringDependentChildrenFather(Person father) {
		dependentChildren.addAll(father.dependentChildrenMotherDead);
		for(Person p : dependentChildren) {
			p.hid = HID;
		}
	}
	
}

