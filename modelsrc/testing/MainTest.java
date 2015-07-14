package testing;

import java.util.ArrayList;

public class MainTest {
    public static void main(String[] args) {
    	Bank	bank = new Bank();
    	Firm	firm = new Firm();
    	ArrayList<Household> households = new ArrayList<>(2);
    	Household household1 = new Household();
    	Household household2 = new Household();
    	households.add(household1);
    	households.add(household2);
    	
    	bank.issueDepositAccounts(households);
    	bank.endowmentAccount.transfer(household1.bankAccount(), 100);
    	bank.endowmentAccount.transfer(household2.bankAccount(), 55);
    	
    	firm.employ(household1);
    	firm.employ(household2);
    	
    	System.out.println("Household 1 = "+household1.bankAccount().balance);
    	System.out.println("Household 2 = "+household2.bankAccount().balance);
    	System.out.println("Total Endowment = "+bank.endowmentAccount.balance);
    }
}
