package mechafinch.sim.e8.deep.stages;

import mechafinch.sim.e8.Instructions;
import mechafinch.sim.e8.deep.PipelineStage;
import mechafinch.sim.e8.deep.PipelinedSimulator;

/**
 * Pipeline stage where RAM is accessed
 * 
 * @author Alex Pickering
 */
public class AccessStage extends PipelineStage {
	
	// This to pass to writeback
	private int genericData,
				register;
	
	private boolean willBranch;
	
	// Our stuff
	private WritebackStage writeback;
	
	public AccessStage(PipelinedSimulator sim, WritebackStage writeback) {
		super(sim);
		
		this.writeback = writeback;
		
		genericData = 0;
		register = 0;
		willBranch = false;
	}
	
	@Override
	public void execute() {
		if(!hasData) return;
		
		// Do them accesses
		// genericData is set to the address for the memory access MOVs and register to the source/destination register 
		switch(instructionType) {
			case MOV_INDEX:
			case MOV_INDIR:
				if(instructionBinary.charAt(5) == '0') { // Load
					genericData = sim.RAM[genericData];
				} else { // Store
					sim.RAM[genericData] = sim.registers[register];
				}
				break;
				
			default:
		}
	}
	
	/**
	 * Receives necessary data
	 * 
	 * @param inst The instruction binary
	 * @param type The enumerated type
	 * @param genericData Generic data created by execute
	 * @param register The register to use
	 * @param willBranch True if we will jump
	 */
	public void receiveData(String inst, Instructions type, int genericData, int register, boolean willBranch) {
		instructionBinary = inst;
		instructionType = type;
		
		this.genericData = genericData;
		this.register = register;
		this.willBranch = willBranch;
	}

	@Override
	public void passData() {
		if(hasData) writeback.receiveData(instructionBinary, instructionType, genericData, register, willBranch);
		else writeback.receiveNoData();
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
