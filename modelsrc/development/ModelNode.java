package development;

import java.util.IdentityHashMap;


public class ModelNode implements IModelNode {
	IdentityHashMap<Class<? extends IModelNode>, IModelNode> 		children;
	IModelNode	parent;
	
	public ModelNode(IModelNode... children) {
		int i;
		this.children = new IdentityHashMap<>(children.length);
		for(i=0; i<children.length; ++i) {
			addTrait(children[i]);
		}
	}

	public <T extends IModelNode> T get(Class<T> type) {
		IModelNode trait = children.get(type);
		if(trait != null) return(type.cast(trait));
		if(type == this.getClass()) return(type.cast(this));
		return(null);
	}

	public <T extends IModelNode> T find(Class<T> type) {
		return(parent.find(type));
	}
	
	public void addTrait(IModelNode trait) {
		children.put(trait.getClass(), trait);
	}
	
	public void removeTrait(IModelNode trait) {
		children.remove(trait);
	}

	@Override
	public void start(IModelNode parent) {
		this.parent = parent;
		for(IModelNode child : children.values()) {
			child.start(this);
		}
	}

	@Override
	public IModelNode parent() {
		return parent;
	}

	@Override
	public void die() {
		for(IModelNode child : children.values()) {
			child.die();
		}
		parent().removeChild(this);
	}

	@Override
	public boolean removeChild(IModelNode child) {
		return(children.remove(child) != null);
	}
}
