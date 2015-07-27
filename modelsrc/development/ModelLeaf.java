package development;

import java.util.IdentityHashMap;

public class ModelLeaf implements IModelNode {
	IdentityHashMap<Class<? extends IModelNode>, IModelNode> 		dependencies;
	IModelNode parent;
	
	public ModelLeaf(IModelNode... depends) {
		dependencies = new IdentityHashMap<>(depends.length);
		for(int i=0; i<depends.length; ++i) {
			addDependency(depends[i]);
		}
	}
	
	@Override
	public void start(IModelNode parent) {
		this.parent = parent;
	}

	@Override
	public boolean receive(IMessage message) {
		return(false);
	}
	
	@Override
	public <T extends IModelNode> T get(Class<T> type) {
		if(type == this.getClass()) return(type.cast(this));
		IModelNode trait = dependencies.get(type);
		if(trait != null) return(type.cast(trait));
		return(null);
	}

	@Override
	public <T extends IModelNode> T find(Class<T> type) {
		return parent.find(type);
	}

	@Override
	public IModelNode parent() {
		return parent;
	}

	@Override
	public void die() {
		parent().removeChild(this);
	}

	@Override
	public boolean removeChild(IModelNode child) {
		System.out.println("Strange: a node leaf git a childDied message");
		return(false);
	}

	@Override
	public <T extends IModelNode> T mustGet(Class<T> type) {
		T node = get(type);
		if(node == null) {
			System.err.println("Error getting module dependency for "+getClass()+". Can't get class "+type);
			System.exit(1);
		}
		return(node);
	}

	@Override
	public <T extends IModelNode> T mustFind(Class<T> type) {
		T node = find(type);
		if(node == null) {
			System.err.println("Error finding module dependency for "+getClass()+". Can't find "+type);
			System.exit(1);
		}
		return(node);
	}

	@Override
	public void addDependency(IModelNode externalDependency) {
		dependencies.put(externalDependency.getClass(), externalDependency);
	}

	@Override
	public boolean removeDependency(IModelNode externalDependency) {
		return(dependencies.remove(externalDependency.getClass()) != null);
	}
}
