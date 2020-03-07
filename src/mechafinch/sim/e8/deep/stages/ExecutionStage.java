package mechafinch.sim.e8.deep.stages;

import mechafinch.sim.e8.deep.PipelineStage;
import mechafinch.sim.e8.deep.PipelinedSimulator;

/**
 * Pipeline stage where execution occurs
 * Arithmetic, branch resolution, address resolution
 * 
 * @author Alex Pickering
 */
public class ExecutionStage extends PipelineStage {
	
	public ExecutionStage(PipelinedSimulator sim) {
		super(sim);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void execute() {
		// TODO Auto-generated method stub
	}

	@Override
	public void addBubbles(int cycles) {}

	@Override
	public void passData() {
		// TODO Auto-generated method stub
		
	}
	
}
