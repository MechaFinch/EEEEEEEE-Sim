package mechafinch.sim.e8.deep;

import java.io.IOException;
import java.util.ArrayList;

import mechafinch.sim.e8.E8Simulator;
import mechafinch.sim.e8.deep.stages.*;
import mechafinch.sim.test.TestUtil;

/**
 * An E8Simulator that simulates pipelining
 * Extends E8Simulator because why write all those helpers again?
 * 
 * @author Alex Pickering
 */
public class PipelinedSimulator extends E8Simulator {
	
    // Grouped stages
	public int[][] groupings;
	
	private int cyclesElapsed,
				cyclesPer;
	
	// Interface stuff
	public boolean incrementIP,
				   willHalt;
	
	// The properly typed stages
	private FetchStage fetchStage;
	private DecodeStage decodeStage;
	private ExecutionStage executionStage;
	private AccessStage accessStage;
	private WritebackStage writebackStage;
	
	// Generic list containing these stages
	private ArrayList<PipelineStage> stages;
	
	/**
	 * Creates a pipelined simulator with the given groupings in the format [group index]{start index 0-4, end index 0-4 inclusive}
	 * groupings of {{0, 4}} would be completely un-pipelined
	 * 
	 * @param groupings
	 */
	public PipelinedSimulator(int[] ram, int[] rom, int[][] groupings) {
		// Use parameters
		super(ram, rom, 16);
		this.groupings = groupings;
		
		// Init other stuff
		cyclesElapsed = 0;
		cyclesPer = 0;
		incrementIP = true;
		
		// Create the stages with whatever they need
		// Also the right order
		writebackStage = new WritebackStage(this);
		accessStage = new AccessStage(this, writebackStage);
		executionStage = new ExecutionStage(this, accessStage);
		decodeStage = new DecodeStage(this, executionStage, groupings);
		fetchStage = new FetchStage(this, decodeStage);
		
		// Give decode its fetch
		decodeStage.setFetchStage(fetchStage);
		
		// Place stages in generic list
		stages = new ArrayList<>();
		stages.add(fetchStage);
		stages.add(decodeStage);
		stages.add(executionStage);
		stages.add(accessStage);
		stages.add(writebackStage);
		
		// Validate groups
		ArrayList<Integer> usedStages = new ArrayList<>();
		
		for(int i = 0, pe = -1; i < groupings.length; i++) {
			int start = groupings[i][0],
				end = groupings[i][1],
				len = end - start;
			
			// Set cyclesPer to the longest stage's length
			if(len > cyclesPer) cyclesPer = len;
			
			// Make sure groups are in order
			if(start <= pe) throw new IllegalArgumentException("Groups must be in order and without overlap");
			pe = end;
			
			// Make sure groups make sense
			if(start > end) throw new IllegalArgumentException(String.format("Invalid group: Start (%s) must be less than or equal to end (%s)", start, end));
			
			// Make sure there aren't duplicates
			for(int j = start; j <= end; j++) {
				if(usedStages.contains(j)) throw new IllegalArgumentException(String.format("Duplicate stage index in (%s - %s): %s", start, end, j));
				usedStages.add(j);
			}
		}
	}
	
	/**
	 * Creates a pipelined simulator with single-stage groups
	 */
	public PipelinedSimulator(int[] ram, int[] rom) {
		this(ram, rom, new int[][] {
			{0, 0},
			{1, 1},
			{2, 2},
			{3, 3},
			{4, 4}
		});
	}
	
	@Override
	public boolean step() throws IOException {
		/*
		 * Loop over each stage, running it
		 * Check that we can continue (exception flag, interrupts)
		 * If we can't stop execution and such
		 * Transfer information between stages
		 */
		
		// Loop over groups (group index) and execute them
		for(int gri = 0; gri < groupings.length; gri++) {
			// Loop over each stage of the group (stage index)
			for(int si = groupings[gri][0]; si <= groupings[gri][1]; si++) {
				// Run the stage
				stages.get(si).execute();
				
				// If there are subsequent stages in the group, pass the data here
				if(si < groupings[gri][1]) stages.get(si).passData();
			}
		}
		
		// Debug time
		System.out.println("Stepped: " + getStageReadout() + "     " + TestUtil.hexString(registers, 16) + "     " + fetchStage.getTimeBubbled() + "     " + decodeStage.getDependenciesString());
		System.out.println("         " + getStageReadout().replaceAll(".", "-"));
		
		// Update running time
		cyclesElapsed += cyclesPer;
		
		// Can we continue?
		if(willHalt) {
			// halt here
			System.out.println("\nExecution completed in " + cyclesElapsed + " cycles.");
			return false;
		}
		
		/*
		 * Pass data between groups
		 * This is done backwards because some stages set flags when receiving
		 * This way they can use those flags for sending data
		 */
		for(int gri = groupings.length - 1; gri >= 0; gri--) {
			stages.get(groupings[gri][1]).passData();
		}
		
		return true;
	}
	
	/**
	 * Gives a visualization of the current stages
	 * 
	 * @return
	 */
	public String getStageReadout() {
		return String.format("%-10s | %-10s | %-10s | %-10s | %-10s", fetchStage.instructionType, decodeStage.instructionType,
																	  executionStage.instructionType, accessStage.instructionType,
																	  writebackStage.instructionType);
	}
	
	public int getElapsedCycles() { return cyclesElapsed; }
	
	@Override
	public String getLoadedLocations() {
		// TODO: implement, maybe
		return "";
	}
	
	@Override
	public String getStoredLocation() {
		// TODO: implement, maybe
		return "";
	}
}








