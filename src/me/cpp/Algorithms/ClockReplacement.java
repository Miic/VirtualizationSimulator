package me.cpp.Algorithms;

public class ClockReplacement {
	private ClockNode pointer;
	
	private ClockNode sudoHead;
	private ClockNode sudoTail;
	
	private VPageTable table;
	
	/**
	 * Creates an internal list version of a passed VPageTable
	 * @param table VPageTable used 
	 */
	
	public ClockReplacement(VPageTable table) {
		this.table = table;
		String[] data = table.toArray();
		if (data.length > 0) {
			sudoHead = new ClockNode(data[0], 0);
			pointer = sudoHead;
			for(int i = 1; i < data.length - 1; i++) {
				ClockNode temp = new ClockNode(data[i], i);
				pointer.setNext(temp);
				pointer = temp;
			}
			sudoTail = pointer;
			sudoTail.setNext(sudoHead);
			pointer = sudoHead;
		} else {
			sudoHead = null;
			sudoTail = null;
			pointer = null;
		}
	} 
	
	/**
	 * Initiates Clock Replacement algorithm, signature intentionally designed to match VPageTable setEntry method.
	 * @param valid Sets Valid Bit - This is usually 1
	 * @param ref Sets Reference bit
	 * @param dirty Sets Dirty Bit
	 * @param pageFrame Sets PageFrameAddress
	 */
	
	public void add(int valid, int ref, int dirty, String pageFrame) {
		ClockNode startNode = pointer;
		boolean flag = false;
		do {
			if (table.getValidBit(pointer.getIndex()) == 0) {
				table.setEntry(pointer.getIndex(), valid, ref, dirty, pageFrame);
				pointer.setData(table.getEntry(pointer.getIndex()));
				flag = true;
				break;
			}
			pointer = pointer.getNext();
		} while (pointer != startNode);
		
		if (flag) {
			table.setEntry(startNode.getIndex(), valid, ref, dirty, pageFrame);
			pointer.setData(table.getEntry(startNode.getIndex()));
		}
	}
}
