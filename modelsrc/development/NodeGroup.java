package development;

import java.util.HashSet;
import java.util.Iterator;

public class NodeGroup<NODETYPE extends IModelNode> extends ModelLeaf implements Iterable<NODETYPE> {
	public NodeGroup() {
		set = new HashSet<>();
		hasStarted = false;
	}
	
	@Override
	public void start(IModelNode parent) {
		super.start(parent);
		for(IModelNode child : set) {
			child.start(this);
		}
		hasStarted = true;
	}
	
	public void add(NODETYPE newNode) {
		set.add(newNode);
		if(hasStarted) newNode.start(this);
	}
	
	public boolean kill(IModelNode node) {
		if(set.contains(node)) {
			node.die(); // node will remove itself via removeChild()
			return(true);
		}
		return(false);
	}
	
	@Override
	public boolean removeChild(IModelNode node) {
		return(set.remove(node));
	}
	
	public int size() {
		return set.size();
	}
	
	@Override
	public void die() {
		for(IModelNode node : set) {
			node.die();
		}
		set.clear();
		super.die();
	}
	
	public HashSet<NODETYPE> set;

	@Override
	public Iterator<NODETYPE> iterator() {
		return set.iterator();
	}
	
	boolean hasStarted;
}
