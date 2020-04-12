package mechafinch.sim.e8.deep.stages;

import java.io.IOException;

import mechafinch.sim.e8.E8Util;
import mechafinch.sim.e8.Instructions;
import mechafinch.sim.e8.deep.PipelineStage;
import mechafinch.sim.e8.deep.PipelinedSimulator;

/**
 * Pipeline stage where execution occurs
 * Arithmetic, branch resolution, address resolution
 * 
 * @author Alex Pickering
 */
public class ExecutionStage extends PipelineStage {
	
	private int genericData, // May be an address, result, etc
				register;	 // Usually destination register, sometimes source
	
	private boolean willBranch; 
	
	private AccessStage access;
	
	public ExecutionStage(PipelinedSimulator sim, AccessStage access) {
		super(sim);
		
		this.access = access;
		
		genericData = 0;
		register = 0;
		willBranch = false;
	}
	
	@Override
	public void execute() {
		if(!hasData) return;
		
		/*
		 * Branch Resolution
		 */
		int valA = getRegisterValue(6),
			valB = getRegisterValue(8);
		
		switch(instructionType) {
			case BEQ:
				willBranch = valA == valB;
				genericData = resolveBranchAddress();
				break;
				
			case BLT:
				willBranch = valA < valB;
				genericData = resolveBranchAddress();
				break;
				
			case BGT:
				willBranch = valA > valB;
				genericData = resolveBranchAddress();
				break;
				
			case BZ:
				willBranch = valB == 0;
				genericData = resolveBranchAddress();
				break;
				
			case BNZ:
				willBranch = valB != 0;
				genericData = resolveBranchAddress();
				break;
				
				// There's no break so that JSR does extra stuff as well as JMP stuff
			case JSR_DIR: case JSR_IND:
				sim.callStack.push(sim.instructionPointer);
				
			case JMP_DIR: case JMP_IND:
				genericData = parseImmediate(6);
				willBranch = true;
				break;
				
			case RET:
				genericData = sim.callStack.pop();
				willBranch = true;
				break;
				
			default:
		}
		
		/*
		 * Address Resolution
		 */
		switch(instructionType) {
			case MOV_INDIR:
				genericData = resolveAddress(8, 10);
				register = E8Util.getRegister(instructionBinary, 6);
				break;
			
			case JMP_IND:
			case JSR_IND:
				genericData = resolveAddress(6, 8);
				break;
				
			default:
		}
		
		/*
		 * Arithmetic and Logic
		 */
		switch(instructionType) {
			case ADD:
				// Get source and deal with carry
				genericData = getRegisterValue(10) + ((sim.cFlag && instructionBinary.charAt(6) == 1) ? 1 : 0) + getBValue();
				
				// Set carry flag
				sim.cFlag = genericData > sim.MAX_VALUE;
				
				// Finalize information
				finalizeArithmetic();
				break;
				
			case SUB:
				// Start with B value
				genericData = getBValue();
				
				// Deal with carry
				if(sim.cFlag && instructionBinary.charAt(6) == '1') genericData++;
				sim.cFlag = genericData > getRegisterValue(10);
				
				// Finish
				genericData = getRegisterValue(10) - genericData;
				finalizeArithmetic();
				break;
				
			case MUL:
				genericData = getRegisterValue(10) * getBValue();
				
				// Shift for high value
				if(instructionBinary.charAt(6) == '1') genericData = genericData >> sim.dataLength;
				
				finalizeArithmetic();
				break;
				
			case DIV:
				genericData = getRegisterValue(10) / getBValue();
				finalizeArithmetic();
				break;
				
			case MOD:
				// souper simple
				genericData = getRegisterValue(10) % getBValue();
				finalizeArithmetic();
				break;
				
			case AND:
				// This structure makes these much nicer
				genericData = getRegisterValue(10) & getBValue();
				finalizeLogic();
				break;
				
			case OR:
				genericData = getRegisterValue(10) | getBValue();
				finalizeLogic();
				break;
				
			case XOR:
				genericData = getRegisterValue(10) ^ getBValue();
				finalizeLogic();
				break;
				
			case NOT:
				genericData = getRegisterValue(10) ^ sim.MAX_VALUE;
				finalizeArithmetic(); // NOT and shifts don't do conditional inverts
				
			case BSL:
				genericData = getRegisterValue(10) << getBValue();
				finalizeArithmetic(); // no inversion
				break;
				
			case BSR:
				genericData = getRegisterValue(10);
				
				if(instructionBinary.charAt(8) == '0') { // Logical
					genericData >>>= getBValue();
				} else {								 // Arithmetic
					int signMask = (genericData >> (sim.dataLength - 1)) << (sim.dataLength - 1), // Isolates the sign at the right spot
						b = getBValue();
					
					// Preserve sign each bit of shifting
					while(b-- > 0) genericData = (genericData >>> 1) | signMask;
				}
				
				finalizeArithmetic();
				break;
				
			default:
		}
		
		/*
		 * Other MOVs, E-Types
		 */
		switch(instructionType) {
			case MOV_IMM:
			case MOV_INDEX: // this works because we only need the address
				genericData = parseImmediate(8) & sim.MAX_VALUE;
				register = E8Util.getRegister(instructionBinary, 6);
				break;
				
			case MOV_REG:
				genericData = sim.registers[E8Util.getRegister(instructionBinary, 8)];
				register = E8Util.getRegister(instructionBinary, 6);
				break;
				
			case PUSH:
				sim.dataStack.push(getBValue());
				break;
				
			case POP:
				genericData = sim.dataStack.pop();
				register = E8Util.getRegister(instructionBinary, 8);
				break;
				
			case PEEK:
				genericData = sim.dataStack.peek();
				register = E8Util.getRegister(instructionBinary, 8);
				break;
				
			case INT: // We'll just run the interrupt here and access and writeback will do nothing
				String code = "";
				
				// I could just convert the B value but asdjkfhaksdjfhaksjdfas
				if(instructionBinary.charAt(7) == '0') code = instructionBinary.substring(8);
				else code = E8Util.paddedBinaryString(sim.registers[E8Util.getRegister(instructionBinary, 14)]);
				
				try {
					if(sim.interrupt(code)) {
						sim.willHalt = true;
					}
				} catch(IOException e) {
					// print it and halt
					e.printStackTrace();
					sim.willHalt = true;
				}
				
			default:
		}
	}
	
	/**
	 * Masks the result, gets the register, and complements the result if needed
	 */
	private void finalizeLogic() {
		genericData ^= (instructionBinary.charAt(6) == '0' ? sim.ZERO_MASK : sim.MAX_VALUE);
		genericData &= sim.MAX_VALUE;
		register = E8Util.getRegister(instructionBinary, 8);
	}
	
	/**
	 * Gets the B value for arithmetic and logic
	 * 
	 * @return B value
	 */
	private int getBValue() {
		if(instructionBinary.charAt(7) == '0') return getRegisterValue(14);
		else return parseImmediate(12);
	}
	
	/**
	 * Masks the result to the max value and gets the register
	 */
	private void finalizeArithmetic() {
		genericData &= sim.MAX_VALUE;
		register = E8Util.getRegister(instructionBinary, 8);
	}
	
	/**
	 * Resolves an indirect address
	 * 
	 * @param registerIndex The index of the register in the binary
	 * @param offsetIndex The index of the offset in the binary
	 * @return The resolved address
	 */
	private int resolveAddress(int registerIndex, int offsetIndex) {
		return sim.registers[E8Util.getRegister(instructionBinary, registerIndex)] + 
			   Integer.parseInt(instructionBinary.substring(offsetIndex), 2);
	}
	
	/**
	 * Determines the target address of a branch
	 * 
	 * @return The resolved address
	 */
	private int resolveBranchAddress() {
		return sim.instructionPointer + (parseImmediate(10) * (instructionBinary.charAt(5) == '0' ? 1 : -1));
	}
	
	/**
	 * Parses an immediate starting at the given index
	 * 
	 * @param index The index of the immediate value
	 * @return The value of the immediate
	 */
	private int parseImmediate(int index) {
		return Integer.parseInt(instructionBinary.substring(index), 2);
	}
	
	/**
	 * Gets the value of a register given its location in the instruction
	 * 
	 * @param index
	 * @return
	 */
	private int getRegisterValue(int index) {
		return sim.registers[E8Util.getRegister(instructionBinary, index)];
	}
	
	/**
	 * Pass the binary and enumerated type of the instruction
	 * 
	 * @param inst Instruction binary string
	 * @param type Enumerated instruction type
	 */
	public void receiveData(String inst, Instructions type) {
		instructionBinary = inst;
		instructionType = type;
	}

	@Override
	public void passData() {
		if(hasData) access.receiveData(instructionBinary, instructionType, genericData, register, willBranch);
		else access.receiveNoData();
	}
	
}
