package mechafinch.sim.e8.deep.stages;

import mechafinch.sim.e8.Instructions;
import mechafinch.sim.e8.deep.PipelineStage;
import mechafinch.sim.e8.deep.PipelinedSimulator;

/**
 * Pipeline stage that determines dependencies and how much the pipeline needs to be bubbled
 * Also separates information for subsequent stages
 * 
 * @author Alex Pickering
 */
public class DecodeStage extends PipelineStage {
	
	private String instructionBinary;
	
	private Instructions instructionType;
	
	public DecodeStage(PipelinedSimulator sim) {
		super(sim);
		
		instructionBinary = "";
		instructionType = Instructions.NOP;
	}
	
	@Override
	public void execute() {
		// TODO Auto-generated method stub
	}

	@Override
	public void addBubbles(int cycles) {
		timeBubbled += cycles;
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
