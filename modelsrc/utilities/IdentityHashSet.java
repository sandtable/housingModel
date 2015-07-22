package utilities;

import java.util.IdentityHashMap;
import java.util.Iterator;

public class IdentityHashSet<E> implements Iterable<E> {
	public IdentityHashSet() {
		data = new IdentityHashMap<>();
		dummy = new Boolean(false);
	}
	
	public void add(E element) {
		data.put(element, dummy);
	}
	
	public boolean discard(Object element) {
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
	
	
	//HashSet<E>				test;
	public IdentityHashMap<E,Boolean>	data;
	public Boolean						dummy;//=new Boolean(false);
}
