package me.cpp.Algorithms;

import java.io.File;
import java.util.Scanner;

public class CPU {
	private String[] TLB;
	// The following two variable cannot be used in production
	private int tlbPointer;  // This is used to point to the current TLB, to keep track if FIFO TLB entries
    
    public CPU() {
    	tlbPointer = 0;  // Delete in production
    	TLB = new String[8];
    	for(int i=0;i<TLB.length;i++) {
    		TLB[i] = "000000000000000";  // 8bits for V-Page#, 1bit for Valid, 1bit for ref, 1bit for dirty, 4bit for page frame#
    				
    	}
    }
    

    
    /**
     * The MMU that reads instruction from test file
     * @param os The OS instance object to retrieve other class instances
     * @param filename The filename of the test file
     */
    public void MMUreadInstructionFromFile(OS os, String filename) {
    	try {
			File file = new File(filename);
			Scanner input = new Scanner(file);
			String data = "";
			String addr = "";
			String temp = "";
			int counter = 0;
			while ( input.hasNextLine() ) {
				temp = input.nextLine();
				if ( temp.compareTo("0") == 0 ) {
					// Reading from memory
					addr = input.nextLine();
					data = this.readAddress(os, addr);
					if ( data.compareTo("false") != 0 ) {
						// Data was successfully read, set ref bit to 1 in both page table and tlb
						//System.out.println("updating vpagetable and tlb");
						os.getPageTable().setRefBit(numconv.getDecimal(addr.substring(0,2), 16), 1);
						this.TLBsetRefBit(numconv.getBinary(numconv.getDecimal(addr.substring(0, 2), 16), 8), 1);
					}
					System.out.println("Addr: " + addr + "==> Data: " + data);
				}
				else {
					addr = input.nextLine();
					data = input.nextLine();
					this.writeAddress(os, addr, data);
				}
				counter++;
				if ( counter > 5 ) {
					os.resetRef();
					counter = 0;
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
    }
    
    /**
     * Writing data by address. First check TLB, then virtual page table, finally memory
     * @param os The OS instance object to retrieve other class instances
     * @param addr The address to retrieve
     * @param data The data to be written
     * @return True on success false on failure
     */
    private boolean writeAddress(OS os, String addr, String data) {
    	String vPage = addr.substring(0, 2);
    	String offset = addr.substring(2);
    	int result;
    	for (int i=0;i<TLB.length;i++) {
    		if ( TLB[i].substring(0, 8).compareTo(numconv.getBinary(numconv.getDecimal(vPage, 16), 8)) == 0 ) {
    			// Entry is in TLB
    			// If the valid bit is 1 the data is in memory, we can use
    			if ( TLB[i].substring(8, 9).compareTo("1") == 0 ) {
    				// Writing to memory based on the record in TLB
    				os.getMemory().setData(numconv.getDecimal(TLB[i].substring(11), 2), numconv.getDecimal(offset, 16), data);
    				//System.out.println("Successfully written " + data + " to address==> " + addr);
    				// Updating the ref and valid bit on both the TLB and vpagetable
    				this.TLBsetRefBit(i, 1);
    				os.getPageTable().setRefBit(numconv.getDecimal(vPage, 16), 1);
    				this.TLBsetDirtyBit(i, 1);
    				os.getPageTable().setDirtyBit(numconv.getDecimal(vPage, 16), 1);
    				return true;
    			}
    		}
    	}
    	// Entry is not in TLB, trying V-Page table now
    	// SOFT MISS occurs here
    	String temp = os.getPageTable().getEntry(numconv.getDecimal(vPage, 16));
    	//System.out.println("soft miss occurred");
    	// If the valid bit is 1, the data is in memory, NO HARD MISS
    	if ( temp.substring(0, 1).compareTo("1") == 0 ) {
    		// Entry is in Virtual page table and valid
    		os.getMemory().setData(numconv.getDecimal(temp.substring(3), 2), numconv.getDecimal(offset, 16), data);
    		//System.out.println("Successfully written " + data + " to address==> " + addr);
    		// Updating the ref and valid bit on vpagetable
    		os.getPageTable().setRefBit(numconv.getDecimal(vPage, 16), 1);
    		os.getPageTable().setDirtyBit(numconv.getDecimal(vPage, 16), 1);
    		return true;
    	}
    	
    	// The data is not in physical memory, load from disk
    	// HARD MISS occurs here
    	else {
    		//System.out.println("Hard missed occurred as well");
    		//  Entry in v page table is not valid, reading from disk
    		result = readInPageFile(os, vPage);
    		// To-do, read again after reading from disk into physical memory
    		if ( result > 0 ) {
    			// Writing to the newly imported page
    			os.getMemory().setData(numconv.getDecimal(TLB[result].substring(11), 2), numconv.getDecimal(offset,  16), data);
    			//System.out.println("Successfully written " + data + " to address==> " + addr);
    			// Updating the ref and valid bit on vpagetable
    			os.getPageTable().setRefBit(numconv.getDecimal(vPage, 16), 1);
    			os.getPageTable().setDirtyBit(numconv.getDecimal(vPage, 16), 1);
    			return true;
    		}
    		else {
    			// Blue screen of Death here, since the page that was just read in and placed on page table and TLB cannot be found
    			return false;
    		}
    	}
    }
    
    
    
    /**
     * Reading by address. First check TLB, then virtual page table, finally memory
     * @param os The OS instance object to retrieve other class instances
     * @param addr The address to retrieve
     * @return The data at the address
     */
    private String readAddress(OS os, String addr) {
    	String vPage = addr.substring(0, 2);
    	String offset = addr.substring(2);
    	int result;
    	for (int i=0;i<TLB.length;i++) {
    		if ( TLB[i].substring(0, 8).compareTo(numconv.getBinary(numconv.getDecimal(vPage, 16), 8)) == 0 ) {
    			//  Entry is in TLB
    			//System.out.println("Entry is in TLB");
    			// If the valid bit is 1 the data is memory, we can use. 
    			//if ( TLB[i].substring(8, 9).compareTo("1") == 0 && TLB[i].substring(10, 11).compareTo("0") == 0) {
    			if ( TLB[i].substring(8, 9).compareTo("1") == 0 ) {	
    				//System.out.println("Entry is valid");
    				return os.getMemory().getData(numconv.getDecimal(TLB[i].substring(11), 2), numconv.getDecimal(offset, 16));
    			}
    		}
    	}
    	//  Entry is not in TLB, trying V-page table now
    	//  SOFT MISS occurs here
    	String temp = os.getPageTable().getEntry(numconv.getDecimal(vPage, 16));
    	//System.out.println("Soft miss occurred");
    	// If the valid bit is 1 the data is in memory, NO HARD MISS.
    	//if (temp.substring(0, 1).compareTo("1") == 0 && temp.substring(2, 3).compareTo("0") == 0) {
    	if (temp.substring(0, 1).compareTo("1") == 0 ) {
    	    // Entry is in Virtual page table and valid
    		return os.getMemory().getData(numconv.getDecimal(temp.substring(3), 2), numconv.getDecimal(offset, 16));
    	}
    	// The data is not in physical memory, load from disk
    	// HARD MISS occurs here
    	
    	else {
    		//System.out.println("Hard missed occurred as well");
    		//  Entry in v page table is not valid, reading from disk
    		result = readInPageFile(os, vPage);
    		// To-do, read again after reading from disk into physical memory
    		if ( result >= 0 ) {
    			return os.getMemory().getData(numconv.getDecimal(TLB[result].substring(11), 2), numconv.getDecimal(offset, 16));
    		}
    		else {
    			// Blue screen of Death here, since the page that was just read in and placed on page table and TLB cannot be found
    			return "false";
    		}
    	}
    }
    
    /**
     * To read in instructor provided page files
     * @param os The OS instance object to access other class instance
     * @param filename The filename to read in
     * Need to be modified to specified the correct physical memory page frame
     */
    public int readInPageFile(OS os, String filename) {
    	return os.readPageFile(filename);
    	
    	
    }
    
    /**
     * Set TLB entry
     * @param vPage The Virtual page table the entry points to 
     * @param valid The valid bit of the entry
     * @param ref The ref bit of the entry
     * @param dirty The dirty bit of the entry
     * @param pageFrame The page Frame it points to
     */
    public int TLBSetEntry(String vPage, int valid, int ref, int dirty, String pageFrame) {
    	String TLBEntry = vPage + Integer.toString(valid) + Integer.toString(ref) + Integer.toString(dirty) + pageFrame;
    	// System.out.println("Adding: " + TLBEntry + " to TLB entry " + entry);
    	int tlbPoint = tlbPointer;
    	this.tlbPointer = (this.tlbPointer + 1) % 8; 
    	TLB[tlbPoint] = TLBEntry;
    	return tlbPoint;
    	
    }
    
    /**
     * Set valid bit of the TLB entry
     * @param entry The entry to update valid bit
     * @param valid The valid bit
     */
    public void TLBsetValidBit(int entry, int valid) {
    	System.out.println("trying to set valid bit in TLB for entry " + entry);
    	String tempEntry = TLB[entry];
    	TLB[entry] = tempEntry.substring(0,8) + Integer.toString(valid) + tempEntry.substring(9);
    	//System.out.println("TLB entry " + entry + " has been updated to " + TLB[entry]);
    	
    }
    
    /**
     * Set the ref bit of the TLB entry
     * @param entry The entry to update ref bit
     * @param ref The ref bit
     */
    public void TLBsetRefBit(int entry, int ref) {
    	String tempEntry = TLB[entry];
    	TLB[entry] = tempEntry.substring(0,9) + Integer.toString(ref) + tempEntry.substring(10);
    	//System.out.println("TLB entry " + entry + " has been updated to " + TLB[entry]);
    }
    
    /**
     * Set the dirty bit of the TLB entry
     * @param entry The entry to update the dirty bit
     * @param dirty The dirty bit
     */
    public void TLBsetDirtyBit(int entry, int dirty) {
    	String tempEntry = TLB[entry];
    	TLB[entry] = tempEntry.substring(0,10) + Integer.toString(dirty) + tempEntry.substring(11);
    	//System.out.println("TLB entry " + entry + " has been updated to " + TLB[entry]);
    }
    
    /**
     * Set the page frame of the TLB entry
     * @param entry The entry to update the page frame
     * @param pageFrame The page frame to update
     */
    public void TLBsetPageFrame(int entry, String pageFrame) {
    	String tempEntry = TLB[entry];
    	TLB[entry] = tempEntry.substring(0,11) + pageFrame;
    	//System.out.println("TLB entry " + entry + " has been updated to " + TLB[entry]);
    }
    
    /**
     * Set the virtual page number for the TLB entry
     * @param entry The entry to update the virtual page number
     * @param vPage The virtual page number
     */
    public void TLBsetVPage(int entry, String vPage) {
    	String tempEntry = TLB[entry];
    	TLB[entry] = vPage + tempEntry.substring(8);
    	//System.out.println("TLB entry " + entry + " has been updated to " + TLB[entry]);
    }
    
    /**
     * Get the TLB entry
     * @param entry The entry to retrieve from the TLB
     * @return The data to retrieve
     */
    public String TLBgetEntry(int entry) {
    	return TLB[entry];
    }
    
    /**
     * Get the TLB entry Virtual page number
     * @param entry The TLB entry to get the v page number
     * @return The virtual page number
     */
    public String TLBgetVPage(int entry) {
    	return TLB[entry].substring(0, 8);
    }
    
    /**
     * Get the valid bit of a TLB entry
     * @param entry The entry to get valid bit from
     * @return The valid bit
     */
    public int TLBgetValidBit(int entry) {
    	return Integer.parseInt(TLB[entry].substring(8, 9));
    }
    
    /** 
     * Get the ref bit of a TLB entry
     * @param entry The entry to get ref bit from
     * @return The ref bit
     */
    public int TLBgetRefBit(int entry) {
    	return Integer.parseInt(TLB[entry].substring(9, 10));
    }
    
    /**  
     * Get the dirty bit of a TLB entry
     * @param entry The entry to get the dirty bit from
     * @return The dirty bit
     */
    public int TLBgetDirtyBit(int entry) {
    	return Integer.parseInt(TLB[entry].substring(10, 11));
    }
    
    /**
     * Get the page frame number of a TLB entry
     * @param entry The entry to get the page frame from
     * @return The page frame
     */
    public String TLBgetPageFrame(int entry) {
    	return TLB[entry].substring(11);
    }
    
    
    /**
     * Set the ref bit using the virtual page frame number, loop through the TLB to 
     * match the frame number, since only one entry per virtual page frame allowed on
     * the TLB, this is acceptable.
     * @param vpage The virtual page frame number being searched for
     * @param ref The ref bit to set
     */
    public void TLBsetRefBit(String vpage, int ref) {
    	for (int i=0;i<TLB.length;i++) {
    		if ( TLB[i].substring(0, 8).compareTo(vpage) == 0 ) {
    			this.TLBsetRefBit(i, ref);
    		}
    	}
    }
    
    /**
     * Set the valid bit using the virtual page frame number, loop through the TLB to
     * match the frame number, since only one entry per virtual page frame allowed on
     * the TLB, this is acceptable
     * @param vpage The virtual page frame number being searched for
     * @param valid The valid bit to set
     */
    public void TLBsetValidBit(String vpage, int valid) {
    	for (int i=0;i<TLB.length;i++) {
    		if ( TLB[i].substring(0, 8).compareTo(vpage) == 0 ) {
    			this.TLBsetValidBit(i, valid);
    		}
    	}
    }
    
    
    
}
