package com.ngisystems;

public class Main {

    public static void main(String[] args) {
	    ConcurrentDynamicPriorityQueue<Integer> q = new ConcurrentDynamicPriorityQueue<>();

	    q.add(1);
	    q.add(4);
	    q.add(7);
	    q.add(5);
	    q.add(6);

	    System.out.println("Done");
    }
}
