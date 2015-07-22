package development;


import java.util.Comparator;
import java.util.TreeSet;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.DecompositionSolver;
import org.apache.commons.math3.linear.LUDecomposition;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

//import testing.Demographics.Birth;
import utilities.DoubleUnaryOperator;
import utilities.ModelTime;
import utilities.Pdf;

public class SetOfHouseholds extends TreeSet<Household> implements ITriggerable {
	
	public SetOfHouseholds() {
//		super(new Comparator<Household>() {
//			public int compare(Household h0, Household h1) {
//				return((int)Math.signum(h0.asLifecycle.birthday.raw() - h1.asLifecycle.birthday.raw()));
//			}
//		});
		// nextBirthEvent().schedule(this); // set birth going
	}
	
	/***
	 * Triggered at each household birth event
	 */
	public void trigger() {
		add(Model.root.newHousehold());
		nextBirthEvent().schedule(this);
	}
	
	public ITrigger nextBirthEvent() {
		Model model = Model.root;		
		double birthsPerYear;
		
		if(ModelTime.now().isBefore(ModelTime.years(SPINUP_YEARS))) {
			// --- still in spinup phase of simulation
			birthsPerYear = spinupBirthRatePerHousehold.getEntry((int)(ModelTime.now().inYears()))*TARGET_POPULATION;
		} else {
			// --- in projection phase of simulation
			birthsPerYear = futureBirthRate(ModelTime.now().inMonths());
		}
		double delay = -Math.log(1.0 - model.random.nextDouble())/birthsPerYear; // inverse CDF of exponential distribution
		return(Trigger.timeIs(ModelTime.now().plus(ModelTime.years(delay))));
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
		double baseAge	= Lifecycle.pdfHouseholdAgeAtBirth.start;
		int i,j;
		
		// --- setup vectors
		for(i=0; i<SPINUP_YEARS; ++i) {
			birthDist.setEntry(i, Lifecycle.pdfHouseholdAgeAtBirth.p(baseAge+i));
			targetDemographic.setEntry(i,pdfAge.p(baseAge+i));
		}
		
		// --- setup timestep matrix
		for(i=0; i<SPINUP_YEARS; ++i) {
			for(j=0; j<SPINUP_YEARS; ++j) {
				if(i == j+1) {
					timeStep.setEntry(i,j,1.0-Lifecycle.probDeathGivenAge(j + baseAge));
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
	
	/**
	 * Target probability density of age of representative householder
	 * at time t=0
	 */
	public static Pdf pdfAge = new Pdf(18.0, 100.0, new DoubleUnaryOperator() {
		public double applyAsDouble(double age) {
			if(age > 18.0 && age < 50.0) {
				return(0.01125 + 0.0002734375*(age-18.0));
			}
			if(age <100.0) {
				return(0.02-0.0004*(age-50.0));
			}
			return(0.0);	
		}
	});

	
	public static final int TARGET_POPULATION = 5000;  	// target number of households
	public static final int SPINUP_YEARS = 80;			// number of years to spinup
	public static RealVector spinupBirthRatePerHousehold = spinupBirthRate(); // birth rate per year by year per household-at-year-0
	private static final long serialVersionUID = 2235358345257196878L;
}
