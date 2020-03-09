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
	
	private String instruction;
	
	/**
	 * Creates a Fetch Stage
	 * 
	 * @param sim Main simulator instance
	 * @param decode The next stage
	 */
	public FetchStage(PipelinedSimulator sim, DecodeStage decode) {
		super(sim);
		this.decode = decode;
		
		instruction = "";
		instructionPointer = 0;
	}
	
	@Override
	public void execute() {
		// We can do things without caring for bubbles but we can't give that data
		// Nab that instruction and store it until we pass our data
		instruction = String.format("%16s", Integer.toBinaryString(sim.ROM[instructionPointer])).replace(' ', '0');
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
		decode.receiveData(instruction, Instructions.getEnumeratedInstruction(instruction));
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
