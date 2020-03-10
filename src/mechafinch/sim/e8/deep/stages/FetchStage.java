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
	
	private int instructionPointer;
	
	/**
	 * Creates a Fetch Stage
	 * 
	 * @param sim Main simulator instance
	 * @param decode The next stage
	 */
	public FetchStage(PipelinedSimulator sim, DecodeStage decode) {
		super(sim);
		this.decode = decode;
		
		instructionPointer = 0;
	}
	
	@Override
	public void execute() {
		// We can do things without caring for bubbles but we can't give that data
		// Nab that instruction and store it until we pass our data
		instructionBinary = String.format("%16s", Integer.toBinaryString(sim.ROM[instructionPointer])).replace(' ', '0');
	}

	@Override
	public void addBubbles(int cycles) {
		timeBubbled += cycles;
		isBubbled = true;
	}

	@Override
	public void passData() {
		// If we're bubbled, we have no data
		if(isBubbled) {
			decode.receiveData("", Instructions.NOP);
			
			timeBubbled--;
			if(timeBubbled == 0) {
				isBubbled = false;
			}
		}
		
		// Instruction type is determined here btw
		decode.receiveData(instructionBinary, Instructions.getEnumeratedInstruction(instructionBinary));
	}
	
	/**
	 * Pass the instruction pointer
	 * 
	 * @param instructionPointer
	 */
	public void receiveData(int instructionPointer) {
		this.instructionPointer = instructionPointer;
	}
	
}
