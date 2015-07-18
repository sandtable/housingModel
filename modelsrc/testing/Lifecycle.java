package testing;

import utilities.DoubleUnaryOperator;
import utilities.ModelTime;
import utilities.Pdf;

public class Lifecycle implements IAgentTrait, Message.IReceiver {
	
	public Lifecycle() {
		// prior distribution over age of new households
		this(ModelTime.years(pdfAgeOfNewHousehold.nextDouble()));
	}
	
	public Lifecycle(ModelTime age) {
		birthday = Model.root.timeNow().minus(age);
	}
	
	@Override
	public boolean receive(Message message) {
		return false;
	}
	
	public ModelTime age() {
		return(Model.root.timeNow().minus(birthday));
	}

	/**
	 * Probability density by age of the representative household given that
	 * the household is newly formed.
	 * New households can be formed by, e.g., children leaving home,
	 * divorce, separation, people leaving an HMO.
	 */
	public static Pdf pdfAgeOfNewHousehold = new Pdf(18.0, 28.0, new DoubleUnaryOperator() {
		public double applyAsDouble(double age) {
			if(age>=18.0 && age < 19.0) {
				return(1.0);
			}
//			if(age>=18.0 && age<28.0) 
//				return(0.1);
			return(0.0);
		}	
	});

	ModelTime birthday;
}
