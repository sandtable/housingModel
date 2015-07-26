package contracts;

import development.IMessage;

public class DemandForPayment implements IMessage {
	public DemandForPayment(DepositAccount iPayeeAC, long iAmount, Contract iContract) {
		payeeAC = iPayeeAC;
		amount = iAmount;
		contract = iContract;
	}
	
	public void pay(DepositAccount payoutAC) {
		payoutAC.transfer(payeeAC, amount);
	}
	
	public DepositAccount 	payeeAC;
	public long			amount;
	public Contract		contract;	// the contract that gave rise to this demand
}
