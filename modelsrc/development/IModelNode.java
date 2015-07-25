package development;

public interface IModelNode {
	public void start(IModelNode parent);					// called before start of simulation
	public <T extends IModelNode> T get(Class<T> type);		// find child (or this node) of given type
	public <T extends IModelNode> T find(Class<T> type);	// do depth first search of entire model
	IModelNode parent();
	public void die();										// called if this node is removed from the simulation
	public boolean removeChild(IModelNode child);				// called after the death of a child node
}
