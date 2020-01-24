package mechafinch.asm;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * I'm at the point where I *need* one of these so... 
 * assembler time
 * 
 * @author Alex Pickering
 */
public class E8Assembler {
	
	/**
	 * Assembles to a pair of hex strings, where RAM contents are at index 0 and ROM contents are at index 1
	 * 
	 * @param source The source file as an array of lines
	 * @return Array of strings {RAM, ROM}
	 */
	public static String[] assembleToDualHex(String[] source) {
		String exec = assemble(source);
		
		String ram = "",
			   rom = "";
		
		int dataLength = 2 * (int) Math.ceil((double) Integer.parseInt(exec.substring(0, 2), 16) / 8),
			ramAddrLength = 2 * (int) Math.ceil((double) Integer.parseInt(exec.substring(2, 4), 16) / 8),
			romAddrLength = 2 * (int) Math.ceil((double) Integer.parseInt(exec.substring(4, 6), 16) / 8);
		
		/*
		System.out.println(dataLength);
		System.out.println(ramAddrLength);
		System.out.println(romAddrLength);
		*/
		
		// Convert from executable hex
		// Sections will be sequential
		// Start from 6 to skip header
		for(int i = 6; i < exec.length();) {
			// Read section type
			boolean isRAM = exec.substring(i, i + 2).equals("00");
			i += 2;
			
			// Section length
			int length = Integer.parseInt(exec.substring(i, i + 4), 16);
			i += 4;
			
			// Starting address
			int addr = Integer.parseInt(exec.substring(i, i + (isRAM ? ramAddrLength : romAddrLength)), 16);
			i += (isRAM ? ramAddrLength : romAddrLength);
			
			// Data
			String data = "";
			int j;
			for(j = i; j < i + (length * (isRAM ? dataLength : 4)); j += (isRAM ? dataLength : 4)) {
				//System.out.println(j);
				data += exec.substring(j, j + (isRAM ? dataLength : 4));
			}
			
			i = j;
			
			// Put it in the strings
			if(isRAM) {
				while(ram.length() < addr * ramAddrLength) ram += "00";
				ram += data;
			} else {
				while(rom.length() < addr * romAddrLength) rom += "0000";
				rom += data;
			}
		}
		
		return new String[] {ram, rom};
	}
	
	/**
	 * Assembles to a hex string of the binary executable format
	 * 
	 * @param source The source file as an array of lines
	 * @return An array of strings in internal format
	 */
	private static String assemble(String[] source) {
		String header = "08080A"; // default header
		
		ArrayList<String> rawLines = new ArrayList<>(); // Lines without comments
		
		// Eliminate comments
		boolean isMultiline = false;
		
		for(int i = 0; i < source.length; i++) {
			String line = source[i].trim();
			System.out.println(line);
			
			// Detect multiline comments
			if(line.startsWith("###")) {
				if(isMultiline) { // End a multiline comment and restart the line
					isMultiline = false;
					source[i--] = (line.equals("###") ? "" : line.substring(4).trim());
					continue;
				} else { // Start a multiline comment and discard the line
					isMultiline = true;
					continue;
				}
			} else if(isMultiline) continue;
			
			// Detect single line comments
			if(line.contains(";")) line = line.substring(0, line.indexOf(';')).trim();
			
			// Add line if it has contents
			if(!line.isEmpty()) rawLines.add(line.toLowerCase());
		}
		
		// Find defines
		HashMap<String, String> definitions = new HashMap<>();
		
		for(int l = 0; l < rawLines.size(); l++) {
			String line = rawLines.get(l);
			
			if(line.startsWith("#define ")) {
				line = line.substring(8);
				
				String k = "", v = "";
				
				// Quote separated or space separated
				// Determine key
				if(line.startsWith("\"")) { // Consume until quote
					boolean escaped = false;
					
					int i;
					for(i = 1; i < line.length(); i++) {
						char c = line.charAt(i);
						
						if(c == '"' && !escaped) break;
						else if(c == '\\' && !escaped) escaped = true;
						else {
							k += c;
							escaped = false;
						}
					}
					
					if(i == line.length()) throw new IllegalArgumentException("Invalid definition: #define " + line);
					line = line.substring(i);
				} else { // Consume until space
					k = line.substring(0, line.indexOf(' '));
					line = line.substring(line.indexOf(' ') + 1);
				}
				
				v = line.trim();
				
				System.out.println("Define \"" + k + "\" as \"" + v + "\"");
				rawLines.remove(l--);
			}
		}
		
		System.out.println(rawLines);
		
		return "08080A0000050F000102030401000400003047410544013447";
	}
}



