package mechafinch.sim.e8.deep.stages;

import mechafinch.sim.e8.deep.PipelineStage;
import mechafinch.sim.e8.deep.PipelinedSimulator;

/**
 * Pipeline stage that fetches instructions from memory
 * 
 * @author Alex Pickering
 */
public class FetchStage extends PipelineStage {
	
	private DecodeStage decode;
	
	/**
	 * Creates a Fetch Stage
	 * 
	 * @param sim Main simulator instance
	 * @param decode The next stage
	 */
	public FetchStage(PipelinedSimulator sim, DecodeStage decode) {
		super(sim);
		this.decode = decode;
	}
	
	@Override
	public void execute() {
		// TODO Auto-generated method stub
	}

	@Override
	public void addBubbles(int cycles) {
		timeBubbled += cycles;
	}

	@Override
	public void passData() {
		// TODO Auto-generated method stub
		
	}
	
}
