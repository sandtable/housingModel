package contracts;

import development.EconAgent;
import development.IMessage;
import development.IModelNode;
import development.Message;
import development.NodeHashSet;

/***
 * A contract is something that may sit on an economic agent's balance
 * sheet. A contract has a defined issuer and owner, in contrast to an
 * Agreement which refers to issuer and owner only by pronoun (you and I).
 * 
 * @author daniel
 *
 */
public class Contract implements IMessage {
	IIssuer	 		issuer; // should be EconAgent?
	public static final boolean			trace = true;

	public Contract(IIssuer issuer) {
		this.issuer = issuer;
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
		
	static public class Issuer<CONTRACT extends Contract> extends NodeHashSet<CONTRACT> implements IIssuer {
		public Issuer(Class<? extends CONTRACT> contractClazz) {
			super(contractClazz);
		}
		
		@Override
		public boolean receive(IMessage message) {
			return(parent().receive(message));
		}
		
		public boolean issue(CONTRACT newContract, IMessage.IReceiver owner) {
			if(owner == null) return(false);
			if(trace) System.out.println(this.getClass().getName()+" issuing "+newContract.getClass().getSimpleName()+" to "+owner.getClass().getName());
			add(newContract);
			boolean accepted = owner.receive(newContract);
			if(accepted) {
				if(trace) System.out.println("Contract accepted");
				return(true);
			}
			if(trace) System.out.println("Contract rejected");
			remove(newContract);
			return(false);
		}
				
		public boolean terminate(Contract contract) {
			return(remove(contract));
		}		
	}
	
	/***
	 * Agent module for deposit account holder
	 * @author daniel
	 */
	static public class Owner<CONTRACT extends Contract> extends NodeHashSet<CONTRACT> implements IMessage.IReceiver {
		public Owner(Class<? extends CONTRACT> contractClazz) {
			super(contractClazz);
		}
		
		@Override
		public void start(IModelNode parent) {
			super.start(parent);
			if(parent instanceof EconAgent) {
				((EconAgent)parent).registerHandler(getElementClass(), this);
			}
		}
		
		public boolean receive(IMessage newContract) {
			if(newContract instanceof Message.Die) {
				discardAll();
				return(true);
			} else if(newContract.getClass() == getElementClass()) {
				if(trace) System.out.println(this.getClass().getName()+" received contract "+newContract.getClass().getSimpleName());
				add(getElementClass().cast(newContract));
				return(true);				
			}
			return(false);
		}
				
		public boolean discard(Object contract) {
			if(!remove(contract) ) {
				return(false);
			}
			return(((Contract)contract).terminate());
		}
		
		public boolean discardAll() {
			boolean result = true;
			for(Contract c : this) {
				result = result && discard(c);
			}
			return(result);
		}
	}
	
	static public interface IIssuer extends IModelNode {
		boolean terminate(Contract contract); // termination of the contract early or after execution
	}
}
