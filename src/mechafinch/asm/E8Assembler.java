package mechafinch.asm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

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
			if(upper.startsWith("DB")) {			// Define Bytes
				assembleDB(line, dataBits, ramAddrBits, ramSections);
			} else if(upper.startsWith("LD")) {		// Load
				inst = assembleLD(line);
				currentROMSection.addData(inst, 4);
			} else if(upper.startsWith("ST")) {		// Store
				inst = assembleST(line);
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
		
		
		return finalString;
		
		// Return this test thing so we don't break the simulator
		//return "08080A0000050F000102030401000400003047410544013447";
	}
	
	private static String assembleST(String line) {
		int startIndex = indexOfNotWhitespaceComma(line, 2);
		
		// Source register
		int reg = interpretRegister(line.charAt(startIndex++));
		
		// Setup
		startIndex = indexOfNotWhitespaceComma(line, startIndex);
		
		int instruction = 0;
		
		// Indexed or indirect
		if(line.charAt(startIndex) != '[') throw new IllegalArgumentException("Destination must be indexed or indirect: " + line);
		
		String sub = line.substring(++startIndex, line.indexOf(']')).trim();
		if(sub.contains("+") || isRegister(sub.charAt(0))) { // Indirect
			instruction = 0b00111100_00000000 | interpretIndirect(line, sub, 6);
		} else { // Indexed
			instruction = 0b00110100_00000000 | interpretInteger(sub, 8);
		}
		
		instruction |= reg << 8;
		return toHex(instruction, 4);
	}
	
	/**
	 * Assembles a LD line
	 * 
	 * @param line
	 * @param dataBits
	 * @param ramAddrBits
	 * @return
	 */
	private static String assembleLD(String line) {
		int startIndex = indexOfNotWhitespaceComma(line, 2);
		
		// Get destination reg
		int reg = interpretRegister(line.charAt(startIndex++));
		
		// Setup
		startIndex = indexOfNotWhitespaceComma(line, startIndex);
		int instruction = 0;
		
		// Immediate, register, indexed, or indirect
		if(isRegister(line.charAt(startIndex))) { // Register
			int sReg = interpretRegister(line.charAt(startIndex));
			instruction = 0b00101000_00000000;
			instruction |= sReg << 6;
		} else if(line.charAt(startIndex) == '[') { // Indexed/indirect
			// Get interior of []
			String sub = line.substring(startIndex + 1, line.indexOf(']')).trim();
			
			if(sub.contains("+") || isRegister(sub.charAt(0))) { // Has a register or addition? indirect
				instruction = 0b00111000_00000000 | interpretIndirect(line, sub, 6);
			} else { // Otherwise indexed
				instruction = 0b00110000_00000000 | interpretInteger(sub, 8);
			}
		} else { // Immediate
			instruction = 0b00100000_00000000 | interpretInteger(line.substring(startIndex), 8);
		}
		
		instruction |= reg << 8;
		return toHex(instruction, 4);
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
		// Isolate start address
		int startIndex = indexOfNotWhitespaceComma(line, 2),
			endIndex = indexOfWhitespaceComma(line, startIndex);
		
		int startAddress = interpretInteger(line.substring(startIndex), endIndex);
		
		// Start the section
		ProgramSection sec = new ProgramSection(startAddress, 0);
		
		// Add them values
		// Loop from known whitespace/comma
		for(startIndex = endIndex; startIndex < line.length(); startIndex = endIndex) {
			startIndex = indexOfNotWhitespaceComma(line, startIndex);
			
			// Apply literal string or integer
			char firstChar = line.charAt(startIndex);
			if(firstChar == '"') {	// String
				// Find closing " and interpret string
				endIndex = line.indexOf('"', ++startIndex);
				
				String hex = interpretStringLiteral(line.substring(startIndex, endIndex));
				sec.addData(hex, 2);
				endIndex++;
			} else if(firstChar == '\'') { // Character
				if(line.charAt(startIndex + 2) != '\'') throw new IllegalArgumentException("Invalid character literal: " + line.substring(startIndex));
				
				sec.addData(toHex(line.charAt(startIndex + 1), 2), 2);
				endIndex = startIndex + 2;
			} else if(Character.isDigit(firstChar)) { // Integer
				// Find end
				endIndex = indexOfWhitespaceComma(line, startIndex);
				
				sec.addData(toHex(interpretInteger(line.substring(startIndex, endIndex), dataBits), 2), 2);
				endIndex++;
			} else { // Error
				throw new IllegalArgumentException("Invalid literal: " + line.substring(startIndex));
			}
		}
		
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
	 * Interprets an indirect address (register or register + offset)
	 * 
	 * @param line The full line for exceptions
	 * @param str The string to interpret (contents of the brackets not including them, trimmed)
	 * @param bitWidth The width of the offset
	 * @return The bit-pattern integer of both register and offset
	 */
	private static int interpretIndirect(String line, String str, int bitWidth) {
		// Source register, offset
		int sReg = -1, offset = 0;
		
		if(str.contains("+")) { // Has register & offset
			// Can be either direction
			if(isRegister(str.charAt(0))) { // Register first
				sReg = interpretRegister(str.charAt(0));
			} else {						// Offset first
				offset = interpretInteger(str.substring(0, str.indexOf('+')).trim(), 6);
			}
			
			// Walk to second part
			int index = str.indexOf('+') + 1;
			while(Character.isWhitespace(str.charAt(index))) index++;
			str = str.substring(index);
			
			// Interpret opposite thing or except
			if(isRegister(str.charAt(0))) {
				if(sReg != -1) throw new IllegalArgumentException("Cannot have two registers in indirect: " + line);
				sReg = interpretRegister(str.charAt(0));
			} else {
				if(sReg == -1) throw new IllegalArgumentException("Cannot have two offsets in indirect: " + line);
				offset = interpretInteger(str.substring(0), 6);
			}
		} else {				// Should be register only
			// Make sure of it
			if(str.length() != 1) { // should work because trim
				throw new IllegalArgumentException("Invalid indirect: " + line);
			}
			
			sReg = interpretRegister(str.charAt(0));
		}
		
		// Apply binary
		int ret = offset & createMask(bitWidth);
		ret |= sReg << bitWidth;
		return ret;  
	}
	
	/**
	 * Creates a bit mask off bitWidth 1's, to limit things to that many bits
	 * 
	 * @param bitWidth
	 * @return Bitmask of bitWidth bits
	 */
	private static int createMask(int bitWidth) {
		int i = 0;
		
		while(bitWidth-- > 0) i = (i << 1) | 1;
		
		return i;
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
	 * Returns the index of the first character that is whitespace
	 * 
	 * @param s
	 * @param start
	 * @return
	 */
	private static int indexOfWhitespace(String s, int start) {
		for(int i = start; i < s.length(); i++) {
			if(Character.isWhitespace(s.charAt(i))) return i;
		}
		
		return -1;
	}
	
	/**
	 * Returns the index of the first character that is whitespace or a comma
	 * 
	 * @param s
	 * @param start
	 * @return
	 */
	private static int indexOfWhitespaceComma(String s, int start) {
		for(int i = start; i < s.length(); i++) {
			if(Character.isWhitespace(s.charAt(i)) || s.charAt(i) == ',') return i;
		}
		
		return -1;
	}
	
	/**
	 * Returns the index of the first character that isn't whitespace
	 * 
	 * @param s
	 * @param start
	 * @return
	 */
	private static int indexOfNotWhitespace(String s, int start) {
		for(int i = start; i < s.length(); i++) {
			if(!Character.isWhitespace(s.charAt(i))) return i;
		}
		
		return -1;
	}
	
	/**
	 * Returns the index of the first character that isn't whitespace or a comma
	 * 
	 * @param s
	 * @param start
	 * @return
	 */
	private static int indexOfNotWhitespaceComma(String s, int start) {
		for(int i = start; i < s.length(); i++) {
			if(!(Character.isWhitespace(s.charAt(i)) || s.charAt(i) == ',')) return i;
		}
		
		return -1;
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
			v = Integer.parseInt(value.substring(2), 2);
		} else if(value.startsWith("0x")) { // hex
			v = Integer.parseInt(value.substring(2), 16);
		} else { // decimal
			v = Integer.parseInt(value);
		}
		
		if(v > maxValue) throw new NumberFormatException("Value (" + value + ") outside of range (0 to " + maxValue + ").");
		return v;
	}
}




