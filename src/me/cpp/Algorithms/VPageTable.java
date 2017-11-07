package me.cpp.Algorithms;

public class VPageTable {
	private String[] pageTable;
	private ClockReplacement alg;
	
	public VPageTable() {
		pageTable = new String[256];
		for (int i=0;i<pageTable.length;i++) {
			pageTable[i] = "0000000";
		}
		alg = new ClockReplacement(this);
	}
	
	
	/**
	 * Adds the Entry to the next Valid spot via ClockReplacement alg
	 * @param index The entry to update
	 * @param valid The valid bit of the entry
	 * @param ref The ref bit of the entry
	 * @param dirty The dirty bit of the entry
	 * @param pageFrame The page frame of the entry
	 */
	public void addEntry(int valid, int ref, int dirty, String pageFrame) {
		alg.add(valid, ref, dirty, pageFrame);
	}
	
	
	/**
	 * Set the virtual page entry, Protected as internal state must not be accessed from the outside
	 * @param index The entry to update
	 * @param valid The valid bit of the entry
	 * @param ref The ref bit of the entry
	 * @param dirty The dirty bit of the entry
	 * @param pageFrame The page frame of the entry
	 */
	protected void setEntry(int index, int valid, int ref, int dirty, String pageFrame) {
		String pageTableEntry = Integer.toString(valid) + Integer.toString(ref) + Integer.toString(dirty) + pageFrame;
		//System.out.println("Adding " + pageTableEntry + " to index: " + index);
		pageTable[index] = pageTableEntry;
	}
	
	/**
	 * Set the valid bit of a virtual page entry, Protected as internal state must not be accessed from the outside
	 * @param index The entry to set valid bit
	 * @param valid The valid bit
	 */
	protected void setValidBit(int index, int valid) {
    	String tempEntry = pageTable[index];
    	pageTable[index] = Integer.toString(valid) + tempEntry.substring(1);
    	System.out.println("Page Table entry " + index + " has been updated to " + pageTable[index]);
    }
	
	/**
	 * Set the ref bit of a virtual page entry, Protected as internal state must not be accessed from the outside
	 * @param index The entry to set the ref bit
	 * @param ref The ref bit
	 */
	protected void setRefBit(int index, int ref) {
    	String tempEntry = pageTable[index];
    	pageTable[index] = tempEntry.substring(0, 1) + Integer.toString(ref) + tempEntry.substring(2);
    	System.out.println("Page Table entry " + index + " has been updated to " + pageTable[index]);
    }
	
	/**
	 * Set the dirty bit of a virtual page entry, Protected as internal state must not be accessed from the outside
	 * @param index The entry to set the dirty bit
	 * @param dirty The dirty bit
	 */
	protected void setDirtyBit(int index, int dirty) {
    	String tempEntry = pageTable[index];
    	pageTable[index] = tempEntry.substring(0, 2) + Integer.toString(dirty) + tempEntry.substring(3);
    	System.out.println("Page Table entry " + index + " has been updated to " + pageTable[index]);
    }
	
	/** 
	 * Sets the page frame of a virtual page entry, Protected as internal state must not be accessed from the outside
	 * @param index The entry to set the page frame
	 * @param pageFrame The page frame to set
	 */
	protected void setPageFrame(int index, String pageFrame) {
		String tempEntry = pageTable[index];
		pageTable[index] = tempEntry.substring(0, 3) + pageFrame;
		System.out.println("Page Table entry " + index + " has been updated to " + pageTable[index]);
	}
	
	
	/**
	 * Get an entry from the virtual page table
	 * @param index The entry to get
	 * @return The data from the entry
	 */
	public String getEntry(int index) {
		return pageTable[index];
	}
	
	/**
	 * Get the valid bit of a virtual page table
	 * @param index The entry to get valid bit
	 * @return The valid bit of the entry
	 */
	public int getValidBit(int index) {
		return Integer.parseInt(pageTable[index].substring(0, 1));
	}
	
	/**
	 * Get the ref bit of a virtual page table
	 * @param index The entry to get ref bit
	 * @return The ref bit of the entry
	 */
	public int getRefBit(int index) {
		return Integer.parseInt(pageTable[index].substring(1, 2));
	}
	
	/**
	 * Get the dirty bit of a virtual page table
	 * @param index The entry to get dirty bit
	 * @return The dirty bit of the entry
	 */
	public int getDirtyBit(int index) {
		return Integer.parseInt(pageTable[index].substring(2, 3));
	}
	
	/**
	 * Get the page frame of a virtual page table
	 * @param index The entry to get page frame
	 * @return The page frame of the entry
	 */
	public String getPageFrame(int index) {
		return pageTable[index].substring(3);
	}

	/**
	 * Get the page table array, Protected as internal state must not be accessed from the outside
	 * @return The page table array
	 */
	protected final String[] toArray() {
		return pageTable;
	}
	
	/**
	 * Override the toString function to display the data of Virtual page table
	 */
	public String toString() {
		String output = "";
		for (int i=0;i<pageTable.length;i++) {
			System.out.println(numconv.getBinary(i, 8) + "  |  " + pageTable[i]);
		}
		return output;
	}
	
	
	
	
}
