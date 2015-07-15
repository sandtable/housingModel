package testing;

import utilities.ModelTime;

public class Lifecycle implements IAgentTrait, Message.IReceiver {
	
	public Lifecycle() {
		// prior distribution over age of new households
		this(ModelTime.years(20.0 + Model.root.random.nextGaussian()));
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

	ModelTime birthday;
}
