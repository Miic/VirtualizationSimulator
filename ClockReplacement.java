package me.cpp.Algorithms;

public class ClockReplacement {
	private ClockNode pointer;
	private int length;
	
	//Never exposed outside the class. Just used for re-stiching the list back to a circle
	private ClockNode sudoHead;
	private ClockNode sudoTail;
	
	private ClockReplacement(int[][] data) {
		if (data.length > 0) {
			sudoHead = new ClockNode(data[0]);
			sudoTail = new ClockNode(data[data.length-1], sudoHead);
			pointer = sudoHead;
			for(int i = 1; i < data.length - 1; i++) {
				ClockNode temp = new ClockNode(data[i]);
				pointer.setNext(temp);
				pointer = temp;
			}
			pointer = sudoHead;
			length = data.length;
		} else {
			sudoHead = null;
			sudoTail = null;
			pointer = null;
		}
	} 
	
	public int[][] add(int[] data) {
		ClockNode startNode = pointer;
		do {
			if (!checkValidity(pointer.getData())) {
				pointer.setData(data);
				break;
			}
		} while (pointer != startNode);
		
		int[][] returnTable = new int[length][];
		ClockNode tempPointer = sudoHead;
		int counter = 0;
		do {
			returnTable[counter] = tempPointer.getData();
			counter++;
		} while(tempPointer != sudoHead);
		return returnTable;
	}
	
	private boolean checkValidity(int[] data) {
		//Placeholder: Depends on how partner sets up the TLB.
		return true;
	}
}
