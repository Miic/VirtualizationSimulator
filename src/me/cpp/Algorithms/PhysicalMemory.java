package me.cpp.Algorithms;

public class PhysicalMemory {
	private String[][] memory;
	
	public PhysicalMemory() {
		memory = new String[16][256];  // Initialize the size of the physical memory
		
	}
	
	/**
	 * Set the page into the correct pageFrame
	 * @param pageFrame The page frame to set
	 * @param data The data to place into frame
	 */
	public void setPage(int pageFrame, String[] data) {
		for (int i=0;i<memory[pageFrame].length;i++) {
			memory[pageFrame][i] = data[i];
		}
	}
	
	/**
	 * Modify the data at the specific pageFrame and offset
	 * @param pageFrame The page frame to modify
	 * @param offset The offset to modify
	 * @param data The data to replace with
	 */
	public void setData(int pageFrame, int offset, String data) {
		memory[pageFrame][offset] = data;
	}
	
	/**
	 * Retrieve the data of an entire page, most likely to evict
	 * @param pageFrame The page frame to retrieve
	 * @return The data from the page frame in array format
	 */
	public String[] getPage(int pageFrame) {
		return memory[pageFrame];
	}
	
	/**
	 * Retrieve the data at a specific offset within a page frame
	 * @param pageFrame The page frame to get data from
	 * @param offset The offset from the page frame to get data from
	 * @return The data from the page frame and offset
	 */
	public String getData(int pageFrame, int offset) {
		return memory[pageFrame][offset];
	}
	
	
	
	
}
