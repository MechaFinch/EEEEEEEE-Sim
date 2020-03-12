package mechafinch.sim.e8.deep;

import java.io.IOException;
import java.util.ArrayList;

import mechafinch.sim.e8.E8Simulator;
import mechafinch.sim.e8.deep.stages.*;

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
	public boolean incrementIP;
	
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
		
		// Create the stages
		fetchStage = new FetchStage(this, decodeStage);
		decodeStage = new DecodeStage(this, executionStage);
		executionStage = new ExecutionStage(this);
		accessStage = new AccessStage(this);
		writebackStage = new WritebackStage(this);
		
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
		
		// Can we continue?
		// TODO: can continue flag
		
		// Pass data between groups
		for(int gri = 0; gri < groupings.length; gri++) {
			stages.get(groupings[gri][1]).passData();
		}
		
		return true;
	}
	
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








