package testing;

import utilities.ModelTime;

/***
 * Deposit account with interest payments
 * @author daniel
 *
 */
public class InvestmentAccount extends DepositAccount implements ITriggerable {
	public InvestmentAccount(DepositAccount sourceAC, double annualInterest, ModelTime interestPeriod) {
		interestPaymentTrigger = Trigger.repeatingEvery(interestPeriod);
		interestPayment = new InterestPaymentAgreement(sourceAC, this, this, annualInterest);
		interestPaymentTrigger.schedule(this);
	}
	
	public void trigger() {
		try {
			interestPayment.trigger();
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	Trigger.RepeatingTrigger interestPaymentTrigger;
	InterestPaymentAgreement interestPayment;
}
