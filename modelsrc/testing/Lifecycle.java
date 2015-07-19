package testing;

import utilities.DoubleUnaryOperator;
import utilities.ModelTime;
import utilities.Pdf;

public class Lifecycle implements IAgentTrait, Message.IReceiver {
	
//	public Lifecycle() {
		// prior distribution over age of new households
//		this(ModelTime.years(pdfAgeOfNewHousehold.nextDouble()));
//	}
	
	public Lifecycle() {
		ModelTime birthAge = ModelTime.years(pdfHouseholdAgeAtBirth.nextDouble());
		ModelTime deathAge;
		do {
			deathAge = ModelTime.years(pdfHouseholdAgeAtDeath.nextDouble());
		} while(deathAge.isBefore(birthAge));
		birthday = Model.root.timeNow().minus(birthAge);
		deathday = birthday.plus(deathAge);
	}
	
	@Override
	public boolean receive(Message message) {
		return false;
	}
	
	public ModelTime age() {
		return(Model.root.timeNow().minus(birthday));
	}

	/**
	 * Probability density by age of the representative householder given that
	 * the household is newly formed.
	 * New households can be formed by, e.g., children leaving home,
	 * divorce, separation, people leaving an HMO.
	 */
	public static Pdf pdfHouseholdAgeAtBirth = new Pdf(18.0, 28.0, new DoubleUnaryOperator() {
		public double applyAsDouble(double age) {
			if(age>=18.0 && age < 19.0) {
				return(1.0);
			}
//			if(age>=18.0 && age<28.0) 
//				return(0.1);
			return(0.0);
		}	
	});

	/***
	 * Probability that a household 'dies' per year given age of the representative householder
	 * Death of a household may occur by marriage, death of single occupant, moving together
	 */
	public static double probDeathGivenAge(double ageInYears) {
		return(SetOfHouseholds.futureBirthRate(0)*1.0/SetOfHouseholds.TARGET_POPULATION);
	}

	/***
	 * This calculates the pdf of Household age at death from probDeathGivenAge() according to
	 * 
	 * P(a) = r(a) exp(-integral_0^a r(a') da')
	 * 
	 * where r(a) is probDeathGivenAge.
	 * 
	 */
	public static Pdf pdfHouseholdAgeAtDeath = new Pdf(0.0, 110.0, new DoubleUnaryOperator() {
		public double applyAsDouble(double age) {
			if(age < 110.0) {
				double a = 0.0;
				double integral = 0.0;
				double p;
				do {
					p = probDeathGivenAge(a + 0.5);
					integral += p;
					a += 1.0;
				} while(a<=age);
				integral -= (a - age)*p;
				return(p*Math.exp(-integral));
			}
			return(0.0);
		}
	}, 50);

	ModelTime birthday;
	ModelTime deathday;	// time of death of this household (decided at birth)
}
