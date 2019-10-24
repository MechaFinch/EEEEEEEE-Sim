package mechafinch.sim.e8;

/**
 * A class for utility methods for the E8 Simulator
 * 
 * @author Alex Pickering
 */
public class E8Util {
	
	/**
	 * Converts a number, 0-3, into a register character, A B C or D
	 * 
	 * @param regBits A number where the least significant 2 bits represent the register to be parsed
	 * @return A B C or D for 0 1 2 and 3 respectively
	 */
	public static String toRegister(String regBits) {
		switch(regBits) {
			case "00":
				return "A";
			
			case "01":
				return "B";
			
			case "10":
				return "C";
				
			default:
				return "D";
		}
	}
}
