package mechafinch.sim.test;

import java.io.IOException;

import mechafinch.sim.e8.E8Simulator;

/**
 * A place to test the simulator in a standard environment
 * 
 * @author Alex Pickering
 */
public class E8SimTest {
	public static void main(String[] args) throws IOException {
		int[] rom = new int[1024],
			  romContents = new int[] {	//Test
				0b00100000_10001111,	//LD A, $8F
				0b00100001_00000011,	//LD B, $03
				0b01011010_10000001,	//SRA C, A, B
				0b01011011_11000101,	//SRA D, A, $5
		};
		
		TestUtil.insert(romContents, rom);
		
		E8Simulator testSim = new E8Simulator(rom);
		
		//Execute until test inst.
		for(int i = 0; i < romContents.length - 1; i++) {
			testSim.step();
		}
		
		//Dump state, execute, dump again
		TestUtil.dumpState(testSim);
		testSim.step();
		TestUtil.dumpState(testSim);
	}
}
