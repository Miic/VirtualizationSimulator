package me.cpp.Algorithms;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Scanner;

public class OS {
	
	private CPU processor;
	private VPageTable pageTable;
	private PhysicalMemory memory;
	private ClockReplacement alg;

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		OS os = new OS();
	    // Reading in modified test file (test5.txt) which is only reading from memory
		os.loadFromFile(args[0]);
	}
	
	public OS() {
		File file;
		File source;
		processor = new CPU();
		pageTable = new VPageTable();
		memory = new PhysicalMemory();
		
		alg = new ClockReplacement(pageTable, memory);
		// System.out.println("The page table content is: ");
		// System.out.println(pageTable);
		try {
			for (int i=0;i<256;i++) {
				String filename = numconv.getHex(i, 2) + ".pg";
				source = new File("src/me/cpp/Algorithms/page_files/" + filename);
				file = new File("src/me/cpp/Algorithms/pages/" + filename);
				deleteIfExist(file);
				copyFile(source, file);  // Copy to new location so we don't overwrite the original files
				//input = new Scanner(file);	
				//processor.PTE(this, 0, 0, 1, "0000");  // Test writing to the Virtual Page Table
				
				//Initialize pageTable state
				pageTable.setEntry(i, 0, 0, 1, "0000");
			}
			/***
			while (input.hasNextLine()) {
				System.out.println(input.nextLine());
			}**/
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	/**
	 * Loading test files from disk
	 * @param filename The filename of the test file in the test_files folder
	 */
	public void loadFromFile(String filename)  {
		processor.MMUreadInstructionFromFile(this, "src/me/cpp/Algorithms/test_files/" + filename);	
		
	}
	
	/**
	 * return the VPageTable(Page Table) object 
	 * @return The VPageTable instance object
	 */
	public VPageTable getPageTable() {
		return pageTable;
	}
	
    /**
     * return the CPU object
     * @return The CPU instance object
     */
	public CPU getProcessor() {
		return processor;
	}
	
	/**
	 * return the PhysicalMemory object
	 * @return The PhysicalMemory instance object
	 */
	public PhysicalMemory getMemory() {
		return memory;
	}
	
	/**
	 * return the ClockReplacement Handler
	 * @return The ClockReplacement instance object
	 */
	public ClockReplacement getAlg() {
		return alg;
	}
	
	/**
	 * Copy files from source to destination. Main purpose is the make an copy of the
	 * page file, and work on the copied version but leave the original copy intact
	 * @param source File object to the source file
	 * @param dest File object to the destination file
	 * @throws IOException
	 */
	private void copyFile(File source, File dest) throws IOException {
		
		Files.copy(source.toPath(), dest.toPath());
	}
	
	/**
	 * At the beginning of each simulation, delete the page files used from the last run
	 * @param dest The File object to delete if exist
	 * @throws IOException
	 */
	private void deleteIfExist(File dest) throws IOException {
		
		Files.deleteIfExists(dest.toPath());
	}

	

}
