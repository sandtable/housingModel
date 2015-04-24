package housing;

import java.util.ArrayList;
import java.util.Random;

public class Person {
	

	
	// Person Characteristics 
	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	
	// identifiers
	public final int PID;
	public int singleHID;
	public int hid;
	
	// socio-economic variables
	public boolean dead = false;
	public double age;
	public int agegroup; // 19 agegroups: 0-4 (=0), 5-9 (=1), 10-14 (=2), ..., 80-84(=16), 85-89(=17), 90+(=18)
	public double income;
	enum Sex {MALE, FEMALE}
	public Sex sex;
	enum Status {DEPENDENTCHILD, SINGLE, MARRIED, DIVORCED, WIDOWED}
	public Status status;
	public boolean marryThisPeriod = false;
	public boolean dieThisPeriod = false;
	public int motherPID;
	public int fatherPID;	
	public int partnerPID;
	public int ageDifferencePartner;
	public double ageAtMarriage;
	
	// Lists
	public ArrayList<Person> mother 			= new ArrayList<Person>();
	public ArrayList<Person> partner 			= new ArrayList<Person>();
	public ArrayList<Person> previousPartners	= new ArrayList<Person>();
	public ArrayList<Person> children 			= new ArrayList<Person>();
	public ArrayList<Person> dependentChildren 	= new ArrayList<Person>();

	
	// Class Characteristics
	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	public static int PersonCount = 0; // keeps track of the number of living people
	public static int PIDCount = 0; // // for unique PIDs, number of all people (including the dead) (first person: PID = 1)

	// Class Parameters
	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	public static int LifecycleFreq = 1;
	public static double PMale = 105.1 / 205.1;


	// Person Constructor/Initialization /////////////////////////////////////////////////
	/**
	 * This constructs a new person agent (PA) of the initial population. 
	 * It assigns a unique and invariant person ID (PID) and subsequently increments PIDCount 
	 * and PersonCount by one unit. 
	 * It also determines the PA's sex, initial age (and agegroup) and initial status to match ONS data. 
	 * This is done using the methods determineSex(), initialAge() (plus determineAgeGroup()) 
	 * as well as initialStatus(). All unmarried females above 15 years of age (16 is the legal marriage limit) 
	 * are assigned to an age group dependent list (orderInitialSingleFemales()) 
	 * which is used to set up initial marriages in setUpInitialMarriages().
	 */
	public Person() {
		PID = PIDCount;
		PIDCount++;		
		PersonCount++; 	
		sex = determineSex();
		age = initialAge();
		agegroup = determineAgeGroup();
		status = initialStatus();
		if(sex == Sex.FEMALE & status != Status.MARRIED & age >= 16) orderInitialSingleFemales();
		Model.householdsAll.add(new Household(this));
		//System.out.println("Person created -  PID: " + PID + ", hid: " + hid);
	}

	// Person Constructor/for newborn children  /////////////////////////////////////////////////
	/**
	 * This constructor is used for all newborn PAs and takes the PID and current hid of the mother as inputs. 
	 * Apart from assigning a PID and incrementing the PIDCount and PersonCount static variables, the new PA is added to the 
	 * list of dependent children of the mother's household and the mother is added to the PA's mother list 
	 * (a list that never has more than one element). The status is set to Status.DEPENDENTCHILD and determineSex() determines the sex.
	 * 
	 * @param motherhid The HID of the household that the PA's mother currently belongs to
	 * @param motherPID The PID of the PA who has given birth to this PA
	 */
	public Person(int motherhid, int motherPID/*, int fatherPID*/) {
		PID = PIDCount;
		PIDCount++;		
		PersonCount++; 	
		this.hid = motherhid;
		mother.add(Model.personsAll.get(motherPID));
		Model.householdsAll.get(motherhid).dependentChildren.add(this);
		
		this.motherPID = motherPID;
		//this.fatherPID = fatherPID;
		sex = determineSex();
		age = 0;
		status = Status.DEPENDENTCHILD;

		//System.out.println("Person " + PID + " was born; Sex: " + sex);
	}
	

	
// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
//						METHODS - METHODS - METHODS - METHODS - METHODS - METHODS
//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	
	// Determine MARITAL STATUS of members of initial population
	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	
	// Step 1: all females single, males according to observed distribution
	/**
	 * This method determines the status of the PAs in the initial population. The status of all females and 
	 * males under 16 years of age is set to single.
	 * Males who are 16 or older (16 is the legal marriage limit) are either single, married, widowed or divorced 
	 * with a probability for each status taken from CumProbInitialStatusMale (see below).
	 * @return The PA's status
	 */
	public Status initialStatus() {
		Status initialStatus = Status.SINGLE;
		if(sex == Sex.MALE & age < 16) {initialStatus = Status.SINGLE;}
		else if(sex == Sex.FEMALE) {
			initialStatus = Status.SINGLE; // needs to be better...divorces/widows
		}
		else if(sex == Sex.MALE & age >= 16) {
			for(int i = 3; i < 19; i++) {
				double random = new Random().nextDouble(); 
				if(agegroup == i) {
					if(random >= 0 & random < CumProbInitialStatusMale[i][0]) 									 {initialStatus = Status.SINGLE;} 
					if(random >= CumProbInitialStatusMale[i][0] & random < CumProbInitialStatusMale[i][1]) 		 {initialStatus = Status.MARRIED;}
					if(random >= CumProbInitialStatusMale[i][1] & random < CumProbInitialStatusMale[i][2]) 		 {initialStatus = Status.WIDOWED;} 
					if(random >= CumProbInitialStatusMale[i][2] & random < 1) 									 {initialStatus = Status.DIVORCED;} 
					break;
				}	
			}
		}
		return initialStatus;
	}
	
	// Step 2: Find wives for married males
	/**
	 * This method finds a wife and manages the marriage process for male PAs of the initial population 
	 * whose status was set to married by the initialStatus()-method. 
	 * The only difference to the marry()-method lies in the determination of the wife's age group. 
	 * For the initial population, the wife is selected from the husband's age group.
	 * The reason for this is the fact that there is only data for the age of the bride given the age of the bridegroom at marriage, 
	 * but for the initial population, we would need data on the age of the wife given that the male is married. I have yet to find these
	 * data and hence opted for the above-mentioned rule of thumb. (The average age difference between husband in wife is between 2 and 3)
	 */
	public void setUpInitialMarriages() {
		if(sex == Sex.MALE & status == Status.MARRIED) {
			int col = 99;
			if(age >= 16 & age < 90) {col = agegroup - 3;}
			if(age >= 90) {col = 14;}
			
			partner.add(selectWife(col)); // contrary to general process: choose wife from same agegroup! (empirically, mean age difference betw. 2 and 3 years.)
			partnerPID = partner.get(0).PID;

			Model.householdsAll.add(new Household(this, partner.get(0)));
			handleMarriageHusband(col);
			partner.get(0).handleMarriageWife(PID, col);
			Model.MarriageCount++;

		}
	}

	
	// Step Method
	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	/**
	 * This method executes the different life cycle events.
	 * First, die() determines whether a PA dies this period. If not, age is increased and the age group adjusted. 
	 * Children who just turned 18 leave the home of their parents.
	 * Unmarried females are put on age-group-specific lists from which they may be chosen in the case of marriage. 
	 * Dependent children females are only put on one of these lists if they are at least 16 years old.
	 * Married couples may split up and new marriages take place.
	 * Women may give birth to children
	 */
	public void step() {
		
		//System.out.println("Step() begins for Person " + PID);
		
		die();
		if(dead == false) {
			age = age +  (1.0 / LifecycleFreq);
			agegroup = determineAgeGroup();
			
			if(age == 18 & status == Status.DEPENDENTCHILD) leaveParentalHome();
			
			if(sex == Sex.FEMALE & (status == Status.SINGLE | status == Status.DEPENDENTCHILD)) orderSingleFemales();
			
			if(status == Status.MARRIED) divorce(); 
			
			if(sex == Sex.MALE & age >= 16) marry(); // divorce and marriage in the same period plausible (month or year?)
			
			if(sex == Sex.FEMALE) giveBirth();
		}		
		
	}	

	
	// Determine SEX of new person
	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	/** 
	 * This method determines a PA's sex.
	 * @return The sex of the PA
	 */
	public Sex determineSex() {
		Sex sex;
		double random = new Random().nextDouble();
		if (random < PMale) {sex = Sex.MALE;} 
		else {sex = Sex.FEMALE;}
		return sex;
	}
	
	// Determine AGE of members of initial population
	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	/**
	 * This method assigns each PA of the initial population an age. The relevant probabilities depend on the PA's sex.
	 * @return Age of PA
	 */
	public int initialAge() {
		int initialAge = 0;
		double random_age = new Random().nextDouble();
		for(int i = 0; i < 18; i++) {
			int j = i+1;
			int min = i*5;
			if(random_age >= CumProbInitialAge[0][i] & random_age < CumProbInitialAge[0][j]) {
				initialAge = min + (int)(Math.random()*(5)); // random integer within the respective agegroup (uniform distribution within agegroups assumed)
			}
		}
		return initialAge;
	}
	
	
// Determining whether a female gives BIRTH (conditional on age)
//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
		// Source: ONS: Births: Characteristics of Mother 2, England and Wales, 2013
	/**
	 * This method determines whether a woman gives birth to a child (probabilities for giving birth are conditional on age and status).
	 * If a child is supposed to be born, a new PA is created and added to the list Model.persons_justborn as the program is currently 
	 * looping over the Model.person list. All newborn PAs are added to the latter list at the end of each step. 
	 * The child is also added to the mother's (and if the woman giving birth is married also to the father's) children and dependentChildren lists.
	 * 
	 * QUESTION: There should be a father in the case of a single mother, but difficult to find appropriate data.
	 */
	public void giveBirth() {
		boolean birthThisPeriod = false;
		int col;
		if(status == Status.MARRIED) {col = 0;} else {col = 1;}
		double random_birth = new Random().nextDouble();
		if(age >= 15 & age < 20 & random_birth 		< ProbBirth[0][col] / (LifecycleFreq)) {birthThisPeriod = true;}	
		else if(age >= 20 & age < 25 & random_birth < ProbBirth[1][col] / (LifecycleFreq)) {birthThisPeriod = true;}
		else if(age >= 25 & age < 29 & random_birth < ProbBirth[2][col] / (LifecycleFreq)) {birthThisPeriod = true;}
		else if(age >= 30 & age < 35 & random_birth < ProbBirth[3][col] / (LifecycleFreq)) {birthThisPeriod = true;}
		else if(age >= 35 & age < 40 & random_birth < ProbBirth[4][col] / (LifecycleFreq)) {birthThisPeriod = true;}
		else if(age >= 40 & age < 45 & random_birth < ProbBirth[5][col] / (LifecycleFreq)) {birthThisPeriod = true;}
		else if(age >= 45 & age < 50 & random_birth < ProbBirth[6][col] / (LifecycleFreq)) {birthThisPeriod = true;}
		// create child/new person. The members of Model.persons_justborn will be added to Model.persons at the end of Model.step()
		if(birthThisPeriod == true) {
			Model.persons_justborn.add(new Person(hid, PID/*, partnerPID*/)); // auxiliary list as we are looping over Model.persons
			children.add(Model.persons_justborn.get(Model.persons_justborn.size()-1));
			dependentChildren.add(Model.persons_justborn.get(Model.persons_justborn.size()-1));
			if(status == Status.MARRIED) {
				partner.get(0).children.add(children.get(children.size()-1));
				partner.get(0).dependentChildren.add(dependentChildren.get(dependentChildren.size()-1));
				children.get(children.size()-1).fatherPID = partnerPID;
			}
			//System.out.println("New child was born!");
		}
	}
	
// Determining whether DEATH (conditional on age, sex)
//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
		// Source: ONS: Mortality Statistics: Deaths Registered in 2013 (Series DR) Tables 1–4 and Tables 6–14 (Excel sheet 988Kb)
		// http://wwhw.ons.gov.uk/ons/rel/vsob1/mortality-statistics--deaths-registered-in-england-and-wales--series-dr-/2013/dr-tables-2013.xls

	/**
	 * This method determines whether a PA dies and if this is the case, makes the relevant adjustments in handleDeath().
	 */
	public void die() {
		if(sex == Sex.MALE) {
			dead = determineIfDeath(ProbDeath[agegroup][0]/LifecycleFreq);
		}
		else {
			dead = determineIfDeath(ProbDeath[agegroup][1]/LifecycleFreq);
		}
		if(dead == true) {
			//System.out.println("Person " + PID + " died at age " + age + ".");
			handleDeath();
		}
	}
	
	// determineIfDeath()
	/**
	 * This is an auxiliary method for die() which uses age and sex dependent probabilities of death to determine whether a PA has to die this period.
	 * @param prob age and sex dependent probability of death
	 * @return True if person dies this period, false if not.
	 */
	public boolean determineIfDeath(double prob) {
		boolean dieThisPeriod;
		double random_death = new Random().nextDouble();
		if(random_death < prob) {dieThisPeriod = true;}
		else {dieThisPeriod = false;}
		return dieThisPeriod;
	}
	
	// handleDeath()
	/**
	 * This method handles the necessary adjustment for a PA that dies. The PA is added to the Model.persons_justremoved list whose elements
	 * removed from the Model.persons list after the completion of the step for all PAs. Note that the PA still exists with dead = true 
	 * on the Model.personsAll list.
	 * In addition, the method Household.handleDeath() is called to take care of the household-level adjustments.
	 * If the dying person was married, the method handleDeathPartner() is called for the partner which becomes a widow and returns to its former
	 * single hid. 
	 * If the dying person had dependent children and was not married, the children become orphans ... TO DO: assign to father (if father available for all kids)
	 * Finally, unmarried females are removed from the single female lists used for marriages.
	 */
	public void handleDeath() {
		Model.persons_justdied.add(this);
		Person.PersonCount = Person.PersonCount - 1;
		Model.householdsAll.get(hid).handleDeath(this);
		if(status == Status.MARRIED){
			Model.MarriageCount = Model.MarriageCount - 1;
			partner.get(0).handleDeathPartner();
		}
		else {
			Model.orphans.addAll(dependentChildren);
		}
		if(sex == Sex.FEMALE & status != Status.MARRIED) {
			for(int i = 0; i<15; i++) {
				Model.females_by_agegroup.get(i).remove(this);
			}
		}
	}
	
	// handleDeathPartner()
	public void handleDeathPartner() {
		status = Status.WIDOWED;
		previousPartners.add(partner.get(0));
		partner.clear();
		Model.householdsAll.get(hid).makePassive();
		Model.householdsAll.get(singleHID).returnToSingleHousehold(this);
	}
	
	
// LEAVING THE PARENTAL HOME
//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	
	/**
	 * This method turns a dependent child into a single. Thereby, a new single household is set up.
	 */
	public void leaveParentalHome() {
		Model.householdsAll.get(hid).dependentChildren.remove(this);
		Model.personsAll.get(motherPID).dependentChildren.remove(this);
		//Model.personsAll.get(fatherPID).dependentChildren.remove(this); // fatherPID of children born to lone mothers?
		status = Status.SINGLE;
		
		Model.householdsAll.add(new Household(this));
	}
	
	
// MARRIAGE
//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	
	// Determining whether MARRIAGE (conditional on age, sex and previous marital status)
	/**
	 * This method first decides whether a PA (only males --> see step) is getting married this period and if that is the case,
	 * finds a suitable wife from the female single list (age group of wife is also determined probabilistically) and
	 * handles the marriage process which includes the adjustment of PA characteristics for both partners as well as the creation of a new household.
	 * If either the husband or the wife is still a dependent child living in the household of their parents, the leaveParentalHome() method is
	 * executed first to ensure that every partner has a single household to return to when the partner dies for the case of divorce.
	 */
	public void marry() {
		int col = 99;
		marryThisPeriod = false;	

		if(status == Status.SINGLE | status == Status.DEPENDENTCHILD) 	determineIfMarriage(ProbMarriageMale[agegroup][0]);
		else if(status == Status.WIDOWED) 								determineIfMarriage(ProbMarriageMale[agegroup][1]);
		else if(status == Status.DIVORCED) 								determineIfMarriage(ProbMarriageMale[agegroup][2]);
		
		if(marryThisPeriod == true) {
			if(age >= 16 & age < 90) {col = agegroup - 3;}
			else if(age >= 90) {col = 14;}
			int ageGroupWife = determineAgeGroupOfWife(col);
			
			partner.add(selectWife(ageGroupWife));
			partnerPID = partner.get(0).PID;
			
			if(status == Status.DEPENDENTCHILD) leaveParentalHome();
			if(partner.get(0).status == Status.DEPENDENTCHILD) partner.get(0).leaveParentalHome();
			
			Model.householdsAll.add(new Household(this, partner.get(0)));
			handleMarriageHusband(ageGroupWife);
			partner.get(0).handleMarriageWife(PID, ageGroupWife);
			Model.MarriageCount++;
			if(age < 18 | partner.get(0).age < 18) {Model.DependentChildMarriageCount++;}
		}
	}
	
	// determineIfMarriage()
	/**
	 * [auxiliary method for marry()] This method determines whether a PA will get married. The probability for that depends on age and
	 * previous marital status.
	 * @param prob Probability of marriage given age and previous marital status
	 */
	public void determineIfMarriage(double prob) {
		marryThisPeriod = false;
		double random_marriage = new Random().nextDouble();
		if(random_marriage < prob/LifecycleFreq) {
			marryThisPeriod = true;
		}
	}
	
	// determineAgeGroupOfWife()
	/**
	 * [auxiliary method for marry()] This method determines the age group of the future wife given that the male PA is getting married.
	 * The probability distribution for the age of the bride depends on the age group of the husband
	 * @param col age bracket of the husband: 0 = under 20; 1 = 20-24, 2 = 25-29; ...; 13 = 80-84; 14 = 85+
	 * @return age bracket from which wife will be selected
	 */
	public int determineAgeGroupOfWife(int col) {
		double random_ageWife = new Random().nextDouble();
		int ageGroupWife = 0;
		if(random_ageWife >= 0 & random_ageWife < CumProbAgeWife[0][col]) {ageGroupWife=0;}	
		for(int i = 0; i<14; i++) {
			if(random_ageWife >= CumProbAgeWife[i][col] & random_ageWife < CumProbAgeWife[i+1][col]) {
				ageGroupWife= i+1;
				 break;
			}
		}
		if(random_ageWife >= CumProbAgeWife[14][col] & random_ageWife < 1)  {ageGroupWife=14;}	
		return ageGroupWife;
	}

	
	// selectWife()
	/**
	 * [auxiliary method for marry()] This method randomly selects a single female of the list corresponding to the age group of the wife
	 * as determined by determineAgeWife().
	 * @param ageGroupWife Age group of the future wife which represents the sublist in Model.females_by_agegroup
	 * @return the wife of the male PA who marries this period.
	 */
	public Person selectWife(int ageGroupWife) {
		Person wife;
		if(Model.females_by_agegroup.get(ageGroupWife).size() == 0) { // needs more work for small samples
			System.out.println("Marriage problem: no single females in the selected age bracket to choose from");
			wife = null;
		}
		else {
			int random = new Random().nextInt(Model.females_by_agegroup.get(ageGroupWife).size());
			wife = Model.females_by_agegroup.get(ageGroupWife).get(random);
		}
		return wife;
	}
	
	// handleMarriageHusband()
	/**
	 * [auxiliary method for marry()] This method adjusts the status of the husband to married and initiates the ageDifferencePartner variable.
	 * Most importantly, the husband's single household is made inactive.
	 * @param ageGroupWife
	 */
	public void handleMarriageHusband(int ageGroupWife) {
		ageDifferencePartner = (int)(age - partner.get(0).age);
		status = Status.MARRIED;
		Model.householdsAll.get(singleHID).makePassive();
	}
	
	// handleMarriageWife()
	/**
	 * [auxiliary method for marry()] This method stores the PID of the husband as the partnerPID and adds him to the partner list. 
	 * It also adjusts the status of the wife to married and initiates the ageDifferencePartner variable.
	 * Most importantly, the wife's single household is made inactive.
	 * @param partnerPID
	 * @param ageGroup
	 */
	public void handleMarriageWife(int partnerPID, int ageGroup) {
		this.partnerPID = partnerPID;
		partner.add(Model.personsAll.get(partnerPID));
		
		ageDifferencePartner = (int)(age - partner.get(0).age);
		status = Status.MARRIED;
		
		Model.females_by_agegroup.get(ageGroup).remove(this);
		Model.householdsAll.get(singleHID).makePassive();
	}
	
//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	
	// DIVORCE
	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	
	// divorce()
	/**
	 * This method decides whether a male PA divorces this period and, if yes, handles the adjustments.
	 * The probability of divorce depends on the marriage duration. In the case of divorce, the marriage counter is reduced
	 * and both partners return to their single households.
	 */
	public void divorce() {
		boolean divorceThisPeriod = false;
		if(sex == Sex.MALE) {
			int marriageDuration = (int)(age - ageAtMarriage);
			double random_divorce = new Random().nextDouble();
			if(random_divorce < ProbDivorceGivenAnniversary[0][Math.min(marriageDuration, 59)]) {divorceThisPeriod = true;}
			if(divorceThisPeriod == true) {
				Model.householdsAll.get(hid).makePassive();
				partner.get(0).handleDivorce();
				handleDivorce();
				Model.MarriageCount = Model.MarriageCount - 1;
			}
		}
	}
	// handleDivorce()
	/**
	 * [auxiliary method for divorce()] This method adjusts the characteristics of both the husband and wife after divorce.
	 * The status is set to divorced and the partner is moved to the previousPartners list.
	 */
	public void handleDivorce() {
		Model.householdsAll.get(singleHID).returnToSingleHousehold(this);
		status = Status.DIVORCED;
		previousPartners.add(partner.get(0));
		partner.clear();
		partnerPID = 0;
	}
	
	
	// AUXILIARY METHODS
	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	
	// determineAgeGroup()
	/**
	 * This method determines a PA's age group. The following 19 age groups exist: 0-4, 5-9, 10-14, ..., 85-89, 90+
	 * @return Age group: integer between 0 and 18
	 */
	public int determineAgeGroup() {
		int agegroup = this.agegroup;
		for(int i = 0; i < 18; i++) {
			if(age >= i*5 & age < i*5+5) {agegroup = i;}
		}
		if(age >= 90) {agegroup = 18;}
		return agegroup;
	}


	// orderSingleFemales(): put single females in corresponding age group list --> needed for assignment of wives
	/**
	 * This method assigns single females to the correct age-specific list.
	 */
	public void orderSingleFemales() {
		if(age == 16) {Model.females_by_agegroup.get(0).add(this);}
		else if(age == 20) {Model.females_by_agegroup.get(0).remove(this);Model.females_by_agegroup.get(1).add(this);}
		else if(age == 25) {Model.females_by_agegroup.get(1).remove(this);Model.females_by_agegroup.get(2).add(this);}
		else if(age == 30) {Model.females_by_agegroup.get(2).remove(this);Model.females_by_agegroup.get(3).add(this);}
		else if(age == 35) {Model.females_by_agegroup.get(3).remove(this);Model.females_by_agegroup.get(4).add(this);}
		else if(age == 40) {Model.females_by_agegroup.get(4).remove(this);Model.females_by_agegroup.get(5).add(this);}
		else if(age == 45) {Model.females_by_agegroup.get(5).remove(this);Model.females_by_agegroup.get(6).add(this);}
		else if(age == 50) {Model.females_by_agegroup.get(6).remove(this);Model.females_by_agegroup.get(7).add(this);}
		else if(age == 55) {Model.females_by_agegroup.get(7).remove(this);Model.females_by_agegroup.get(8).add(this);}
		else if(age == 60) {Model.females_by_agegroup.get(8).remove(this);Model.females_by_agegroup.get(9).add(this);}
		else if(age == 65) {Model.females_by_agegroup.get(9).remove(this);Model.females_by_agegroup.get(10).add(this);}
		else if(age == 70) {Model.females_by_agegroup.get(10).remove(this);Model.females_by_agegroup.get(11).add(this);}
		else if(age == 75) {Model.females_by_agegroup.get(11).remove(this);Model.females_by_agegroup.get(12).add(this);}
		else if(age == 80) {Model.females_by_agegroup.get(12).remove(this);Model.females_by_agegroup.get(13).add(this);}
		else if(age == 85) {Model.females_by_agegroup.get(13).remove(this);Model.females_by_agegroup.get(14).add(this);}
	}
	
	// orderInitialSingleFemales(): put INITIAL single females in corresponding age group list --> needed for assignment of wives
	/**
	 * This method assigns single females of the initial population to the correct age-specific list.
	 */
	public void orderInitialSingleFemales() {
		if(agegroup == 3) {Model.females_by_agegroup.get(0).add(this);}
		else if(agegroup == 4) {Model.females_by_agegroup.get(1).add(this);}
		else if(agegroup == 5) {Model.females_by_agegroup.get(2).add(this);}
		else if(agegroup == 6) {Model.females_by_agegroup.get(3).add(this);}
		else if(agegroup == 7) {Model.females_by_agegroup.get(4).add(this);}
		else if(agegroup == 8) {Model.females_by_agegroup.get(5).add(this);}
		else if(agegroup == 9) {Model.females_by_agegroup.get(6).add(this);}
		else if(agegroup == 10) {Model.females_by_agegroup.get(7).add(this);}
		else if(agegroup == 11) {Model.females_by_agegroup.get(8).add(this);}
		else if(agegroup == 12) {Model.females_by_agegroup.get(9).add(this);}
		else if(agegroup == 13) {Model.females_by_agegroup.get(10).add(this);}
		else if(agegroup == 14) {Model.females_by_agegroup.get(11).add(this);}
		else if(agegroup == 15) {Model.females_by_agegroup.get(12).add(this);}
		else if(agegroup == 16) {Model.females_by_agegroup.get(13).add(this);}
		else if(agegroup >= 17) {Model.females_by_agegroup.get(14).add(this);}
	}
	

	
	
// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
// 		PROBABILITIES - PROBABILITIES - PROBABILITIES - PROBABILITIES - PROBABILITIES - PROBABILITIES
// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	
	
	
	// Probabilities of giving birth (conditional on age and marital Status) (7 rows, 2 columns)
	// SOURCE: ONS: Births: Characteristics of Mother 2, England and Wales, 2013 (2012 data)
	// rows stand for age brackets: Under 20, 20-24, 25-29, 30-34, 35-39, 40-44, 45+
	// column 0 = married; column 1 = not married
	/**
	 * Probabilities of giving birth (conditional on age and marital Status) (7 rows, 2 columns). 
	 * Rows = age brackets {under 20, 20-24, 25-29, 30-34, 35-39, 40-44, 45+}; 
	 * Columns = {married, not married}
	 */
	public static final double [][] ProbBirth = {
		{0.2232, 0.0190},
		{0.2749, 0.0579},
		{0.2290, 0.0657},
		{0.1806, 0.0659},
		{0.0762, 0.0438},
		{0.0139, 0.0130},
		{0.0009, 0.0010}
	};
	
	
	// Probabilities of dying this year depending on age and sex (19 rows, 2 columns)
	// column 1: males, column 2: females
	// rows stand for agegroups
	/**
	 * Probabilities of dying this year depending on age and sex (19 rows, 2 columns)
	 * Rows = {0-4, 5-9, ..., 85-89, 90+}
	 * Columns = {male, female}
	 */
	public static final double [][] ProbDeath = { 
		//males	  females
		{0.00101, 0.00081}, // 0-4
		{0.00009, 0.00007}, // 5-9
		{0.00010, 0.00009}, // 10-14
		{0.00030, 0.00014}, // ...
		{0.00046, 0.00021}, 
		{0.00060, 0.00029}, 
		{0.00079, 0.00043},
		{0.00119, 0.00066}, 
		{0.00172, 0.00103}, 
		{0.00248, 0.00156},
		{0.00368, 0.00248}, 
		{0.00592, 0.00396}, 
		{0.00172, 0.00614}, 
		{0.00248, 0.00944},
		{0.02448, 0.01605}, // 70-74 
		{0.04074, 0.02809}, // 75-79 
		{0.07318, 0.05326}, // 80-84 
		{0.12938, 0.10041}, // 85-89
		{0.23828, 0.21012}, // 90+ 
		
	};
	
	// Probabilities of getting married depending on age and previous marital status for MALES (19 rows, 3 columns)
	/**
	 * Probabilities of getting married depending on age and previous marital status for MALES (19 rows, 3 columns)
	 * Rows = {0-4, 5-9, ..., 85-89, 90+}
	 * Columns = {single, widowed, divorced}
	 */
	public static final double [][] ProbMarriageMale = { 
		// rows stand for age groups
		// column 1: single; column 2: widowed; column 3: divorced
		{0.0000, 0.0000, 0.0000}, // 0-4
		{0.0000, 0.0000, 0.0000}, // 5-9
		{0.0000, 0.0000, 0.0000}, // 10-14
		{0.0006, 0.0000, 0.0000}, // 15-19
		{0.0103, 0.0109, 0.0593}, // 20-24
		{0.0383, 0.0209, 0.0599}, // 25-29
		{0.0498, 0.0353, 0.0677}, // 30-34
		{0.0361, 0.0412, 0.0548}, // 35-39
		{0.0221, 0.0412, 0.0548}, // 40-44
		{0.0131, 0.0267, 0.0346}, // 45-49
		{0.0081, 0.0267, 0.0346}, // 50-54
		{0.0033, 0.0052, 0.0154}, // 55-59
		{0.0033, 0.0052, 0.0154}, // 60-64
		{0.0033, 0.0052, 0.0154}, // 65-69
		{0.0033, 0.0052, 0.0154}, // 70-74
		{0.0033, 0.0052, 0.0154}, // 75-79
		{0.0033, 0.0052, 0.0154}, // 80-84
		{0.0033, 0.0052, 0.0154}, // 85-89
		{0.0033, 0.0052, 0.0154}  // 90+
	};
	
	// Probability of divorce in interval to next anniversary (1 row, 60 columns)
	/**
	 * Probability of divorce in interval to next anniversary (1 row, 60 columns)
	 * Columns  {1, 2, ..., 60}
	 */
	public static final double [][] ProbDivorceGivenAnniversary = { 
		// SOURCE: ONS: Age at marriage, duration of marriage and cohort analyses, 2011 (data from 2010)
		// Attention: this is only one row with 60 columns (0-59)
		// Columns stand for duration of marriage (column 0 --> before first anniversary)
		// Example 1: column 0 = Probability of divorce given that marriage has lasted for less than one year
		// Example 2: column 5 = Probability of divorce given that marriage has lasted for 5 years (6th anniversary coming up)
		{0.0001, 0.0081, 0.0188, 0.0267, 0.0305, 0.0316, 0.0325, 0.0324, 0.0291, 0.0263, 0.0239, 0.0226, 0.0218, 0.0204, 0.0190, 0.0176, 
			0.0172, 0.0154, 0.0152, 0.0148, 0.0143, 0.0134, 0.0124, 0.0121, 0.0106, 0.0103, 0.0096, 0.0082, 0.0073, 0.0069, 0.0065, 
			0.0057, 0.0048, 0.0044, 0.0037, 0.0042, 0.0038, 0.0025, 0.0023, 0.0023, 0.0018, 0.0014, 0.0012, 0.0011, 0.0009, 0.0007, 
			0.0007, 0.0005, 0.0005, 0.0004, 0.0002, 0.0003, 0.0003, 0.0002, 0.0001, 0.0001, 0.0001, 0.0001, 0.0001, 0.0000}

	};

		
	// Cumulative Probabilities of the bride having a certain age given marriage and age (15 rows, 15 columns)
	/**
	 * Cumulative Probabilities of the bride having a certain age given marriage and age (15 rows, 15 columns)
	 * Rows = {Age groups of wife: under 20,20-24,25-29, ... , 75-79,80-84,85+}
	 * Columns = {Age groups of husband: 20,20-24,25-29, ... , 75-79,80-84,85+}
	 */
	public static final double [][] CumProbAgeWife = { 
		// rows stand for age groups of bride (under 20,20-24,25-29, ... , 75-79,80-84,85+)
		// columns stand for age groups of bridegroom (under 20,20-24,25-29, ... , 75-79,80-84,85+)	
		// SOURCE: ONS: Age at Marriage and Previous Marital Status, 2011
		{0.6012,	0.0783,	0.0101,	0.0028,	0.0016,	0.0010,	0.0006,	0.0006,	0.0001,	0.0004,	0.0000,	0.0000,	0.0000,	0.0000,	0.0000},
		{0.9071,	0.7118,	0.2458,	0.0824,	0.0394,	0.0222,	0.0122,	0.0075,	0.0030,	0.0026,	0.0021,	0.0021,	0.0000,	0.0000,	0.0000},
		{0.9718,	0.9274,	0.8300,	0.4772,	0.2360,	0.1189,	0.0618,	0.0324,	0.0160,	0.0116,	0.0064,	0.0091,	0.0013,	0.0030,	0.0067},
		{0.9871,	0.9762,	0.9625,	0.8829,	0.5969,	0.3231,	0.1627,	0.0808,	0.0465,	0.0268,	0.0182,	0.0181,	0.0108,	0.0089,	0.0067},
		{0.9941,	0.9915,	0.9883,	0.9697,	0.8767,	0.6142,	0.3370,	0.1730,	0.0973,	0.0575,	0.0367,	0.0265,	0.0189,	0.0178,	0.0067},
		{0.9965,	0.9967,	0.9959,	0.9912,	0.9668,	0.8676,	0.6140,	0.3647,	0.2034,	0.1279,	0.0742,	0.0418,	0.0337,	0.0356,	0.0134},
		{0.9988,	0.9989,	0.9986,	0.9975,	0.9923,	0.9671,	0.8741,	0.6586,	0.4064,	0.2608,	0.1619,	0.0933,	0.0620,	0.0475,	0.0201},
		{1.0000,	0.9997,	0.9995,	0.9995,	0.9983,	0.9925,	0.9701,	0.9048,	0.6936,	0.4712,	0.3077,	0.1811,	0.1226,	0.0890,	0.0604},
		{1.0000,	0.9999,	0.9999,	0.9998,	0.9996,	0.9986,	0.9931,	0.9757,	0.9071,	0.7239,	0.5105,	0.3148,	0.2008,	0.1454,	0.1141},
		{1.0000,	0.9999,	1.0000,	1.0000,	0.9998,	0.9998,	0.9985,	0.9948,	0.9761,	0.9223,	0.7647,	0.5286,	0.3423,	0.2107,	0.1879},
		{1.0000,	0.9999,	1.0000,	1.0000,	0.9999,	0.9999,	0.9996,	0.9989,	0.9962,	0.9805,	0.9430,	0.7765,	0.5957,	0.3561,	0.2886},
		{1.0000,	0.9999,	1.0000,	1.0000,	0.9999,	0.9999,	0.9999,	0.9998,	0.9988,	0.9942,	0.9889,	0.9457,	0.8167,	0.5964,	0.5034},
		{1.0000,	0.9999,	1.0000,	1.0000,	0.9999,	1.0000,	0.9999,	0.9999,	0.9996,	0.9987,	0.9986,	0.9805,	0.9488,	0.8160,	0.7383},
		{1.0000,	0.9999,	1.0000,	1.0000,	0.9999,	1.0000,	1.0000,	0.9999,	0.9999,	0.9994,	0.9996,	0.9972,	0.9919,	0.9466,	0.8859},
		{1.0000,	0.9999,	1.0000,	1.0000,	0.9999,	1.0000,	1.0000,	1.0000,	0.9999,	1.0000,	1.0000,	1.0000,	1.0000,	1.0000,	1.0000}
	};
	
	// Initial Cumulative Probabilities of having a certain age (1 row, 19 columns)
	/**
	 * Initial Cumulative Probabilities of having a certain age (1 row, 19 columns)
	 * Columns = {0,0-4,5-9,10-14,15-20,20-24,25-29, ... , 75-79,80-84,85+}
	 */
	public static final double [][] CumProbInitialAge = { 
		// Source ?
		// columns 2-19 stand for age groups 0-4,5-9,10-14,15-19,20-24,25-29, ... ,75-79,80-84,85+)
		// Example: column 4 = 0.1773 = Prob(age < 14)
		{0.0000, 0.0631, 0.1221, 0.1773, 0.2378, 0.3049, 0.3730, 0.4408, 0.5029, 0.5731, 0.6458, 0.7131, 0.7711, 0.8261, 0.8806, 
			0.9201, 0.9526, 0.9769, 1.0000}

	};
	
	// Initial cumulative probabilities for marital status for MALES (19 rows, 4 columns)
	/**
	 * Initial cumulative probabilities for marital status for MALES (19 rows, 4 columns)
	 * Rows = {0-4,5-9,10-14,15-19,20-24,25-29, ... ,75-79,80-84,85-89,90+}
	 * Columns = {single, married, widowed, divorced}
	 */
	public static final double [][] CumProbInitialStatusMale = { 
		// rows 1-19 stand for age groups 0-4,5-9,10-14,15-19,20-24,25-29, ... ,75-79,80-84,85-89,90+)
		// SOURCE: ONS: Principal projection legal marital status: Summary of population by age & legal marital status
		
	//  Single		Married	Widowed	Divorced
		{1.0000,	1.0000,	1.0000,	1.0000},
		{1.0000,	1.0000,	1.0000,	1.0000},
		{1.0000,	1.0000,	1.0000,	1.0000},
		{0.9991,	1.0000,	1.0000,	1.0000},
		{0.9743,	0.9992,	0.9993,	1.0000},
		{0.8491,	0.9889,	0.9896,	1.0000},
		{0.6298,	0.9612,	0.9621,	1.0000},
		{0.4332,	0.9184,	0.9201,	1.0000},
		{0.3005,	0.8746,	0.8776,	1.0000},
		{0.2172,	0.8419,	0.8475,	1.0000},
		{0.1586,	0.8236,	0.8342,	1.0000},
		{0.1194,	0.8217,	0.8410,	1.0000},
		{0.0908,	0.8283,	0.8615,	1.0000},
		{0.0745,	0.8300,	0.8835,	1.0000},
		{0.0667,	0.8164,	0.9071,	1.0000},
		{0.0660,	0.7812,	0.9320,	1.0000},
		{0.0654,	0.7023,	0.9527,	1.0000},
		{0.0609,	0.5800,	0.9706,	1.0000},
		{0.1113,	0.4763,	0.9795,	1.0000}
	};

	
}

