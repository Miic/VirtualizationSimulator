package me.cpp.Algorithms;

public class Clock {
	private ClockNode pointer;
	private ClockNode sudoHead;
	private ClockNode sudoTail;
	
	private VPageTable table;
	private PhysicalMemory memory;
	private CPU processor;
	
	private int dirtyBitSet;
	
	/**
	 * Creates an internal list version of a passed VPageTable
	 * @param table VPageTable used 
	 */
	
	public Clock(VPageTable table, PhysicalMemory memory, CPU processor) {
		this.table = table;
		this.memory = memory;
		this.processor = processor;
		this.dirtyBitSet = 0;
		String[][] data = memory.toArray();
		if ( memory.toArray().length > 0) {
			sudoHead = new ClockNode(0);
			pointer = sudoHead;
			for(int i = 1; i <= data.length - 1; i++) {
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
	
	/**
	 * Returns the dirtyBitSet variable which is set if a page being evicted has the dirty bit set
	 * @return dirtyBitSet
	 */
	public int getDirtyBitSet() {
		int temp = this.dirtyBitSet;
		this.dirtyBitSet = 0;
		return temp;
	}
	
	/**
	 * Used to determine where to place the new page into memory. 
	 * Every frame to put into memory requires to go through this method
	 * @param os The OS instance object
	 * @param vPage The vPage to place into page frame in memory
	 * @return The page frame in phy memory
	 */
	public int newPage(OS os, int vPage) {
		int pageFrame = -1;
		int temp = -1;
		if ( !memory.memFull ) {
			// Memory is not full
			pageFrame = memory.newPage();
			pointer.setVPageFrame(vPage);
			temp = pointer.getIndex();
			pointer = pointer.getNext();
			memory.pageTaken(pageFrame);
		}
		else {
			// Memory is full, swapping required
			ClockNode startNode = pointer;
			do {
				int ref = table.getRefBit(pointer.getVPageFrame());
				if ( ref == 0 ) {
					// Swapping this page out
					//System.out.println("swapping out frame: " + pointer.getIndex());
					//System.out.println("Swapping out vpage frame: " + pointer.getVPageFrame());
					int dirty = table.getDirtyBit(pointer.getVPageFrame());
					if ( dirty == 1 ) {
						// Data is dirty, writing to disk
						this.dirtyBitSet = 1;
						os.writePageFile(numconv.getHex(pointer.getVPageFrame(), 2));
					}
					// Safety precaution to set the valid bit to 0, meaning the data is no longer trustworthy
					table.setValidBit(pointer.getVPageFrame(), 0);
					processor.TLBsetValidBit(numconv.getBinary(pointer.getVPageFrame(), 8), 0);
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
	
	
}
