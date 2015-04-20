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
	enum MaritalStatus {SINGLE, COUPLE, MARRIED, DIVORCED, WIDOWED}
	public MaritalStatus maritalStatus;
	public boolean marryThisPeriod = false;
	public int motherPID;
	public int fatherPID;	
	public int partnerPID;
	public int ageDifferencePartner;
	
	// Lists
	public static ArrayList<Person> parents 	= new ArrayList<Person>();
	public static ArrayList<Person> partner 	= new ArrayList<Person>();
	public static ArrayList<Person> children 	= new ArrayList<Person>();

	
	
	// Class Characteristics
	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	public static int PersonCount = 0; // keeps track of the number of living people
	public static int PIDcount = 0; // // for unique PIDs, number of all people (including the dead) (first person: PID = 1)

	// Class Parameters
	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	public static int LifecycleFreq = 1;
	public static double PMale = 105.1 / 205.1;


	// Person Constructor/Initialization /////////////////////////////////////////////////
	public Person() {
		PID = PIDcount;
		PIDcount++;		
		PersonCount++; 	
		sex = determineSex();
		age = initialAge();
		agegroup = determineAgeGroup();
		maritalStatus = initialMaritalStatus();
		income = 0;
		if(sex == Sex.FEMALE & maritalStatus == MaritalStatus.SINGLE) orderInitialSingleFemales();
		//System.out.println("Person created -  ID " + PID + ", Sex " + sex + ", Age " + age + ", Status " + maritalStatus);
	}

	// Person Constructor/for newborn children  /////////////////////////////////////////////////
	public Person(int hid, int motherPID/*, int fatherPID*/) {
		PID = PIDcount;
		PIDcount++;		
		PersonCount++; 	
		this.motherPID = motherPID;
		//this.fatherPID = fatherPID;
		this.hid = hid;
		sex = determineSex();
		age = 0;
		maritalStatus = MaritalStatus.SINGLE;
		income = 0;
		//System.out.println("Person " + PID + " was born; Sex: " + sex);
	}
	
	
	
	
// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
//						METHODS - METHODS - METHODS - METHODS - METHODS - METHODS
//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	
	// Determine MARITAL STATUS of members of initial population
	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	
	// Step 1: all females single, males according to observed distribution
	public MaritalStatus initialMaritalStatus() {
		MaritalStatus initialMaritalStatus = MaritalStatus.SINGLE;
		if(sex == Sex.MALE) {
			for(int i = 0; i < 15; i++) {
				int min = i*5;
				int max = min + 5;
				double random = new Random().nextDouble(); 
				if(age >= min & age < max) {
					if(random >= 0 & random < CumProbInitialMaritalStatusMale[i][0]) 									 {initialMaritalStatus = MaritalStatus.SINGLE;} // random integer within the respective agegroup (uniform distribution within agegroups assumed)
					if(random >= CumProbInitialMaritalStatusMale[i][0] & random < CumProbInitialMaritalStatusMale[i][1]) {initialMaritalStatus = MaritalStatus.MARRIED;} // random integer within the respective agegroup (uniform distribution within agegroups assumed)
					if(random >= CumProbInitialMaritalStatusMale[i][1] & random < CumProbInitialMaritalStatusMale[i][2]) {initialMaritalStatus = MaritalStatus.WIDOWED;} // random integer within the respective agegroup (uniform distribution within agegroups assumed)
					if(random >= CumProbInitialMaritalStatusMale[i][2] & random < 1) 									 {initialMaritalStatus = MaritalStatus.DIVORCED;} // random integer within the respective agegroup (uniform distribution within agegroups assumed)
				}	
			}
		}
		if(sex == Sex.FEMALE) {
			initialMaritalStatus = MaritalStatus.SINGLE;
		}
		return initialMaritalStatus;
	}
	
	// Step 2: Find wives for married males
	public void setUpInitialMarriages() {
		int col = 99;
		if(sex == Sex.MALE & maritalStatus == MaritalStatus.MARRIED) {
			System.out.println();
			System.out.println("Begin Marriage " + PID);
			if(age >= 16 & age < 90) {col = agegroup - 3;}
			if(age >= 90) {col = 14;}
			partnerPID = selectWife(col); // contrary to general process: choose wife from same agegroup! (empirically, mean age difference betw. 2 and 3 years.)
			ageDifferencePartner = (int)(age - Model.personsAll.get(partnerPID).age);
			maritalStatus = MaritalStatus.MARRIED;
			Model.personsAll.get(partnerPID).justMarriedBride(PID, col);
			Model.MarriageCount++;
			
			// checks
			System.out.println("MARRIAGE: PID of Husband: " + PID + ", PID of Wife: " + partnerPID + ", Age Difference: " + ageDifferencePartner 
					+ ", agegroup wife: " + Model.personsAll.get(partnerPID).agegroup);
			System.out.println("End Marriage " + PID);
			System.out.println();
		}
	}

	
	// Step Method
	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	public void step() {
		
		
		age = age +  (1.0 / LifecycleFreq);
		agegroup = determineAgeGroup();
		//System.out.println("Step() begins for Person " + PID + " " + agegroup + " " + age + " " + sex);
		
		if(sex == Sex.FEMALE) {
			orderSingleFemales();
			giveBirth();
		}
		
		if(sex == Sex.MALE) marriage();
		
		death();
	}	

	
	// Determine SEX of new person
	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	public Sex determineSex() {
		Sex sex;
		double random = new Random().nextDouble();
		if (random < PMale) {sex = Sex.MALE;} 
		else {sex = Sex.FEMALE;}
		return sex;
	}
	
	// Determine AGE of members of initial population
	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
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
		// These probabilities lead to Prob(no birth) = 13% which is not realistic (actual value in 2013: 20%)
		// But the expected number of children of 1,847 fits the actual fertility rate (2013) of 1,85 very well!
			//http://www.ons.gov.uk/ons/rel/fertility-analysis/cohort-fertility--england-and-wales/2011/sty-1-in-5-women-are-childless-at-45.html
		 	//http://www.ons.gov.uk/ons/rel/vsob1/birth-summary-tables--england-and-wales/2013/stb-births-in-england-and-wales-2013.html
	public void giveBirth() {
		boolean birth = false;
		double random_birth = new Random().nextDouble();
		if(age >= 15 & age < 20 & random_birth < 0.0174 / (LifecycleFreq)) {birth = true;}	
		else if(age >= 20 & age < 25 & random_birth < 0.0637 / (LifecycleFreq)) {birth = true;}
		else if(age >= 25 & age < 29 & random_birth < 0.1115 / (LifecycleFreq)) {birth = true;}	// real value: 0.1015
		else if(age >= 30 & age < 35 & random_birth < 0.1594 / (LifecycleFreq)) {birth = true;}	// real value: 0.1094	
		else if(age >= 35 & age < 40 & random_birth < 0.0629 / (LifecycleFreq)) {birth = true;}
		else if(age >= 40 & age < 45 & random_birth < 0.0135 / (LifecycleFreq)) {birth = true;}
		else if(age >= 45 & age < 50 & random_birth < 0.001 / (LifecycleFreq)) {birth = true;}
		// create child/new person. The members of Model.persons_justborn will be added to Model.persons at the end of Model.step()
		if(birth == true) {
			Model.persons_justborn.add(new Person(hid, PID/*, partnerPID*/)); // auxiliary list as we are looping over Model.persons
			children.add(Model.persons_justborn.get(Model.persons_justborn.size()-1));
			//System.out.println("New child was born!");
		}
	}
	
	// Determining whether DEATH (conditional on age, sex)
	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
		// Source: ONS: Mortality Statistics: Deaths Registered in 2013 (Series DR) Tables 1–4 and Tables 6–14 (Excel sheet 988Kb)
		// http://www.ons.gov.uk/ons/rel/vsob1/mortality-statistics--deaths-registered-in-england-and-wales--series-dr-/2013/dr-tables-2013.xls

	public void death() {
		if(sex == Sex.MALE) {
			dead = die(ProbDeath[agegroup][0]/LifecycleFreq);
		}
		else {
			dead = die(ProbDeath[agegroup][1]/LifecycleFreq);
		}
		
		if(dead == true) {
			//System.out.println("Person " + PID + " died at age " + age + ".");
			Model.persons_justdied.add(this);
			Person.PersonCount = Person.PersonCount - 1;
			if(maritalStatus == MaritalStatus.MARRIED){
				Model.MarriageCount = Model.MarriageCount - 1;
				Model.personsAll.get(partnerPID).maritalStatus = MaritalStatus.WIDOWED;
				//Model.households.get(hid).members.remove(this);
			}
		}
		
	}
	
	public boolean die(double prob) {
		boolean dieThisPeriod;
		double random_death = new Random().nextDouble();
		if(random_death < prob) {dieThisPeriod = true;}
		else {dieThisPeriod = false;}
		return dieThisPeriod;
	}
	
	
	
// MARRIAGE
//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	// Source: ONS: Number of Marriages, Marriage Rates and Period of Occurrence, 2011
	// Source: ONS: Age at Marriage and Previous Marital Status, 2011
	
	
	// Determining whether MARRIAGE (conditional on age, sex and previous marital status)
	public void marriage() {
		int col = 99;
		marryThisPeriod = false;	
		// determine whether marriage this period
		if(sex == Sex.MALE & age >= 16) {
			
			if(maritalStatus == MaritalStatus.SINGLE) {determineMarriage(ProbMarriageMale[agegroup][0]);}
			else if(maritalStatus == MaritalStatus.WIDOWED) {determineMarriage(ProbMarriageMale[agegroup][1]);}
			else if(maritalStatus == MaritalStatus.DIVORCED) {determineMarriage(ProbMarriageMale[agegroup][2]);}
			
			if(marryThisPeriod == true) {
				System.out.println();
				System.out.println("Begin Marriage " + PID + ", Age " + age);
				if(age >= 16 & age < 90) {col = agegroup - 3;}
				if(age >= 90) {col = 14;}
				int ageListWife = determineAgeBride(col);
				partnerPID = selectWife(ageListWife);
				ageDifferencePartner = (int)(age - Model.personsAll.get(partnerPID).age);
				maritalStatus = MaritalStatus.MARRIED;
				Model.personsAll.get(partnerPID).justMarriedBride(PID, ageListWife);
				Model.MarriageCount++;
				
				// checks
				System.out.println("MARRIAGE: PID of Husband: " + PID + ", PID of Wife: " + partnerPID + ", Age Difference: " + ageDifferencePartner 
						+ ", agegroup wife: " + Model.personsAll.get(partnerPID).agegroup);
				System.out.println("End Marriage " + PID + ", Age " + age);
				System.out.println();
			}
		}

	}
	
	// determine whether marriage this period
	public void determineMarriage(double prob) {
		marryThisPeriod = false;
		double random_marriage = new Random().nextDouble();
		if(random_marriage < prob/LifecycleFreq) {
			marryThisPeriod = true;
		}
		System.out.println("Check determineMarriage() " + marryThisPeriod);
	}
	
	// determine age bracket from which wife will be selected
	public int determineAgeBride(int col) {
		double random_ageWife = new Random().nextDouble();
		System.out.println("random_ageWife = " + random_ageWife);
		int index = 0;
		if(random_ageWife >= 0 & random_ageWife < CumProbAgeBride[0][col]) {index=0;}	
		for(int i = 0; i<14; i++) {
			//System.out.println("Index = " + i);
			if(random_ageWife >= CumProbAgeBride[i][col] & random_ageWife < CumProbAgeBride[i+1][col]) {
				 index=i+1;
				 break;
			}
		}
		if(random_ageWife >= CumProbAgeBride[14][col] & random_ageWife < 1)  {index=14;}	
		System.out.println("Check determineAgeBride() " + index);
		return index;
	}

	
	// randomly select wife from list of candidates and CREATE NEW HOUSEHOLD!
	public int selectWife(int index) {
		int pidWife;
		System.out.println("Number of Women to choose from: " + Model.females_by_agegroup.get(index).size());
		pidWife = Model.females_by_agegroup.get(index).get(new Random().nextInt(Model.females_by_agegroup.get(index).size())).PID;
		//int randomint = (int)(Math.random() * (Model.females_by_agegroup.get(index).size() + 1)); // random integer between 0 and Model.females_by_agegroup.get(agegroup).size()
		//pidWife = Model.females_by_agegroup.get(index).get(randomint).PID;
		System.out.println("Check selectWife() " + Model.personsAll.get(pidWife).PID + ", agegroup: " + Model.personsAll.get(pidWife).agegroup);
		return pidWife;
	}
	
	// adjust characteristics of women who just got married
	public void justMarriedBride(int partner, int ageList) {
		Model.personsAll.get(partnerPID).partnerPID = partner;
		Model.personsAll.get(partnerPID).maritalStatus = MaritalStatus.MARRIED;
		Model.females_by_agegroup.get(ageList).remove(this);
		System.out.println("Check justMarriedBride() " + Model.personsAll.get(partnerPID).maritalStatus);
	}
	
//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	
	
	// determine age group
	public int determineAgeGroup() {
		int agegroup = this.agegroup;
		for(int i = 0; i < 18; i++) {
			if(age >= i*5 & age < i*5+5) {agegroup = i;}
		}
		if(age >= 90) {agegroup = 18;}
		return agegroup;
	}


	// put single females in corresponding age group list --> needed for assignment of wives
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
	
	// put INITIAL single females in corresponding age group list --> needed for assignment of wives
	public void orderInitialSingleFemales() {
		if(age >= 16 & agegroup == 3) {Model.females_by_agegroup.get(0).add(this);}
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
	
	
	// Probabilities of dying this year depending on age and sex (19 rows, 2 columns)
	// column 1: males, column 2: females
	// rows stand for agegroups
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
		
	// Cumulative Probabilities of the bride having a certain age given marriage and age (15 rows, 15 columns)
	public static final double [][] CumProbAgeBride = { 
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
	public static final double [][] CumProbInitialAge = { 
		// columns 2-19 stand for age groups 0-4,5-9,10-14,15-19,20-24,25-29, ... ,75-79,80-84,85+)
		// Example: column 4 = 0.1773 = Prob(age < 14)
		{0.0000, 0.0631, 0.1221, 0.1773, 0.2378, 0.3049, 0.3730, 0.4408, 0.5029, 0.5731, 0.6458, 0.7131, 0.7711, 0.8261, 0.8806, 0.9201, 0.9526, 0.9769, 1.0000}

	};
	
	// Initial cumulative probabilities for marital status for MALES (19 rows, 4 columns)
	public static final double [][] CumProbInitialMaritalStatusMale = { 
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

	// Initial cumulative probabilities for marital status for FEMALES (19 rows, 4 columns)
	public static final double [][] CumProbInitialMaritalStatusFemale = { 
		// rows 1-19 stand for age groups 0-4,5-9,10-14,15-19,20-24,25-29, ... ,75-79,80-84,85-89,90+)
		// SOURCE: ONS: Principal projection legal marital status: Summary of population by age & legal marital status
		
		//Single	Married	Widowed	Divorced
		{1.0000,	1.0000,	1.0000,	1.0000},
		{1.0000,	1.0000,	1.0000,	1.0000},
		{1.0000,	1.0000,	1.0000,	1.0000},
		{0.9968,	0.9999,	0.9999,	1.0000},
		{0.9431,	0.9977,	0.9978,	1.0000},
		{0.7508,	0.9776,	0.9789,	1.0000},
		{0.5156,	0.9391,	0.9414,	1.0000},
		{0.3462,	0.8903,	0.8946,	1.0000},
		{0.2358,	0.8358,	0.8439,	1.0000},
		{0.1638,	0.7964,	0.8114,	1.0000},
		{0.1098,	0.7745,	0.8036,	1.0000},
		{0.0757,	0.7613,	0.8139,	1.0000},
		{0.0531,	0.7435,	0.8345,	1.0000},
		{0.0449,	0.6995,	0.8610,	1.0000},
		{0.0458,	0.6154,	0.8875,	1.0000},
		{0.0516,	0.4957,	0.9179,	1.0000},
		{0.0582,	0.3511,	0.9441,	1.0000},
		{0.0619,	0.2269,	0.9642,	1.0000},
		{0.0779,	0.1782,	0.9926,	1.0000}
	};
	
	
}

