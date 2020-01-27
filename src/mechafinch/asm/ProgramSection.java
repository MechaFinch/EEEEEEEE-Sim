package mechafinch.asm;

/**
 * Thing that holds the data for a section
 * 
 * @author Alex Pickering
 */
public class ProgramSection {
	
	private int startAddress,
				type,
				length;
	
	private String data;
	
	/**
	 * Creates an empty Section of the given type at the given address
	 * 
	 * @param startAddress
	 * @param type
	 */
	public ProgramSection(int startAddress, int type) {
		this.startAddress = startAddress;
		this.type = type;
		
		length = 0;
		data = "";
	}
	
	/**
	 * Adds more data to the data
	 * 
	 * @param data The data
	 * @param len The length of each word (2 for 1 byte)
	 */
	public void addData(String data, int len) {
		this.data += data;
		length += data.length() / len;
	}
	
	/**
	 * Converts the section to its hexadecimal representation
	 * 
	 * @param dataLength Data length in characters
	 * @param ramAddressLength RAM Address length in characters
	 * @param romAddressLength ROM Address length in characters
	 * @return The section as a hexadecimal string
	 */
	public String toHexString(int dataLength, int ramAddressLength, int romAddressLength) {
		return toPrintedHexString(dataLength, ramAddressLength, romAddressLength).replace(" ", "");
	}
	
	/**
	 * Converts the section to its hexadecimal representation, with spaces separating the parts
	 * 
	 * @param dataLength Data length in characters
	 * @param ramAddressLength RAM Address length in characters
	 * @param romAddressLength ROM Address length in characters
	 * @return The section as a hexadecimal string
	 */
	public String toPrintedHexString(int dataLength, int ramAddressLength, int romAddressLength) {
		String s = "";
		
		// Apply type as byte
		switch(type) {
			case 0:
				s += "00 ";
				break;
				
			case 1:
				s += "01 ";
				break;
		}
		
		// Apply length as 2 bytes
		s += String.format("%4s", Integer.toHexString(length).toUpperCase()).replace(' ', '0') + " ";
		
		// Apply starting address
		s += String.format("%" + (type == 0 ? ramAddressLength : romAddressLength) + "s", Integer.toHexString(startAddress).toUpperCase()).replace(' ', '0') + " ";
		
		// Add data
		s += data;
		
		return s;
	}
}
