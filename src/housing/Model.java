package housing;

import housing.LifecycleHousehold.Status;

import java.util.ArrayList;

import sim.engine.SimState;
import sim.engine.Steppable;
import ec.util.MersenneTwisterFast;

/**
 * Simulates the housing market.
 * 
 * @author daniel
 *
 */
@SuppressWarnings("serial")
public class Model extends SimState implements Steppable {

	public Model(long seed) {
		super(seed);
	}

	// CREATE HOUSEHOLDS, PERSONS
	public void start() {
		super.start();
        schedule.scheduleRepeating(this);
        
        int j;
		for(j = 0; j<N; ++j) { 
			households.add(new LifecycleHousehold());
			//System.out.println("Household created: " + j);
		}
		t=0;
		System.out.println();
		System.out.println("Number of households, Total: " + LifecycleHousehold.HouseholdTotalCount);
		System.out.println("Number of households, Single: " + LifecycleHousehold.HouseholdSingleCount);
		System.out.println("Number of households, Married: " + LifecycleHousehold.HouseholdMarriedCount);
		System.out.println("Number of people: " + Person.PersonCount);
		System.out.println();
	}
	
	// STEP
	public void step(SimState simulationStateNow) {
		int j;
        if (schedule.getTime() >= N_STEPS) simulationStateNow.kill();
        
		System.out.println("Step " + t + " begins ...");	
		
		for (LifecycleHousehold h : households) {
			h.step();
			if(h.personsInHousehold == 0) {
				//h.inherit();
				
				LifecycleHousehold.HouseholdTotalCount = LifecycleHousehold.HouseholdTotalCount - 1;
				if (LifecycleHousehold.HouseholdTotalCount == 0) simulationStateNow.kill();
				if(h.status == Status.SINGLE) {
					LifecycleHousehold.HouseholdSingleCount = LifecycleHousehold.HouseholdSingleCount - 1;					
				}
				else {
					LifecycleHousehold.HouseholdMarriedCount = LifecycleHousehold.HouseholdMarriedCount - 1;
				}
				h = null;
			}
		}
		t++;
		
	}
	
	
	// FINISH
	public void finish() {
		
		System.out.println();
		System.out.println("Number of households, Total: " + LifecycleHousehold.HouseholdTotalCount);
		System.out.println("Number of households, Single: " + LifecycleHousehold.HouseholdSingleCount);
		System.out.println("Number of households, Married: " + LifecycleHousehold.HouseholdMarriedCount);
		System.out.println("Number of people: " + Person.PersonCount);
		System.out.println();
		super.finish();
	}
	
	

	////////////////////////////////////////////////////////////////////////
	// Getters/setters for the console
	////////////////////////////////////////////////////////////////////////
	
/*
	public LifecycleHousehold.Config getLifecycleConfig() {
		return(new LifecycleHousehold.Config());
	}
*/	

	public static int getN_STEPS() {
		return N_STEPS;
	}

	public static void setN_STEPS(int n_STEPS) {
		N_STEPS = n_STEPS;
	}

	public String nameN_STEPS() {return("Number of timesteps");}

	////////////////////////////////////////////////////////////////////////

	public static final int N = 1; // number of households	
	public static final int Nh = 4100; // number of houses
	public static int N_STEPS = 12*100; // timesteps

	public static Firm								firm;
	public static ArrayList<LifecycleHousehold> 	households = new ArrayList<LifecycleHousehold>(N);
	public static int 								t;
	public static MersenneTwisterFast				rand = new MersenneTwisterFast(1L);
	
	
	
}
