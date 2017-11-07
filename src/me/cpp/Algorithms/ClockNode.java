package me.cpp.Algorithms;

public class ClockNode {
	
	private String data;
	private int index;
	private ClockNode next;
	
	public ClockNode(String data, int index) {
		this.data = data;
		this.index = index;
	}
	
	public ClockNode(String data, int index, ClockNode next) {
		this.data = data;
		this.next = next;
		this.index = index;
		
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public ClockNode getNext() {
		return next;
	}

	public void setNext(ClockNode next) {
		this.next = next;
	}
	
	public void setIndex(int index) {
		this.index = index;
	}
	
	public int getIndex() {
		return index;
	}
}
