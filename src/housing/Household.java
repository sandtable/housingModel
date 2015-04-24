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
	public static int HouseholdCount = 0;
	public static int HIDCount = 0;
	public static int HouseholdMarriedCount = 0;
	public static int HouseholdSingleCount = 0;


// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
//			CONSTRUCTOR - CONSTRUCTOR - CONSTRUCTOR - CONSTRUCTOR - CONSTRUCTOR - CONSTRUCTOR
//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	
	// Constructor for single households
	public Household(Person person) {
		// set household id and count number of households
		HID = HIDCount;
		HouseholdCount++;
		HIDCount++;
		adults.add(person);
		adults.get(0).singleHID = HID;
		adults.get(0).hid = HID;
		
		if(person.sex == Sex.FEMALE) bringChildren(person);
		householdMembers = adults.size() + dependentChildren.size();
		Model.households.add(this);

	}

	// Constructor for marriage households 
	public Household(Person husband, Person wife) {
		// set household id and count number of households
		HID = HIDCount;
		HouseholdCount++;
		HIDCount++;
		adults.add(husband);
		adults.add(wife);
		adults.get(0).hid = HID;
		adults.get(1).hid = HID;
		
		bringChildren(wife);
		householdMembers = adults.size() + dependentChildren.size();
		Model.households.add(this);
	}
	
	public void step() {

	}

	
// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
//					METHODS - METHODS - METHODS - METHODS - METHODS - METHODS
//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	
	public void economicDecisions() {
		
	}
	
	public void makePassive() {
		adults.clear();
		dependentChildren.clear();
		householdMembers = 0;
		Model.households.remove(this);
		HouseholdCount = HouseholdCount - 1;
	}
	
	public void returnToSingleHousehold(Person person) {
		adults.add(person);
		person.hid = HID;
		if(adults.get(0).sex == Sex.FEMALE) {
			bringChildren(person);
		}
		Model.households.add(this);
		HouseholdCount++;
	}
	
	public void handleDeath(Person person) {
		if(person.status == Status.DEPENDENTCHILD) {dependentChildren.remove(person);}
		else {adults.remove(person);}
		if(adults.size() == 0 & dependentChildren.size() > 0) {
			// what happens to orphans?
		}
		if(adults.size() == 0) makePassive();
	}
	
	public void bringChildren(Person mother) {
		dependentChildren.addAll(mother.dependentChildren);
		for(Person p : dependentChildren) {
			p.hid = HID;
		}
	}
	
	
}

