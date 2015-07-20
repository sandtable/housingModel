package testing;

import java.util.Iterator;

import utilities.IdentityHashSet;
/***
 * A contract is something that may sit on an economic agent's balance
 * sheet. A contract has a defined issuer and owner, in contrast to an
 * Agreement which refers to issuer and owner only by pronoun (you and I).
 * 
 * @author daniel
 *
 */
public class Contract implements IMessage {

//	public Contract() {
//		this(null);
//	}
	
	public Contract(IIssuer terminationHandler) {
		issuer = terminationHandler;
	}
	
	public boolean terminate() {
		if(issuer != null && issuer.terminate(this)) {
			issuer = null;
			return(true);
		}
		return(false);
	}

//	public interface Set {
//		public Class<? extends Contract> getElementClass();
//		public Iterator<? extends Contract> iterator();
//	}
	
	static public class HashSet<CONTRACT extends Contract> extends IdentityHashSet<CONTRACT> {
		public HashSet(Class<CONTRACT> clazz) {
			contractClazz = clazz;
		}
		
		public Class<? extends Contract> getElementClass() {
			return(contractClazz);
		}
		
		Class<CONTRACT> contractClazz;
	}
	
	static public class Issuer<CONTRACT extends Contract> extends HashSet<CONTRACT> implements IIssuer {
		public Issuer(Class<CONTRACT> contractClazz) {
			super(contractClazz);
		}
		public boolean issue(CONTRACT newContract, Owner<CONTRACT> owner) {
			if(owner == null) return(false);
			add(newContract);
			boolean accepted = owner.receive(newContract);
			if(accepted) return(true);
			remove(newContract);
			return(false);
		}
		
		public boolean terminate(Contract contract) {
			return(super.remove(contract));
		}
	}
	
	/***
	 * Agent module for deposit account holder
	 * @author daniel
	 */
	static public class Owner<CONTRACT extends Contract> extends HashSet<CONTRACT> implements IMessage.IReceiver, IAgentTrait {
		public Owner(Class<CONTRACT> contractClazz) {
			super(contractClazz);
		}
		
		public boolean receive(IMessage newContract) {
			if(newContract instanceof Message.Die) {
				discardAll();
				return(true);
			} else if(newContract.getClass() == contractClazz) {
				add(contractClazz.cast(newContract));
				return(true);				
			}
			return(false);
		}
		
		public boolean discard(Contract contract) {
			remove(contract);
			return(contract.terminate());
		}
		
		public boolean discardAll() {
			boolean result = true;
			for(Contract c : this) {
				result = result && discard(c);
			}
			return(result);
		}
	}
	
	static public interface IIssuer extends IAgentTrait {
		boolean terminate(Contract contract);
	}
	
	IIssuer	 		issuer; // should be EconAgent?

}
