package housing;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.UUID;

import org.apache.commons.math3.distribution.LogNormalDistribution;
import org.apache.commons.math3.random.RandomGenerator;

import ec.util.MersenneTwisterFast;
import sim.engine.SimState;
import sim.engine.Steppable;
import sim.engine.Stoppable;

/**
 * This is the root object of the simulation. Upon creation it creates
 * and initialises all the agents in the model.
 * 
 * @author daniel
 *
 **/
@SuppressWarnings("serial")
public class Model extends SimState implements Steppable {

	////////////////////////////////////////////////////////////////////////

	public static int N_STEPS = 1000; // Simulation duration in timesteps
	public static int TIME_TO_START_RECORDING = 500; // Timesteps to wait before recording statistics (initialisation time)
	public static int N_SIMS = 1; // Number of simulations to run (monte-carlo)

	public boolean recordCoreIndicators = true; // True to write time series for each core indicator
	public boolean recordMicroData = false; // True to write micro data for each transaction made

	////////////////////////////////////////////////////////////////////////

	// public static void main(String[] args) {
	// 	//doLoop(ModelNoGUI.class, args);
	// 	doLoop(Model.class,args);
	// 	System.exit(0);//Stop the program when finished.
	// }

	public static void main(String[] args) {
		long seed = Long.parseLong(args[0]);
		String paramsFile = args[1];
		Model model = new Model(seed, paramsFile);
		model.start();
		double time;
		while((time = model.schedule.getTime()) < N_STEPS)
			{
			if (!model.schedule.step(model)) break;
			if (time%100==0 && time!=0)            System.out.println("Time Step " + time);
			}
		model.finish();
	}

	public Model(long seed, String paramsFile) {
		super(seed);
		System.out.println(seed);
		System.out.println(paramsFile);
		government = new Government();
		demographics = new Demographics(paramsFile);
		recorder = new Recorder();
		transactionRecorder = new MicroDataRecorder();
		rand = new MersenneTwister(seed);

		centralBank = new CentralBank();
		mBank = new Bank();
		mConstruction = new Construction();
		mHouseholds = new ArrayList<Household>(Demographics.TARGET_POPULATION*2);
		housingMarket = mHousingMarket = new HouseSaleMarket();
		rentalMarket = mRentalMarket = new HouseRentalMarket();
		mCollectors = new Collectors();
		nSimulation = 0;

		setupStatics();
		init();
	}
	
	@Override
	public void awakeFromCheckpoint() {
		super.awakeFromCheckpoint();
		setupStatics();
	}
	
	protected void setupStatics() {
//		centralBank = mCentralBank;
		bank = mBank;
		construction = mConstruction;
		households = mHouseholds;
		housingMarket = mHousingMarket;
		rentalMarket = mRentalMarket;
		collectors = mCollectors;
		root = this;
		setRecordCoreIndicators(recordCoreIndicators);
		setRecordMicroData(recordMicroData);
	}

	
	public void init() {
		construction.init();
		housingMarket.init();
		rentalMarket.init();
		bank.init();
		households.clear();
		collectors.init();
		t = 0;
		if(!monteCarloCheckpoint.equals("")) {//changed this from != ""
			File f = new File(monteCarloCheckpoint);
			readFromCheckpoint(f);
		}
	}

	/**
	 * This method is called before the simulation starts. It schedules this
	 * object to be stepped at each timestep and initialises the agents.
	 */
	public void start() {
		super.start();
		scheduleRepeat = schedule.scheduleRepeating(this);

		if(!monteCarloCheckpoint.equals("")) {//changed from != ""
			File f = new File(monteCarloCheckpoint);
			readFromCheckpoint(f);
		}
			// recorder.start();
	}
	
	public void stop() {
		scheduleRepeat.stop();
	}

	/**
	 * This is the main time-step of the whole simulation. Everything starts
	 * here.
	 */
	public void step(SimState simulationStateNow) {
		if (schedule.getTime() >= N_STEPS*N_SIMS) simulationStateNow.kill();
		if(t >= N_STEPS) {
			// start new simulation
			nSimulation += 1;
			if (nSimulation >= N_SIMS) {
				// this was the last simulation, clean up
				if(recordCoreIndicators) recorder.finish();
				if(recordMicroData) transactionRecorder.finish();
				simulationStateNow.kill();
				return;
			}
			if(recordCoreIndicators) recorder.endOfSim();
			if(recordMicroData) transactionRecorder.endOfSim();
			init();
		}

		modelStep();

		if (t>=TIME_TO_START_RECORDING) {
			if(recordCoreIndicators) recorder.step();
		}

		collectors.step();
	}

	public void modelStep() {
		demographics.step();
		construction.step();
		
		for(Household h : households) h.step();
		collectors.housingMarketStats.record();
		housingMarket.clearMarket();
		collectors.rentalMarketStats.record();
		rentalMarket.clearMarket();
		bank.step();
		centralBank.step(getCoreIndicators());
		t += 1;        
	}
	
	
	/**
	 * Cleans up after a simulation ends.
	 */
	public void finish() {
		super.finish();
		if(recordCoreIndicators) recorder.finish();
		if(recordMicroData) transactionRecorder.finish();
	}
	
	/*** @return simulated time in months */
	static public int getTime() {
		return(Model.root.t);
	}

	static public int getMonth() {
		return(Model.root.t%12 + 1);
	}

	public Stoppable scheduleRepeat;

	// non-statics for serialization
	public ArrayList<Household>    	mHouseholds;
	public Bank						mBank;
//	public CentralBank				mCentralBank;
	public Construction				mConstruction;
	public HouseSaleMarket			mHousingMarket;
	public HouseRentalMarket		mRentalMarket;
	public Collectors				mCollectors;
	
	public static CentralBank		centralBank;
	public static Bank 				bank;
	public static Government		government;
	public static Construction		construction;
	public static HouseSaleMarket 	housingMarket;
	public static HouseRentalMarket	rentalMarket;
	public static ArrayList<Household>	households;
	public static Demographics		demographics;
	public static MersenneTwister	rand;
	public static Model				root;
	
	public static Collectors		collectors;// = new Collectors();
	public static Recorder			recorder; // records info to file
	public static MicroDataRecorder transactionRecorder;

	public static int	nSimulation; // number of simulations run
	public int	t; // time (months)
//	public static LogNormalDistribution grossFinancialWealth;		// household wealth in bank balances and investments

	/*** proxy class to allow us to work with apache.commons distributions */
	public static class MersenneTwister extends MersenneTwisterFast implements RandomGenerator {
		public MersenneTwister(long seed) {super(seed);}
		public void setSeed(int arg0) {
			super.setSeed((long)arg0);
		}		
	}

	////////////////////////////////////////////////////////////////////////
	// Getters/setters for MASON console
	////////////////////////////////////////////////////////////////////////
	
	public CreditSupply getCreditSupply() {
		return collectors.creditSupply;
	}

	public HousingMarketStats getHousingMarketStats() {
		return collectors.housingMarketStats;
	}

	public HousingMarketStats getRentalMarketStats() {
		return collectors.rentalMarketStats;
	}

	public CoreIndicators getCoreIndicators() {
		return collectors.coreIndicators;
	}

	public HouseholdStats getHouseholdStats() {
		return collectors.householdStats;
	}	
	
	public static int getN_STEPS() {
		return N_STEPS;
	}

	public static void setN_STEPS(int n_STEPS) {
		N_STEPS = n_STEPS;
	}
	public String nameN_STEPS() {return("Number of timesteps");}

	public static int getN_SIMS() {
		return N_SIMS;
	}

	public static void setN_SIMS(int n_SIMS) {
		N_SIMS = n_SIMS;
	}
	public String nameN_SIMS() {return("Number of monte-carlo runs");}

	String monteCarloCheckpoint = "";
	
	
	public String getMonteCarloCheckpoint() {
		return monteCarloCheckpoint;
	}

	public void setMonteCarloCheckpoint(String monteCarloCheckpoint) {
		this.monteCarloCheckpoint = monteCarloCheckpoint;
	}

	public boolean isRecordCoreIndicators() {
		return recordCoreIndicators;
	}

	public void setRecordCoreIndicators(boolean recordCoreIndicators) {
		this.recordCoreIndicators = recordCoreIndicators;
		if(recordCoreIndicators) {
			collectors.coreIndicators.setActive(true);
			collectors.creditSupply.setActive(true);
			collectors.householdStats.setActive(true);
			collectors.housingMarketStats.setActive(true);
			collectors.rentalMarketStats.setActive(true);
			try {
				recorder.start();
			} catch (FileNotFoundException | UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
// 		else {
//			recorder.finish();
//		}
	}
	public String nameRecordCoreIndicators() {return("Record core indicators");}

	public boolean isRecordMicroData() {
		return transactionRecorder.isActive();
	}

	public void setRecordMicroData(boolean record) {
		transactionRecorder.setActive(record);
	}
	public String nameRecordMicroData() {return("Record micro data");}


}
