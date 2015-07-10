package utilities;

import java.util.ArrayList;
import java.util.Iterator;

public class FlattenedIterable<T> implements Iterable<T> {
	public FlattenedIterable(Iterable<? extends Iterable<T> > iCollectionOfCollections) {
		collectionOfCollections = iCollectionOfCollections;
	}
	

	@Override
	public Iterator<T> iterator() {
		return(this.new FlattenedIterator());
	}
	
	
	Iterable<? extends Iterable<T> > collectionOfCollections;
	
	class FlattenedIterator implements Iterator<T> {
		public FlattenedIterator() {
			currentCollection = collectionOfCollections.iterator();
			currentIterator = (new ArrayList<T>(0)).iterator();
		}
		
		@Override
		public boolean hasNext() {
			if(currentIterator.hasNext() == false) nextCollection();
			return(currentCollection.hasNext());
		}

		@Override
		public T next() {
			return(currentIterator.next());
		}

		@Override
		public void remove() {
			currentIterator.remove();
		}

		private void nextCollection() {
			while(currentCollection.hasNext() && !currentIterator.hasNext()) {
				currentIterator = currentCollection.next().iterator();
			}
		}
		
		Iterator<? extends Iterable<T> > currentCollection;
		Iterator<T> currentIterator;
	}
}
