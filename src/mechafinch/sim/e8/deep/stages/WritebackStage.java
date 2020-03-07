package mechafinch.sim.e8.deep.stages;

import mechafinch.sim.e8.deep.PipelineStage;
import mechafinch.sim.e8.deep.PipelinedSimulator;

/**
 * Pipeline stage where registers are written
 * 
 * @author Alex Pickering
 */
public class WritebackStage extends PipelineStage {
	
	public WritebackStage(PipelinedSimulator sim) {
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
