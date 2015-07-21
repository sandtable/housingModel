package utilities;

import java.util.TreeSet;

public class TestPriorityQueue2D {
	
	static class MyComparator implements PriorityQueue2D.XYComparator<MyClass> {
		@Override
		public int XCompare(MyClass arg0, MyClass arg1) {
			return((int)Math.signum(arg0.x-arg1.x));
		}
		@Override
		public int YCompare(MyClass arg0, MyClass arg1) {
			return((int)Math.signum(arg0.y-arg1.y));
		}
	}
	
	static class MyClass {
		public MyClass(double ix, double iy) {
			x = ix; y = iy;
		}
		double x;
		double y;
	}
	/***
	static class MyClass2 implements Comparable<MyClass2> {
		public MyClass2(double ix) {
			x = ix;
		}
		
		@Override
		public int compareTo(MyClass2 arg0) {
			return((int)Math.signum(x-arg0.x));
		}
		double x;
	}
	***/
    public static void main(String[] args) {
    	PriorityQueue2D<MyClass>	myObj = new PriorityQueue2D<MyClass>(new MyComparator());
    	int i;
    	
    	for(i = 0; i<1000; ++i) {
    		myObj.add(new MyClass(Math.random(), Math.random()));
    	}
    	
    	MyClass query = new MyClass(0.5,0.0);
    	MyClass result;
    	while((result = myObj.poll(query)) != null) {
    		System.out.println(myObj.size()+" "+myObj.uncoveredSize()+" "+result.x + ", " + result.y);
    	}
    	System.out.println("Done");
    	query.x = 0.6;
    	while((result = myObj.poll(query)) != null) {
    		System.out.println(myObj.size()+" "+myObj.uncoveredSize()+" "+result.x + ", " + result.y);
    	}    	
    	System.out.println("Done");
    }
}
