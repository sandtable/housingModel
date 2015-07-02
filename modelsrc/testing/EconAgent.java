package testing;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.function.Consumer;

public class EconAgent extends ArrayList<Contract.Set> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3312378780978236238L;
	
	/***
	 * @return an iterator that iterates over all contracts that belong to
	 * type T
	 */
	<T> Iterator<T> iteratorOf(Class<T> runtimeType) {
		return(new TypeFilteredIterator<T>(runtimeType));
	}

	/***
	 * @return An iterable container that contains all the contracts that
	 * belong to type T
	 */
	<T> Iterable<T> setOf(Class<T> runtimeType) {
		return(new TypeFilteredIterable<T>(runtimeType));
	}

	public class TypeFilteredIterable<T> implements Iterable<T> {
		public TypeFilteredIterable(Class<T> clazz) {
			elementClazz = clazz;
		}
		public Iterator<T> iterator() {
			return(new TypeFilteredIterator<T>(elementClazz));
		}
		Class<T> elementClazz;
	}
	
	public class TypeFilteredIterator<T> implements Iterator<T> {
		public TypeFilteredIterator(Class<T> iclazz) {
			classFilter = iclazz;
			moduleIterator = EconAgent.this.iterator();
			nextModule();
		}
		@Override
		public boolean hasNext() {
			return(moduleIterator.hasNext() || contractIterator.hasNext());
		}

		@Override
		public T next() {
			if(!contractIterator.hasNext()) {
				nextModule();
			}
			return(contractIterator.next());
		}

		@Override
		public void remove() {
			contractIterator.remove();
		}

		@Override
		public void forEachRemaining(Consumer<? super T> action) {
			
		}
		
		@SuppressWarnings("unchecked")
		private void nextModule() {
			do {
				currentModule = moduleIterator.next();
			} while(currentModule != null && 
					(!classFilter.isAssignableFrom(currentModule.getElementClass()) ||
					 currentModule.iterator().hasNext() == false));
			if(currentModule != null) {
				contractIterator = (Iterator<T>)currentModule.iterator();
			} else {
				contractIterator = new ArrayList<T>().iterator();
			}
		}
		
		Class<T> classFilter;
		Iterator<T> contractIterator;
		Iterator<Contract.Set> moduleIterator;
		Contract.Set currentModule;
	}
}
