package mechafinch.sim.test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import mechafinch.asm.E8Assembler;
import mechafinch.sim.e8.E8Simulator;
import mechafinch.sim.e8.deep.PipelinedSimulator;

/**
 * A place to test the simulator in a standard environment
 * 
 * @author Alex Pickering
 */
public class E8SimTest {
	public static void main(String[] args) throws IOException {
		/*
		 * Assemble
		 */
		BufferedReader br = new BufferedReader(new FileReader("asm_tests/is prime mul.txt"));
		List<String> linesList = br.lines().collect(Collectors.toList());
		br.close();
		
		String[] lines = linesList.toArray(new String[linesList.size()]);
		//for(String s : lines) System.out.println(s);
		
		String[] assembled = E8Assembler.assembleToDualHex(lines);
		
		/*
		 * Run
		 */
		int[] ram = new int[65536],
			  rom = new int[1024];
		/*	  romContents = new int[] {	//Test
				0b00100000_01011100, // LD A, 0x5C
				0b10011001_00000011, // BNZ A, +0x03
				0b00100011_11111111, // LD D, 0xFF
				0b11100000_00000000, // exit
				0b10011100_00000001, // BZ A, -0x02
				0b01100000_00000011, // JMP 0x003
		};
		
		TestUtil.insert(romContents, rom);
		*/
		
		/*
		// Load from the f i l e
		BufferedReader br = new BufferedReader(new FileReader("asm_tests/is prime simple.txt"));
		String ramString = br.readLine(),
			   romString = br.readLine();
		br.close();
		*/
		
		String ramString = assembled[0],
			   romString = assembled[1];
		
		// Parse ram and rom
		for(int i = 0; i < ramString.length(); i += 4) {
			ram[i / 4] = Integer.parseInt(ramString.substring(i, i + 4), 16);
		}
		
		for(int i = 0; i < romString.length(); i += 4) {
			rom[i / 4] = Integer.parseInt(romString.substring(i, i + 4), 16);
		}
		
		// Individual Testing
		/*
		//E8Simulator testSim = new E8Simulator(ram, rom, 16);
		
		// Pipelined simulator without pipelining so we don't have do deal with it
		//PipelinedSimulator testSim = new PipelinedSimulator(ram, rom, new int[][]{{0, 4}});
		
		PipelinedSimulator testSim = new PipelinedSimulator(ram, rom, new int[][]{{0, 2}, {3, 4}});
		
		// Pipelined simulator with full pipelining
		//PipelinedSimulator testSim = new PipelinedSimulator(ram, rom);
		
		//Execute order 66
		int i = 0;
		for(; i < 200_000 && testSim.step(); i++);
		
		//Dump state, execute, dump again
		//TestUtil.dumpState(testSim);
		//testSim.step();
		System.out.println();
		TestUtil.dumpState(testSim);
		TestUtil.dumpSegment(testSim, 11, 11 + 0x0100);
		//TestUtil.dumpSegment(testSim, 0, 100);
		System.out.println("\n\n" + i);
		*/
		
		// Systemic Testing
		// Loop over possible pipelines
		recursiveAnalysis(ram, rom, new int[0]);
		System.out.println("\n");
	}
	
	/**
	 * Recursively goes over all possible combinations of pipeline structures
	 * 
	 * @param ram
	 * @param rom
	 * @param start The stage index to start from
	 */
	private static void recursiveAnalysis(int[] ram, int[] rom, int[] lengths) throws IOException {		
		int sumOfLengths = 0;
		
		for(int l : lengths) sumOfLengths += l;
		
		if(sumOfLengths == 5) { // All stages used
			// Construct groups
			int[][] groups = new int[lengths.length][2];
			
			for(int i = 0, g = 0; i < lengths.length; i++) {
				groups[i][0] = g;
				g += lengths[i] - 1;
				groups[i][1] = g++;
			}
			
			// Clone ram
			int[] ramClone = new int[ram.length];
			System.arraycopy(ram, 0, ramClone, 0, ram.length);
			
			// Run the program
			PipelinedSimulator sim = new PipelinedSimulator(ramClone, rom, groups);
			
			System.out.print(String.format("%-41s     ", Arrays.deepToString(groups) + ":"));
			
			int i = 0;
			for(; i < 5_000_000 & sim.step(); i++);
			
			if(i >= 5_000_000) System.out.println("Execution incomplete");
		} else { // Go over possible lengths
			// Create new lengths
			int[] nextLengths = new int[lengths.length + 1];
			System.arraycopy(lengths, 0, nextLengths, 0, lengths.length);
			
			// Loop
			for(int len = 1; len + sumOfLengths <= 5; len++) {
				nextLengths[nextLengths.length - 1] = len;
				recursiveAnalysis(ram, rom, nextLengths);
			}
		}
	}
}
