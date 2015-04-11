package housing;

import java.util.ArrayList;

import ec.util.MersenneTwisterFast;
import sim.engine.SimState;
import sim.engine.Steppable;

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

	public void start() {
		super.start();
        schedule.scheduleRepeating(this);
		t=0;
	}
	
	public void step(SimState simulationStateNow) {
		int j;
        if (schedule.getTime() >= N_STEPS) simulationStateNow.kill();
		
		for(j = 0; j<N; ++j) households.get(j).step();

		t++;
	}
	
	public void finish() {
		super.finish();
	}
	
	

	////////////////////////////////////////////////////////////////////////
	// Getters/setters for the console
	////////////////////////////////////////////////////////////////////////
	

	public Household.Config getHouseholdConfig() {
		return(new Household.Config());
	}
	

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
	public static int N_STEPS = 50000; // timesteps

	public static ArrayList<LifecycleBehaviour> 		households = new ArrayList<LifecycleBehaviour>(N);
	public static int 				t;
	public static MersenneTwisterFast			rand = new MersenneTwisterFast(1L);
	
}
