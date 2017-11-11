package me.cpp.Algorithms;

public class Clock {
	private ClockNode pointer;
	private ClockNode sudoHead;
	private ClockNode sudoTail;
	
	private VPageTable table;
	private PhysicalMemory memory;
	private CPU processor;
	
	/**
	 * Creates an internal list version of a passed VPageTable
	 * @param table VPageTable used 
	 */
	
	public Clock(VPageTable table, PhysicalMemory memory, CPU processor) {
		this.table = table;
		this.memory = memory;
		this.processor = processor;
		String[][] data = memory.toArray();
		System.out.println("memory.length: " + memory.toArray().length);
		if ( memory.toArray().length > 0) {
			sudoHead = new ClockNode(0);
			pointer = sudoHead;
			for(int i = 1; i < data.length - 1; i++) {
				ClockNode temp = new ClockNode(i);
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
	
	public int newPage(OS os, int vPage) {
		int pageFrame = -1;
		int temp = -1;
		if ( !memory.memFull ) {
			// Memory is not full
			pageFrame = memory.newPage();
			pointer.setVPageFrame(pageFrame);
			temp = pointer.getIndex();
			pointer = pointer.getNext();
		}
		else {
			// Memory is full, swapping required
			ClockNode startNode = pointer;
			do {
				int ref = table.getRefBit(pointer.getVPageFrame());
				if ( ref == 0 ) {
					// Swapping this page out
					int dirty = table.getDirtyBit(pointer.getVPageFrame());
					if ( dirty == 1 ) {
						// Data is dirty, writing to disk
						os.writePageFile(numconv.getHex(pointer.getVPageFrame(), 2));
					}
					// Safety precaution to set the valid bit to 0, meaning the data is no longer trustworthy
					table.setValidBit(pointer.getVPageFrame(), 0);
					processor.TLBsetValidBit(pointer.getVPageFrame(), 0);
					pointer.setVPageFrame(vPage);
					temp = pointer.getIndex();
					pointer = pointer.getNext();
					break;
				}
				pointer = pointer.getNext();
			} while(pointer != startNode);
			
		}
		return temp;
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
		boolean flag = true;
		do {
			if (table.getValidBit(pointer.getIndex()) == 0) {
				//Dirty bit handling
				if (table.getDirtyBit(pointer.getIndex()) == 1) {
					//I'm not sure if the first parameter is in hex.
					//Not sure what to put for offset param here
					//Not sure what to put for data here
					
					//memory.setData(table.getPageFrame(pointer.getIndex()), 0);
				}
				table.setEntry(pointer.getIndex(), valid, ref, dirty, pageFrame);
				pointer.setData(table.getEntry(pointer.getIndex()));
				flag = false;
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
