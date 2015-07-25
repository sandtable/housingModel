package development;

public class ModelLeaf implements IModelNode {
	IModelNode vparent;
	
	@Override
	public void start(IModelNode parent) {
		this.vparent = parent;
	}

	@Override
	public <T extends IModelNode> T get(Class<T> type) {
		if(type == this.getClass()) return(type.cast(this));
		return null;
	}

	@Override
	public <T extends IModelNode> T find(Class<T> type) {
		return vparent.find(type);
	}

	@Override
	public IModelNode parent() {
		return vparent;
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

}
