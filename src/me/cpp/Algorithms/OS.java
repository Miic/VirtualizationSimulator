package me.cpp.Algorithms;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.util.Scanner;

public class OS {
	
	private CPU processor;
	private VPageTable pageTable;
	private PhysicalMemory memory;
	private Clock alg;
	private String outputString;
	private String testfileName;
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		OS os = new OS();
		os.loadFromFile(args[0]);
		os.outputFile();
	}
	
	public OS() {
		File file;
		File source;
		processor = new CPU();
		pageTable = new VPageTable();
		memory = new PhysicalMemory();
		outputString = "";
		
		try {
			for (int i=0;i<256;i++) {
				String filename = numconv.getHex(i, 2) + ".pg";
				source = new File("src/me/cpp/Algorithms/page_files/" + filename);
				file = new File("src/me/cpp/Algorithms/pages/" + filename);
				deleteIfExist(file);
				copyFile(source, file);
				pageTable.setEntry(i, 0, 0, 0, "0000");
			}
			alg = new Clock(pageTable, memory, processor);
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	/**
	 * Loading test files from disk
	 * @param filename The filename of the test file in the test_files folder
	 */
	public void loadFromFile(String filename)  {
		this.testfileName = filename.substring(0, filename.lastIndexOf('.'));
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
	 * return the Clock Handler
	 * @return The Clock instance object
	 */
	public Clock getAlg() {
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
	
	/**
	 * REad page file from disk into memory
	 * @param filename The page file to read in
	 * @return The TLB entry the new page in stored in
	 */
	public int readPageFile(String filename) {
    	int tlbEntry = -1;
    	try {
    		File file = new File("src/me/cpp/Algorithms/pages/" + filename + ".pg");
        	Scanner input = new Scanner(file);
        	int offset = 0;
        	int pageFrame = -1;
        	
        	pageFrame = alg.newPage(this, numconv.getDecimal(filename, 16));
        	while ( input.hasNextLine() ) {
        		memory.setData(pageFrame, offset, input.nextLine());
        		offset += 1;
        		
        	}
        	// Update the page table for this virtual page entry
        	pageTable.setEntry(numconv.getDecimal(filename, 16), 1, 0, 0, numconv.getBinary(pageFrame, 4));
        	// Possibly update the TLB as well
        	tlbEntry = processor.TLBSetEntry(numconv.getBinary(Integer.parseInt(filename, 16), 8), 1, 0, 0, numconv.getBinary(pageFrame, 4));
        	// Remove the following two lines in production
        	return tlbEntry;
        	
    	} catch (Exception ex) {
    		ex.printStackTrace();
    		return tlbEntry;
    	}
	}
	
	/**
	 * Used to evict and write page file to disk
	 * @param filename The page file to write back
	 */
	public void writePageFile(String filename) {
		
		try {
			File file = new File("src/me/cpp/Algorithms/pages/" + filename + ".pg");
			FileOutputStream fos = new FileOutputStream(file);
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
			bw.write(memory.toString(numconv.getDecimal(this.pageTable.getPageFrame(numconv.getDecimal(filename, 16)), 2)));
			bw.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	/**
	 * According to the project specs, "The OS should unset the r-bits of all 
	 * table entries after the CPU processes five instructions" in the page replacement section
	 */
	public void resetRef() {
		for (int i=0;i<pageTable.toArray().length;i++) {
			pageTable.setRefBit(i, 0);
		}
	}
	
	/**
	 * Used to generate the output csv string
	 * @param addr The address being read or write
	 * @param readWrite Read(0) instruction or Write(1) instruction
	 * @param hit 1 if it was a hit
	 * @param softmiss 1 if it was a softmiss
	 * @param hardmiss 1 if it was a hardmiss
	 * @param value The value being written or read
	 */
	public void generateOutputString(String addr, int readWrite, int hit, int softmiss, int hardmiss, String value) {
		String temp = (addr + "," + readWrite + "," + softmiss + "," + hardmiss + "," + hit + "," + alg.getDirtyBitSet() + "," + value + "\n");
		this.outputString += temp;
		
	}

	/**
	 * Outputs CSV File with Statistics
	 */	
	public File outputFile() {
		File file = new File(this.testfileName + "-output.csv");
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try {
			FileWriter writer = new FileWriter(file);
			writer.write("addr,R/W,Softmiss,Hardmiss,Hit,DirtySet,Value\n");
			writer.write(this.outputString);
			writer.close();
			System.out.println("Written output to: " + this.testfileName + "-output.csv");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return file;
	}
	

}
