package development;

import java.util.HashSet;

public class ModelNode extends ModelLeaf {
	HashSet<IModelNode> 		children;
	
	public ModelNode(IModelNode... children) {
		super(children);
		this.children = new HashSet<>(children.length);
		for(int i=0; i<children.length; ++i) {
			this.children.add(children[i]);
		}
	}

	public void addChild(IModelNode child) {
		children.add(child);
		addDependency(child);
	}
	
	@Override
	public boolean removeChild(IModelNode child) {
		if(children.remove(child)) {
			removeDependency(child);
			return(true);
		}
		return(false);
	}

	@Override
	public void start(IModelNode parent) {
		super.start(parent);
		for(IModelNode child : children) {
			child.start(this);
		}
	}

	@Override
	public void die() {
		for(IModelNode child : children) {
			child.die();
		}
		super.die();
	}
}
