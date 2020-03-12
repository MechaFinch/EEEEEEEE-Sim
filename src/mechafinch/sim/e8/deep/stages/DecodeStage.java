package mechafinch.sim.e8.deep.stages;

import java.util.ArrayList;

import mechafinch.sim.e8.Instructions;
import mechafinch.sim.e8.deep.DataDependency;
import mechafinch.sim.e8.deep.PipelineStage;
import mechafinch.sim.e8.deep.PipelinedSimulator;

/**
 * Pipeline stage that determines dependencies and how much the pipeline needs to be bubbled
 * Also separates information for subsequent stages
 * 
 * @author Alex Pickering
 */
public class DecodeStage extends PipelineStage {
	
	private ExecutionStage exec;
	
	private ArrayList<DataDependency> dependencies;
	
	public DecodeStage(PipelinedSimulator sim, ExecutionStage exec) {
		super(sim);
		
		this.exec = exec;
		dependencies = new ArrayList<>();
	}
	
	@Override
	public void execute() {
		// Decrement dependency timers and remove them if they expire
		
		// No data no other changes
		if(!hasData) return;
		
		// Note: change sim.incrementIP if needed
		// Note: interrupts clear the pipeline before running, and don't use the rest of it
		
		
		/*
		 * Look for dependencies that prevent this instruction from continuing
		 * If there is a dependency, bubble the Fetch Stage which will implicitly bubble subsequent stages (including this one) until it has passed
		 * Dependencies have a time limit
		 */
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
		if(!hasData) exec.receiveNoData();
		
	}
	
}
