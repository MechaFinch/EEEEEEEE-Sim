package mechafinch.asm;

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
		String[] internalRep = assemble(source);
		
		// Convert from internal representation
		
		return null;
	}
	
	/**
	 * Assembles to an internal hex string representation
	 * <p> this method does the actual work
	 * 
	 * @param source The source file as an array of lines
	 * @return An array of strings in internal format
	 */
	private static String[] assemble(String[] source) {
		return null;
	}
}
