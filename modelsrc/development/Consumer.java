package development;

import contracts.DepositAccount;
import utilities.ModelTime;

public class Consumer extends ModelLeaf implements IMessage.IReceiver {
	DepositAccount.Owner 	accounts;
	Firm					shops;
	ModelTime				lastConsumption;
	Employee				employeeTrait;
	ModelRoot				root;
	
	@Override
	public boolean receive(IMessage message) {
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
		ModelTime now = ModelTime.now();
		
		monthlyConsumption = 
				10.0*Math.max((accounts.defaultAccount().balance/100.0 - Math.exp(4.07*Math.log(employeeTrait.monthlyIncome()*0.12)-33.1 + 0.2*root.random.nextGaussian())),0.0);
		consumption = (long)(monthlyConsumption * now.minus(lastConsumption).inMonths()); 
		accounts.defaultAccount().transfer(shops.getSalesAC(), consumption);
		lastConsumption = now;
	}
	
	@Override
	public void start(IModelNode parent) {
		super.start(parent);
		root = parent.mustFind(ModelRoot.class);
		accounts = parent.get(DepositAccount.Owner.class);
		shops = root.get(Firm.class);
		lastConsumption = ModelTime.now();
		employeeTrait = parent.get(Employee.class);
	}
}
