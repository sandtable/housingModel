package testing;

import housing.Model;
import utilities.ModelTime;

public class Lifecycle implements IAgentTrait, Message.IReceiver {
	
	public Lifecycle() {
		// prior distribution over age of new households
		this(ModelTime.years(20.0 + Model.rand.nextGaussian()));
	}
	
	public Lifecycle(ModelTime age) {
		birthday = Model.timeNow().minus(age);
	}
	
	@Override
	public boolean receive(Message message) {
		return false;
	}
	
	public ModelTime age() {
		return(Model.timeNow().minus(birthday));
	}

	ModelTime birthday;
	
}
