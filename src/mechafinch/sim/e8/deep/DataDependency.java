package mechafinch.sim.e8.deep;

import mechafinch.sim.e8.E8Util;
import mechafinch.sim.e8.Instructions;

/**
 * Represents a dependency in the pipeline
 * If a piece of data (register, memory location, etc) is dependent, it mustn't be written to but may be able to be read from
 * 
 * @author Alex Pickering
 */
public class DataDependency {
	
    // Describes what is dependent
	private String descriptor;
	
	// Tracks which group the dependency is in
	private int location;
	
	/**
	 * Creates a dependency with the given descriptor
	 * A dependency represents a location that will be written to
	 * <p> Format:
	 * <p> Character indicating if it is a register or memory location, then the register number or the address, or 'all' if the write is indirect
	 * 
	 * @param descriptor
	 * @param location the index of the stage group to start in
	 */
	public DataDependency(String descriptor, int location) {
		this.descriptor = descriptor;
		this.location = location;
	}
	
	/**
	 * Returns the group this dependency is a part of
	 * 
	 * @return
	 */
	public int getLocation() { return location; }
	
	/**
	 * Updates the location of the depenency
	 * 
	 * @param groups Array of groups
	 * @return true if the dependency has expired
	 */
	public boolean updateLocation(int[][] groups) {
		location++;
		
		return location >= groups.length;
	}
	
	/**
	 * Determines if the given instruction is allowed to read by this dependency
	 * "Will this dependency modify what the instruction will read?"
	 * We only need to deal with Read After Write data hazards thanks to the pipeline structure
	 * 
	 * @param bin Instrucion's binary string
	 * @param type Enumerated instruction
	 * @return true if the instruction isn't allowed to read, false if it can
	 */
	public boolean cannotRead(String bin, Instructions type) {
		boolean isRegister = descriptor.charAt(0) == 'r',						// Is the dependency a register
				all = descriptor.substring(1).equals("all");					// Is this all of memory or a known location
		int reg = isRegister ? Integer.parseInt("" + descriptor.charAt(1)) : 0,	// Which register is it?
			addr = all ? 0 : Integer.parseInt(descriptor.substring(1));			// What address is it at?
		
		switch(type) {
			case MOV_REG:
			case BZ:
			case BNZ:
				return isRegister && reg == E8Util.getRegister(bin, 8);	// Will we read from the register
			
			case MOV_INDEX:
				if(bin.charAt(5) == '0') {	// Reads from specific address 
					return !isRegister && (all || addr == Integer.parseInt(bin.substring(8), 2));
				} else {					// Reads from register
					return isRegister && reg == E8Util.getRegister(bin, 6);
				}
				
			case MOV_INDIR:
				if(bin.charAt(5) == '0') {	// Read from any address and a register
					return !isRegister || reg == E8Util.getRegister(bin, 8);
				} else {					// Read from 2 registers
					return isRegister && (reg == E8Util.getRegister(bin, 6) || reg == E8Util.getRegister(bin, 8));
				}
				
			// Most A types have the same reads
			// Read from 1 or 2 registers
			case ADD: case SUB: case MUL: case DIV: case MOD:
			case AND: case OR: case XOR: case BSL: case BSR:
				if(bin.charAt(7) == '0') {	// 2 registers
					return isRegister && (reg == E8Util.getRegister(bin, 10) || reg == E8Util.getRegister(bin, 14));
				} else {					// 1 register
					return isRegister && reg == E8Util.getRegister(bin, 10);
				}
				
			case NOT:
			case JMP_IND:
			case JSR_IND:
				return isRegister && reg == E8Util.getRegister(bin, 10);
				
			// Branch instructions also share a lot
			case BEQ: case BLT: case BGT:
				return isRegister && (reg == E8Util.getRegister(bin, 6) || reg == E8Util.getRegister(bin, 8));
				
			case PUSH:
				return bin.charAt(7) == '0' && isRegister && reg == E8Util.getRegister(bin, 14);
				
			default:
				return false;
		}
	}
	
	@Override
	public String toString() {
		return descriptor + ":" + location;
	}
}
