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
	
	private int[][] groupings;
	
	private int cyclesElapsed;
	
	protected FetchStage fetchStage;
	protected DecodeStage decodeStage;
	protected ExecutionStage executionStage;
	protected AccessStage accessStage;
	protected WritebackStage writebackStage;
	
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
		
		fetchStage = new FetchStage(this, decodeStage);
		decodeStage = new DecodeStage(this);
		executionStage = new ExecutionStage(this);
		accessStage = new AccessStage(this);
		writebackStage = new WritebackStage(this);
		
		// Validate groups
		ArrayList<Integer> usedStages = new ArrayList<>();
		
		for(int i = 0; i < groupings.length; i++) {
			int start = groupings[i][0],
				end = groupings[i][1];
			
			// Make sure things are in order
			if(start < end) throw new IllegalArgumentException(String.format("Invalid group: Start (%s) must be less than or equal to end (%s)", start, end));
			
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
		 * <<Planning>>
		 * Loop over each stage, running it, and tracking cycles according to groups (this may become unnecessary and group lengths can be tracked beforehand instead)
		 * Take the longest cycle count and add it to the elapsed cycles
		 * Check that we can continue (exception flag, interrupts)
		 * If we can't stop execution and such
		 * Transfer information between stages
		 */
		
		return true;
	}
	
	@Override
	public String getLoadedLocations() {
		// TODO: implement
		return "";
	}
	
	@Override
	public String getStoredLocation() {
		// TODO: implement
		return "";
	}
}








