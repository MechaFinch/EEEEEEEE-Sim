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
				0b00100000_00000100,	//LD A, $04
				0b00100001_00001001,	//LD B, $09
				0b00100010_00001100,	//LD C, $0C
				0b00100011_00010001,	//LD D, $11
				0b11100001_00000000,	//INT A
				0b11100001_00000001,	//INT B
				0b11100001_00000010,	//INT C
				0b11100001_00000011,	//INT D
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
