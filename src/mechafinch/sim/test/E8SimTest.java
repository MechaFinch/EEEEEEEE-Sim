package mechafinch.sim.test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import mechafinch.asm.E8Assembler;
import mechafinch.sim.e8.E8Simulator;

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
		BufferedReader br = new BufferedReader(new FileReader("asm_tests/is prime.txt"));
		List<String> linesList = br.lines().collect(Collectors.toList());
		br.close();
		
		String[] lines = linesList.toArray(new String[linesList.size()]);
		//for(String s : lines) System.out.println(s);
		
		String[] assembled = E8Assembler.assembleToDualHex(lines);
		
		/*
		 * Run
		 */
		int[] ram = new int[256],
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
		for(int i = 0; i < ramString.length(); i += 2) {
			ram[i / 2] = Integer.parseInt(ramString.substring(i, i + 2), 16);
		}
		
		for(int i = 0; i < romString.length(); i += 4) {
			rom[i / 4] = Integer.parseInt(romString.substring(i, i + 4), 16);
		}
		
		E8Simulator testSim = new E8Simulator(ram, rom);
		
		//Execute order 66 (at most 256x)
		for(int i = 0; i < 256 && testSim.step(); i++);
		
		//Dump state, execute, dump again
		//TestUtil.dumpState(testSim);
		//testSim.step();
		TestUtil.dumpState(testSim);
	}
}
