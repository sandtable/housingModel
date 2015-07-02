package utilities;

import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Iterator;

import testing.Contract;

public class IdentityHashSet<E> {
	public IdentityHashSet() {
		data = new IdentityHashMap<>();
	}
	
	public boolean add(E element) {
		return(data.put(element, dummy));
	}
	
	public boolean remove(E element) {
		return(data.remove(element));
	}
	
	public boolean contains(Object element) {
		return(data.containsKey(element));
	}
	
	public Iterator<E> iterator() {
		return(data.keySet().iterator());
	}
	
	//HashSet<E>				test;
	IdentityHashMap<E,Boolean>	data;
	Boolean						dummy=new Boolean(false);
}
