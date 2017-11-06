package me.cpp.Algorithms;

public class numconv {
	public static String getBinary(int num, int digits) {
		String output =  Integer.toString(num, 2);
		if ( output.length() < digits ) {
			output =  ("00000000" + output).substring((output.length()+8)-digits);
			//output = String.format("%0" + digits + "d", output);
		}
		
		return output;
	}
	
	public static String getHex(int i, int digits) {
		String hex = Integer.toString(i, 16).toUpperCase();
		if (hex.length() < digits) {
			hex = ("00000000" + hex).substring((hex.length()+8)-digits);
		}
		
		return hex;
	}
	
	// String str: non-decimal number, if hex=> base=16, if binary=> base=2
	public static int getDecimal(String str, int base) {
		return Integer.parseInt(str, base);
	}
	
	public static void main(String[] args) {
		System.out.println(getHex(32, 2));
	}
}
