package mechafinch.sim.e8.deep.stages;

import mechafinch.sim.e8.Instructions;
import mechafinch.sim.e8.deep.PipelineStage;
import mechafinch.sim.e8.deep.PipelinedSimulator;

/**
 * Pipeline stage that fetches instructions from memory
 * 
 * @author Alex Pickering
 */
public class FetchStage extends PipelineStage {
	
	private DecodeStage decode;
	
	private int timeBubbled;
	
	/**
	 * Creates a Fetch Stage
	 * 
	 * @param sim Main simulator instance
	 * @param decode The next stage
	 */
	public FetchStage(PipelinedSimulator sim, DecodeStage decode) {
		super(sim);
		this.decode = decode;
		
		timeBubbled = 0;
	}
	
	@Override
	public void execute() {
		// We can do things without caring for bubbles but we can't give that data
		// Nab that instruction and store it until we pass our data
		instructionBinary = String.format("%16s", Integer.toBinaryString(sim.ROM[sim.instructionPointer])).replace(' ', '0');
	}
	
	/**
	 * Bubbles the origin of the pipeline for some number of cycles
	 * 
	 * @param cycles
	 */
	public void addBubbles(int cycles) {
		timeBubbled += cycles;
	}

	@Override
	public void passData() {
		// If we're bubbled, we have no data
		if(timeBubbled > 0) {
			decode.receiveData("", Instructions.NOP);
			timeBubbled--;
		}
		
		// Instruction type is determined here btw
		decode.receiveData(instructionBinary, Instructions.getEnumeratedInstruction(instructionBinary));
		
		// Increment IP if needed, only done once data from the old ip was passed and necessity determined
		if(sim.incrementIP) sim.instructionPointer++;
	}
	
}
