package me.cpp.Algorithms;

public class ClockNode {
	
	private int[] data;
	private ClockNode next;
	
	public ClockNode(int[] data) {
		this.data = data;
	}
	
	public ClockNode(int[] data, ClockNode next) {
		this.data = data;
		this.next = next;
	}

	public int[] getData() {
		return data;
	}

	public void setData(int[] data) {
		this.data = data;
	}

	public ClockNode getNext() {
		return next;
	}

	public void setNext(ClockNode next) {
		this.next = next;
	}
}
