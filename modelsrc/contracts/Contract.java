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
	final IIssuer	 					issuer; // should be EconAgent?
	IOwner								owner;
	public static final boolean			trace = false;

	public Contract(IIssuer issuer) {
		this.issuer = issuer;
	}
	
	public boolean ownerDiscarded() {
		return(issuer.ownerDiscarded(this));
	}

	public boolean issuerTerminated() {
		return(owner.issuerTerminated(this));
	}

	IIssuer getIssuer() {
		return(issuer);
	}

	IOwner getOwner() {
		return(owner);
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
			if(trace) System.out.println(this+" issuing "+newContract.getClass().getSimpleName()+" to "+owner);
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
				
		public boolean ownerDiscarded(Contract contract) {
			if(trace) {
				if(!contains(contract)) System.out.println("Error: trying to terminate a contract that I didn't issue");
			}
			return(remove(contract));
		}
		
		// issuer terminates (e.g. upon death)
		public boolean terminate(CONTRACT contract) {
			if(remove(contract)) {
				if(contract.issuerTerminated()) {
					return(true);
				} else {
					add(contract); //TODO: what should we do if the owner refuses to terminate?
					return(false);
				}
			}
			return(false);
		}
		
		@Override
		public void die() {
			while(size() > 0) {
				if(!terminate(first())) {
					System.out.println("Error in Contract.Issuer: Can't terminate a contract, but I'm dying!");
				}
			}
			super.die();
		}
		
	}
	
	/***
	 * Agent module for deposit account holder
	 * @author daniel
	 */
	static public class Owner<CONTRACT extends Contract> extends NodeHashSet<CONTRACT> implements IOwner {
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
//			if(newContract instanceof Message.Die) {
//				discardAll();
//				return(true);
			if(newContract.getClass() == getElementClass()) {
				if(trace) System.out.println(this+" received contract "+newContract);
				add(getElementClass().cast(newContract));
				getElementClass().cast(newContract).owner = this;
				return(true);				
			}
			return(false);
		}
				
		public boolean discard(Contract contract) {
			if(!contract.ownerDiscarded()) {
				System.out.println("Error: terminate failed");
				return(false);
			}
			return(remove(contract));
		}
		
//		public boolean discardAll() {
//			boolean result = true;
//			for(Contract c : this) {
//				result = result && discard(c);
//			}
//			return(result);
//		}
		
		@Override
		public void die() {
			while(size() > 0) {
				if(!discard(first())) {
					System.out.println("Error in Contract.Owner: Can't terminate a contract, but I'm dying!");
				}
			}
			super.die();
		}
		
		public boolean issuerTerminated(Contract contract) {
			return(remove(contract));
		}
	}
	
	static public interface IIssuer extends IModelNode {
//		boolean issue(Contract newContract, IMessage.IReceiver owner);
		boolean ownerDiscarded(Contract contract); // termination of the contract early or after execution
	}
	static public interface IOwner extends IModelNode, IMessage.IReceiver {
		boolean discard(Contract contract);
//		boolean discardAll();
		boolean issuerTerminated(Contract contract);
		int size();
	}
}
