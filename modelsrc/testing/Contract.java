package testing;

import java.util.Iterator;

import utilities.IdentityHashSet;

public class Contract {

	public Contract() {
		this(null);
	}
	
	public Contract(IIssuer terminationHandler) {
		issuer = terminationHandler;
	}
	
	public void terminate() {
		if(issuer != null) issuer.terminate(this);
		issuer = null;
	}

	public interface Set {
		public Class<? extends Contract> getElementClass();
		public Iterator<? extends Contract> iterator();
	}
	
	static public class HashSet<CONTRACT extends Contract> extends IdentityHashSet<CONTRACT> implements Set {
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
			add(newContract);
			boolean accepted = owner.receive(newContract, this);
			if(accepted) return(true);
			remove(newContract);
			return(false);
		}
		
		@SuppressWarnings("unchecked")
		public boolean terminate(Contract contract) {
			if(contains(contract)) {
				return(super.remove((CONTRACT)contract));
			}
			return(false);
		}
	}
	
	/***
	 * Agent module for deposit account holder
	 * @author daniel
	 */
	static public class Owner<CONTRACT extends Contract> extends HashSet<CONTRACT> implements IOwner {
		public Owner(Class<CONTRACT> contractClazz) {
			super(contractClazz);
		}
		
		public boolean receive(Contract newContract, IIssuer from) {
			if(newContract.getClass() == contractClazz) {
				add((CONTRACT)newContract);
				return(true);				
			}
			return(false);
		}
		
		public boolean discard(CONTRACT contract) {
			remove(contract);
			contract.terminate();
			return(true);
		}
	}
	
	static public interface IIssuer {
		boolean terminate(Contract contract);
	}

	static public interface IOwner {
		boolean receive(Contract newContract, IIssuer from);
	}
	
	IIssuer	 		issuer; // should be EconAgent?

}
