package mechafinch.sim.e8.deep.stages;

import java.util.ArrayList;
import java.util.Arrays;

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
	
	private int[][] groups;
	
	private FetchStage fetch;
	private ExecutionStage exec;
	
	private ArrayList<DataDependency> dependencies;
	
	public DecodeStage(PipelinedSimulator sim, FetchStage fetch, ExecutionStage exec, int[][] groups) {
		super(sim);
		
		this.fetch = fetch;
		this.exec = exec;
		this.groups = groups;
		
		dependencies = new ArrayList<>();
	}
	
	@Override
	public void execute() {
		
		// No data no changes
		if(hasData) {
			
			// Note: change sim.incrementIP if needed
			
			/*
			 * Look for dependencies that prevent this instruction from continuing
			 * If there is a dependency, bubble the Fetch Stage which will implicitly bubble subsequent stages (including this one) until it has passed
			 * Dependencies have a time limit
			 */
			int timeToBubble = 0;
			
			// Deal with special stuff
			if(isSpecialType()) {
				// TODO: implement
				// For all, bubble until writeback occurs (where IP is set by jumps and branches, and when interrupts are run)
			}
			
			// Deal with generic stuff
			for(DataDependency dependency : dependencies) {
				if(dependency.cannotRead(instructionBinary, instructionType)) {	// We can't read with this instruction, bubble accordingly 
					// Bubble until writeback has gone through
					// If a dependency is in the last group, it will write back during this pipeline cycle and is invalidated
					int thisBubble = groups.length - dependency.getLocation() - 1;
					
					if(thisBubble > timeToBubble) timeToBubble = thisBubble;
				}
			}
			
			//Bubble until the dependencies are cleared
			fetch.addBubbles(timeToBubble);
		}
		
		// Decrement dependency timers
		for(DataDependency d : dependencies) d.updateLocation(groups);
	}
	
	/**
	 * Returns if an instruction requires a full pipeline clear
	 * 
	 * @return
	 */
	private boolean isSpecialType() {
		switch(instructionType) {
			case INT:
			case JMP_DIR: case JMP_IND: case JSR_DIR: case JSR_IND:
			case BEQ: case BLT: case BGT: case BZ: case BNZ:
				return true;
		
			default:
				return false;
		}
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
