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
	boolean birth = false;
	boolean death = false;
	enum Sex {
		MALE, FEMALE
	}
	Sex sex;
	enum Status {
		SINGLE, COUPLE
	}
	Status status;
	
	// List of Parents and Children
	ArrayList<Person> parents = new ArrayList<Person>();
	ArrayList<Person> children = new ArrayList<Person>();

	
	
	// Class Characteristics
	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	public static int PersonCount;
	public static int PIDcount;

	


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
		income = 0;
		System.out.println("Person created -  ID " + PID + ", Sex " + sex);
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
		income = 0;
		System.out.println("Person " + PID + " was born; Sex: " + sex);
	}

	public void step() {
		
		// age increases
		age = age +  (1.0 / 12);
		//System.out.println("Number of children: " + children.size());
		//System.out.println("Age: " + age);

		// birth?
		if(sex == Sex.FEMALE) {
			birth();	
		}
		
	
		// marriage?
		
	
		
		// death in LifecycleHousehold
		death();
		

	}	
	
	// Determining whether birth in current month, given age and number of previous children
	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	// Source: ONS: Births: Characteristics of Mother 2, England and Wales, 2013
	// These probabilities lead to Prob(no birth) = 13% which is not realistic (actual value in 2013: 20%)
	// But the expected number of children of 1,847 fits the actual fertility rate (2013) of 1,85 very well!
		//http://www.ons.gov.uk/ons/rel/fertility-analysis/cohort-fertility--england-and-wales/2011/sty-1-in-5-women-are-childless-at-45.html
	 	//http://www.ons.gov.uk/ons/rel/vsob1/birth-summary-tables--england-and-wales/2013/stb-births-in-england-and-wales-2013.html
	public void birth() {
		birth = false;
		double random_birth = new Random().nextDouble();
		if(age >= 15 & age < 20 & random_birth < 0.0174 / (12)) {
			birth = true;
		}	
		else if(age >= 20 & age < 25 & random_birth < 0.0637 / (12)) {
			birth = true;
		}
		else if(age >= 25 & age < 29 & random_birth < 0.1015 / (12)) {
			birth = true;
		}	
		else if(age >= 30 & age < 35 & random_birth < 0.1094 / (12)) {
			birth = true;
		}		
		else if(age >= 35 & age < 40 & random_birth < 0.0629 / (12)) {
			birth = true;
		}
		else if(age >= 40 & age < 45 & random_birth < 0.0135 / (12)) {
			birth = true;
		}
		else if(age >= 45 & age < 50 & random_birth < 0.001 / (12)) {
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
	
	
	public void die(double prob) {
		double random_death = new Random().nextDouble();
		if(random_death < prob) {
			System.out.println("Person " + PID + " died at age " + age + ".");
			Model.persons_justdied.add(this);
			Person.PersonCount = Person.PersonCount - 1;
		}
	}
	
	public void death() {
		if(sex == Sex.MALE) {
			if(age < 1) 				  die(0.0044/12.0);
			else if(age >= 1 & age < 5)   die(0.0002/12.0);
			else if(age >= 5 & age < 10)  die(0.0001/12.0);			
			else if(age >= 10 & age < 15) die(0.0001/12.0);			
			else if(age >= 15 & age < 20) die(0.0003/12.0);			
			else if(age >= 20 & age < 25) die(0.0005/12.0);			
			else if(age >= 25 & age < 30) die(0.0006/12.0);			
			else if(age >= 30 & age < 35) die(0.0008/12.0);			
			else if(age >= 35 & age < 40) die(0.0012/12.0);			
			else if(age >= 40 & age < 45) die(0.0017/12.0);			
			else if(age >= 45 & age < 50) die(0.0025/12.0);			
			else if(age >= 50 & age < 55) die(0.0037/12.0);			
			else if(age >= 55 & age < 60) die(0.0059/12.0);			
			else if(age >= 60 & age < 65) die(0.0096/12.0);			
			else if(age >= 65 & age < 70) die(0.0143/12.0);			
			else if(age >= 70 & age < 75) die(0.0245/12.0);			
			else if(age >= 75 & age < 80) die(0.0407/12.0);			
			else if(age >= 80 & age < 85) die(0.0732/12.0);			
			else if(age >= 85 & age < 90) die(0.1294/12.0);			
			else if(age >= 90) 			  die(0.2383/12.0);	
		}
		else if(sex == Sex.FEMALE) {
			if(age < 1) 				  die(0.0035/12.0);
			else if(age >= 1 & age < 5)   die(0.0002/12.0);
			else if(age >= 5 & age < 10)  die(0.0001/12.0);			
			else if(age >= 10 & age < 15) die(0.0001/12.0);			
			else if(age >= 15 & age < 20) die(0.0001/12.0);			
			else if(age >= 20 & age < 25) die(0.0002/12.0);			
			else if(age >= 25 & age < 30) die(0.0003/12.0);			
			else if(age >= 30 & age < 35) die(0.0004/12.0);			
			else if(age >= 35 & age < 40) die(0.0007/12.0);			
			else if(age >= 40 & age < 45) die(0.0010/12.0);			
			else if(age >= 45 & age < 50) die(0.0016/12.0);			
			else if(age >= 50 & age < 55) die(0.0025/12.0);			
			else if(age >= 55 & age < 60) die(0.0040/12.0);			
			else if(age >= 60 & age < 65) die(0.0061/12.0);			
			else if(age >= 65 & age < 70) die(0.0094/12.0);			
			else if(age >= 70 & age < 75) die(0.0160/12.0);			
			else if(age >= 75 & age < 80) die(0.0281/12.0);			
			else if(age >= 80 & age < 85) die(0.0533/12.0);			
			else if(age >= 85 & age < 90) die(0.1004/12.0);			
			else if(age >= 90) 			  die(0.2101/12.0);	
		}
		
	}
	

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
