package contracts;

import development.ITriggerable;
import development.Trigger;
import development.Trigger.Repeating;
import utilities.ModelTime;

/***
 * Deposit account with interest payments
 * @author daniel
 *
 */
public class InvestmentAccount extends DepositAccount implements ITriggerable {
	/***
	 * @param sourceAC 			source of interest payments
	 * @param annualInterest
	 * @param interestPeriod	period between interest payments
	 */
	public InvestmentAccount(InvestmentAccount.IIssuer accountIssuer, DepositAccount sourceAC, double annualInterest, ModelTime interestPeriod) {
		super(accountIssuer);
		interestPaymentTrigger = Trigger.repeatingEvery(interestPeriod);
		interestPayment = new InterestPaymentAgreement(sourceAC, this, this, annualInterest);
	}
	
	public void trigger() {
		try {
			interestPayment.trigger();
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/*** start interest payments ***/
	public void activate() {
		interestPayment.resetInterestTimer();
		interestPaymentTrigger.schedule(this);
	}
	
	@Override
	public boolean terminate() {
		if(super.terminate()) {
			interestPaymentTrigger.stop();
			return(true);
		}
		return(false);
	}
	
	Trigger.Repeating interestPaymentTrigger;
	InterestPaymentAgreement interestPayment;
}
