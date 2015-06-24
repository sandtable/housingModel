package testing;

import utilities.ModelTime;

/***
 * Deposit account with interest payments
 * @author daniel
 *
 */
public class InvestmentAccountAgreement extends DepositAccountAgreement implements ITriggerable {
	public InvestmentAccountAgreement(DepositAccountAgreement sourceAC, double annualInterest, ModelTime interestPeriod) {
		interestPaymentTrigger = Trigger.repeatingEvery(interestPeriod);
		interestPayment = new InterestPaymentAgreement(sourceAC, this, this, annualInterest);
		interestPaymentTrigger.schedule(this);
	}
	
	public void trigger() {
		try {
			interestPayment.honour();
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	Trigger.RepeatingTrigger interestPaymentTrigger;
	InterestPaymentAgreement interestPayment;
}
