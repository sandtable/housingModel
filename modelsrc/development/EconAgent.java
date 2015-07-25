package development;

import java.util.IdentityHashMap;

public class EconAgent extends ModelNode implements IMessage.IReceiver {	
	public EconAgent(IModelNode... iTraits) {
		super(iTraits);
		handlers = new IdentityHashMap<>(iTraits.length);
	}
	
	public boolean registerHandler(Class<? extends IMessage> messageClass, IMessage.IReceiver handler) {
		if(handlers.containsKey(messageClass)) {
			System.out.println("EconAgent: Trying to register two handlers for one message type");
			return(false);
		}
		handlers.put(messageClass, handler);
		return(true);
	}

	public boolean unregisterHandler(Class<? extends IMessage> messageClass, IMessage.IReceiver handler) {
		if(handlers.containsKey(messageClass) && handlers.get(messageClass) == handler) {
			handlers.remove(messageClass);
			return(true);
		}
		System.out.println("EconAgent: Trying to unregister unrecognised handler");
		return(false);
	}
	
	public boolean receive(IMessage message) {
		IMessage.IReceiver handler = handlers.get(message.getClass());
		if(handler != null) {
			return(handler.receive(message));	
		}
		if(message instanceof IBroadcastMessage) {
			for(IModelNode trait : children.values()) {
				if(trait instanceof IMessage.IReceiver) {
					((IMessage.IReceiver)trait).receive(message);
				}
			}
			return(true);
		}
		return(false);
	}
		
	IdentityHashMap<Class<? extends IMessage>, IMessage.IReceiver> 	handlers;
}
