package testing;

import utilities.ModelTime;

public class Consumer implements IAgentTrait, Message.IReceiver {

	public Consumer(DepositAccount iMyAccount, DepositAccount iShopsAccount, Employee iEmployeeTrait) {
		myAccount = iMyAccount;
		shopsAccount = iShopsAccount;
		lastConsumption = Model.root.timeNow();
		employeeTrait = iEmployeeTrait;
	}
	
	@Override
	public boolean receive(Message message) {
		if(message instanceof IntrospectMessage) {
			consume();
			return(true);
		}
		return false;
	}
	
	/********************************
	 * How much a household consumes
	 * Consumption rule made to fit ONS wealth in Great Britain data.
	 * @author daniel
	 *
	 ********************************/
	public void consume() {
		double monthlyConsumption;
		long consumption;
		ModelTime now = Model.root.timeNow();
		
		monthlyConsumption = 
				10.0*Math.max((myAccount.balance/100.0 - Math.exp(4.07*Math.log(employeeTrait.monthlyIncome()*0.12)-33.1 + 0.2*Model.root.random.nextGaussian())),0.0);
		consumption = (long)(monthlyConsumption * now.minus(lastConsumption).inMonths()); 
		myAccount.transfer(shopsAccount, consumption);
		lastConsumption = now;
	}
	
	DepositAccount 	myAccount;
	DepositAccount 	shopsAccount;
	ModelTime		lastConsumption;
	Employee		employeeTrait;
}
