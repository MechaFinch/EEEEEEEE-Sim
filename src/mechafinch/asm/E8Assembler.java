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
		boolean foundHeader = false;
		
		// Get the lines without comments
		ArrayList<String> rawLines = removeComments(source);
		
		// Find the header if it exists
		for(int l = 0; l < rawLines.size(); l++) {
			String s = rawLines.get(l);
			
			if(s.startsWith("$")) {
				header = s.substring(1);
				foundHeader = true;
				break;
			}
		}
		
		// Determine definitions
		HashMap<String, String> definitions = parseDefines(rawLines);
		
		// Record lines before definitions applied
		System.out.println(rawLines + "\n");
		
		// If there isn't a header, apply its definition if available
		if(!foundHeader) {
			if(definitions.keySet().contains("header")) header = definitions.get("header");
		}
		
		/*
		 * Apply Definitions
		 */
		for(String term : definitions.keySet()) {
			//System.out.println(term);
			
			// Go over each line replacing the term with the definition
			for(int l = 0; l < rawLines.size(); l++) {
				rawLines.set(l, rawLines.get(l).replace(term, definitions.get(term)));
			}
		}
		
		/*
		 *  Find Labels
		 */
		HashMap<String, Integer> labels = new HashMap<>();
		
		for(int ln = 0; ln < rawLines.size(); ln++) {
			String line = rawLines.get(ln);
			
			if(line.contains(":")) { // Found a label
				labels.put(line.substring(0, line.indexOf(':')), ln);
				
				if(line.substring(line.indexOf(':') + 1).equals("")) { // Label on its own line
					rawLines.remove(ln); // Label will point to the next instruction
				} else { // inline label
					rawLines.set(ln, line.substring(line.indexOf(':') + 1).trim());
				}
			}
		}
		
		// List labels
		for(String lbl : labels.keySet()) System.out.println(lbl + ": " + labels.get(lbl));
		System.out.println();
		
		/*
		 * Interpret header because we need the values
		 */
		int dataBits = Integer.parseInt(header.substring(0, 2), 16),
			ramAddrBits = Integer.parseInt(header.substring(2, 4), 16),
			romAddrBits = Integer.parseInt(header.substring(4, 6), 16);
		
		/*
		 * Interpret Lines
		 */
		ArrayList<ProgramSection> ramSections = new ArrayList<>(),
								  romSections = new ArrayList<>(); // Rom can have multiple sections via ORG
		
		ProgramSection currentROMSection = new ProgramSection(0, 1);
		                          
		for(int ln = 0; ln < rawLines.size(); ln++) {
			String line = rawLines.get(ln),		// The raw line
				   upper = line.toUpperCase(),	// The uppercase line
				   inst = "";					// The assembled instruction
			
			// Switch over opcodes
			if(upper.startsWith("DB")) {		// Define Bytes
				assembleDB(line, dataBits, ramAddrBits, ramSections);
			} else if(upper.startsWith("LD")) {	// Load
				inst = assembleLD(line, dataBits, ramAddrBits, romAddrBits);
				currentROMSection.addData(inst, 4);
			}
			
			// Print the instruction
			System.out.println(String.format("%-16s", line) + " " + inst);
		}
		
		// Add last rom section
		romSections.add(currentROMSection);
		
		/*
		 * Debug Output, sort sections
		 */
		ramSections.sort((a, b) -> a.getStartAddress() - b.getStartAddress());
		romSections.sort((a, b) -> a.getStartAddress() - b.getStartAddress());
		
		// Header stuff again
		int dataLength = 2 * (int) Math.ceil((double) dataBits / 8),
			ramAddrLength = 2 * (int) Math.ceil((double) ramAddrBits / 8),
			romAddrLength = 2 * (int) Math.ceil((double) romAddrBits / 8);
		
		// Print sections
		System.out.println("\n<< RAM SECTIONS >>");
		ramSections.forEach(e -> System.out.println(e.toPrintedHexString(dataLength, ramAddrLength, romAddrLength)));
		System.out.println("\n<< ROM SECTIONS >>");
		romSections.forEach(e -> System.out.println(e.toPrintedHexString(dataLength, ramAddrLength, romAddrLength)));
		
		System.out.println("\n\n" + rawLines + "\n");
		
		/*
		 * Convert to single string
		 */
		String finalString = header;
		
		// Add RAM sections
		for(int i = 0; i < ramSections.size(); i++) finalString += ramSections.get(i).toHexString(dataLength, ramAddrLength, romAddrLength);
		
		// Add ROM sections
		for(int i = 0; i < romSections.size(); i++) finalString += romSections.get(i).toHexString(dataLength, ramAddrLength, romAddrLength);
		
		// Return this test thing so we don't break the simulator
		return "08080A0000050F000102030401000400003047410544013447";
	}
	
	/**
	 * Assembles a LD line
	 * 
	 * @param line
	 * @param dataBits
	 * @param ramAddrBits
	 * @param romAddrBits
	 * @return The assembled instruction
	 */
	private static String assembleLD(String line, int dataBits, int ramAddrBits, int romAddrBits) {
		// Determine destination register
		int startIndex = 2, endIndex;
		
		// First non-whitespace
		while(isWhitespaceComma(line.charAt(startIndex))) startIndex++;
		
		// We expect a register (also increment to next char
		int reg = interpretRegister(line.charAt(startIndex++));
		
		// Continue until next non-whitespace
		while(isWhitespaceComma(line.charAt(startIndex))) startIndex++;
		
		int instruction = 0;
		
		// Determine immediate vs register vs indexed/indirect
		if(isRegister(line.charAt(startIndex))) {	// Register
			int sReg = interpretRegister(line.charAt(startIndex));
			
			instruction = 0b00101000_00000000; // load register template
			instruction |= reg << 8;
			instruction |= sReg << 6;
		} else if(line.charAt(startIndex) == '[') {	// Indexed or indirect
			
		} else {									// Immediate
			
		}
		
		return Integer.toHexString(instruction).toUpperCase();
	}
	
	/**
	 * Assembles a DB line
	 * 
	 * @param line
	 * @param dataBits
	 * @param ramAddrBits
	 * @param ramSections
	 */
	private static void assembleDB(String line, int dataBits, int ramAddrBits, ArrayList<ProgramSection> ramSections) {
		// Get start address
		int startIndex = 2, endIndex;
		
		// Walk until not whitespace, we've found the address
		while(isWhitespaceComma(line.charAt(startIndex))) startIndex++;
		// Walk until whitespace, we've finished the address
		endIndex = startIndex;
		while(!(isWhitespaceComma(line.charAt(endIndex)))) endIndex++;
		
		// Try to interpret the address
		int startingAddress = interpretInteger(line.substring(startIndex, endIndex), ramAddrBits);
		
		
		// Start a RAM section
		ProgramSection sec = new ProgramSection(startingAddress, 0);
		
		// Add values
		for(startIndex = endIndex; startIndex < line.length(); startIndex = endIndex) { // Loop by starting from rightmost known whitespace/comma
			// Increment until we find a literal
			while(isWhitespaceComma(line.charAt(startIndex))) startIndex++;
			
			// Determine and apply if the literal is a string or number
			char startChar = line.charAt(startIndex);
			if(startChar == '"') {						// String literal
				endIndex = startIndex++ + 1;
				
				// Determine the end of the string
				while(line.charAt(endIndex) != '"') endIndex++;
				
				// Interpret string literal and add as bytes
				String strHex = interpretStringLiteral(line.substring(startIndex, endIndex));
				sec.addData(strHex, 2);
				endIndex++;
				
			} else if(startChar == '\'') {				// Character literal
				// Make sure its a single character
				if(line.charAt(startIndex + 2) != '\'') throw new IllegalArgumentException("Invalid character literal starting at " + line.substring(startIndex));
				
				// Interpret
				sec.addData(toHex(line.charAt(startIndex + 1), 2), 2);
				endIndex = startIndex + 2;
				
			} else if(Character.isDigit(startChar)) {	// Integer literal
				// Find end of literal
				endIndex = startIndex;
				while(endIndex < line.length() && !isWhitespaceComma(line.charAt(endIndex))) endIndex++;
				
				// Interpret, words only
				sec.addData(toHex(interpretInteger(line.substring(startIndex, endIndex), dataBits), 2), 2);
				endIndex++;
				
			} else {									// oops something went wrong
				throw new IllegalArgumentException("Invalid literal starting at " + line.substring(startIndex));
			}
		}
		
		// Add section
		ramSections.add(sec);
	}
	
	/**
	 * Removes comments from the source strings
	 * 
	 * @param source
	 * @return The arraylist of trimmed lines without comments
	 */
	private static ArrayList<String> removeComments(String[] source) {
		ArrayList<String> rawLines = new ArrayList<>();
		
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
			if(!line.isEmpty()) rawLines.add(line);
		}
		
		return rawLines;
	}
	
	/**
	 * Finds #define definitions and parses them
	 * 
	 * @param rawLines
	 * @param header
	 * @return A HashMap with the term as the key and the definition as the value
	 */
	private static HashMap<String, String> parseDefines(ArrayList<String> rawLines) {
		HashMap<String, String> definitions = new HashMap<>();
		
		// Loop over lines
		for(int l = 0; l < rawLines.size(); l++) {
			String line = rawLines.get(l);
			
			// Define found
			if(line.startsWith("#define ")) {
				line = line.substring(8);
				
				String k = "", v = "";
				
				// Quote separated or space separated
				// Determine key
				if(line.startsWith("\"")) { // Consume until quote
					boolean escaped = false;
					
					// Loop over the line's chars
					int i;
					for(i = 1; i < line.length(); i++) {
						char c = line.charAt(i);
						
						// Stop when we reach the closing quote, ignoring escaped chars
						if(c == '"' && !escaped) break;
						else if(c == '\\' && !escaped) escaped = true;
						else {
							k += c;
							escaped = false;
						}
					}
					
					// Make sure the result exists
					if(i == line.length()) throw new IllegalArgumentException("Invalid definition: #define " + line);
					line = line.substring(i);
				} else { // Consume until space
					k = line.substring(0, line.indexOf(' '));
					line = line.substring(line.indexOf(' ') + 1);
				}
				
				// remove whitespace and quotes
				v = line.trim();
				
				if(v.startsWith("\"")) v = v.substring(1, v.length() - 1);
				
				// Log and add the definition
				System.out.println("Define \"" + k + "\" as \"" + v + "\"");
				definitions.put(k, v);
				rawLines.remove(l--);
			}
		}
		
		return definitions;
	}
	
	/**
	 * Determines the register (a=0, d=3) represented by the given character
	 * 
	 * @param c
	 * @return The number of the register
	 */
	private static int interpretRegister(char c) {
		switch(Character.toUpperCase(c)) {
			case 'A':
				return 0;
			
			case 'B':
				return 1;
				
			case 'C':
				return 2;
				
			case 'D':
				return 3;
		}
		
		throw new IllegalArgumentException("Invalid register: " + c);
	}
	
	/**
	 * Determines if the given character represents a register
	 * 
	 * @param c
	 * @return True if C is A, B, C, or D
	 */
	private static boolean isRegister(char c) {
		c = Character.toUpperCase(c);
		return c == 'A' || c == 'B' || c == 'C' || c == 'D';
	}
	
	/**
	 * Determines if a character is whitespace or a comma, or something else
	 * 
	 * @param c The character
	 * @return True if the character is whitespace or a comma
	 */
	private static boolean isWhitespaceComma(char c) {
		return Character.isWhitespace(c) || c == ',';
	}
	
	/**
	 * Converts a string literal to ASCII hexadecimal
	 * 
	 * @param literal
	 * @return ASCII hex of the string
	 */
	private static String interpretStringLiteral(String literal) {
		String s = "";
		
		for(int i = 0; i < literal.length(); i++) {
			s += toHex(literal.charAt(i), 2);
		}
		
		return s;
	}
	
	/**
	 * Converts an integer to its hexidecimal representation with the specified number of digits
	 * 
	 * @param val Value
	 * @param len Length
	 * @return The hexadecimal string value
	 */
	private static String toHex(int val, int len) {
		return String.format("%" + len + "s", Integer.toHexString(val).toUpperCase()).replace(' ', '0');
	}
	
	/**
	 * Attempts to interpret an integer literal
	 * 
	 * @param value The value to interpret
	 * @param bits The number of bits it is represented with
	 * @return The value of the literal
	 */
	private static int interpretInteger(String value, int bits) {
		value = value.toLowerCase().replace("_", ""); // Lowercase and remove underscores
		
		// Determine max value
		int maxValue = (int) (Math.pow(2, bits)) - 1;
		
		// Determine and use type (binary, decimal, hex)
		int v = 0;
		if(value.startsWith("0b")) { // binary
			v = Integer.parseInt(value, 2);
		} else if(value.startsWith("0x")) { // hex
			v = Integer.parseInt(value, 16);
		} else { // decimal
			v = Integer.parseInt(value);
		}
		
		if(v > maxValue) throw new NumberFormatException("Value (" + value + ") outside of range (0 to " + maxValue + ").");
		return v;
	}
}




