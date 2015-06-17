package utilities;

import java.util.Comparator;
import java.util.TreeSet;

/***
 * A 2-dimensional priority queue: The items in the queue have two unrelated orderings:
 * X and Y. For a given p, we can extract the object with the Y-greatest entry that is
 * not X-greater than p.
 * 
 * Extraction has amortised complexity of O(sqrt(N))
 * Insertion has complexity O(log(N))
 *  
 *  Objects inserted into PriorityQueue2D must implement the interface
 *  PriorityQueue2D::Comparable
 *  
 * @author daniel
 *
 */
public class PriorityQueue2D<E extends PriorityQueue2D.Comparable<E>> {
	public interface Comparable<T> {
		/***
		 * @return (-1, 0, 1) if this is (less than, equal to, greater than) other
		 */
		public int XCompareTo(T other);
		public int YCompareTo(T other);
	}
	
	public PriorityQueue2D() {
		uncoveredElements = new TreeSet<E>(new Comparator<E>() {
			@Override
			public int compare(E arg0, E arg1) {
				return(arg0.XCompareTo(arg1));
			}

		});
		ySortedElements = new TreeSet<E>(new Comparator<E>() {
			@Override
			public int compare(E arg0, E arg1) {
				return(arg0.YCompareTo(arg1));
			}

		});
	}
	
	public boolean add(E element) {
		ySortedElements.add(element);
		if(isUncovered(element)) {
			uncoveredElements.add(element);
			// remove any members of uncoveredElements that are covered by the new element
			E nextHigher = uncoveredElements.higher(element);
			while(nextHigher != null && element.YCompareTo(nextHigher) == 1) {
				uncoveredElements.remove(nextHigher);
				nextHigher = uncoveredElements.higher(element);
			}
		}
		return(true);
	}
	
	public E poll(E xGreatestBoundary) {
		E head = uncoveredElements.floor(xGreatestBoundary);
		if(head == null) return(null);
		removeUncovered(head);
		return(head);
	}

	public boolean remove(E element) {
		if(uncoveredElements.contains(element)) {
			removeUncovered(element);
		} else {
			ySortedElements.remove(element);
		}
		return(true);
	}
	
	/***
	 * Removes element. Element must be an uncovered member of
	 * this set. Removing an uncovered element may uncover other elements,
	 * which then need to be added to the uncoveredElements container. This
	 * is done 
	 * 
	 * @param element Element to remove (must be an uncovered member of this set).
	 */
	protected void removeUncovered(E element) {
		uncoveredElements.remove(element);
		ySortedElements.remove(element);
		if(ySortedElements.size() == 0) return;
		boolean inclusive = false;
		E nextxLower = uncoveredElements.lower(element);
		if(nextxLower == null) { // removing the lowest uncovered element
			inclusive = true;
			nextxLower = ySortedElements.first();
			if(element.YCompareTo(nextxLower) == -1) { // y-least element doesn't cover anything
				return;
			}
		}
		E nextxHigher = uncoveredElements.higher(element);
		if(nextxHigher == null) { // removing the highest uncovered element (must be tail of ySortedElements)
			nextxHigher = ySortedElements.last();
			uncoveredElements.add(nextxHigher);
		}
		for(E e : ySortedElements.subSet(nextxLower, inclusive, element, true).descendingSet()) {
			if(e.XCompareTo(nextxHigher) == -1) {
				uncoveredElements.add(e);
				nextxHigher = e;
			}
		}
	}
	
	/***
	 * An element, a, is said to be "covered" by and element, b, iff
	 * b is Y-greater than a and b is X-less than a.
	 * 
	 * By construction, if a is covered by an element is must also
	 * be covered by an uncovered element.
	 * 
	 * @param element
	 * @return true if there doesn't exist an element in the
	 * queue that is both Y-greater and X-less than the given
	 * element.
	 */
	public boolean isUncovered(E element) {
		E nextLower = uncoveredElements.lower(element);
		if(nextLower == null) {
			return(true);
		}
		if(nextLower.YCompareTo(element) == -1) {
			return(true);
		}
		return(false);
	}
	
	public int size() {return(ySortedElements.size());}
	public int uncoveredSize() {return(uncoveredElements.size());}
	
	//////////////////////////////////////////////
	
	TreeSet<E> uncoveredElements; // x-sorted
	TreeSet<E> ySortedElements;
}
