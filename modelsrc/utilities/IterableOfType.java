package utilities;

import java.util.Iterator;
public class IterableOfType<T> implements Iterable<T> {
	public IterableOfType(Class<T> clazz, Iterable<? super T> iCollection) {
		elementClazz = clazz;
		collection = iCollection;
	}
	
	public Iterator<T> iterator() {
		return(new IteratorOfType<T>(elementClazz, collection.iterator()));
	}
	Class<T> elementClazz;
	Iterable<? super T> collection;
}
