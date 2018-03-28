package com.app.endpoints.entities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.app.endpoints.utilities.Pair;

public class PositionZPriorityContainer {

	int priority_c;
	int priority_1b;
	int priority_2b;
	int priority_ss;
	int priority_3b;
	int priority_of;
	int priority_p;
	int priority_dh;
	
	List<String> pos_priority;

	public PositionZPriorityContainer() {
		
		this.priority_c = -1;
		this.priority_1b = -1;
		this.priority_2b = -1;
		this.priority_ss = -1;
		this.priority_3b = -1;
		this.priority_of = -1;
		this.priority_p = -1;
		this.priority_dh = -1;

	}
	
	public PositionZPriorityContainer(double zc, double z1b, double z2b, double zss, 
			double z3b, double zof, double zp, double zdh) {
		
		List<Pair<String,Double>> listpair = new ArrayList<Pair<String,Double>>();
		pos_priority = new ArrayList<String>();
		
		listpair.add(Pair.of("c", zc));
		listpair.add(Pair.of("1b", z1b));
		listpair.add(Pair.of("2b", z2b));
		listpair.add(Pair.of("ss", zss));
		listpair.add(Pair.of("3b", z3b));
		listpair.add(Pair.of("of", zof));
		listpair.add(Pair.of("p", zp));
		
	    Comparator<Pair<String, Double>> comparator = new Comparator<Pair<String, Double>>()
	    	    {

	    	        public int compare(Pair<String, Double> tupleA,
	    	        		Pair<String, Double> tupleB)
	    	        {
	    	            return tupleA.getSecond().compareTo(tupleB.getSecond());
	    	        }

	    	    };

	    Collections.sort(listpair, comparator);
	    
	    // Collections.reverse(listpair);
	    
	    for (Pair p : listpair){
	    	pos_priority.add((String) p.getFirst());
	    }
		
		
		this.priority_c = -1;
		this.priority_1b = -1;
		this.priority_2b = -1;
		this.priority_ss = -1;
		this.priority_3b = -1;
		this.priority_of = -1;
		this.priority_p = -1;
		this.priority_dh = -1;

	}

	public List<String> getPos_priority() {
		return pos_priority;
	}

	public void setPos_priority(List<String> pos_priority) {
		this.pos_priority = pos_priority;
	}

	public int getPriority_c() {
		return priority_c;
	}

	public void setPriority_c(int priority_c) {
		this.priority_c = priority_c;
	}

	public int getPriority_1b() {
		return priority_1b;
	}

	public void setPriority_1b(int priority_1b) {
		this.priority_1b = priority_1b;
	}

	public int getPriority_2b() {
		return priority_2b;
	}

	public void setPriority_2b(int priority_2b) {
		this.priority_2b = priority_2b;
	}

	public int getPriority_ss() {
		return priority_ss;
	}

	public void setPriority_ss(int priority_ss) {
		this.priority_ss = priority_ss;
	}

	public int getPriority_3b() {
		return priority_3b;
	}

	public void setPriority_3b(int priority_3b) {
		this.priority_3b = priority_3b;
	}

	public int getPriority_of() {
		return priority_of;
	}

	public void setPriority_of(int priority_of) {
		this.priority_of = priority_of;
	}

	public int getPriority_p() {
		return priority_p;
	}

	public void setPriority_p(int priority_p) {
		this.priority_p = priority_p;
	}

	public int getPriority_dh() {
		return priority_dh;
	}

	public void setPriority_dh(int priority_dh) {
		this.priority_dh = priority_dh;
	}


}
