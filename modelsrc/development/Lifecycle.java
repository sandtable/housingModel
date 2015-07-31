package development;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.DecompositionSolver;
import org.apache.commons.math3.linear.LUDecomposition;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

import contracts.DepositAccount;

import sim.engine.SimState;
import utilities.ModelTime;

public class Lifecycle extends ModelLeaf {	
	public Lifecycle() {
		ModelTime birthAge = ModelTime.years(Data.Lifecycle.pdfHouseholdAgeAtBirth.nextDouble());
		ModelTime deathAge;
		do { // calculate age at death given we're alive at current age
			deathAge = ModelTime.years(Data.Lifecycle.pdfHouseholdAgeAtDeath.nextDouble());
		} while(deathAge.isBefore(birthAge));
		
		birthday = ModelTime.now().minus(birthAge);
		deathday = birthday.plus(deathAge);
		// schedule death
		Trigger.timeIs(deathday).schedule(new ITriggerable() {public void trigger() {die();}});
	}
	
	@Override
	public void start(final IModelNode parent) {
		super.start(parent);
		Trigger.timeIs(deathday).schedule(new ITriggerable() {public void trigger() {parent.die();}});
	}
	
	@Override
	public void die() {
		DepositAccount.Owner accounts = parent().get(DepositAccount.Owner.class);
		Government government = parent().get(Government.class);
		if(accounts != null && government != null) {
			for(DepositAccount ac : accounts) {
				ac.transfer(government.bankAccount(), ac.balance);
			}
		}
		super.die();
	}
			
	public ModelTime age() {
		return(ModelTime.now().minus(birthday));
	}

	ModelTime 	birthday;
	ModelTime 	deathday;	// time of death of this household (decided at birth)

	/////////////////////////////////////////////////////////////////////////////
	// Birth stuff (static)
	/////////////////////////////////////////////////////////////////////////////
	/***
	 * A trigger that will trigger whenever a new household is born. This includes
	 * spin-up period and birth rates into the future.
	 * @author daniel
	 */
	@SuppressWarnings("serial")
	static public class BirthTrigger extends Trigger.PoissonProcess {
		public BirthTrigger() {
			super(ModelTime.days(0.0));
			meanInterval = birthInterval();
			delay = nextDelay();
		}
		
		@Override
		public void step(SimState arg0) {
			meanInterval = birthInterval();
			super.step(arg0);
		}

		/** number of births in unit raw schedule time */
		public ModelTime birthInterval() {
			double birthsPerYear;
			if(ModelTime.now().isBefore(ModelTime.years(SPINUP_YEARS))) {
				// --- still in spin-up phase of simulation
				birthsPerYear = spinupBirthRatePerHousehold.getEntry((int)(ModelTime.now().inYears()))*TARGET_POPULATION;
			} else {
				// --- in projection phase of simulation
				birthsPerYear = futureBirthRate(ModelTime.now().inMonths());
			}
			return(ModelTime.years(1.0/birthsPerYear));
		}
	}
			
	/***
	 * Calculates the birth rate over time so that at the end
	 * of spinup period we hit the target population and age distribution
	 */
	public static RealVector spinupBirthRate() {
		RealVector targetDemographic = new ArrayRealVector(SPINUP_YEARS);
		RealVector birthDist 		 = new ArrayRealVector(SPINUP_YEARS);
		RealMatrix M			 	 = new Array2DRowRealMatrix(SPINUP_YEARS, SPINUP_YEARS);
		RealMatrix timeStep 		 = new Array2DRowRealMatrix(SPINUP_YEARS, SPINUP_YEARS);
		double baseAge	= Data.Lifecycle.pdfHouseholdAgeAtBirth.start;
		int i,j;
		
		// --- setup vectors
		for(i=0; i<SPINUP_YEARS; ++i) {
			birthDist.setEntry(i, Data.Lifecycle.pdfHouseholdAgeAtBirth.p(baseAge+i));
			targetDemographic.setEntry(i,Data.Lifecycle.pdfAge.p(baseAge+i));
		}
		
		// --- setup timestep matrix
		for(i=0; i<SPINUP_YEARS; ++i) {
			for(j=0; j<SPINUP_YEARS; ++j) {
				if(i == j+1) {
					timeStep.setEntry(i,j,1.0-Data.Lifecycle.probDeathGivenAge(j + baseAge));
				} else {
					timeStep.setEntry(i,j,0.0);					
				}
			}
		}
		
		// --- setup aged birth distribution matrix
		for(i=0; i<SPINUP_YEARS; ++i) {
			M.setColumnVector(i, birthDist);
			birthDist = timeStep.operate(birthDist);
		}
		
		DecompositionSolver solver = new LUDecomposition(M).getSolver();
		return(solver.solve(targetDemographic));
	}

	/****
	 * Birth rates into the future
	 * @param t	time (months) into the future
	 * @return number of births per year
	 */
	public static double futureBirthRate(double t) {
		return(TARGET_POPULATION * 0.012);
	}
	
	public static final int TARGET_POPULATION = 5000;  	// target number of households
	public static final int SPINUP_YEARS = 80;			// number of years to spinup
	public static RealVector spinupBirthRatePerHousehold = spinupBirthRate(); // birth rate per year by year per household-at-year-0

}
