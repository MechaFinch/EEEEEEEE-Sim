package mechafinch.sim.e8.deep.stages;

import java.util.ArrayList;
import java.util.Arrays;

import mechafinch.sim.e8.E8Util;
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
	
	private int groupIndex;
	
	private int[][] groups;
	
	private boolean dependencyIssue;
	
	private FetchStage fetch;
	private ExecutionStage exec;
	
	private ArrayList<DataDependency> dependencies;
	
	public DecodeStage(PipelinedSimulator sim, ExecutionStage exec, int[][] groups, boolean dependencyIssue) {
		super(sim);
		
		this.exec = exec;
		this.groups = groups;
		this.dependencyIssue = dependencyIssue;
		
		dependencies = new ArrayList<>();
		
		// We need to know which group we're in
		for(int i = 0; i < groups.length; i++) {
			if(groups[i][1] >= 1) { // Find the first group containing decode
				groupIndex = i;
				break;
			}
		}
	}
	
	// Because this will be constructed before the fetch stage is, we need to get this afterwards
	public void setFetchStage(FetchStage f) { fetch = f; }
	
	@Override
	public void execute() {
		
		// No data no changes
		if(hasData) {
			
			// Note: change sim.incrementIP if needed
			
			/*
			 * Look for dependencies that prevent this instruction from continuing
			 * If there is a dependency, bubble the Fetch Stage which will implicitly bubble subsequent stages (including this one) until it has passed
			 * Dependencies have a time limit
			 * 
			 * This stuff will make us wait until this instruction is ready to be executed, and invalidates the current data
			 */
			int timeToBubble = 0;
			
			// Deal with generic stuff
			for(DataDependency dependency : dependencies) {
				if(dependency.cannotRead(instructionBinary, instructionType)) {	// We can't read with this instruction, bubble accordingly 
					// Bubble until writeback has gone through
					// If a dependency is in the last group, it will write back during this pipeline cycle and is invalidated
					int thisBubble = groups.length - dependency.getLocation() - (dependencyIssue ? 0 : 1);
					
					if(thisBubble > timeToBubble) timeToBubble = thisBubble;
				}
			}
			
			//Bubble until the dependencies are cleared
			if(timeToBubble > 0) {
				fetch.addBubbles(timeToBubble);
				hasData = false;
				
				// Since fetch will increment the IP, decrement it to get the same instruction once bubbling finishes
				// This may cause problems later but asdkfjhasdkjfhaskdjfkasjdf
				sim.instructionPointer--;
			} else { // This instruciton is ready to go. If its a special type, we need to have other things wait while it executes
				if(isSpecialType()) {
					// For all, bubble until writeback occurs (where IP is set by jumps and branches, and when interrupts are run)
					if(groups.length != 1) fetch.addBubbles(groups.length - groupIndex);
				} else if(instructionType != Instructions.NOP) { // Create dependencies for the instruction
					switch(instructionType) {
						// Things that always write to the register at 6
						case MOV_IMM:
						case MOV_REG:
							dependencies.add(new DataDependency("r" + E8Util.getRegister(instructionBinary, 6), groupIndex));
							break;
							
						// Either writes to the register at 6 or memory
						case MOV_INDEX:
						case MOV_INDIR:
							if(instructionBinary.charAt(5) == '0') { // Writes to register
								dependencies.add(new DataDependency("r" + E8Util.getRegister(instructionBinary, 6), groupIndex));
							} else { // Writes to memory
								if(instructionType == Instructions.MOV_INDEX) { // Specific location
									dependencies.add(new DataDependency("m" + Integer.parseInt(instructionBinary.substring(8), 2), groupIndex));
								} else { // Somewhere
									dependencies.add(new DataDependency("mall", groupIndex));
								}
							}
							break;
							
						// Writes to the register at 8
						case ADD: case SUB: case MUL: case DIV: case MOD:
						case AND: case OR: case XOR: case NOT: case BSL: case BSR:
						case POP: case PEEK:
							dependencies.add(new DataDependency("r" + E8Util.getRegister(instructionBinary, 8), groupIndex));
							break;
							
						default:
							break;
					}
				}
			}
		}
		
		// Decrement dependency timers
		for(int i = 0; i < dependencies.size(); i++) 
			if(dependencies.get(i).updateLocation(groups)) 
				dependencies.remove(i--);
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
		
		hasData = true;
	}

	@Override
	public void passData() {
		if(hasData) exec.receiveData(instructionBinary, instructionType);
		else exec.receiveNoData();
	}
	
	/**
	 * Gets the current dependencies
	 * 
	 * @return
	 */
	public String getDependenciesString() {
		return dependencies.toString();
	}
}
