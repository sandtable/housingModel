package utilities;

import java.util.Iterator;

public class IteratorOfType<T> implements Iterator<T> {
	public IteratorOfType(Class<T> iclazz, Iterator<? super T> iUnderlyingIterator) {
		classFilter = iclazz;
		underlyingIterator = iUnderlyingIterator;
		getNext();
	}
	
	@Override
	public boolean hasNext() {
		return(nextElement != null);
	}

	@Override
	public T next() {
		T n = nextElement;
		getNext();
		return(n);
	}

	@Override
	public void remove() {
		// can't have remove and hasNext()!
		throw(new UnsupportedOperationException());
	}

//	@Override
//	public void forEachRemaining(Consumer<? super T> action) {
//		while(hasNext()) action.accept(next());
//	}
	
	private void getNext() {
		Object element;
		nextElement = null;
		do {
			element = underlyingIterator.next();
			if(classFilter.isInstance(element)) {
				nextElement = classFilter.cast(element);
				break;
			}
		} while(underlyingIterator.hasNext());
	}
	
	
	Class<T> classFilter;
	Iterator<? super T> underlyingIterator;
	T nextElement;
}
