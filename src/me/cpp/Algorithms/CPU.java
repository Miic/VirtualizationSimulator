package me.cpp.Algorithms;

import java.io.File;
import java.util.Scanner;

public class CPU {
	private String[] TLB;
	// The following two variable cannot be used in production
	private int pageFrame; // This is only used to test moving page file into physical memory
	private int tlbentry;  // This is only used to test moving entry into the TLB
    
    public CPU() {
    	pageFrame = 0; // Delete in production
    	tlbentry = 0;  // Delete in production
    	TLB = new String[8];
    	for(int i=0;i<TLB.length;i++) {
    		TLB[i] = "000000000000000";  // 8bits for V-Page#, 1bit for Valid, 1bit for ref, 1bit for dirty, 4bit for page frame#
    				
    	}
    }
    
    /**
     * Setting an entry on the Virtual page table
     * @param os The OS object to get the virtual page table instance object
     * @param valid The valid bit of the page table entry
     * @param ref The ref bit of the page table entry
     * @param dirty The dirty bit of the page table entry
     * @param pageFrame The page frame the virtual page points to in the physical memory
     */
    public void PTE(OS os, int valid, int ref, int dirty, String pageFrame) {
    	os.getAlg().add(valid, ref, dirty, pageFrame);
    	
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
			while (input.hasNextLine()) {
				temp = input.nextLine();
				if ( temp.compareTo("0") == 0 ) {
					addr = input.next();
					data = this.readAddress(os, addr);
					System.out.println("Addr: " + addr + "==> Data: " + data);
				}
				else {
					addr = input.next();
					data = input.next();
					// To-do writing to memory
					// this.writeToMemory(os, addr, data); 
					// System.out.println(data + " has been written to memory addr: " + addr);
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
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
    	for (int i=0;i<TLB.length;i++) {
    		if ( TLB[i].substring(0, 8).compareTo(numconv.getBinary(numconv.getDecimal(vPage, 16), 8)) == 0 ) {
    			//  Entry is in TLB
    			// If the valid bit is 1, and dirty bit is 0, the data is clean, we can use. 
    			if ( TLB[i].substring(8, 9).compareTo("1") == 0 && TLB[i].substring(10, 11).compareTo("0") == 0) {
    				
    				return os.getMemory().getData(numconv.getDecimal(TLB[i].substring(11), 2), numconv.getDecimal(offset, 16));
    			}
    		}
    	}
    	//  Entry is not in TLB, trying V-page table now
    	String temp = os.getPageTable().getEntry(numconv.getDecimal(vPage, 16));
    	// If the valid bit is 1, and dirty bit is 0, the data is clean, we can use.
    	if (temp.substring(0, 1).compareTo("1") == 0 && temp.substring(2, 3).compareTo("0") == 0) {
    		// Entry is in Virtual page table and valid
    		return os.getMemory().getData(numconv.getDecimal(temp.substring(3), 2), numconv.getDecimal(offset, 16));
    	}
    	// The data is not in physical memory, load from disk
    	else {
    		//  Entry in v page table is not valid, reading from disk
    		readInPageFile(os, vPage + ".pg");
    		// To-do, read again after reading from disk into physical memory
    		return "False";
    	}
    }
    
    /**
     * To read in instructor provided page files
     * @param os The OS instance object to access other class instance
     * @param filename The filename to read in
     * Need to be modified to specified the correct physical memory page frame
     */
    public void readInPageFile(OS os, String filename) {
    	try {
    		File file = new File("src/me/cpp/Algorithms/pages/" + filename);
        	Scanner input = new Scanner(file);
        	filename = filename.substring(0, 2);
        	int offset = 0;
        	while ( input.hasNextLine() ) {
        		os.getMemory().setData(pageFrame, offset, input.nextLine());
        		offset += 1;
        		
        	}
        	// Update the page table for this virtual page entry
        	os.getPageTable().setEntry(numconv.getDecimal(filename, 16), 1, 0, 0, numconv.getBinary(pageFrame, 4));
        	// Possibly update the TLB as well
        	this.TLBSetEntry(tlbentry, numconv.getBinary(Integer.parseInt(filename, 16), 8), 1, 0, 0, numconv.getBinary(pageFrame, 4));
        	// Remove the following two lines in production
        	pageFrame += 1;  // Delete in production, only for testing
        	tlbentry += 1;   // Delete in production, only for testing
        	
    	} catch (Exception ex) {
    		ex.printStackTrace();
    	}
    	
    	
    }
    
    /**
     * Set TLB entry
     * @param entry The entry to update entry in
     * @param vPage The Virtual page table the entry points to 
     * @param valid The valid bit of the entry
     * @param ref The ref bit of the entry
     * @param dirty The dirty bit of the entry
     * @param pageFrame The page Frame it points to
     */
    public void TLBSetEntry(int entry, String vPage, int valid, int ref, int dirty, String pageFrame) {
    	String TLBEntry = vPage + Integer.toString(valid) + Integer.toString(ref) + Integer.toString(dirty) + pageFrame;
    	// System.out.println("Adding: " + TLBEntry + " to TLB entry " + entry);
    	TLB[entry] = TLBEntry;
    	
    	
    }
    
    /**
     * Set valid bit of the TLB entry
     * @param entry The entry to update valid bit
     * @param valid The valid bit
     */
    public void TLBsetValidBit(int entry, int valid) {
    	String tempEntry = TLB[entry];
    	TLB[entry] = tempEntry.substring(0,8) + Integer.toString(valid) + tempEntry.substring(9);
    	System.out.println("TLB entry " + entry + " has been updated to " + TLB[entry]);
    	
    }
    
    /**
     * Set the ref bit of the TLB entry
     * @param entry The entry to update ref bit
     * @param ref The ref bit
     */
    public void TLBsetRefBit(int entry, int ref) {
    	String tempEntry = TLB[entry];
    	TLB[entry] = tempEntry.substring(0,9) + Integer.toString(ref) + tempEntry.substring(10);
    	System.out.println("TLB entry " + entry + " has been updated to " + TLB[entry]);
    }
    
    /**
     * Set the dirty bit of the TLB entry
     * @param entry The entry to update the dirty bit
     * @param dirty The dirty bit
     */
    public void TLBsetDirtyBit(int entry, int dirty) {
    	String tempEntry = TLB[entry];
    	TLB[entry] = tempEntry.substring(0,10) + Integer.toString(dirty) + tempEntry.substring(11);
    	System.out.println("TLB entry " + entry + " has been updated to " + TLB[entry]);
    }
    
    /**
     * Set the page frame of the TLB entry
     * @param entry The entry to update the page frame
     * @param pageFrame The page frame to update
     */
    public void TLBsetPageFrame(int entry, String pageFrame) {
    	String tempEntry = TLB[entry];
    	TLB[entry] = tempEntry.substring(0,11) + pageFrame;
    	System.out.println("TLB entry " + entry + " has been updated to " + TLB[entry]);
    }
    
    /**
     * Set the virtual page number for the TLB entry
     * @param entry The entry to update the virtual page number
     * @param vPage The virtual page number
     */
    public void TLBsetVPage(int entry, String vPage) {
    	String tempEntry = TLB[entry];
    	TLB[entry] = vPage + tempEntry.substring(8);
    	System.out.println("TLB entry " + entry + " has been updated to " + TLB[entry]);
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
    
    
}
