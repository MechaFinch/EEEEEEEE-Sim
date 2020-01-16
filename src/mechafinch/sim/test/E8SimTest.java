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
				0b10100001_00101110, // PUSH 0x2E
				0b00100000_00000100, // LD A, 0x04
				0b10100000_00000000, // PUSH A
				0b10100011_01000000, // PEEK B
				0b10100010_10000000, // POP C
				0b10100010_11000000, // POP D
				0b11100000_00000000, // exit
		};
		
		TestUtil.insert(romContents, rom);
		
		E8Simulator testSim = new E8Simulator(rom);
		
		//Execute until test inst.
		for(int i = 0; i < romContents.length; i++) {
			if(!testSim.step()) {
				break;
			}
		}
		
		//Dump state, execute, dump again
		//TestUtil.dumpState(testSim);
		//testSim.step();
		TestUtil.dumpState(testSim);
	}
}
