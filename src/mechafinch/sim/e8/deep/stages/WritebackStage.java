package mechafinch.sim.e8.deep.stages;

import mechafinch.sim.e8.Instructions;
import mechafinch.sim.e8.deep.PipelineStage;
import mechafinch.sim.e8.deep.PipelinedSimulator;

/**
 * Pipeline stage where registers are written
 * 
 * @author Alex Pickering
 */
public class WritebackStage extends PipelineStage {
	
	private int genericData,
				register;
	
	boolean willBranch;
	
	public WritebackStage(PipelinedSimulator sim) {
		super(sim);
		
		genericData = 0;
		register = 0;
		willBranch = false;
	}
	
	@Override
	public void execute() {
		if(!hasData) return;
		
		switch(instructionType) {
			case MOV_IMM: case MOV_REG:
			case ADD: case SUB: case MUL: case DIV: case MOD:
			case AND: case OR: case XOR: case NOT: case BSL: case BSR:
			case POP: case PEEK:
				sim.registers[register] = genericData;
				break;
				
			case MOV_INDEX:
			case MOV_INDIR:
				if(instructionBinary.charAt(5) == '0') {
					sim.registers[register] = genericData;
				}
				break;
				
			case BEQ: case BLT: case BGT: case BZ: case BNZ:
			case JMP_DIR: case JMP_IND:
			case JSR_DIR: case JSR_IND: case RET:
				if(willBranch) sim.instructionPointer = genericData & sim.ADDRESS_MASK;
				break;
				
			default:
		}
	}
	
	/**
	 * Receives necessary data
	 * 
	 * @param inst Instruction binary
	 * @param type Enumerated data
	 * @param genericData Generic data
	 * @param register Destination register
	 * @param willBranch True if we jump
	 */
	public void receiveData(String inst, Instructions type, int genericData, int register, boolean willBranch) {
		instructionBinary = inst;
		instructionType = type;
		
		this.genericData = genericData;
		this.register = register;
		this.willBranch = willBranch;
		
		hasData = true;
	}

	@Override
	public void passData() {
		// We don't pass anything
	}
	
}
