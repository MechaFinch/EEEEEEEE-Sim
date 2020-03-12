package mechafinch.sim.e8.deep.stages;

import mechafinch.sim.e8.deep.PipelineStage;
import mechafinch.sim.e8.deep.PipelinedSimulator;

/**
 * Pipeline stage where RAM is accessed
 * 
 * @author Alex Pickering
 */
public class AccessStage extends PipelineStage {
	
	
	
	public AccessStage(PipelinedSimulator sim) {
		super(sim);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void execute() {
		// TODO Auto-generated method stub
	}

	@Override
	public void passData() {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * Returns where the stage will read from in memory
	 * 
	 * @return
	 */
	public String getLoadedLocation() {
		if(!hasData) return ""; // If we don't have data, we don't run
		
		switch(instructionType) {
			//Immediate address
			//because of how this and indirect are positioned, the 'register at bits 8-9' section is called
			//if it stores to a register 
			case MOV_INDEX:
				if(instructionBinary.charAt(5) == '1') {
					return ""; // return E8Util.toHex(Integer.parseInt(instructionBinary.substring(8), 2), 4);
				}
				
			//Indirect address
			case MOV_INDIR:
				if(instructionBinary.charAt(5) == '1') {
					return "RDM";
				}
				
			default:
				return "";
		}
	}
	
	/**
	 * Returns where the stage will write to in memory
	 * 
	 * @return
	 */
	public String getStoredLocation() {
		if(!hasData) return ""; // no data no storage
		
		return ""; 
	}
	
}
