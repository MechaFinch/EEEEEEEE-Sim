package mechafinch.sim.e8.deep;

import mechafinch.sim.e8.E8Util;
import mechafinch.sim.e8.Instructions;

/**
 * Represents a dependency in the pipeline
 * 
 * @author Alex Pickering
 */
public class DataDependency {
	
	private String descriptor;
	
	/**
	 * Creates a dependency with the given descriptor
	 * <p> Format:
	 * <p> Character indicating if it is a register or memory location, then the register number or the address, or 'all' if the read/write is indirect
	 * 
	 * @param descriptor
	 */
	public DataDependency(String descriptor) {
		this.descriptor = descriptor;
	}
	
	/**
	 * Determines if an instruction is dependent on a later state
	 * 
	 * @param bin The binary string of the instruction
	 * @param type The enumeration of the instruction
	 * @return If this instruction is dependent
	 */
	public boolean isDependent(String bin, Instructions type) {
		boolean isRegister = descriptor.charAt(0) == 'r',
				all = descriptor.substring(1).equals("all");
		int reg = Integer.parseInt("" + descriptor.charAt(1)),
			addr = all ? 0 : Integer.parseInt(descriptor.substring(1));
		
		switch(type) {
			case MOV_IMM:
			case JMP_IND:
			case JSR_IND:
				if(isRegister) return reg == E8Util.getRegister(bin, 6); // dest register
				return false;
		
			case MOV_REG:
			case BEQ:
			case BLT:
			case BGT:
				if(isRegister) return reg == E8Util.getRegister(bin, 6) || // Both source and dest matter
									  reg == E8Util.getRegister(bin, 8);
				return false;
				
			case MOV_INDEX:
				if(isRegister) return reg == E8Util.getRegister(bin, 6); // s/d reg
				else if(all) return true;												  // is addr equal?
				return addr == Integer.parseInt(bin.substring(8), 2);
				
			case MOV_INDIR:
				if(isRegister) return reg == E8Util.getRegister(bin, 6) || // Is s/d reg dependant
									  reg == E8Util.getRegister(bin, 8);
				return true;															// Don't deal with registers, always dependent on any memory
				
			// Arithmetic argument patters are mostly the same
			case ADD:
			case SUB:
			case MUL:
			case DIV:
			case MOD:
			case AND:
			case OR:
			case XOR:
			case BSL:
			case BSR:
				if(isRegister) return reg == E8Util.getRegister(bin, 8) ||  // dest
									  reg == E8Util.getRegister(bin, 10) || // src a
									  (bin.charAt(7) == '0' && reg == E8Util.getRegister(bin, 14)); // src b if not immediate
				return false;
				
			case NOT:
				if(isRegister) return reg == E8Util.getRegister(bin, 8) || // no immediates in this one
									  reg == E8Util.getRegister(bin, 10);
				
			case BZ:
			case BNZ:
			case POP:
			case PEEK:
				if(isRegister) return reg == E8Util.getRegister(bin, 8);
				return false;
				
			case PUSH:
			case INT:
				if(isRegister) return bin.charAt(7) == '0' && reg == E8Util.getRegister(bin, 14);
				return false;
							
			default: // Any isntruction that cannot be dependent is a default case
				return false;
		}
	}
}
