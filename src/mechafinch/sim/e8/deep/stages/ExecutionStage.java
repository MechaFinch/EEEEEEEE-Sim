package mechafinch.sim.e8.deep.stages;

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
	
	public ExecutionStage(PipelinedSimulator sim) {
		super(sim);
		
		genericData = 0;
		register = 0;
		willBranch = false;
	}
	
	@Override
	public void execute() {
		/*
		 * Address Resolution
		 */
		switch(instructionType) {
			case MOV_INDIR:
				genericData = resolveAddress(8, 10);
				break;
			
			case JMP_IND:
			case JSR_IND:
				genericData = resolveAddress(6, 8);
				break;
				
			default:
		}
		
		/*
		 * Branch Resolution
		 */
		int valA = getRegisterValue(6),
			valB = getRegisterValue(8);
		
		switch(instructionType) {
			case BEQ:
				willBranch = valA == valB;
				break;
				
			case BLT:
				willBranch = valA < valB;
				break;
				
			case BGT:
				willBranch = valA > valB;
				break;
				
			case BZ:
				willBranch = valB == 0;
				break;
				
			case BNZ:
				willBranch = valB != 0;
				break;
				
			case JMP_DIR: case JMP_IND:
			case JSR_DIR: case JSR_IND: case RET:
				willBranch = true;
				break;
				
			default:
		}
		
		/*
		 * Arithmetic
		 */
		switch(instructionType) {
			case ADD:
				// Get source and deal with carry
				genericData = getRegisterValue(10) + ((sim.cFlag && instructionBinary.charAt(6) == 1) ? 1 : 0);
				
				// Add B value
				if(instructionBinary.charAt(7) == '0') genericData += getRegisterValue(14);
				else genericData += parseImmediate(12);
				
				// Set carry flag
				sim.cFlag = genericData > sim.MAX_VALUE;
				
				// Finalize information
				finalizeGenericArithmetic();
				break;
				
			case SUB:
				// Start with B value
				if(instructionBinary.charAt(7) == '0') genericData = getRegisterValue(14);
				else genericData = parseImmediate(12);
				
				// Deal with carry
				if(sim.cFlag && instructionBinary.charAt(6) == '1') genericData++;
				sim.cFlag = genericData > getRegisterValue(10);
				
				// Finish
				genericData = getRegisterValue(10) - genericData;
				finalizeGenericArithmetic();
				break;
				
			case MUL:
				
		}
	}
	
	/**
	 * Masks the result to the max value and gets the register
	 */
	private void finalizeGenericArithmetic() {
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
		// TODO Auto-generated method stub
		
	}
	
}
