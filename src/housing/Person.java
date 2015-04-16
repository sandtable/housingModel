package housing;

import java.util.ArrayList;
import java.util.Random;

public class Person {
	

	
	// Person Characteristics 
	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	
	// identifiers
	public final int PID;
	public int motherPID;
	public int fatherPID;	
	public int partnerPID;
	public int hid;
	
	// Parameters
	public static double PMale = 105.1 / 205.1;
	public static double LifeDuration = 75;	
	
	// socio-economic variables
	double age;
	double income;
	enum Sex {
		MALE, FEMALE
	}
	Sex sex;
	enum MaritalStatus {
		SINGLE, COUPLE, MARRIED, DIVORCED, WIDOWED
	}
	MaritalStatus status;
	
	// List of Parents and Children
	ArrayList<Person> parents = new ArrayList<Person>();
	ArrayList<Person> children = new ArrayList<Person>();

	
	
	// Class Characteristics
	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	public static int PersonCount;
	public static int PIDcount;
	public static int LifecycleFreq = 1;

	


	// Person Constructor/Initialization /////////////////////////////////////////////////
	public Person() {
		PersonCount++;
		PIDcount++;
		PID = PIDcount;
		double random = new Random().nextDouble();
		if (random < PMale) {
			sex = Sex.MALE;
		} else {
			sex = Sex.FEMALE;
		}
		age = 0;
		status = MaritalStatus.SINGLE;
		income = 0;
		//System.out.println("Person created -  ID " + PID + ", Sex " + sex);
	}

	// Person Constructor/for newborn children  /////////////////////////////////////////////////
	public Person(int hid, int motherPID/*, int fatherPID*/) {
		PersonCount++; 	// keeps track of the number of living people
		PIDcount++;		// used for assignment of unique PIDs
		PID = PIDcount;
		this.motherPID = motherPID;
		this.fatherPID = fatherPID;
		this.hid = hid;
		double random = new Random().nextDouble();
		if (random < PMale) {
			sex = Sex.MALE;
		} else {
			sex = Sex.FEMALE;
		}
		age = 0;
		status = MaritalStatus.SINGLE;
		income = 0;
		//System.out.println("Person " + PID + " was born; Sex: " + sex);
	}

	public void step() {
		
		// age increases
		age = age +  (1.0 / LifecycleFreq);
		//System.out.println("Number of children: " + children.size());
		//System.out.println("Age: " + age);
		if(sex == Sex.FEMALE & age == 16) {Model.female_singles_16to19.add(this);}
		if(sex == Sex.FEMALE & age == 20) {
			Model.female_singles_20to24.add(this);
			Model.female_singles_16to19.remove(this);
		}
		if(sex == Sex.FEMALE & age == 20) {
			Model.female_singles_25to29.add(this);
			Model.female_singles_20to24.remove(this);
		}
		if(sex == Sex.FEMALE & age == 20) {
			Model.female_singles_30to34.add(this);
			Model.female_singles_25to29.remove(this);
		}
		if(sex == Sex.FEMALE & age == 20) {
			Model.female_singles_35to39.add(this);
			Model.female_singles_30to34.remove(this);
		}

		// birth?
		if(sex == Sex.FEMALE) {
			birth();	
		}
		
	
		// marriage?
		if(sex == Sex.MALE) marriage();
		marriage();
	
		
		// death in LifecycleHousehold
		death();
		

	}	
	
	// Determining whether birth, given age
	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	// Source: ONS: Births: Characteristics of Mother 2, England and Wales, 2013
	// These probabilities lead to Prob(no birth) = 13% which is not realistic (actual value in 2013: 20%)
	// But the expected number of children of 1,847 fits the actual fertility rate (2013) of 1,85 very well!
		//http://www.ons.gov.uk/ons/rel/fertility-analysis/cohort-fertility--england-and-wales/2011/sty-1-in-5-women-are-childless-at-45.html
	 	//http://www.ons.gov.uk/ons/rel/vsob1/birth-summary-tables--england-and-wales/2013/stb-births-in-england-and-wales-2013.html
	public void birth() {
		boolean birth = false;
		double frequency = LifecycleFreq; // if frequency = 12 (=1): monthly (yearly/every 12 ticks) execution of birth();
		double random_birth = new Random().nextDouble();
		if(age >= 15 & age < 20 & random_birth < 0.0174 / (frequency)) {
			birth = true;
		}	
		else if(age >= 20 & age < 25 & random_birth < 0.0637 / (frequency)) {
			birth = true;
		}
		else if(age >= 25 & age < 29 & random_birth < 0.1015 / (frequency)) {
			birth = true;
		}	
		else if(age >= 30 & age < 35 & random_birth < 0.1094 / (frequency)) {
			birth = true;
		}		
		else if(age >= 35 & age < 40 & random_birth < 0.0629 / (frequency)) {
			birth = true;
		}
		else if(age >= 40 & age < 45 & random_birth < 0.0135 / (frequency)) {
			birth = true;
		}
		else if(age >= 45 & age < 50 & random_birth < 0.001 / (frequency)) {
			birth = true;
		}
		// create child/new person. The members of Model.persons_justborn will be added to Model.persons at the end of Model.step()
		if(birth == true) {
			Model.persons_justborn.add(new Person(hid, PID/*, partnerPID*/)); // auxiliary list as we are looping over Model.persons
			int help = Model.persons_justborn.size();			// is there a better way of coding this?
			children.add(Model.persons_justborn.get(help-1));	// list of 
			//System.out.println("New child was born!");
		}
		else {
			//System.out.println("No new child!");
		}
	}
	
	
	// Determining whether death, given age, sex
	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	// Source: ONS: Mortality Statistics: Deaths Registered in 2013 (Series DR) Tables 1–4 and Tables 6–14 (Excel sheet 988Kb)
	// http://www.ons.gov.uk/ons/rel/vsob1/mortality-statistics--deaths-registered-in-england-and-wales--series-dr-/2013/dr-tables-2013.xls
	
	public void die(double prob) {
		double random_death = new Random().nextDouble();
		if(random_death < prob) {
			//System.out.println("Person " + PID + " died at age " + age + ".");
			Model.persons_justdied.add(this);
			Person.PersonCount = Person.PersonCount - 1;
		}
	}
	
	public void death() {
		double frequency = LifecycleFreq; // if frequency = 12: monthly execution of death();
		if(sex == Sex.MALE) {
			if(age >= 0 & age < 5)   	  die(0.00101/frequency);
			else if(age >= 5 & age < 10)  die(0.00009/frequency);			
			else if(age >= 10 & age < 15) die(0.00010/frequency);			
			else if(age >= 15 & age < 20) die(0.00030/frequency);			
			else if(age >= 20 & age < 25) die(0.00046/frequency);			
			else if(age >= 25 & age < 30) die(0.00060/frequency);			
			else if(age >= 30 & age < 35) die(0.00079/frequency);			
			else if(age >= 35 & age < 40) die(0.00119/frequency);			
			else if(age >= 40 & age < 45) die(0.00172/frequency);			
			else if(age >= 45 & age < 50) die(0.00248/frequency);			
			else if(age >= 50 & age < 55) die(0.00368/frequency);			
			else if(age >= 55 & age < 60) die(0.00592/frequency);			
			else if(age >= 60 & age < 65) die(0.00961/frequency);			
			else if(age >= 65 & age < 70) die(0.01434/frequency);			
			else if(age >= 70 & age < 75) die(0.02448/frequency);			
			else if(age >= 75 & age < 80) die(0.04074/frequency);			
			else if(age >= 80 & age < 85) die(0.07318/frequency);			
			else if(age >= 85 & age < 90) die(0.12938/frequency);			
			else if(age >= 90) 			  die(0.23828/frequency);	
		}
		else if(sex == Sex.FEMALE) {
			if(age >= 0 & age < 5)   	  die(0.00081/frequency);
			else if(age >= 5 & age < 10)  die(0.00007/frequency);			
			else if(age >= 10 & age < 15) die(0.00009/frequency);			
			else if(age >= 15 & age < 20) die(0.00014/frequency);			
			else if(age >= 20 & age < 25) die(0.00021/frequency);			
			else if(age >= 25 & age < 30) die(0.00029/frequency);			
			else if(age >= 30 & age < 35) die(0.00043/frequency);			
			else if(age >= 35 & age < 40) die(0.00066/frequency);			
			else if(age >= 40 & age < 45) die(0.00103/frequency);			
			else if(age >= 45 & age < 50) die(0.00156/frequency);			
			else if(age >= 50 & age < 55) die(0.00248/frequency);			
			else if(age >= 55 & age < 60) die(0.00396/frequency);			
			else if(age >= 60 & age < 65) die(0.00614/frequency);			
			else if(age >= 65 & age < 70) die(0.00944/frequency);			
			else if(age >= 70 & age < 75) die(0.01605/frequency);			
			else if(age >= 75 & age < 80) die(0.02809/frequency);			
			else if(age >= 80 & age < 85) die(0.05326/frequency);			
			else if(age >= 85 & age < 90) die(0.10041/frequency);			
			else if(age >= 90) 			  die(0.21012/frequency);	
		}
		
	}
	
	
	// Determining whether marriage, given age, sex and previous marital status
	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	// Source: ONS: Number of Marriages, Marriage Rates and Period of Occurrence (Excel sheet 239Kb)
	// http://www.ons.gov.uk/ons/rel/vsob1/marriages-in-england-and-wales--provisional-/2012/rtd-number-of-marriages--marriage-rates-and-period-of-occurrence.xls
	
	public void marry(double prob) {
		int frequencyMarriage = LifecycleFreq; // if frequency = 12: monthly execution of marriage();
		double random_marriage = new Random().nextDouble();
		
		if(random_marriage < prob/frequencyMarriage) {
			Model.males_marryThisPeriod.add(this);
			status = MaritalStatus.MARRIED;
		}
	}

	public void marriage() {
		int row = 99;
		if(age >= 16 & age < 20) 	  {row = 0;}
		else if(age >= 20 & age < 25) {row = 1;}			
		else if(age >= 25 & age < 30) {row = 2;}			
		else if(age >= 30 & age < 35) {row = 3;}			
		else if(age >= 35 & age < 40) {row = 4;}			
		else if(age >= 40 & age < 45) {row = 5;}			
		else if(age >= 45 & age < 50) {row = 6;}			
		else if(age >= 50 & age < 55) {row = 7;}			
		else if(age >= 55 & age < 60) {row = 8;}			
		else if(age >= 60 & age < 65) {row = 9;}			
		else if(age >= 65 & age < 70) {row = 10;}			
		else if(age >= 70 & age < 75) {row = 11;}			
		else if(age >= 75 & age < 80) {row = 12;}			
		else if(age >= 80 & age < 85) {row = 13;}			
		else if(age >= 85) 			  {row = 14;}
	
		// determine whether marriage this period
		if(sex == Sex.MALE & age >= 16) {
			if(status == MaritalStatus.SINGLE) {marry(ProbMarriageMale[row][0]);}
			else if(status == MaritalStatus.WIDOWED) {marry(ProbMarriageMale[row][1]);}
			else if(status == MaritalStatus.DIVORCED) {marry(ProbMarriageMale[row][2]);}
		}
		
		// determine age bracket of wife
		double random_ageWife = new Random().nextDouble();
		
		if(random_ageWife >= 0 & random_ageWife < ProbAgeBride[0][row]) findWife(1);	
		for(int i = 0; i<14; i++) {
			int j = i+1;
			if(random_ageWife >= ProbAgeBride[i][row] & random_ageWife < ProbAgeBride[j][row]) {
				findWife(j);
			}
		}
		if(random_ageWife >= ProbAgeBride[14][row] & random_ageWife < 1) findWife(15);		

	}
	
	// randomly select wife from list of candidates (depending on age)
	public void findWife(int agegroup) {
		if(agegroup == 1) {
			int randomint = (int)(Math.random() * (Model.female_singles_16to19.size() + 1)); // random integer between 0 and Model.female_singles_16to19.size()
			partnerPID = Model.female_singles_16to19.get(randomint).PID;			
		}
		
		// HIER WEITER!!!
		
	}

	public static final double [][] ProbMarriageMale = { // rows stand for age groups
		// rows stand for age groups
		// column 1: single; column 2: widowed; column 3: divorced
		{0.0006, 0.0000, 0.0000}, // age < 20
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
		{0.0033, 0.0052, 0.0154}, // 85 +
	};
	
	public static final double [][] ProbMarriageFemale = { 
		// rows stand for age groups
		// column 1: single; column 2: divorced; column 3: widowed
		{0.0021, 0.0000, 0.0000}, // age < 20
		{0.0191, 0.0248, 0.0894}, // 20-24
		{0.0508, 0.0284, 0.0776}, // 25-29
		{0.0511, 0.0388, 0.0652}, // 30-34
		{0.0303, 0.0285, 0.0414}, // 35-39
		{0.0169, 0.0285, 0.0414}, // 40-44
		{0.0105, 0.0135, 0.0236}, // 45-49
		{0.0075, 0.0135, 0.0236}, // 50-54
		{0.0024, 0.0011, 0.0070}, // 55 +
	};
	
	public static final double [][] ProbAgeBride = { 
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
	
	
	
}

/*////////////////////////////////////////////////////////////////////////////////
	
	static class Config {
		public static double PMale = 105.1/205.1; // Probability of being male given that you were born in the UK 2007-2011 (Source: Birth ratios in the UK, 2013, Dept of health)
		public static SampledFunction PBirthGivenMarried = // monthly probability of birth by age for married female
				// source: ONS Characteristics of mother 2 (2012 figures)
			new SampledFunction(new Double[][] {
				{16.0, 0.0},	// minimum age for marriage in England and wales
				{20.0, 0.2232/12.0},
				{25.0, 0.2749/12.0},
				{30.0, 0.2290/12.0},
				{35.0, 0.1806/12.0},
				{40.0, 0.0762/12.0},
				{45.0, 0.0139/12.0},
				{51.0, 0.0009/12.0} // average age of menopause in UK (NHS choices http://www.nhs.uk/Conditions/Menopause/Pages/Introduction.aspx)
			});
		public static SampledFunction PBirthGivenUnmarried = // monthly probability of birth by age for unmarried female
				// source: ONS Characteristics of mother 2 (2012 figures)
			new SampledFunction(new Double [][] {
				{11.0, 0.0},	// average age of puberty in UK (NHS choices http://www.nhs.uk/conditions/Puberty/Pages/Introduction.aspx)
				{20.0, 0.0190/12.0},
				{25.0, 0.0579/12.0},
				{30.0, 0.0657/12.0},
				{35.0, 0.0659/12.0},
				{40.0, 0.0438/12.0},
				{45.0, 0.0130/12.0},
				{51.0, 0.0010/12.0} // average age of menopause in UK (NHS choices http://www.nhs.uk/Conditions/Menopause/Pages/Introduction.aspx)
			});
		

}

	public Person(LifecycleHousehold h) {
		household = h;
		status = Status.SINGLE;
		sex = (Model.rand.nextDouble() < Config.PMale?Sex.MALE:Sex.FEMALE);
		age = 0.0;
		income = 0.0;
	}
	
	enum Sex {
		MALE,
		FEMALE
	}
	enum Status {
		SINGLE,
//		PARTNERSHIP,
		MARRIED, // married or in civil partnership
		COHABITINGCOUPLE // income support distinction...? 
//		COHABITINGORMARRIED
	}
	
	public void step() {
		age += 1.0/12.0;
		
	}

	
	double		age;
	Sex 		sex;
	Status		status;
	double		income;
	LifecycleHousehold 	household;
}
*/
