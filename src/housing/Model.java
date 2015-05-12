package housing;

import housing.Person.Sex;

import java.util.ArrayList;
import java.util.Random;

import org.apache.commons.math3.distribution.LogNormalDistribution;

import sim.engine.SimState;
import sim.engine.Steppable;
import ec.util.MersenneTwisterFast;

/**
 * This is the root object of the simulation. Upon creation it creates
 * and initialises all the agents in the model.
 * 
 * @author daniel
 *
 **/
@SuppressWarnings("serial")
public class Model extends SimState implements Steppable {

	public Model(long seed) {
		super(seed);
		households.ensureCapacity(Demographics.TARGET_POPULATION*2);
	}

	
	// START: INITIAL POPULATION OF PERSON AGENTS
	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	/** This method creates the setting for the simulation. Most importantly, the initial population of person and 
	 * household agents is created.
	 */
	public void start() {
		super.start();
        schedule.scheduleRepeating(this);

        makeInitialPopulation();
        MarriageTarget = MarriageCount;
        
		for(int i = 0; i<15; i++) {
			females_by_agegroup.get(i).clear();
		}

		t=0;
		System.out.println();
		System.out.println("Number of Households: " + households.size());
		System.out.println("Number of Households: " + Household.HouseholdCount);
		System.out.println("Number of People: " + persons.size());
		System.out.println("Number of people: " + Person.PersonCount);
		System.out.println("Number of marriages: " + MarriageCount);
		System.out.println();
	}

	
	
	// MAIN SIMULATION: spin-up and main simulation
	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	/**
	 * This is the main time-step of the whole simulation. Everything starts
	 * here.
	 */
	public void step(SimState simulationStateNow) {
		
		if (schedule.getTime() >= N_STEPS) simulationStateNow.kill();
		
		int help = (int) t/PersonFreq;
		if (help*PersonFreq == t) {
			System.out.println("Year " + help + " starts");
			if(schedule.getTime() <= N_SPINUPSTEPS) {
				spinUpDemographics(); 
				System.out.println("spinUpDemographics() executed");
			}
			else {mainDemographics(); System.out.println("mainDemographics() executed");}
			if(Person.PersonCount == 0) simulationStateNow.kill();
		}
		
		economicDecisions();
        t++;
	}
	
	/**
	 * Cleans up after a simulation ends.
	 */
	public void finish() {
		super.finish();
	}
	
	
// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
//	METHODS - METHODS - METHODS - METHODS - METHODS - METHODS
//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	
	
	public void startOld() {
		super.start();
        schedule.scheduleRepeating(this);
		t = 0;
	}
	
	// Economic Decisions
	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	public void economicDecisions() {
		construction.step();
		for(Household h : households) h.preHouseSaleStep();
		housingMarket.clearMarket();
		for(Household h : households) h.preHouseLettingStep();
		housingMarket.clearBuyToLetMarket();
		rentalMarket.clearMarket();
	    bank.step();
	}
	    
	// Spin-up
	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	public void spinUpDemographics() {
		NumberOfDivorcesNeeded = MarriageCount - MarriageTarget;
		
		for (Person p : persons) p.spinUpStepBasic();
		persons.removeAll(persons_justdied);
		persons_justdied.clear();
		
		for (Person p : persons) p.spinUpStep();
		
		PopulationTargetMales = (int) (N_PERSON * Person.PMale * Person.PopulationTargetShareByAge[90-t][0]);
		PopulationTargetFemales = (int) (N_PERSON * (1-Person.PMale) * Person.PopulationTargetShareByAge[90-t][1]);
		RequiredNumberMales = (int) (PopulationTargetMales / Person.ProbabilityOfSurvival[90-t][0]); 
		RequiredNumberFemales = (int) (PopulationTargetFemales / Person.ProbabilityOfSurvival[90-t][1]); 
		
		for (Person p : persons_justborn) {
			if(p.sex == Sex.FEMALE) females_justborn.add(p);
			else males_justborn.add(p);
		}
		
		excessMales = males_justborn.size() - RequiredNumberMales;
		for (int i = 0; i < excessMales; i++) {
			int random = new Random().nextInt(Model.males_justborn.size());
			persons_justborn.remove(males_justborn.get(random));
			males_excess.add(males_justborn.get(random));
			males_justborn.remove(random);
			Person.PersonCount = Person.PersonCount - 1;
		}
		excessFemales = females_justborn.size() - RequiredNumberFemales;
		for (int i = 0; i < excessFemales; i++) {
			int random = new Random().nextInt(Model.females_justborn.size());
			persons_justborn.remove(females_justborn.get(random));
			females_excess.add(females_justborn.get(random));
			females_justborn.remove(random);
			Person.PersonCount = Person.PersonCount - 1;
		}
		System.out.println("Females: required: " + RequiredNumberFemales + ", created: " + females_justborn.size());
		System.out.println("Males: required: " + RequiredNumberMales + ", created: " + males_justborn.size());
		
		for (Person p : males_excess) p.removeNewborn();
		for (Person p : females_excess) p.removeNewborn();			
		males_justborn.clear();
		females_justborn.clear();
		males_excess.clear();
		females_excess.clear();
		
		personsAll.addAll(persons_justborn);
		persons.addAll(persons_justborn);
		persons_justborn.clear();
		
		for(int i = 0; i<15; i++) females_by_agegroup.get(i).clear();
		for(int i = 0; i<15; i++) males_by_agegroup.get(i).clear();
      
		System.out.println("Number of people: " + persons.size());
        System.out.println("Number of households: " + households.size());
        System.out.println();
	}		
	
	
	public void mainDemographics() {
		for (Person p : persons) p.stepBasic();
		
		persons.removeAll(persons_justdied);
		persons_justdied.clear();
		
		for (Person p : persons) p.step();
								
		personsAll.addAll(persons_justborn);
		persons.addAll(persons_justborn);
		persons_justborn.clear();
		
		for(int i = 0; i<15; i++) females_by_agegroup.get(i).clear();
		for(int i = 0; i<15; i++) males_by_agegroup.get(i).clear();
		
	    System.out.println("Number of people: " + persons.size());
	    System.out.println("Number of households: " + households.size());
	    System.out.println();
	}
	
	
	// Make initial population
	/** This method creates the initial population of person and household agents. First, the person agents are created.
	 * Second, marriages are established.
	 */
	public void makeInitialPopulation() {
		
		females_by_agegroup.clear();
		females_by_agegroup.add(female_singles_16to19);
		females_by_agegroup.add(female_singles_20to24);
		females_by_agegroup.add(female_singles_25to29);
		females_by_agegroup.add(female_singles_30to34);
		females_by_agegroup.add(female_singles_35to39);
		females_by_agegroup.add(female_singles_40to44);
		females_by_agegroup.add(female_singles_45to49);
		females_by_agegroup.add(female_singles_50to54);
		females_by_agegroup.add(female_singles_55to59);
		females_by_agegroup.add(female_singles_60to64);
		females_by_agegroup.add(female_singles_65to69);
		females_by_agegroup.add(female_singles_70to74);
		females_by_agegroup.add(female_singles_75to79);
		females_by_agegroup.add(female_singles_80to84);
		females_by_agegroup.add(female_singles_85plus);

		males_by_agegroup.clear();
		males_by_agegroup.add(male_singles_16to19);
		males_by_agegroup.add(male_singles_20to24);
		males_by_agegroup.add(male_singles_25to29);
		males_by_agegroup.add(male_singles_30to34);
		males_by_agegroup.add(male_singles_35to39);
		males_by_agegroup.add(male_singles_40to44);
		males_by_agegroup.add(male_singles_45to49);
		males_by_agegroup.add(male_singles_50to54);
		males_by_agegroup.add(male_singles_55to59);
		males_by_agegroup.add(male_singles_60to64);
		males_by_agegroup.add(male_singles_65to69);
		males_by_agegroup.add(male_singles_70to74);
		males_by_agegroup.add(male_singles_75to79);
		males_by_agegroup.add(male_singles_80to84);
		males_by_agegroup.add(male_singles_85plus);

        // create persons
		for(int p = 0; p<N_PERSON; ++p) { 
			persons.add(new Person());
		}        
		personsAll.addAll(persons);
		
		for (Person p : persons) {
			p.setUpInitialMarriages();
		}
		persons_justborn.clear();
		persons_justdied.clear();
	}


	////////////////////////////////////////////////////////////////////////
	// Getters/setters for the console
	////////////////////////////////////////////////////////////////////////
	
	public Bank.Config getBankConfig() {
		return(bank.config);
	}

	public Household.Config getHouseholdConfig() {
		return(new Household.Config());
	}
	public String nameBankConfig() {return("Bank Configuration");}
	public String nameHouseholdConfig() {return("Household Configuration");}

	public Bank.Diagnostics getBankDiagnostics() {
		return(bank.diagnostics);
	}
	public String nameBankDiagnostics() {return("Bank Diagnostics");}
	
	public HouseSaleMarket.Diagnostics getMarketDiagnostics() {
		return(housingMarket.diagnostics);
	}
	public String nameMarketDiagnostics() {return("Housing Market Diagnostics");}

	public HouseRentalMarket.Diagnostics getRentalDiagnostics() {
		return(rentalMarket.diagnostics);
	}
	public String nameRentalDiagnostics() {return("Rental Market Diagnostics");}
	
	public static int getN() {
		return households.size();
	}
	public String nameN() {return("Current number of households");}
	
	public static int getN_STEPS() {
		return N_STEPS;
	}

	public static void setN_STEPS(int n_STEPS) {
		N_STEPS = n_STEPS;
	}

	public String nameN_STEPS() {return("Number of timesteps");}

	////////////////////////////////////////////////////////////////////////

	public static int 								N_STEPS = 100; // timesteps
	public static int 								N_SPINUPSTEPS = 90; // timesteps
	public static final int 						N_PERSON = 10000; // number of households	
	public static int								PersonFreq = 1;
	

	public static Bank 								bank = new Bank();
	public static Government						government = new Government();
	public static Construction						construction = new Construction();
	public static HouseSaleMarket 					housingMarket = new HouseSaleMarket();
	public static HouseRentalMarket					rentalMarket = new HouseRentalMarket();
	public static Demographics						demographics = new Demographics();
	public static MersenneTwisterFast				rand = new MersenneTwisterFast(1L);
	
	public static int								t; // time (months)
	public static LogNormalDistribution 			grossFinancialWealth;		// household wealth in bank balances and investments

	public static ArrayList<Person> 				personsAll = new ArrayList<Person>(); // record of all people who ever lived
	public static ArrayList<Person> 				persons = new ArrayList<Person>();
	public static ArrayList<Person> 				persons_justborn = new ArrayList<Person>();
	public static ArrayList<Person> 				females_justborn = new ArrayList<Person>();
	public static ArrayList<Person> 				males_justborn = new ArrayList<Person>();
	public static ArrayList<Person> 				females_excess = new ArrayList<Person>();
	public static ArrayList<Person> 				males_excess = new ArrayList<Person>();

	public static ArrayList<Person> 				persons_justdied = new ArrayList<Person>();
	public static ArrayList<Person> 				orphans = new ArrayList<Person>();
	public static ArrayList<Household> 				householdsAll = new ArrayList<Household>();
	public static ArrayList<Household>				households = new ArrayList<Household>();
	
	
	public static ArrayList<ArrayList<Person>> 		females_by_agegroup = new ArrayList<ArrayList<Person>>();
	public static ArrayList<Person> 				female_singles_16to19 = new ArrayList<Person>();
	public static ArrayList<Person> 				female_singles_20to24 = new ArrayList<Person>();
	public static ArrayList<Person> 				female_singles_25to29 = new ArrayList<Person>();
	public static ArrayList<Person> 				female_singles_30to34 = new ArrayList<Person>();
	public static ArrayList<Person> 				female_singles_35to39 = new ArrayList<Person>();
	public static ArrayList<Person> 				female_singles_40to44 = new ArrayList<Person>();
	public static ArrayList<Person> 				female_singles_45to49 = new ArrayList<Person>();
	public static ArrayList<Person> 				female_singles_50to54 = new ArrayList<Person>();
	public static ArrayList<Person> 				female_singles_55to59 = new ArrayList<Person>();
	public static ArrayList<Person> 				female_singles_60to64 = new ArrayList<Person>();
	public static ArrayList<Person> 				female_singles_65to69 = new ArrayList<Person>();
	public static ArrayList<Person> 				female_singles_70to74 = new ArrayList<Person>();
	public static ArrayList<Person> 				female_singles_75to79 = new ArrayList<Person>();
	public static ArrayList<Person> 				female_singles_80to84 = new ArrayList<Person>();
	public static ArrayList<Person> 				female_singles_85plus = new ArrayList<Person>();

	public static ArrayList<ArrayList<Person>> 		males_by_agegroup = new ArrayList<ArrayList<Person>>();
	public static ArrayList<Person> 				male_singles_16to19 = new ArrayList<Person>();
	public static ArrayList<Person> 				male_singles_20to24 = new ArrayList<Person>();
	public static ArrayList<Person> 				male_singles_25to29 = new ArrayList<Person>();
	public static ArrayList<Person> 				male_singles_30to34 = new ArrayList<Person>();
	public static ArrayList<Person> 				male_singles_35to39 = new ArrayList<Person>();
	public static ArrayList<Person> 				male_singles_40to44 = new ArrayList<Person>();
	public static ArrayList<Person> 				male_singles_45to49 = new ArrayList<Person>();
	public static ArrayList<Person> 				male_singles_50to54 = new ArrayList<Person>();
	public static ArrayList<Person> 				male_singles_55to59 = new ArrayList<Person>();
	public static ArrayList<Person> 				male_singles_60to64 = new ArrayList<Person>();
	public static ArrayList<Person> 				male_singles_65to69 = new ArrayList<Person>();
	public static ArrayList<Person> 				male_singles_70to74 = new ArrayList<Person>();
	public static ArrayList<Person> 				male_singles_75to79 = new ArrayList<Person>();
	public static ArrayList<Person> 				male_singles_80to84 = new ArrayList<Person>();
	public static ArrayList<Person> 				male_singles_85plus = new ArrayList<Person>();

	public static int 								MarriageCount = 0; 
	public static int								DependentChildMarriageCount = 0;
	public static int 								OrphanCount = 0;
	public static int 								NumberOfDivorcesNeeded = 0;
	public static int								MarriageTarget;
	public static int								PopulationTargetMales;
	public static int								PopulationTargetFemales;
	public static int								RequiredNumberMales;
	public static int								RequiredNumberFemales;
	public static int								excessMales;
	public static int								excessFemales;


		

	
	
	
}
