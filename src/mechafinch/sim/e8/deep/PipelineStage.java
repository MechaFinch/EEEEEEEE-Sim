package mechafinch.sim.e8.deep;

import mechafinch.sim.e8.Instructions;

/**
 * A simulated stage in the pipeline
 * 
 * @author Alex Pickering
 */
public abstract class PipelineStage {
	
	protected PipelinedSimulator sim;
	
	protected boolean hasData;
	
	protected String instructionBinary;
	
	protected Instructions instructionType;
	
	/**
	 * Creates a pipeline stage as a part of the given PipelinedSimulator
	 * 
	 * @param sim
	 */
	public PipelineStage(PipelinedSimulator sim) {
		this.sim = sim;
		
		hasData = false;
		instructionBinary = "";
		instructionType = Instructions.NOP;
	}
	
	/**
	 * Runs the stage
	 */
	public abstract void execute();
	
	
	/**
	 * Passes information to the next stage of the pipeline
	 */
	public abstract void passData();
	
	/**
	 * Specifically receives no data
	 */
	public void receiveNoData() {
		instructionBinary = "";
		instructionType = Instructions.NOP;
		hasData = false;
	}
}
