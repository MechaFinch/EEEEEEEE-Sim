package mechafinch.sim.e8.deep;

/**
 * A simulated stage in the pipeline
 * 
 * @author Alex Pickering
 */
public abstract class PipelineStage {
	
	private PipelinedSimulator sim;
	
	/**
	 * Creates a pipeline stage as a part of the given PipelinedSimulator
	 * 
	 * @param sim
	 */
	public PipelineStage(PipelinedSimulator sim) {
		this.sim = sim;
	}
	
	/**
	 * Runs the stage, returning how many cycles it took
	 * @return The number of cycles used
	 */
	public abstract int execute();
}
