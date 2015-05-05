package housing;

import java.util.ArrayList;

import org.apache.commons.math3.distribution.LogNormalDistribution;

import ec.util.MersenneTwisterFast;
import sim.engine.SimState;
import sim.engine.Steppable;

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

	/**
	 * This method is called before the simualtion starts. It schedules this
	 * object to be stepped at each timestep and initialises the agents.
	 */
	public void startOld() {
		super.start();
        schedule.scheduleRepeating(this);
		initialise();
		t = 0;
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

		t=0;
		System.out.println();
		System.out.println("Number of Households: " + households.size());
		System.out.println("Number of Households: " + Household.HouseholdCount);
		System.out.println("Number of People: " + persons.size());
		System.out.println("Number of people: " + Person.PersonCount);
		System.out.println("Number of marriages: " + Model.MarriageCount);
		System.out.println();
	}

	
	/**
	 * This is the main time-step of the whole simulation. Everything starts
	 * here.
	 */
	public void step(SimState simulationStateNow) {
		if (schedule.getTime() >= N_STEPS) simulationStateNow.kill();
		
		int help = (int) t/Person.LifecycleFreq;
		
		if (help*Person.LifecycleFreq == t) {
			System.out.println("Year " + help + " starts");
			for (Person p : persons) {
				p.stepBasic();
				if(Person.PersonCount == 0) {
					System.out.println("Everybody dead!");
					simulationStateNow.kill();
				}
			}
			personsAll.addAll(persons_justborn);
			persons.removeAll(persons_justdied);
			persons.addAll(persons_justborn);
			persons_justdied.clear();
			persons_justborn.clear();
			for (Person p : persons) {
				p.step();
			}
			for(int i = 0; i<15; i++) {
				females_by_agegroup.get(i).clear();
			}
	        System.out.println("Number of people: " + persons.size());
	        System.out.println("Number of households: " + households.size());
	        System.out.println();
		}
		
//		demographics.step();
		construction.step();
		for(Household h : households) h.preHouseSaleStep();
		housingMarket.clearMarket();
		for(Household h : households) h.preHouseLettingStep();
		housingMarket.clearBuyToLetMarket();
		rentalMarket.clearMarket();
        bank.step();
        
        t++;
        
	}
	
	/**
	 * Cleans up after a simulation ends.
	 */
	public void finish() {
		super.finish();
	}
	
	
	////////////////////////////////////////////////////////////////////////
	// Initialisation
	////////////////////////////////////////////////////////////////////////
	/**
	 * Initialises the agents after first creation.
	 * 
	 * Assigns houses to agents, creates aged mortgages. Assigns income and
	 * wealth. Puts renters in rented accomodation, creates rental agreements
	 * assigns rented houses to buy-to-let investors.
	 */
	static void initialise() {		
		/***
		final double RENTERS = 0.32; // proportion of population who rent
		final double OWNERS = 0.32;  // proportion of population outright home-owners (no mortgage)
		final double INFLATION = 0.5; // average house-price inflation over last 25 years (not verified)
		final double INCOME_SUPPORT = 113.7*52.0; // married couple lower earnings from income support Source: www.nidirect.gov.uk
		
		int i, j, p, n;
		double price;
		LogNormalDistribution incomeDistribution;		// Annual household post-tax income
		LogNormalDistribution buyToLetDistribution; 	// No. of houses owned by buy-to-let investors (ARLA review and index 2014)
		
		incomeDistribution 	  = new LogNormalDistribution(Household.Config.INCOME_LOG_MEDIAN, Household.Config.INCOME_SHAPE);
//		residenceDistribution = new LogNormalDistribution(Math.log(190000), 0.568); // Source: ONS, Wealth in Great Britain wave 3
		buyToLetDistribution  = new LogNormalDistribution(Math.log(3.44), 1.050); 	// Source: ARLA report Q2 2014
		grossFinancialWealth  = new LogNormalDistribution(Math.log(9500), 2.259); 	// Source: ONS Wealth in Great Britain table 5.8
		
		for(j = 0; j<Nh; ++j) { // setup houses
			houses[j] = new House();
			houses[j].quality = (int)(House.Config.N_QUALITY*j*1.0/Nh); // roughly same number of houses in each quality band
		}
		
		for(j = 0; j<N; ++j) { // setup households
			households[j] = new Household();
			households[j].annualEmploymentIncome = incomeDistribution.inverseCumulativeProbability((j+0.5)/N);
			if(households[j].annualEmploymentIncome < INCOME_SUPPORT) households[j].annualEmploymentIncome = INCOME_SUPPORT;
			households[j].bankBalance = 1e8; // Interim value to ensure liquidity during initialisation of housing
			// System.out.println(households[j].annualEmploymentIncome);
			// System.out.println(households[j].getMonthlyEmploymentIncome());
		}
		
		i = Nh-1;
		for(j = N-1; j>=0 && i > RENTERS*Nh; --j) { // assign houses to homeowners with sigma function probability
			if(1.0/(1.0+Math.exp((j*1.0/N - RENTERS)/0.04)) < rand.nextDouble()) {
				houses[i].owner = households[j];
				houses[i].resident = households[j];
				households[j].home = houses[i];
				if(rand.nextDouble() < OWNERS/(1.0-RENTERS)) {
					// household owns house outright
					households[j].completeHousePurchase(new HouseSaleRecord(houses[i], 0));
					households[j].housePayments.get(houses[i]).nPayments = 0;
				} else {
					// household is still paying off mortgage
					p = (int)(bank.config.N_PAYMENTS*rand.nextDouble()); // number of payments outstanding
					price = HousingMarket.referencePrice(houses[i].quality)/(Math.pow(1.0+INFLATION,Math.floor((bank.config.N_PAYMENTS-p)/12.0)));
					if(price > bank.getMaxMortgage(households[j], true)) {
						price = bank.getMaxMortgage(households[j], true);
					}
					households[j].completeHousePurchase(new HouseSaleRecord(houses[i], price));
					households[j].housePayments.get(houses[i]).nPayments = p;
				}
				--i;
			}
		}

		while(i>=0) { // assign buyToLets
			do {
				j = (int)(rand.nextDouble()*N);
			} while(!households[j].isHomeowner());
//			n = (int)(buyToLetDistribution.sample() + 0.5); // number of houses owned
			n = (int)(Math.exp(rand.nextGaussian()*buyToLetDistribution.getShape() + buyToLetDistribution.getScale()) + 0.5); // number of houses owned
			while(n>0 && i>=0) { 			
				houses[i].owner = households[j];
				houses[i].resident = null;
				p = (int)(bank.config.N_PAYMENTS*rand.nextDouble()); // number of payments outstanding
				price = Math.min(
						HousingMarket.referencePrice(houses[i].quality)
						/(Math.pow(1.0+INFLATION,Math.floor((bank.config.N_PAYMENTS-p)/12.0))),
						bank.getMaxMortgage(households[j], false)
						);
				households[j].completeHousePurchase(new HouseSaleRecord(houses[i], price));	
				--i;
				--n;
			}
			households[j].desiredPropertyInvestmentFraction = 123.4; // temporary flag for later
		}

		for(j = 0; j<N; ++j) { // setup financial wealth
			households[j].bankBalance = grossFinancialWealth.inverseCumulativeProbability((j+0.5)/N);
//			System.out.println(households[j].monthlyPersonalIncome*12+" "+households[j].bankBalance/households[j].monthlyPersonalIncome);
			if(households[j].isPropertyInvestor()) {
				price = households[j].getPropertyInvestmentValuation();
				households[j].setDesiredPropertyInvestmentFraction(price/(price + households[j].bankBalance));
//				System.out.println(households[j].desiredPropertyInvestmentFraction + " " + households[j].getDesiredPropertyInvestmentValue()+" "+households[j].getPropertyInvestmentValuation());
			}
		}

		
		for(j = 0; j<N; ++j) { // homeless bid on rental market
			if(households[j].isHomeless()) rentalMarket.bid(households[j], households[j].desiredRent());
		}
		rentalMarket.clearMarket();				
		***/
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

	public static int N_STEPS = 5000; // timesteps
	public static final int N_PERSON = 10000; // number of households	

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
	
}
