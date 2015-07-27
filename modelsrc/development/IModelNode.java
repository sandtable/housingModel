package development;

public interface IModelNode {
	void start(IModelNode parent);					// called before start of simulation
	<T extends IModelNode> T get(Class<T> type);		// find child (or this node) of given type
	<T extends IModelNode> T find(Class<T> type);	// do depth first search of entire model
	<T extends IModelNode> T mustGet(Class<T> type);		// find child (or this node) of given type
	<T extends IModelNode> T mustFind(Class<T> type);	// do depth first search of entire model
	IModelNode parent();
	void die();										// called if this node is to be removed from the simulation
	boolean removeChild(IModelNode child);				// called after the death of a child node
	void addDependency(IModelNode externalDependency);		// allows access to non-child objects via get
	boolean removeDependency(IModelNode externalDependency);		// allows access to non-child objects via get
	boolean receive(IMessage message);				// message reception
}
