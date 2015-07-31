package development;

import java.util.IdentityHashMap;
import java.util.Iterator;


public class NodeHashSet<E> extends ModelLeaf implements Iterable<E> {
	public Class<? extends E> 					contractClazz;
	public IdentityHashMap<E,Boolean>	data;
	public Boolean						dummy;

	public NodeHashSet(Class<? extends E> clazz) {
		contractClazz = clazz;
		data = new IdentityHashMap<>();
		dummy = new Boolean(false);
	}
	
	public Class<? extends E> getElementClass() {
		return(contractClazz);
	}
		
	public void add(E element) {
		data.put(element, dummy);
	}
	
	public boolean remove(Object element) {
		return(data.remove(element) != null);
	}
	
	public boolean contains(Object element) {
		return(data.containsKey(element));
	}
	
	public Iterator<E> iterator() {
		return(data.keySet().iterator());
	}

	public E first() {
		return(data.keySet().iterator().next());
	}
	
	public int size() {
		return(data.size());
	}
}
