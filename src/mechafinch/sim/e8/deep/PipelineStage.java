package mechafinch.sim.e8.deep;

/**
 * A simulated stage in the pipeline
 * 
 * @author Alex Pickering
 */
public abstract class PipelineStage {
	
	protected PipelinedSimulator sim;
	
	protected int timeBubbled;
	
	protected boolean isBubbled;
	
	/**
	 * Creates a pipeline stage as a part of the given PipelinedSimulator
	 * 
	 * @param sim
	 */
	public PipelineStage(PipelinedSimulator sim) {
		this.sim = sim;
		
		timeBubbled = 0;
		isBubbled = false;
	}
	
	/**
	 * Runs the stage
	 */
	public abstract void execute();
	
	/**
	 * Adds time for this stage to be bubbled
	 * 
	 * @param cycles Number of cycles to wait for
	 */
	public abstract void addBubbles(int cycles);
	
	/**
	 * Passes information to the next stage of the pipeline
	 */
	public abstract void passData();
}
