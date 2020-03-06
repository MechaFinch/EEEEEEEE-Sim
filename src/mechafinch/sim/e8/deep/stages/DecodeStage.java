package mechafinch.sim.e8.deep.stages;

import mechafinch.sim.e8.deep.PipelineStage;
import mechafinch.sim.e8.deep.PipelinedSimulator;

/**
 * Pipeline stage that determines dependencies and how much the pipeline needs to be bubbled
 * Also separates information for subsequent stages
 * 
 * @author Alex Pickering
 */
public class DecodeStage extends PipelineStage {
	
	public DecodeStage(PipelinedSimulator sim) {
		super(sim);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public int execute() {
		// TODO Auto-generated method stub
		return 0;
	}
	
}
