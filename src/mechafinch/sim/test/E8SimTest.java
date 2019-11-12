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
				0b00100000_11110000,	//LD A, $F0
				0b00100001_00001110,	//LD B, $0D
				0b01001110_10001101,	//OR C, A, $D c=$FD
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
